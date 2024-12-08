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

    public ExchangeInput ExchangeInputRev(){
        ExchangeInput rev = new ExchangeInput();
        rev.from = to;
        rev.to = from;
        rev.rate = 1/rate;
        rev.timestamp = timestamp;

        return rev;
    }
}
