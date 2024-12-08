package org.poo.management.Accounts;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.management.Database;

@Getter
@Setter
public class SAccount extends Account implements AccountType {
    double interestRate;

    public SAccount(CommandInput command) {
        super(command);
        interestRate = command.getInterestRate();
    }

    @Override
    public void pay(double amount) {
        this.setBalance(this.getBalance() - amount);
    }
}
