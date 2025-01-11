package org.poo.utils;

import java.util.Random;

public final class Utils {
    private Utils() {
        // Checkstyle error free constructor
    }

    private static final int IBAN_SEED = 1;
    private static final int CARD_SEED = 2;
    private static final int DIGIT_BOUND = 10;
    private static final int DIGIT_GENERATION = 16;
    private static final int SILVER = 100;
    private static final int GOLD = 250;
    private static final String RO_STR = "RO";
    private static final String POO_STR = "POOB";

    private static Random ibanRandom = new Random(IBAN_SEED);
    private static Random cardRandom = new Random(CARD_SEED);

    /**
     * Utility method for generating an IBAN code.
     *
     * @return the IBAN as String
     */
    public static String generateIBAN() {
        StringBuilder sb = new StringBuilder(RO_STR);
        for (int i = 0; i < RO_STR.length(); i++) {
            sb.append(ibanRandom.nextInt(DIGIT_BOUND));
        }

        sb.append(POO_STR);
        for (int i = 0; i < DIGIT_GENERATION; i++) {
            sb.append(ibanRandom.nextInt(DIGIT_BOUND));
        }

        return sb.toString();
    }

    /**
     * Utility method for generating a card number.
     *
     * @return the card number as String
     */
    public static String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DIGIT_GENERATION; i++) {
            sb.append(cardRandom.nextInt(DIGIT_BOUND));
        }

        return sb.toString();
    }

    /**
     * Resets the seeds between runs.
     */
    public static void resetRandom() {
        ibanRandom = new Random(IBAN_SEED);
        cardRandom = new Random(CARD_SEED);
    }

    /**
     * Determines if a specific upgrade condition is met between two types.
     *
     * @param a the current type to be checked
     * @param b the target type to be compared
     * @return {@code true} if the upgrade condition is satisfied; {@code false} otherwise
     */
    public static boolean isUp(final String a, final String b) {
        if ((a.equals("standard") || a.equals("student"))
                && !b.equals("standard") && !b.equals("student")) {
            return true;
        }

        return a.equals("silver") && b.equals("gold");
    }

    /**
     * Calculates the amount required for an upgrade based on the current and target types.
     *
     * @param a the current type
     * @param b the target type
     * @return the upgrade amount as a {@code double}
     */
    public static double getAmount(final String a, final String b) {
        if (a.equals("standard") || a.equals("student")) {
            if (b.equals("silver")) {
                return SILVER;
            }

            return SILVER + GOLD;
        }

        return GOLD;
    }
}
