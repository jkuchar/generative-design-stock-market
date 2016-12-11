package model.dealers;

import model.CompletedOrder;
import model.Exchange;
import model.ExchangeObserver;
import model.Order;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;

/**
 * Market makes tries to find this situation:
 * When someone sells for lower price and someone else buys for higher price.
 * When this happens market makes will then make the spread and that is what they live from.
 *
 * @link http://www.investopedia.com/university/electronictrading/trading3.asp
 */
public class MarketMakerDealer extends Thread implements Dealer, ExchangeObserver {

    private final Exchange exchange;

    public MarketMakerDealer(Exchange exchange)
    {
        this.exchange = exchange;
        this.exchange.addObserver(this);
    }

    private int positionSize = 0;
    private int priceTotal = 0;

    private static enum State {WAIT, BUY, SELL};

    private List<Order> asks = new ArrayList<>();
    private List<Order> bids = new ArrayList<>();

    private State state = State.WAIT;

    @Override
    public void run() {
        do {
            try {
                makeMarket();

                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
    }

    private void makeMarket() throws InterruptedException {
        if(state == State.WAIT) {
            // wait until there is a higher spread
            if(exchange.getSpread() > 10) {
                state = State.BUY;
            }
            return;
        }

        if(state == State.BUY) {
            NavigableSet<Order> sellQueue = exchange.getSellQueue();

            for (Order lowestPriceToBuy : sellQueue) {
                asks.add(exchange.ask(lowestPriceToBuy.getPrice()));
            }
            Thread.sleep(10000);
            state = State.SELL;
        }

        if(state == State.SELL) {
            // now try to sell with as high amount of possible
            final Iterator<Order> buyIterator = exchange.getBuyQueue().descendingIterator();

            while(buyIterator.hasNext()) {
                Order highestPriceToSell = buyIterator.next();

                bids.add(exchange.bid(highestPriceToSell.getPrice()));
            }

            bids.forEach(exchange::cancelOrder);
            asks.forEach(exchange::cancelOrder);
            bids.clear();
            asks.clear();

            Thread.sleep(10000);
            state = State.WAIT;
        }

        // try to get some shares for low price

        NavigableSet<Order> buyQueue = exchange.getBuyQueue();
        final Iterator<Order> buyIterator = buyQueue.descendingIterator();


//        while(sellIterator.hasNext()) {
//            Order lowestPriceToBuy = sellIterator.next();
//
//            if(highestPriceToSell.getPrice() <= lowestPriceToBuy.getPrice()) {
//                continue;
//            }
//
//            exchange.ask(lowestPriceToBuy.getPrice());
//            exchange.bid(highestPriceToSell.getPrice());
//
//            System.out.println("Spread: " + (highestPriceToSell.getPrice() - lowestPriceToBuy.getPrice()));
//        }

//        while(buyIterator.hasNext()) {
//            Order highestPriceToSell = buyIterator.next();
//
//            while(sellIterator.hasNext()) {
//                Order lowestPriceToBuy = sellIterator.next();
//
//                if(highestPriceToSell.getPrice() <= lowestPriceToBuy.getPrice()) {
//                    continue;
//                }
//
//                exchange.ask(lowestPriceToBuy.getPrice());
//                exchange.bid(highestPriceToSell.getPrice());
//
//                System.out.println("Spread: " + (highestPriceToSell.getPrice() - lowestPriceToBuy.getPrice()));
//            }
//        }
    }


    // Exchange observer interface
    @Override
    public void newSell(Order o) {

    }

    @Override
    public void newBuy(Order o) {

    }

    @Override
    public void orderCompleted(CompletedOrder o) {
        if(asks.contains(o.getBuyer())) {
            positionSize += 1;
            priceTotal += o.getBuyer().getPrice();
            asks.remove(o.getBuyer());
        }
    }

    @Override
    public void spoofingDetected(Order buyPeek, Order sellPeek) {

    }
}
