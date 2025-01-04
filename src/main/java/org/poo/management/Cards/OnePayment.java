package org.poo.management.Cards;

import org.poo.utils.Utils;

public final class OnePayment implements PaymentStrategy {
    @Override
    public void pay(final Card card) {
        card.setCardNumber(Utils.generateCardNumber());
    }
}
