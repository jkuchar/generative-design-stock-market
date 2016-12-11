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
    private SimpleBuyerAction action;

    public RandomDeltaDealer(Exchange exchange, int delta) {
        this.exchange = exchange;
        this.delta = delta;
        this.action = SimpleBuyerAction.BUY;
    }

    @Override
    public void run() {
        do{
            int price = (int) Math.round(Math.random() * this.delta);
            if(Math.random() < 0.5) { // 50% chance
                price *= -1;
            }

            price += exchange.getLastDealPrice();

            action.doAction(exchange, price);

            // new action
            if(Math.random() < 0.5) { // 50% chance
                action = SimpleBuyerAction.BUY;
            } else {
                action = SimpleBuyerAction.SELL;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
    }
}
