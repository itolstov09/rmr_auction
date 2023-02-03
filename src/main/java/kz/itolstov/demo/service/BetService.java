package kz.itolstov.demo.service;

import kz.itolstov.demo.exception.BetException;
import kz.itolstov.demo.model.Bet;
import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.model.User;
import kz.itolstov.demo.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BetService.class);

    private final ItemService itemService;
    private final NotificationService notificationService;
    private final BetRepository repository;

    private Map<Long, ArrayList<Bet>> betPool = new HashMap<>();

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
//            todo перевести в получение параметра из настроек
            item.setAuctionEndsAt(now.plusMinutes(240L));
            itemService.save(item);
        } else if (auctionEndsAt.isBefore(now)) {
            throw new BetException("Аукцион уже закончился");
        }

//        ставка считается валидной. можно добавлять в пул
        addBetToPool(bet);
    }

    private void addBetToPool(Bet bet) {
        ArrayList<Bet> bets = betPool.getOrDefault(bet.getItem().getId(), new ArrayList<>());
        bets.add(bet);
        // если ставка всего одна, значит пул лота пустой
        if (bets.size() == 1) {
            betPool.put(bet.getItem().getId(), bets);
        }
    }

    @Scheduled(fixedDelay = 1000)
    // todo рефакторинг - разбить на методы
    // todo потенциальный баг. В момент вычисления может поступить ставка
    // todo потенциальный баг. пока что нет проверки того, завершились ли торги
    public void betPoolWorker() {
        long startTime = System.currentTimeMillis();
        this.betPool.forEach((itemId, bets) -> {
            if (bets.size() > 0) {
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
                User newBuyer = maxBet.getAuthor();
                User oldBuyer = item.getBuyer();
                if ( (oldBuyer != null) && (!Objects.equals(oldBuyer, newBuyer)) ){
                    notificationService.oldBetIsBeatenNotification(oldBuyer, item.getName());
                }
                item.setBuyer(newBuyer);
                Item save = itemService.save(item);

                //рассылаем уведомления по оставшимся ставкам, так как их перебила другая ставка
                for (Bet loser : bets) {
                    notificationService.sendBetIsRejectedNotification(loser);
                }

//          очищаем список ставок
                bets.clear();
            }

        });
        long elapsedMillis = System.currentTimeMillis() - startTime;
        LOGGER.info("betPoolWorker - Elapsed time (ms): " + elapsedMillis);
    }

    public Bet save(Bet bet) {
        return repository.save(bet);
    }

}
