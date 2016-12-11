package model;

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
        this.sell = new TreeSet<>(new ForwardOrderComparator());
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

        Order buyPeek = buy.last(); // highest price
        Order sellPeek = sell.last(); // lowest price
        if(buyPeek == null || sellPeek == null) return;

        if(sellPeek.getPrice() > buyPeek.getPrice()) {
            //System.out.println("Spoofing detected; removed from exchange. " + sellPeek.getPrice());

            //throw new RuntimeException("Spoofing detected.");
            // todo: add to observer
            sell.remove(sellPeek);

            observers.forEach( (ExchangeObserver observer) -> observer.spoofingDetected(buyPeek, sellPeek) );
        }

        if(buyPeek.getPrice() == sellPeek.getPrice()) {
            // cool! Do the transaction

            CompletedOrder co = completeOrder(buyPeek, sellPeek);

            observers.forEach( (ExchangeObserver observer) -> observer.orderCompleted(co) );
        }

        // there is spread bigger then zero
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


    public synchronized Collection<Order> getBuyQueue() {
        return Collections.unmodifiableCollection(this.buy);
    }

    public synchronized Collection<Order> getSellQueue() {
        return Collections.unmodifiableCollection(this.sell);
    }

    public synchronized List<CompletedOrder> getCompletedOrders() {
        return Collections.unmodifiableList(completedOrders);
    }

    public int getLastDealPrice() {
        return lastDealPrice;
    }
}
