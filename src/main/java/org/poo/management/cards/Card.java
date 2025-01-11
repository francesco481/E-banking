package org.poo.management.cards;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

@Getter
@Setter
public abstract class Card {
    private String cardNumber;
    private String status;
    private PaymentStrategy paymentStrategy;

    public Card() {
        cardNumber = Utils.generateCardNumber();
        status = "active";
    }

    /**
     * Executes the payment operation using the assigned payment strategy.
     * <p>
     * This method delegates the payment process to the currently assigned
     * {@link PaymentStrategy} by calling its {@code pay} method. The current card
     * instance is passed to the strategy for processing the payment.
     * </p>
     *
     * @see PaymentStrategy#pay(Card)
     */
    public final void execPay() {
        paymentStrategy.pay(this);
    }
}
