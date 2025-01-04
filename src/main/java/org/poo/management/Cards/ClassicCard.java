package org.poo.management.Cards;

public final class ClassicCard extends Card {
    public ClassicCard() {
        super();
        this.setPaymentStrategy(new ClassicPayment());
    }
}
