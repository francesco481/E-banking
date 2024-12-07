package org.poo.management.Accounts;

import org.poo.fileio.CommandInput;

public class SAccount extends Account implements AccountType {
    double interestRate;

    public SAccount(CommandInput command) {
        super(command);
        interestRate = command.getInterestRate();
    }

    @Override
    public void pay() {
        System.out.println("Pay SAccount");
    }
}
