package model.dealers;

import model.Exchange;
import model.Order;

/**
 * This file is part of PA165 school project.
 */
public enum SimpleBuyerAction {
    SELL {
        @Override
        public Order doAction(Exchange exchange, int delta) {
            return exchange.bid(exchange.getBidPrice() + delta);
        }
    },

    BUY {
        @Override
        public Order doAction(Exchange exchange, int delta) {
            return exchange.ask(exchange.getAskPrice() + delta);
        }
    };

    public abstract Order doAction(Exchange exchange, int price);
}
