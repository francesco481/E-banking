package org.poo.fileio;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class ExchangeInput {
    private String from;
    private String to;
    private double rate;
    private int timestamp;

    /**
     * Creates a reversed `ExchangeInput` object based on the current one.
     * The reversed object swaps the 'from' and 'to' currencies, and calculates
     * the new exchange rate as the reciprocal of the original rate. The timestamp
     * remains unchanged.
     *
     * @return a new `ExchangeInput` object with reversed currencies and the reciprocal rate.
     */
    public ExchangeInput exchangeRev() {
        ExchangeInput rev = new ExchangeInput();
        rev.from = to;
        rev.to = from;
        rev.rate = 1 / rate;
        rev.timestamp = timestamp;

        return rev;
    }
}
