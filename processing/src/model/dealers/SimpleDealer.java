package model.dealers;

import model.Exchange;
import model.Order;

/**
 * This file is part of PA165 school project.
 */
public class SimpleDealer extends Thread implements Dealer {

    private Exchange exchange;

    private int delta;
    private SimpleBuyerAction action;

    Order lastOrder = null;

    public SimpleDealer(Exchange exchange, int delta, SimpleBuyerAction action)
    {
        this.delta = delta;
        this.action = action;
        this.exchange = exchange;
    }

    @Override
    public void run() {
        do{
            if(lastOrder != null) {
                exchange.cancelOrder(lastOrder);
            }

            // todo: cancel orders after ten ticks or so
            lastOrder = action.doAction(
                exchange,
                delta
            );

//            System.out.println("placed order; current price: " + exchange.getLastDealPrice() +  "; new my price: " + o.getPrice());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted.");
                return;
            }

        } while (true);
    }
}
