package org.poo.management.Accounts;

import org.poo.fileio.CommandInput;

public class CAccount extends Account implements AccountType {
    public CAccount(CommandInput command) {
        super(command);
    }
    @Override
    public void pay(){
        System.out.println("Paying account...");
    }
}
