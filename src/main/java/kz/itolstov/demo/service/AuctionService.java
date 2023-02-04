package kz.itolstov.demo.service;

import kz.itolstov.demo.exception.BetException;
import kz.itolstov.demo.model.Bet;
import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.model.User;
import kz.itolstov.demo.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuctionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionService.class);

    private final int AUCTION_DURATION_IN_MINUTES;

    private final ItemService itemService;
    private final NotificationService notificationService;
    private final BetRepository repository;

    public AuctionService(
            @Value("${auction.duration-in-minutes}")
            String AUCTION_DURATION_IN_MINUTES,
            ItemService itemService,
            NotificationService notificationService,
            BetRepository repository
    ) {
        this.AUCTION_DURATION_IN_MINUTES = Integer.parseInt(AUCTION_DURATION_IN_MINUTES);
        this.itemService = itemService;
        this.notificationService = notificationService;
        this.repository = repository;
    }

    // todo сделать класс-обертку над структурой данных. Либо заменить весь пул на класс
    private final Map<Long, Map<String, Object>> betPool = new HashMap<>();

    public void addBet(Bet bet) {
        Item item = itemService.findById(bet.getItem().getId());
        if (!item.getStatus().equals(Item.Status.ACTIVE)) {
            throw new BetException(String.format("Лот \"%s\" не участвует в торгах", bet.getItem().getName()));
        }

        Integer currentPrice = item.getCurrentPrice();
        Integer itemPrice = currentPrice == null ? item.getMinimalPrice() : currentPrice;

        Integer amount = bet.getAmount();
        if (amount <= itemPrice) {
            throw new BetException("Ставка должна быть выше минимальной цены");
        }

        //todo нужно обрезать секунды, чтобы конец аукциона был в 00 секунд
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime auctionEndsAt = item.getAuctionEndsAt();
        if (auctionEndsAt == null) {
            item.setAuctionEndsAt(now.plusMinutes(AUCTION_DURATION_IN_MINUTES));
            itemService.save(item);
        } else if (auctionEndsAt.isBefore(now)) {
            throw new BetException("Аукцион уже закончился");
        }

//        ставка считается валидной. можно сохранять и добавлять в пул
        repository.save(bet);
        addBetToPool(bet);
    }

    private void addBetToPool(Bet bet) {
        Map<String, Object> itemBetPool = betPool.getOrDefault(bet.getItem().getId(), new HashMap<>());
        boolean isBetForItemLocked = (boolean) itemBetPool.get("isLocked");
        if (isBetForItemLocked) {
            throw new BetException("Идет расчет ставки. Повторите попытку позже");
        }

        ArrayList<Bet> bets = (ArrayList<Bet>) itemBetPool.get("bets");

        bets.add(bet);
        // если ставка всего одна, значит исходный пул лота пустой. Нужно его заменить
        if (bets.size() == 1) {
            itemBetPool.put("bets", bets);
        }
        // обновляем пул
        betPool.put(bet.getItem().getId(), itemBetPool);
    }

    @Scheduled(fixedDelay = 1000)
    // todo рефакторинг - разбить на методы
    public void betPoolWorker() {
        long startTime = System.currentTimeMillis();
        this.betPool.forEach((itemId, poolMap) -> {
            ArrayList<Bet> bets = (ArrayList<Bet>) poolMap.get("bets");
            boolean isPossibleToGetMaxBet = bets.size() > 0 && itemService.findById(itemId).getStatus().equals(Item.Status.ACTIVE);
            if (isPossibleToGetMaxBet) {
                lockBetPool(itemId);

                int maxBetIndex = 0;
                int maxAmount = bets.get(maxBetIndex).getAmount();
                List<Integer> maxBetsIndexes = new ArrayList<>();
                for (int betIndex = 1; betIndex < bets.size(); betIndex++) {
                    Bet bet = bets.get(betIndex);
                    int betAmount = bet.getAmount();
                    if (betAmount > maxAmount) {
                        maxAmount = betAmount;
                        maxBetIndex = betIndex;
                        maxBetsIndexes.clear();
                        maxBetsIndexes.add(betIndex);
                    } else if (betAmount == maxAmount) {
                        maxBetsIndexes.add(betIndex);
                    }
                }

                // если есть несколько ставок с максимальной суммой - ищем ту ставку, которая самая ранняя
                if (maxBetsIndexes.size() > 1) {
                    LocalDateTime maxBetCreatedAt = bets.get(0).getCreatedAt();
                    for (int i = 1; i < maxBetsIndexes.size(); i++) {
                        Bet bet = bets.get(i);
                        LocalDateTime createdAt = bet.getCreatedAt();
                        if (createdAt.isBefore(maxBetCreatedAt)) {
                            maxBetCreatedAt = createdAt;
                            maxBetIndex = maxBetsIndexes.get(i);
                        }
                    }
                }

                // удаляем максимальную ставку из списка
                Bet maxBet = bets.remove(maxBetIndex);

                Item item = itemService.findById(itemId);
                item.setCurrentPrice(maxBet.getAmount());
                // Если предыдущая ставка есть и она не от того же человека - отправляем уведомление о перебитой ставке
                User newBuyer = maxBet.getAuthor();
                User oldBuyer = item.getBuyer();
                if ( (oldBuyer != null) && (!Objects.equals(oldBuyer, newBuyer)) ){
                    notificationService.oldBetIsBeatenNotification(oldBuyer, item.getName());
                }
                item.setBuyer(newBuyer);
                // обновляем информацию о лоте
                itemService.save(item);

                //рассылаем уведомления по оставшимся ставкам, так как их перебила другая ставка
                for (Bet loser : bets) {
                    notificationService.sendBetIsRejectedNotification(loser);
                }

//          очищаем пул ставок лота
                clearItemBetPool(itemId);

                unlockBetPool(itemId);
            }

        });
        long elapsedMillis = System.currentTimeMillis() - startTime;
        LOGGER.info("betPoolWorker - Elapsed time (ms): " + elapsedMillis);
    }

    //todo возможно этот метод не нужен
    @Scheduled(fixedDelay = 100)
    public void removeDiscardItemFromBetPool() {
        long startTime = System.currentTimeMillis();

        Set<Long> activeItemIds = itemService.getActiveItemIds();
        Set<Long> betPoolIds = this.betPool.keySet();
        for (Long betPoolId : betPoolIds) {
            if (!activeItemIds.contains(betPoolId)) {
                this.betPool.remove(betPoolId);
                LOGGER.info("removed item from pool with id {}", betPoolId);
            }
        }

        long elapsedMillis = System.currentTimeMillis() - startTime;
        LOGGER.info("removeDiscardItemFromBetPool - Elapsed time (ms): " + elapsedMillis);
    }

    //todo дать внятное имя методу.
    @Scheduled(fixedDelay = 100)
    public void discardItems() {
        long startTime = System.currentTimeMillis();
        List<Item> needToDiscardItems = itemService.getItemsNeedToDiscard();
        for (Item item : needToDiscardItems) {
            LOGGER.info("Auction is over for: {}", item);
            item.setStatus(Item.Status.DISCARD);
            itemService.save(item);

            // удаляем ставки из пула
            // todo возможно стоит отправлять или вызывать ошибку для тех ставок, которые оказались в пуле..
            this.betPool.remove(item.getId());
            notificationService.sendAuctionIsOver(item);


        }

        long elapsedMillis = System.currentTimeMillis() - startTime;
        LOGGER.info("removeDiscardItemFromBetPool - Elapsed time (ms): " + elapsedMillis);
    }

    private void clearItemBetPool(Long itemId) {
        ArrayList<Bet> bets = (ArrayList<Bet>) this.betPool.get(itemId).get("bets");
        bets.clear();
    }


    private void unlockBetPool(Long itemId) {
        this.betPool.get(itemId).put("isLocked", false);
    }

    private void lockBetPool(Long itemId) {
        this.betPool.get(itemId).put("isLocked", true);
    }


    public void saveItem(Item item1) {
        Item savedItem = itemService.save(item1);
        createItemBetPool(savedItem.getId());
    }

    private void createItemBetPool(Long itemId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("isLocked", false);
        map.put("bets", new ArrayList<Bet>());

        this.betPool.put(itemId, map);
    }
}
