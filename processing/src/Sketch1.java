import model.*;
import model.dealers.*;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class Sketch1 extends PApplet {

    static public void main(String args[]) {
        PApplet.main(new String[]{"Sketch1"});
    }

    private Exchange exchange;

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

        drawPositions();

        frameNumer++;
        if(frameNumer > (width - 100)) {
            frameNumer = 0;
        }
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
