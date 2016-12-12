package model.dealers;

import model.Exchange;
import model.Order;

/**
 * This file is part of PA165 school project.
 */
public class WantToSellForAmount extends BaseDealer {

    private Exchange exchange;

    private int minPrice;
    private int amountToSell;

    private Order lastOrder = null;

    public WantToSellForAmount(Exchange exchange) {
        this(exchange, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public WantToSellForAmount(Exchange exchange, int minPrice, int amountToSell)
    {
        this.exchange = exchange;
        this.minPrice = minPrice;
        this.amountToSell = amountToSell;
    }


    protected void doThings() {
        if(lastOrder != null) {
            exchange.cancelOrder(lastOrder);
            lastOrder = null;
        }
        if(amountToSell < 0) {
            throw new RuntimeException("Run out of money.");
        }

        int bidPrice = exchange.getBidPrice();
        if(bidPrice < minPrice) {
            return;
        }

        lastOrder = exchange.bid(bidPrice);
        lastOrder.addCompleted(() -> {
            this.amountToSell -= lastOrder.getPrice();
        });
    }
}
