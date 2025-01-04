package org.poo.management;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Alias {
    private final String email;
    private final String name;
    private final String iban;

    public Alias(final String email, final String name, final String iban) {
        this.email = email;
        this.name = name;
        this.iban = iban;
    }
}
