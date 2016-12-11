package model;

import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.*;

/**
 * Very simple model of stock exchange
 * - is fully synchronous
 * - price is integer
 * - does not support spoofing
 * - you have to call doDeals(); there is no automatic async-processing
 */
public class Exchange {

    private final SortedSet<Order> buy;
    private final SortedSet<Order> sell;

    private final List<CompletedOrder> completedOrders;

    private final List<ExchangeObserver> observers;

    private int lastDealPrice; // cached value

    public Exchange() {
        int openingPrice = 1000;
        this.lastDealPrice = openingPrice;
        this.buy = new TreeSet<>(new BackwardOrderComparator());
        this.sell = new TreeSet<>(new BackwardOrderComparator());
        this.completedOrders = new LinkedList<>();
        observers = new LinkedList<>();
    }

    public synchronized Order buy(int price) {
        Order order = new Order(price);
        buy.add(order);

        // fire observers
        observers.forEach( (ExchangeObserver observer) -> observer.newBuy(order) );

        return order;
    }

    public synchronized Order sell(int price) {
        Order order = new Order(price);
        sell.add(order);

        // fire observers
        observers.forEach( (ExchangeObserver observer) -> observer.newSell(order) );

        return order;
    }

    // in real world this will happen automatically after buy/sell
    // This is really ineffective way how to do things.
    public synchronized void doDeals() {
        if(buy.isEmpty() || sell.isEmpty()) return;

        // doesn't matter which queue we will iterate over
        Iterator<Order> buyIterator = buy.iterator();
        Iterator<Order> sellIterator = sell.iterator();

        Order sellOrder = null;
        Order buyOrder = null;

        //System.out.println("Starting doDeals()");
        while(sellIterator.hasNext()) {
            //if(sellOrder == null) {
                sellOrder = sellIterator.next();
            //}

            // now go from highest value to the lowest
            // parallely iterate over buy and sell queue
            // this has only O(n)

            while(buyIterator.hasNext()) {
                if(buyOrder == null) {
                    buyOrder = buyIterator.next();
                }

                //System.out.println("sell:" + sellOrder.getPrice() + " buy:" + buyOrder.getPrice());
                if(sellOrder.getPrice() < buyOrder.getPrice()) {
                    buyOrder = null; // got to the next buy request
                    continue;
                }
                if (buyOrder.getPrice() == sellOrder.getPrice()) {
                    sellIterator.remove();
                    buyIterator.remove();

                    CompletedOrder co = completeOrder(buyOrder, sellOrder);
                    observers.forEach( (ExchangeObserver observer) -> observer.orderCompleted(co) );

                    buyOrder = null; // got to the next buy request
                    continue;
                }

                // intentionally didn't deleted buyOrder
                // This will go out of buy-loop into sell-loop without moving to next buy request
                break;

            }

        };
    }

    private CompletedOrder completeOrder(Order seller, Order buyer) {
        CompletedOrder co = new CompletedOrder(seller, buyer);
        completedOrders.add(co);
        this.lastDealPrice = co.getBuyer().getPrice();
        return co;
    }

    public void addObserver(ExchangeObserver exchangeObserver) {
        observers.add(exchangeObserver);
    }


    public synchronized SortedSet<Order> getBuyQueue() {
        return new TreeSet<>(this.buy);
    }

    public synchronized SortedSet<Order> getSellQueue() {
        return new TreeSet<>(this.sell);
    }

    public synchronized List<CompletedOrder> getCompletedOrders() {
        return new ArrayList<>(completedOrders);
    }

    public synchronized int getLastDealPrice() {
        return lastDealPrice;
    }
}
