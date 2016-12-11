package model.dealers;

import model.Exchange;
import model.Order;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This file is part of PA165 school project.
 */
public class RandomDeltaDealer extends Thread implements Dealer {

    private final Exchange exchange;
    private final int delta;
    private SimpleBuyerAction action = null;

    private List<Order> orders;

    private double sentiment;

    private int millisecondsSleep;

    /**
     * @param sentiment 1 = 100% optimistic; 0 = 100% pessimistic
     */
    public RandomDeltaDealer(Exchange exchange, int delta, double sentiment, int millisecondsSleep) {
        this.exchange = exchange;
        this.delta = delta;
        this.sentiment = sentiment;
        this.millisecondsSleep = millisecondsSleep;
        orders = new ArrayList<>();
    }

    @Override
    public void run() {
        do{
            int delta = (int) Math.round(Math.random() * this.delta);
            if(Math.random() < 0.5) { // 50% chance
                delta *= -1;
            }

            // new action
            boolean optimist = Math.random() < sentiment;

            if((optimist && delta > 0) || (!optimist && delta < 0)) {
                action = SimpleBuyerAction.SELL;
            } else {
                action = SimpleBuyerAction.BUY;
            }

            orders.add(action.doAction(exchange, delta));

            try {
                Thread.sleep(millisecondsSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.cleanup();
        } while (true);
    }

    private void cleanup() {
        int size = this.orders.size();
        if(size > 10) {

            int toRemove = size - 5;
            final Iterator<Order> iterator = orders.iterator();
            while(iterator.hasNext()) {
                Order o = iterator.next();
                iterator.remove();

                exchange.cancelOrder(o);

                toRemove--;
                if(toRemove == 0) break;
            }

        }
    }
}
