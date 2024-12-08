package org.poo.management.Cards;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

@Getter
@Setter
public class Card {
    private String cardNumber;
    private String status;

    public Card() {
        cardNumber = Utils.generateCardNumber();
        status = "active";
    }
}
