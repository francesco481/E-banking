package org.poo.management.Cards;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

@Getter
@Setter
public class OneCard extends Card implements CardType {
    /**
     * Represents a specialized card with unique payment capabilities.
     * This class extends from `Card` and implements the `CardType` interface.
     * The `pay` method sets a unique card number for the `OneCard` using a utility function.
     */
    @Override
    public void pay() {
        this.setCardNumber(Utils.generateCardNumber());
    }
}
