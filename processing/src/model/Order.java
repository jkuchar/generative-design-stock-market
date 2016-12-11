package model;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of PA165 school project.
 */
public class Order {
    private final int price;

    private List<Runnable> completed = new ArrayList<>();

    public Order(int price) {
        this.price = price;
    }

    public void addCompleted(Runnable r) {
        this.completed.add(r);
    }

    void fireCompletedEvent() {
        completed.forEach((Runnable r) -> {
            try{
                r.run();
            } catch (Exception e) {
                e.printStackTrace();;
            }
        });
    }

    public int getPrice() {
        return price;
    }
}
