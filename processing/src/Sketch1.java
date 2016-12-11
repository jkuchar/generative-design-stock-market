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
                System.out.println("QUEUE: BUY:" + exchange.getAskPrice()+ "; SELL:" + exchange.getBidPrice() + "; spread: "+exchange.getSpread());
            }

            @Override
            public void spoofingDetected(Order buyPeek, Order sellPeek) {

            }
        });

        List<Dealer> dealers = new ArrayList<>();

//         Buys every frameNumer for price cheaper then is current one
        dealers.add(new SimpleDealer(
                exchange,
                2,
                SimpleBuyerAction.BUY
        ));

        dealers.add(new SimpleDealer(
                exchange,
                -2,
                SimpleBuyerAction.SELL
        ));

//        // Sells every frameNumer for price higher then is current one
//
//        for(int i = 0; i < 10; i++) {
//                dealers.add(new SimpleDealer(
//                    exchange,
//                    (int) -(Math.random() * 100),
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
////        }
//        for (int i = 0; i < 5; i++) {
//            dealers.add(new RandomDeltaDealer(
//                            exchange,
//                            (int) Math.round(Math.random() * 10),
//                            0.9
//                    )
//            );
//        }

        dealers.add(new MarketMakerDealer(exchange));

        exchange.start();
        dealers.forEach(Dealer::start);
    }

    public void setup() {

        this.initExchange();

        colorMode(HSB, 360, 100, 100);
        size(800, 800);

//        color(360,100, 100);

        noSmooth();
    }

    static int frameNumer = 0;

    private float mapE(int value, int max) {
        return max - map(value, 700,1300, 0, max);
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

        stroke(360, 0, 100); // white
        point(frameNumer, mapE(exchange.getLastDealPrice(), height));
        point(frameNumer, 1+mapE(exchange.getLastDealPrice(), height));


        frameNumer++;
        if(frameNumer > width) {
            frameNumer = 0;
        }
    }
}
