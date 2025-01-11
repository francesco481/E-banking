package org.poo.fileio;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.management.Transactions;

import java.util.ArrayList;
import java.util.HashMap;

@Data
@NoArgsConstructor
public final class UserInput {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;
    private String plan;
    private int gold = 0;
    private ArrayList<Transactions> transactions = new ArrayList<>();
    private HashMap<String, Integer> number = new HashMap<>();

    /**
     * Adds a transaction to the list of transactions for this account.
     * This method appends the provided transaction object to the internal list,
     * allowing it to be tracked as part of the account's transaction history.
     *
     * @param transaction the transaction to be added to the account.
     */
    public void addTransaction(final Transactions transaction) {
        if (this.transactions.isEmpty() ||  transaction.getTimestamp() >= this.transactions.getLast().getTimestamp()) {
            this.transactions.addLast(transaction);
        } else {
            int position = 0;
            for (int i = 0; i < this.transactions.size(); i++) {
                if (this.transactions.get(i).getTimestamp() > transaction.getTimestamp()) {
                    position = i;
                    break;
                }
            }
            this.transactions.add(position, transaction);
        }
    }

    public void increaseGold() {
        this.gold += 1;
    }
}
