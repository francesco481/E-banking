package org.poo.management.Accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.management.Transactions;
import org.poo.utils.Pair;
import org.poo.utils.ShowTransaction;

import java.util.ArrayList;
import java.util.Comparator;

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
        ArrayNode transactionsArray = mapper.createArrayNode();

        ArrayList<Pair<String, Double>> commerciants = new ArrayList<>();

        for (Transactions transactions : super.getTransactions()) {
            if (transactions.getDescription().equals("Card payment")
                 && transactions.getTimestamp() >= start && transactions.getTimestamp() <= stop) {
                ShowTransaction.extract(mapper, transactions, transactionsArray);

                boolean updated = false;
                Pair<String, Double> newPair = new Pair<>(transactions.getCommerciant(),
                                                          transactions.getAmount());

                for (int i = 0; i < commerciants.size(); i++) {
                    Pair<String, Double> existingPair = commerciants.get(i);
                    if (existingPair.getFirst().equals(newPair.getFirst())) {
                        commerciants.set(i, new Pair<>(existingPair.getFirst(),
                                                existingPair.getSecond() + newPair.getSecond()));
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    commerciants.add(newPair);
                    commerciants.sort(Comparator.comparing(Pair::getFirst));
                }
            }
        }

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("command", "spendingsReport");

        ObjectNode reportDetails = mapper.createObjectNode();
        reportDetails.put("IBAN", super.getIban());
        reportDetails.put("balance", super.getBalance());
        reportDetails.put("currency", super.getCurrency());
        reportDetails.set("transactions", transactionsArray);

        ArrayNode commerciantsArray = mapper.createArrayNode();
        for (Pair<String, Double> curr : commerciants) {
            ObjectNode commerciantNode = mapper.createObjectNode();
            commerciantNode.put("commerciant", curr.getFirst());
            commerciantNode.put("total", curr.getSecond());
            commerciantsArray.add(commerciantNode);
        }
        reportDetails.set("commerciants", commerciantsArray);
        outputNode.set("output", reportDetails);
        outputNode.put("timestamp", timestamp);

        return outputNode;
    }
}
