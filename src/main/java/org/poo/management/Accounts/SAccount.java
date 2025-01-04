package org.poo.management.Accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.management.Transactions;
import org.poo.utils.ShowTransaction;

@Getter
@Setter
public class SAccount extends Account implements AccountType {
    public SAccount(final CommandInput command) {
        super(command);
        super.setInterest(command.getInterestRate());
    }

    /**
     * Deducts the specified amount from the account's balance.
     * This method updates the account balance by subtracting the given amount.
     * It is used for handling payment transactions such as withdrawals or purchases.
     *
     * @param amount the amount to be deducted from the account balance.
     */
    @Override
    public void pay(final double amount) {
        this.setBalance(this.getBalance() - amount);
    }

    /**
     * Prints the transaction details for the account within a specified time range.
     * This method filters transactions that fall within the given start and stop timestamps
     * and returns them as a JSON object. It includes details such as transaction amount,
     * description, and timestamp.
     *
     * @param start the start timestamp of the time range.
     * @param stop the end timestamp of the time range.
     * @param timestamp the timestamp at which the method is called.
     *                  Used for logging or tracking purposes.
     * @return a JSON object containing the transaction details within the specified time range.
     */
    @Override
    public ObjectNode printTransaction(final int start, final int stop, final int timestamp) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode transactionsArray = mapper.createArrayNode();
        for (Transactions transactions : super.getTransactions()) {
            if (transactions.getTimestamp() >= start && transactions.getTimestamp() <= stop) {
                ShowTransaction.extract(mapper, transactions, transactionsArray);
            }
        }

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("command", "report");

        ObjectNode reportDetails = mapper.createObjectNode();
        reportDetails.put("IBAN", super.getIban());
        reportDetails.put("balance", super.getBalance());
        reportDetails.put("currency", super.getCurrency());
        reportDetails.set("transactions", transactionsArray);

        outputNode.set("output", reportDetails);
        outputNode.put("timestamp", timestamp);

        return outputNode;
    }

    /**
     * Prints the spending details for the account within a specified time range.
     * This method filters spending transactions (e.g., card payments) that occurred
     * between the given start and stop timestamps and returns them as a JSON object.
     * It also aggregates spending by commerciant.
     *
     * @param start the start timestamp of the time range.
     * @param stop the end timestamp of the time range.
     * @param timestamp the timestamp at which the method is called.
     *                  Used for logging or tracking purposes.
     * @return a JSON object containing the spending details within the specified time range.
     */
    @Override
    public ObjectNode printSpendings(final int start, final int stop, final int timestamp) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();

        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("error", "This kind of report is not supported for a saving account");

        outputNode.put("command", "spendingsReport");
        outputNode.set("output", errorNode);
        outputNode.put("timestamp", timestamp);

        return outputNode;
    }
}
