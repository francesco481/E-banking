package org.poo.management.Cards;

public class OneCard extends Card implements CardType {
    @Override
    public void pay()
    {
        System.out.println("OneCard.pay()");
    }
}
