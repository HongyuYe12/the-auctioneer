package controller;

import model.*;

import util.RandomUtil;
import util.InventoryManager;
import util.AuctionLevelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuctionStateMachine {

    private final AuctionSession session = new AuctionSession();
    private final Player player;

    private final List<SaleResult> saleResults = new ArrayList<>();

    public static class SaleResult {

        public Artifact item;
        public boolean sold;
        public int soldPrice;

        public SaleResult(
                Artifact item,
                boolean sold,
                int soldPrice) {

            this.item = item;
            this.sold = sold;
            this.soldPrice = soldPrice;
        }
    }

    private volatile boolean itemActive = false;

    private int startValue;
    private volatile int countdown = 10;

    private int nextBidTimeLocation;
    private NPC nextBidder;
    private int nextBidPrice;

    private int currentNpcIndex = 0;
    // 记录上一位出价者
    private NPC lastBidder = null;

    // 记录当前拍品已经出价次数
    // 用于后续概率衰减
    private int bidCount = 0;
    

    // 每件拍品随机封顶倍率
    private double currentMaxMultiplier;

    public AuctionStateMachine(Player player) {
        this.player = player;
    }
    
    private double calculateMaxMultiplier(Artifact item)
    {
        double npcPower = 0;

        for (NPC npc : session.getNpcs())
        {
            npcPower += npc.getBidChance();
        }

        npcPower /= session.getNpcs().size(); // 平均参与度

        double itemHotness = (1 - item.getDrawProbability()); // 稀有越高越容易炒

        double base = 1.8;

        double multiplier =
                base
                + npcPower * 1.2
                + itemHotness * 1.0;

        return Math.min(multiplier, 3.2); // 上限防爆
    }

    public void startAuction(
            List<Artifact> items,
            List<NPC> npcs) {

        for (Artifact item : items) {
            item.appraise();
        }

        session.setItems(items);
        session.setNpcs(npcs);
        session.setCurrentIndex(0);

        session.setActive(true);
        session.setPhase(
                AuctionPhase.AUCTIONING);

        startNextItem();
    }

    private void startNextItem() {

        if (session.getCurrentIndex()
                >= session.getItems().size()) {

            finishAuction();
            return;
        }

        Artifact item =
                session.getItems().get(
                        session.getCurrentIndex());

        startValue =
                item.getAuthenticValue();

        session.setCurrentPrice(
                startValue);

        session.setCurrentWinner(
                "SYSTEM");

        session.setHasBid(false);

        session.setPhase(
                AuctionPhase.BIDDING);

        countdown = 10;

        session.setHammerCountdown(
                countdown);

        itemActive = true;

        currentNpcIndex = 0;

        // 每件新拍品重置出价次数
        bidCount = 0;
        lastBidder = null;

        // 每件拍品随机成交上限
        currentMaxMultiplier =
                calculateMaxMultiplier(item);
        calculateNextBidder(item);

        runAuctionTimerLoop(item);
    }

    private void calculateNextBidder(
            Artifact item) {

        nextBidder = null;

        List<NPC> npcs =
                session.getNpcs();

        if (npcs == null
                || npcs.isEmpty())
            return;

        // 打乱NPC顺序
        // 防止固定轮流出价
        List<NPC> shuffled =
                new ArrayList<>(npcs);

        Collections.shuffle(
                shuffled);

        for (NPC npc : shuffled) {
        	
        	if (npc == lastBidder)
            {
                continue;
            }

            // 使用NPC倾向值
            double bidChance =
                    (1 -
                    item.getDrawProbability())
                    *
                    npc.getBidChance()
                    *
                    RandomUtil.randomDouble(
                            0.8,
                            1.2);

            // 出价次数越多概率越低
            double decay =
                    Math.max(
                            1.0 -
                            (bidCount * 0.12),
                            0.35);

            bidChance *= decay;

            bidChance =
                    Math.min(
                            bidChance,
                            1.0);

            if (
            RandomUtil.randomDouble(
                    0.0,
                    1.0)
                    < bidChance) {

                boolean isFirstBid =
                        "SYSTEM".equals(
                                session.getCurrentWinner());

                int currentPrice =
                        isFirstBid
                                ? startValue
                                : session.getCurrentPrice();

                int newPrice;

                if (isFirstBid) {

                    double multiplier =
                            RandomUtil.randomDouble(
                                    1.1,
                                    1.3);

                    newPrice =
                            (int)(
                                    startValue
                                    *
                                    multiplier);

                }
                else {

                    double raise =
                            RandomUtil.randomDouble(
                                    0.05,
                                    0.20);

                    int increment =
                            Math.max(
                                    (int)(
                                    startValue
                                    *
                                    raise),
                                    10);

                    newPrice =
                            currentPrice
                            +
                            increment;
                }

                if (newPrice
                        <=
                        session.getCurrentPrice()) {

                    newPrice =
                            session.getCurrentPrice()
                            +
                            10;
                }

                int maxPrice =
                        (int)(
                                startValue
                                *
                                currentMaxMultiplier);

                if (newPrice > maxPrice)
                    continue;

                nextBidPrice =
                        newPrice;

                int delay =
                        RandomUtil.randomInt(
                                1,
                                9);

                nextBidTimeLocation =
                        10 - delay;

                nextBidder =
                        npc;
                
                lastBidder = npc;

                break;
            }
        }
    }

    private void runAuctionTimerLoop(
            Artifact item) {

        new Thread(() -> {

            while (itemActive) {

                try {

                    Thread.sleep(
                            1000);

                }
                catch (Exception e) {
                    return;
                }

                if (!itemActive)
                    return;

                countdown--;

                session.setHammerCountdown(
                        countdown);

                if (
                nextBidder != null
                &&
                countdown
                ==
                nextBidTimeLocation) {

                    session.setCurrentPrice(
                            nextBidPrice);

                    session.setCurrentWinner(
                            nextBidder.getName());

                    session.setHasBid(true);

                    session.setPhase(
                            AuctionPhase.WAITING_HAMMER);

                    // 出价次数+1
                    bidCount++;

                    LogController.log(
                            nextBidder.getName()
                            +
                            " bids "
                            +
                            nextBidPrice
                            +
                            "!");

                    countdown = 10;

                    session.setHammerCountdown(
                            countdown);

                    calculateNextBidder(
                            item);

                    continue;
                }

                if (countdown <= 0) {

                    failItem(item);
                    return;
                }
            }

        }).start();
    }

    public void playerHammer() {

        synchronized (this) {

            if (!itemActive
                    ||
                    !session.isHasBid()) {

                return;
            }

            itemActive = false;
        }

        Artifact item =
                session.getItems().get(
                        session.getCurrentIndex());

        finalizeItem(item);
    }

    private synchronized void finalizeItem(
            Artifact item) {

        session.setPhase(
                AuctionPhase.ITEM_END);

        int price =
                session.getCurrentPrice();

        player.addGold(price);
        AuctionLevelService.updateLevel(
                player);

        InventoryManager.remove(
                player,
                item);

        saleResults.add(
                new SaleResult(
                        item,
                        true,
                        price));

        LogController.log(
                "Sold: "
                +
                item.getName()
                +
                " for "
                +
                price);

        delayNextItem();
    }

    private void failItem(
            Artifact item) {

        synchronized (this) {

            if (!itemActive)
                return;

            itemActive = false;
        }

        session.setPhase(
                AuctionPhase.ITEM_END);

        saleResults.add(
                new SaleResult(
                        item,
                        false,
                        0));

        LogController.log(
                "Unsold/Expired: "
                +
                item.getName());

        delayNextItem();
    }

    private void delayNextItem() {

        new Thread(() -> {

            try {

                Thread.sleep(
                        2000);

            }
            catch (Exception e) {
                return;
            }

            session.setCurrentIndex(
                    session.getCurrentIndex()
                    +
                    1);

            startNextItem();

        }).start();
    }

    private void finishAuction() {

        session.setPhase(
                AuctionPhase.FINISHED);

        session.setActive(false);

        LogController.log(
                "Auction Finished");
    }

    public AuctionSession getSession() {
        return session;
    }

    public List<SaleResult> getSaleResults() {
    	
        return saleResults;
    }
}