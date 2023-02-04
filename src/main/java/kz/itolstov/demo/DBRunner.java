package kz.itolstov.demo;

import kz.itolstov.demo.model.Bet;
import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.model.User;
import kz.itolstov.demo.service.AuctionService;
import kz.itolstov.demo.service.ItemService;
import kz.itolstov.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DBRunner implements ApplicationRunner  {
    private final static Logger LOGGER = LoggerFactory.getLogger(DBRunner.class);

    private final UserService userService;
    private final ItemService itemService;
    private final AuctionService auctionService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user1 = new User("user1@mail.com", "pass1");
        userService.save(user1);
        LOGGER.info("Saved user: {}", user1);

        Item item1 = new Item(user1, "item1", "d1", 100, Item.Status.ACTIVE);
        auctionService.saveItem(item1);

        User buyer1 = new User("buyer1@mail.com", "pass2");
        userService.save(buyer1);
        LOGGER.info("Saved user: {}", buyer1);

        User buyer2 = new User("buyer2@mail.com", "pass3");
        userService.save(buyer2);
        LOGGER.info("Saved user: {}", buyer2);

        User buyer3 = new User("buyer3@mail.com", "pass4");
        userService.save(buyer3);
        LOGGER.info("Saved user: {}", buyer3);

        Bet bet1 = new Bet(buyer1, item1, 120);
        Bet bet2 = new Bet(buyer2, item1, 120);
        Bet bet3 = new Bet(buyer3, item1, 200);

        auctionService.addBet(bet1);
        LOGGER.info("New bet added: {}", bet1);
        auctionService.addBet(bet2);
        LOGGER.info("New bet added: {}", bet2);
        auctionService.addBet(bet3);
        LOGGER.info("New bet added: {}", bet3);


        LOGGER.info("Thread sleep: 2 seconds");
        Thread.sleep(2000);

        Item item1Afterbets = itemService.findById(item1.getId());
        LOGGER.info("item1 after first round of bets: {}", item1Afterbets);

        Bet bet4 = new Bet(buyer1, item1, 220);
        Bet bet5 = new Bet(buyer2, item1, 500);
        Bet bet6 = new Bet(buyer3, item1, 400);

        auctionService.addBet(bet4);
        LOGGER.info("New bet added: {}", bet4);
        auctionService.addBet(bet5);
        LOGGER.info("New bet added: {}", bet5);
        auctionService.addBet(bet6);
        LOGGER.info("New bet added: {}", bet6);

        LOGGER.info("Thread sleep: 2 seconds");
        Thread.sleep(2000);

        Item item1AfterSecondBetRun = itemService.findById(item1.getId());
        LOGGER.info("item1 after second round of bets: {}", item1AfterSecondBetRun);

        // для теста. так как продолжительность аукциона указывается в минутах, для
        // теста указывается длительность 1 минута
//        поэтому слип на 80 секунд, чтобы задача отработала по таймеру
//        Thread.sleep(1000 * 80);
//        Item timedoutItem = itemService.findById(item1.getId());





    }
}
