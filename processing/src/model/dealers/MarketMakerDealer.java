package model.dealers;

import model.CompletedOrder;
import model.Exchange;
import model.ExchangeObserver;
import model.Order;

import java.util.Iterator;
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

    @Override
    public void run() {
        do {

            NavigableSet<Order> buyQueue = exchange.getBuyQueue();
            NavigableSet<Order> sellQueue = exchange.getSellQueue();

            final Iterator<Order> buyIterator = buyQueue.descendingIterator();
            final Iterator<Order> sellIterator = sellQueue.iterator();

            while(buyIterator.hasNext()) {
                Order highestPriceToSell = buyIterator.next();

                while(sellIterator.hasNext()) {
                    Order lowestPriceToBuy = sellIterator.next();

                    if(highestPriceToSell.getPrice() <= lowestPriceToBuy.getPrice()) {
                        continue;
                    }

                    exchange.ask(lowestPriceToBuy.getPrice());
                    exchange.bid(highestPriceToSell.getPrice());

                    System.out.println("Spread: " + (highestPriceToSell.getPrice() - lowestPriceToBuy.getPrice()));
                }
            }


            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
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

    }

    @Override
    public void spoofingDetected(Order buyPeek, Order sellPeek) {

    }
}
