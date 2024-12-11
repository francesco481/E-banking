package org.poo.management.Accounts;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface AccountType {
    /**
     * Deducts the specified amount from the account's balance.
     * This method handles the transaction of subtracting funds from the account.
     * It checks if there are sufficient funds before performing the deduction.
     *
     * @param amount the amount to be deducted from the account balance.
     */
    void pay(double amount);

    /**
     * Prints the transactions for the account within a specified time range.
     * This method retrieves all transactions that occurred between the given
     * start and stop timestamps and returns them as a JSON object.
     *
     * @param start the start timestamp of the time range.
     * @param stop the end timestamp of the time range.
     * @param timestamp the timestamp at which the method is called.
     *                  Used for logging or tracking purposes.
     * @return a JSON object containing the transaction details within the specified time range.
     */
    ObjectNode printTransaction(int start, int stop, int timestamp);

    /**
     * Prints the spending details for the account within a specified time range.
     * This method retrieves all spending transactions (e.g., card payments)
     * that occurred between the given start and stop timestamps and returns
     * them as a JSON object.
     *
     * @param start the start timestamp of the time range.
     * @param stop the end timestamp of the time range.
     * @param timestamp the timestamp at which the method is called.
     *                  Used for logging or tracking purposes.
     * @return a JSON object containing the spending details within the specified time range.
     */
    ObjectNode printSpendings(int start, int stop, int timestamp);

    /**
     * Returns the current balance of the account.
     * This method retrieves the amount of funds available in the account.
     *
     * @return the current balance of the account.
     */
    double getBalance();
}
