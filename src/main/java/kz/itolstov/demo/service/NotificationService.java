package kz.itolstov.demo.service;

import kz.itolstov.demo.model.Bet;
import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationService {
    private final static Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService() {
        LOGGER.debug("NotificationService started at {}", new Date(System.currentTimeMillis()));
    }

    public void sendBetIsRejectedNotification(Bet bet) {
        String message = String.format(
                "Ваша ставка на лот \"%s\" в размере %s была перебита",
                bet.getItem().getName(), bet.getAmount()
        );
        LOGGER.info(String.format("send notification to '%s': %s", bet.getAuthor().getEmail(), message));
    }

    public void oldBetIsBeatenNotification(User oldBuyer, String itemName) {
        String message = String.format("Во время торгов Вашу ставку на лот %s перебили", itemName);

        LOGGER.info(String.format("send notification to '%s': %s", oldBuyer.getEmail(), message));
    }

    public void sendAuctionIsOver(Item item) {
        String ownerMessage = String.format("Завершился аукцион для лота %s. Победил пользователь %s", item.getName(), item.getBuyer().getEmail());
        String buyerMessage = String.format("Поздравляем! Ваша ставка для лота %s выиграла!", item.getName());

        LOGGER.info("Send notification to '{}': {}", item.getOwner().getEmail(), ownerMessage);
        LOGGER.info("Send notification to '{}': {}", item.getBuyer().getEmail(), buyerMessage);
    }
}
