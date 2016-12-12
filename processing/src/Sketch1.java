import model.*;
import model.dealers.*;
import processing.core.PApplet;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Sketch1 extends PApplet {

    static public void main(String args[]) {
        PApplet.main(new String[]{"Sketch1"});
    }

    private Exchange exchange;

    int closedDeals = 0;

    private void initExchange() {
        exchange = new Exchange();

        exchange.addObserver(new ExchangeObserver() {
            @Override
            public void newSell(Order o) {
                //System.out.println("req: SELL: " + o.getPrice());
            }

            @Override
            public void newBuy(Order o) {
                //System.out.println("req:  BUY: " + o.getPrice());
            }

            @Override
            public void orderCompleted(CompletedOrder o) {
//                System.out.println("PRICE new price: " + o.getBuyer().getPrice() + " ; QUEUE: ask:" + exchange.getBuyQueue().size() + " bid:" + exchange.getSellQueue().size());
//                System.out.println("QUEUE: BUY:" + exchange.getAskPrice()+ "; SELL:" + exchange.getBidPrice() + "; spread: "+exchange.getSpread());
                closedDeals++;
            }

            @Override
            public void spoofingDetected(Order buyPeek, Order sellPeek) {

            }
        });

        List<Dealer> dealers = new ArrayList<>();

        for(int i = 0; i < 100; i++) {
            exchange.bid(1008);
            exchange.bid(1007);
            exchange.bid(1005);

            exchange.ask(989);
            exchange.ask(988);
            exchange.ask(987);
        }

        for(int i = 0; i<10; i++) {
            for(int y = 0; y < 3; y++) {
                dealers.add(new SimpleDealer(
                        exchange,
                        i,
                        SimpleBuyerAction.SELL
                ));

                dealers.add(new SimpleDealer(
                        exchange,
                        -i,
                        SimpleBuyerAction.BUY
                ));
            }
        }

        // dumping:
        for(int i = 0; i<3; i++) {
            for(int y = 0; y < 3; y++) {
                dealers.add(new SimpleDealer(
                        exchange,
                        i,
                        SimpleBuyerAction.BUY
                ));

                dealers.add(new SimpleDealer(
                        exchange,
                        -i,
                        SimpleBuyerAction.SELL
                ));
            }
        }

//        // Sells every frameNumer for price higher then is current one
//
//        for(int i = 0; i < 10; i++) {
//                dealers.add(new SimpleDealer(
//                    exchange,
//                    (int) -(Math.random() * 50),
//                    SimpleBuyerAction.SELL
//                ));
//        }


//        for(double sentiment = 0.0; sentiment<=1; sentiment+=0.1) {
//            for (int i = 0; i < 5; i++) {
//                dealers.add(new RandomDeltaDealer(
//                                exchange,
//                                (int) Math.round(Math.random() * 1),
//                                sentiment
//                        )
//                );
//            }
//        }
//
//        for (int i = 0; i < 1; i++) {
//        dealers.add(new RandomDeltaDealer(
//                exchange,
//                (int) Math.round(Math.random() * 20),
//                0,
//                500)
//        );
//        dealers.add(new RandomDeltaDealer(
//                exchange,
//                (int) Math.round(Math.random() * 15),
//                .5,
//                300)
//        );
//        dealers.add(new RandomDeltaDealer(
//                exchange,
//                (int) Math.round(Math.random() * 10),
//                1,
//                100
//        ));
//
//        dealers.add(new RandomDeltaDealer(
//                exchange,
//                (int) Math.round(Math.random() * 3),
//                1,
//                10
//        ));
//        dealers.add(new RandomDeltaDealer(
//                exchange,
//                (int) Math.round(Math.random() * 1),
//                1,
//                5
//        ));

//        dealers.add(new WantToBuyForAmount(exchange));

//        dealers.add(new WantToSellForAmount(exchange));
//        dealers.add(new WantToSellForAmount(exchange));
//        dealers.add(new WantToSellForAmount(exchange));
//        dealers.add(new WantToSellForAmount(exchange));


//        dealers.add(new WantToBuyForAmount(exchange, 1200, 5000));
//        dealers.add(new WantToSellForAmount(exchange, 900, 5000));
//        dealers.add(new WantToSellForAmount(exchange, 900, 500000));
//        dealers.add(new WantToBuyForAmount(exchange, 1200, 500000));
//
//        dealers.add(new MarketMakerDealer(exchange));

        exchange.start();
        dealers.forEach((Dealer d) -> {
            d.start();
            try {
                Thread.sleep((int) (Math.random() * 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void setup() {

        this.initExchange();

        colorMode(HSB, 360, 100, 100);
        size(1500, 800);

        frameRate(60);

//        color(360,100, 100);

        noSmooth();


        positions_leftX = width-100;
        positions_rightX = width;


        sellersRed = color(360, 100, 100);
        buyersBlue = color(200, 100, 100);
        dealsWhite = color(360, 0, 100);
    }

    private static int frameNumer = 0;

    private float mapE(int value, int max) {
        return max - map(value, 700,1300, 0, max);
    }

    private int sellersRed;
    private int buyersBlue;
    private int dealsWhite;

    private int positions_leftX;
    private int positions_rightX;

    @Override
    public void draw() {
        handleMouse();
        drawGraph();
        drawInstruction();
        drawAccountStatus();
        drawPositions();
    }

    private void drawInstruction() {
        StringBuilder status = new StringBuilder();
        status.append("Click left to buy shares. Click right to sell shares. See what happens on your account.\n");
        status.append("Use keyboard to control big-boy account. Press 's' to sell or 'b' to buy 100 000.\n\n");
        status.append("Graph: red dots are sellers requests, blue dots are buyers requests. White is closed deal.");
        //status.append("Try to double your money ");

        fill(255);
        noStroke();
        textSize(12);
        text(status.toString(), 250, 20);
    }

    private void drawAccountStatus() {
        stroke(255);
        fill(0);
        rect(0, 0, 220, 110);

        StringBuilder status = new StringBuilder();
        status.append("Your account:\n");
        status.append("Money: ");
        status.append(money);
        status.append("\n");

        status.append("Own shares:");
        status.append(ownShares);
        status.append("\n");

        status.append("Shares bought / sold:");
        status.append(sharesBought);
        status.append(" / ");
        status.append(sharesSold);
        status.append("\n");

        fill(255);
        noStroke();
        textSize(12);
        text(status.toString(), 10, 20);

    }

    int money = 50000;
    int ownShares = 0;
    int sharesBought = 0;
    int sharesSold = 0;


    private void handleMouse() {
        if (mousePressed && (mouseButton == LEFT)) {
            if(money < 1) {
                return;
            }
            // buy
            Order o = exchange.ask(exchange.getAskPrice());
            o.addCompleted(() -> {
                money -= o.getPrice();
                sharesBought++;
                ownShares++;
            });

        } else if (mousePressed && (mouseButton == RIGHT)) {
            if(ownShares < 1) {
                return;
            }

            // sell
            Order o = exchange.bid(exchange.getBidPrice());
            o.addCompleted(() -> {
                money += o.getPrice();
                sharesSold++;
                ownShares--;
            });
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'b':
                (new WantToBuyForAmount(exchange, 1200, 100000)).start();
                break;
            case 's':
                (new WantToSellForAmount(exchange, 800, 100000)).start();
                break;
//            case 'r':
////                new RandomDeltaDealer(
////                        exchange,
////                        (int) Math.round(Math.random() * 3),
////                        1,
////                        10
////                ).start();
//                new RandomDeltaDealer(
//                        exchange,
//                        (int) Math.round(Math.random() * 1),
//                        0,
//                        5
//                ).start();
//                break;
        }
    }

    private void drawGraph() {
        if(frameNumer == 0) {
            background(0);
        }

//        stroke(360, 100, 100);
//        point(frameNumer, mapE(exchange.getLastDealPrice()));

        stroke(sellersRed);
        exchange.getSellQueue().forEach((Order o) -> {
            point(frameNumer, mapE(o.getPrice(), height));
        });

        stroke(buyersBlue);
        exchange.getBuyQueue().forEach((Order o) -> {
            point(frameNumer, mapE(o.getPrice(), height));
        });

        stroke(dealsWhite);
        point(frameNumer, mapE(exchange.getLastDealPrice(), height));
        point(frameNumer, 1+mapE(exchange.getLastDealPrice(), height));


        drawDeals();

        frameNumer++;
        if(frameNumer > (width - 100)) {
            frameNumer = 0;
        }
    }

    private void drawDeals() {
        int waitFrames = 20;
        if(frameNumer % waitFrames != 0) {
            return;
        }

        fill(255);
//        line(frameNumer, height - closedDeals, frameNumer, height);
        rect(frameNumer, height - closedDeals, waitFrames, closedDeals);

        // reset counter
        closedDeals = 0;
    }

    private void drawPositions() {
        noStroke();
        fill(0, 80);
        rect(positions_leftX, 0, 100, height);

        stroke(sellersRed, 50);
        exchange.getSellQueue().forEach((Order o) -> {
            positions_drawALine(o.getPrice(), 50, 50);
        });

        stroke(buyersBlue, 50);
        exchange.getBuyQueue().forEach((Order o) -> {
            positions_drawALine(o.getPrice(), 50, 50);
        });


        stroke(sellersRed);
        positions_drawALine(exchange.getBidPrice(), 25, 25);

        stroke(buyersBlue);
        positions_drawALine(exchange.getAskPrice(), 25, 25);

        stroke(dealsWhite);
        positions_drawALine(exchange.getLastDealPrice(), 0, 25);


        drawStatusText();

    }

    static int lastPrice = 0;

    private void drawStatusText() {

        StringBuilder status = new StringBuilder();
        status.append("price:\n");
        status.append(exchange.getLastDealPrice());
        status.append("\n");
        status.append("buy / sell price:\n");
        status.append("\n");
        status.append(exchange.getAskPrice());
        status.append(" / ");
        status.append(exchange.getBidPrice());
        status.append("\n");
        status.append("\n");
        status.append("spread:\n");
        status.append(exchange.getBidPrice() - exchange.getAskPrice());
        status.append("\n");
        status.append("\n");
        status.append("number of buers / sellers:\n");
        status.append(exchange.getAskSize());
        status.append(" / ");
        status.append(exchange.getBigSize());

        int currentPrice = exchange.getLastDealPrice();
        if(lastPrice < currentPrice) {
            fill(121, 80, 80); // green
        } else {
            fill(0, 80, 80); // red
        }

        lastPrice = currentPrice;

        noStroke();
        textSize(12);
        text(status.toString(), positions_leftX + 10, 20);
    }

    private void positions_drawALine(int price, double leftStartProcent, double widthProcent) {
        leftStartProcent /= 100;
        widthProcent /= 100;
        float y = mapE(price, height);
        float leftX = positions_leftX + (float) (100 * leftStartProcent);
        float rightX = leftX + (float) (100 * widthProcent);
        line(leftX, y, positions_rightX, y);
    }
}
