package kz.itolstov.demo.service;

import kz.itolstov.demo.model.Bet;
import kz.itolstov.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final static Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    public void sendBetIsRejectedNotification(Bet bet) {
        String message = String.format(
                "Ваша ставка на лот \"%s\" в размере %s была перебита",
                bet.getItem().getName(), bet.getAmount()
        );
        LOGGER.info(String.format("send notification to `%s`: %s", bet.getAuthor().getEmail(), message));
    }

    public void oldBetIsBeatenNotification(User oldBuyer, String itemName) {
        String message = String.format("Вашу ставку на лот %s перебили", itemName);

        LOGGER.info(String.format("send notification to '%s': %s", oldBuyer.getEmail(), message));
    }
}
