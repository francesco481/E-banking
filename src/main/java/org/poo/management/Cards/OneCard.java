package org.poo.management.Cards;

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
