package model.dealers;

import model.Exchange;
import model.dealers.Dealer;
import model.dealers.SimpleBuyerAction;

import java.util.Random;

/**
 * This file is part of PA165 school project.
 */
public class RandomDeltaDealer extends Thread implements Dealer {

    private final Exchange exchange;
    private final int delta;
    private SimpleBuyerAction action = null;

    private double sentiment;

    /**
     * @param sentiment 1 = 100% optimistic; 0 = 100% pessimistic
     */
    public RandomDeltaDealer(Exchange exchange, int delta, double sentiment) {
        this.exchange = exchange;
        this.delta = delta;
        this.sentiment = sentiment;
    }

    @Override
    public void run() {
        do{
            int delta = (int) Math.round(Math.random() * this.delta);
            if(Math.random() < 0.5) { // 50% chance
                delta *= -1;
            }

            int price = exchange.getLastDealPrice() + delta;


            // new action
            boolean optimist = Math.random() < sentiment;

            if((optimist && delta > 0) || (!optimist && delta < 0)) {
                action = SimpleBuyerAction.SELL;

            } else {

                action = SimpleBuyerAction.BUY;
            }

            action.doAction(exchange, price);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
    }
}
