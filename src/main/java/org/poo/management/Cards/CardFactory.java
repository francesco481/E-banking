package org.poo.management.Cards;

public final class CardFactory {
    private CardFactory() {

    }

    /**
     * Creates and returns an instance of the specified card type.
     * This method checks the type identifier and returns either a `OneCard` or
     * a `ClassicCard` based on the provided type.
     *
     * @param type the type identifier for the card to be created.
     *             `1` represents a `OneCard`, while any other value represents a `ClassicCard`.
     * @return an instance of `OneCard` if `type` is `1`, otherwise a `ClassicCard`.
     */
    public static CardType getCard(final int type) {
        if (type == 1) {
            return new OneCard();
        } else {
            return new ClassicCard();
        }
    }
}
