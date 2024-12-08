package org.poo.management.Cards;

public class CardFactory {
    public static CardType getCard(int type){
        if (type == 1){
            return new OneCard();
        }
        else {
            return new ClassicCard();
        }
    }
}
