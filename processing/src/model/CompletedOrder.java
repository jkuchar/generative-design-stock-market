package model;

/**
 * This file is part of PA165 school project.
 */
public class CompletedOrder {

    private Order seller;
    private Order buyer;

    public CompletedOrder(Order seller, Order buyer) {
        this.seller = seller;
        this.buyer = buyer;
    }

    public Order getSeller() {
        return seller;
    }

    public Order getBuyer() {
        return buyer;
    }
}
