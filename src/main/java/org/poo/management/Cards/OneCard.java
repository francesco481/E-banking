package org.poo.management.Cards;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

@Getter
@Setter
public class OneCard extends Card implements CardType {
    @Override
    public void pay()
    {
        this.setCardNumber(Utils.generateCardNumber());
    }
}
