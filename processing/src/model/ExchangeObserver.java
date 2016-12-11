package model;

public interface ExchangeObserver {

    void newSell(Order o);
    void newBuy(Order o);

    void orderCompleted(CompletedOrder o);

    void spoofingDetected(Order buyPeek, Order sellPeek);
}
