package org.poo.management.Accounts;

import org.poo.fileio.CommandInput;

public final class AccountFactory {
    private AccountFactory() {

    }
    /**
     * Creates and returns an instance of the specified account type.
     * The method checks the type string and returns either a savings or a checking account
     * based on the provided command input.
     *
     * @param type the type of the account to be created ("savings" or "checking").
     * @param command the command input containing the necessary details for creating the account.
     * @return an instance of `SAccount` if the type is "savings", otherwise a `CAccount`.
     */
    public static AccountType getAccount(final String type, final CommandInput command) {
        if (type.equals("savings")) {
            return new SAccount(command);
        } else {
            return new CAccount(command);
        }
    }
}
