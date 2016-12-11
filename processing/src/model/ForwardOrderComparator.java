package model;

import java.util.Comparator;

public class ForwardOrderComparator implements Comparator<Order>
{
    public int compare( Order x, Order y )
    {
        return x.getPrice() - y.getPrice();
    }
}
