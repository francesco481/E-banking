package org.poo.management.Cards;

public class ClassicCard extends Card implements CardType {
    @Override
    public void pay()
    {
        System.out.println("ClassicCard.pay()");
    }
}
