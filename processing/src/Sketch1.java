import model.*;
import model.dealers.Dealer;
import model.dealers.RandomDeltaDealer;
import model.dealers.SimpleDealer;
import model.dealers.SimpleBuyerAction;
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
//                System.out.println("PRICE new price: " + o.getBuyer().getPrice() + " ; QUEUE: buy:" + exchange.getBuyQueue().size() + " sell:" + exchange.getSellQueue().size());
            }

            @Override
            public void spoofingDetected(Order buyPeek, Order sellPeek) {

            }
        });

        List<Dealer> dealers = new ArrayList<>();

//         Buys every frameNumer for price cheaper then is current one
//        dealers.add(new SimpleDealer(
//                exchange,
//                -2,
//                SimpleBuyerAction.BUY
//        ));
//
//        // Sells every frameNumer for price higher then is current one

//        for(int i = 0; i < 2; i++) {
//            dealers.add(new SimpleDealer(
//                exchange,
//                3,
//                SimpleBuyerAction.BUY
//            ));
//        }

//
        for (int i = 0; i < 10; i++) {
            dealers.add(new RandomDeltaDealer(
                            exchange,
                            (int) Math.round(Math.random() * 10),
                            0.1
                    )
            );
        }

        for (int i = 0; i < 10; i++) {
            dealers.add(new RandomDeltaDealer(
                            exchange,
                            (int) Math.round(Math.random() * 10),
                            0.9
                    )
            );
        }

        dealers.forEach(Dealer::start);

        // Exchange engine
        (new Thread(() -> {
            do {
                exchange.doDeals();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Exchange matched failed.");
                }
            } while (true);
        })).start();
    }

    public void setup() {

        this.initExchange();

        colorMode(HSB, 360, 100, 100);
        size(800, 800);

//        color(360,100, 100);
    }

    static int frameNumer = 0;

    private float mapE(int value, int max) {
        return max - map(value, 900,1100, 0, max);
    }


    @Override
    public void draw() {
        if(frameNumer == 0) {
            background(0);
        }

//        stroke(360, 100, 100);
//        point(frameNumer, mapE(exchange.getLastDealPrice()));

        stroke(360, 100, 100);
        exchange.getSellQueue().forEach((Order o) -> {
            point(frameNumer, mapE(o.getPrice(), height));
        });
        stroke(200, 100, 100);
        exchange.getBuyQueue().forEach((Order o) -> {
            point(frameNumer, mapE(o.getPrice(), height));
        });
        stroke(360, 0, 100);
        point(frameNumer, mapE(exchange.getLastDealPrice(), height));
        point(frameNumer, 1+mapE(exchange.getLastDealPrice(), height));


        frameNumer++;
        if(frameNumer > width) {
            frameNumer = 0;
        }
    }
}
