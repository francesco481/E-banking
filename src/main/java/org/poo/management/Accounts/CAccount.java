package org.poo.management.Accounts;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.management.Database;

@Getter
@Setter
public class CAccount extends Account implements AccountType {
    public CAccount(CommandInput command) {
        super(command);
    }
    @Override
    public void pay(double amount) {
        this.setBalance(this.getBalance() - amount);
    }
}
