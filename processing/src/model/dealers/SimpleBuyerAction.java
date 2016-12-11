package model.dealers;

import model.Exchange;
import model.Order;

/**
 * This file is part of PA165 school project.
 */
public enum SimpleBuyerAction {
    SELL {
        @Override
        public Order doAction(Exchange exchange, int price) {
            return exchange.sell(price);
        }
    },

    BUY {
        @Override
        public Order doAction(Exchange exchange, int price) {
            return exchange.buy(price);
        }
    };

    public abstract Order doAction(Exchange exchange, int price);
}
