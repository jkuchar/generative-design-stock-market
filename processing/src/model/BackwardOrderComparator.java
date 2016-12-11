package model;

import java.util.Comparator;

public class BackwardOrderComparator implements Comparator<Order>
{
    public int compare( Order x, Order y )
    {
        return y.getPrice() - x.getPrice();
    }
}
