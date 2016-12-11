package model.dealers;

import model.Exchange;
import model.Order;

/**
 * This file is part of PA165 school project.
 */
public class WantToBuyForAmount extends BaseDealer {

    private Exchange exchange;

    private int maxPrice;
    private int amountToSpend;

    private Order lastOrder = null;

    public WantToBuyForAmount(Exchange exchange) {
        this(exchange, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public WantToBuyForAmount(Exchange exchange, int maxPrice, int amountToSpend)
    {
        this.exchange = exchange;
        this.maxPrice = maxPrice;
        this.amountToSpend = amountToSpend;
    }

    protected void doThings() {
        if(lastOrder != null) {
            exchange.cancelOrder(lastOrder);
            lastOrder = null;
        }
        if(amountToSpend < 0) {
            throw new RuntimeException("Run out of money.");
        }

        int askPrice = exchange.getAskPrice();
        if(askPrice > maxPrice) {
            return;
        }
        lastOrder = exchange.ask(askPrice);
        lastOrder.addCompleted(() -> {
            this.amountToSpend -= lastOrder.getPrice();
        });
    }
}
