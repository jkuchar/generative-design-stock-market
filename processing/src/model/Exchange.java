package model;

import java.util.*;

/**
 * Very simple model of stock exchange
 * - is fully synchronous
 * - price is integer
 * - does not support spoofing
 * - you have to call doDeals(); there is no automatic async-processing
 */
public class Exchange extends Thread {

    private final SortedSet<Order> ask;
    private final SortedSet<Order> bid;

    private final List<CompletedOrder> completedOrders;

    private final List<ExchangeObserver> observers;

    private int lastDealPrice; // cached value

    public Exchange() {
        int openingPrice = 1000;
        this.lastDealPrice = openingPrice;
        this.ask = new TreeSet<>(new BackwardOrderComparator());
        this.bid = new TreeSet<>(new BackwardOrderComparator());
        this.completedOrders = new LinkedList<>();
        observers = new LinkedList<>();
    }

    /**
     * Try to buy shares
     */
    public synchronized Order ask(int price) {
        Order order = new Order(price);
        ask.add(order);

        // fire observers
        observers.forEach( (ExchangeObserver observer) -> observer.newBuy(order) );

        return order;
    }

    /**
     * Try to sell shares
     */
    public synchronized Order bid(int price) {
        Order order = new Order(price);
        bid.add(order);

        // fire observers
        observers.forEach( (ExchangeObserver observer) -> observer.newSell(order) );

        return order;
    }

    public synchronized void cancelOrder(Order o) {
        bid.remove(o);
        ask.remove(o);
    }

    // in real world this will happen automatically after ask/bid
    // This is really ineffective way how to do things.
    private synchronized void doDeals() {
        if(ask.isEmpty() || bid.isEmpty()) return;

        // doesn't matter which queue we will iterate over
        Iterator<Order> buyIterator = ask.iterator();
        Iterator<Order> sellIterator = bid.iterator();

        Order sellOrder = null;
        Order buyOrder = null;

        //System.out.println("Starting doDeals()");
        while(sellIterator.hasNext()) {
            //if(sellOrder == null) {
                sellOrder = sellIterator.next();
            //}

            // now go from highest value to the lowest
            // parallel iteration over ask and bid queue
            // this has only O(n)

            while(buyIterator.hasNext()) {
                if(buyOrder == null) {
                    buyOrder = buyIterator.next();
                }

                //System.out.println("bid:" + sellOrder.getPrice() + " ask:" + buyOrder.getPrice());
                if(sellOrder.getPrice() < buyOrder.getPrice()) {
                    buyOrder = null; // got to the next ask request
                    continue;
                }
                if (buyOrder.getPrice() == sellOrder.getPrice()) {
                    sellIterator.remove();
                    buyIterator.remove();

                    CompletedOrder co = completeOrder(buyOrder, sellOrder);
                    observers.forEach( (ExchangeObserver observer) -> observer.orderCompleted(co) );

                    buyOrder = null; // got to the next ask request
                    continue;
                }

                // intentionally didn't deleted buyOrder
                // This will go out of ask-loop into bid-loop without moving to next ask request
                break;

            }

        };
    }

    @Override
    public void run() {
        do {
            this.doDeals();

            try {
                Thread.sleep((int) (1000/60));
            } catch (InterruptedException e) {
                System.out.println("Exchange matched failed.");
            }
        } while (true);
    }

    private CompletedOrder completeOrder(Order seller, Order buyer) {
        seller.fireCompletedEvent();
        buyer.fireCompletedEvent();
        CompletedOrder co = new CompletedOrder(seller, buyer);
        completedOrders.add(co);
        this.lastDealPrice = co.getBuyer().getPrice();
        return co;
    }

    public void addObserver(ExchangeObserver exchangeObserver) {
        observers.add(exchangeObserver);
    }


    public synchronized NavigableSet<Order> getBuyQueue() {
        return new TreeSet<>(this.ask);
    }

    public synchronized NavigableSet<Order> getSellQueue() {
        return new TreeSet<>(this.bid);
    }

    public synchronized List<CompletedOrder> getCompletedOrders() {
        return new ArrayList<>(completedOrders);
    }

    public synchronized int getLastDealPrice() {
        return lastDealPrice;
    }

    public synchronized int lastPriceAveraged(int historyLength) {
        if(completedOrders.isEmpty()) return lastDealPrice;

        int size = completedOrders.size();
        int startIndex = size - historyLength;
        int endIndex = size;

        List<CompletedOrder> list = completedOrders.subList(startIndex, endIndex);
        int total = 0;
        int count = 0;
        for(CompletedOrder o : list) {
            total += o.getBuyer().getPrice();
            count++;
        }
        return total / count;
    }

    /**
     * Price for seller
     */
    public synchronized Integer getBidPrice() {
        try{
            return ask.first().getPrice();
        } catch (NoSuchElementException e) {
            return getLastDealPrice();
        }
    }

    /**
     * Price for buyer
     */
    public synchronized Integer getAskPrice() {
        try{
            return bid.last().getPrice();
        } catch (NoSuchElementException e) {
            return getLastDealPrice();
        }
    }

    public synchronized int getSpread() {
        return getBidPrice() - getAskPrice();
    }
}
