package org.poo.management.Cards;

public interface PaymentStrategy {
    /**
     * Represents a card with specific payment capabilities.
     * Each implementation of this interface defines how a particular type of card
     * handles payment transactions.
     */
    void pay(Card card);
}
