package org.poo.management.cards;

public final class ClassicCard extends Card {
    public ClassicCard() {
        super();
        this.setPaymentStrategy(new ClassicPayment());
    }
}
