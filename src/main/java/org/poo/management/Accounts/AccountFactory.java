package org.poo.management.Accounts;

import org.poo.fileio.CommandInput;

public class AccountFactory {
    public static Account getAccount(String type, CommandInput command) {
        if (type.equals("savings")) {
            return new SAccount(command);
        }
        else {
            return new CAccount(command);
        }
    }
}
