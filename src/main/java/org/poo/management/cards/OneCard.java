package org.poo.management.cards;
import org.poo.management.cards.OnePayment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OneCard extends Card {
    public OneCard() {
        super();
        super.setPaymentStrategy(new OnePayment());
    }
}
