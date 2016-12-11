import model.*;
import model.dealers.Dealer;
import model.dealers.RandomDeltaBuyer;
import model.dealers.SimpleDealer;
import model.dealers.SimpleBuyerAction;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class Sketch1 extends PApplet {

    static public void main(String args[]) {
        PApplet.main(new String[] { "Sketch1" });
    }

    private Exchange exchange;

    public void setup() {
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
                System.out.println("Order has been made; stock price has been update " + o.getBuyer().getPrice() + "!");
                System.out.println("Still waiting: buy:" + exchange.getBuyQueue().size() + " sell:" + exchange.getSellQueue().size());

                System.out.println("Waiting requsts: BUY:" + exchange.getBuyQueue().size() + " SELL:" + exchange.getSellQueue().size());
            }

            @Override
            public void spoofingDetected(Order buyPeek, Order sellPeek) {

            }
        });

        List<Dealer> dealers = new ArrayList<>();

        // Buys every time for price cheaper then is current one
        dealers.add(new SimpleDealer(
                exchange,
                -2,
                SimpleBuyerAction.BUY
        ));

        // Sells every time for price higher then is current one
        dealers.add(new SimpleDealer(
                exchange,
                2,
                SimpleBuyerAction.SELL
        ));

        dealers.add(new RandomDeltaBuyer(
                exchange,
                10
        ));


        dealers.forEach(Dealer::start);

        do {
            exchange.doDeals();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Exchange matched failed.");
            }
        } while (true);

//        do {
//            exchange.doDeals();
//            exchange.buy(5);
//            exchange.buy(6);
//            exchange.buy(7);
//            exchange.buy(7);
//            exchange.doDeals();
//            exchange.buy(7);
//
//            exchange.sell(7);
//            exchange.sell(7);
//            exchange.doDeals();
//            exchange.sell(7);
//            exchange.sell(6);
//            exchange.doDeals();
//            exchange.sell(9);
//
//            exchange.doDeals();
//            exchange.doDeals();
//            exchange.doDeals();
//
//            System.out.println("Buy queue:");
//            for (Order order : exchange.getBuyQueue()) {
//                System.out.println("Buy: " + order.getPrice());
//            }
//
//            System.out.println("Sell queue:");
//            for (Order order : exchange.getSellQueue()) {
//                System.out.println("Buy: " + order.getPrice());
//            }
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } while (true);


//        size(500,500);

    }
}
