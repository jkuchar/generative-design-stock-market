package model.dealers;

/**
 * This file is part of PA165 school project.
 */
public abstract class BaseDealer extends Thread implements Dealer {


    @Override
    public void run() {
        do{
            doThings();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted.");
                return;
            }

        } while (true);
    }

    protected abstract void doThings();


}
