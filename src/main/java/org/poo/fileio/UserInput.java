package org.poo.fileio;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.management.Transactions;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public final class UserInput {
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<Transactions> transactions = new ArrayList<>();

    /**
     * Adds a transaction to the list of transactions for this account.
     * This method appends the provided transaction object to the internal list,
     * allowing it to be tracked as part of the account's transaction history.
     *
     * @param transaction the transaction to be added to the account.
     */
    public void addTransaction(final Transactions transaction) {
        this.transactions.add(transaction);
    }
}
