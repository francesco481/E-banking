package org.poo.management.Accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.utils.Pair;
import org.poo.utils.ShowTransaction;

import java.util.ArrayList;
import java.util.Comparator;

@Getter
@Setter
public class SAccount extends Account implements AccountType {
    public SAccount(CommandInput command) {
        super(command);
        super.setInterest(command.getInterestRate());
    }

    @Override
    public void pay(double amount) {
        this.setBalance(this.getBalance() - amount);
    }

    @Override
    public ObjectNode printTransaction(int start, int stop, int timestamp) {
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
        reportDetails.put("IBAN", super.getIBAN());
        reportDetails.put("balance", super.getBalance());
        reportDetails.put("currency", super.getCurrency());
        reportDetails.set("transactions", transactionsArray);

        outputNode.set("output", reportDetails);
        outputNode.put("timestamp", timestamp);

        return outputNode;
    }

    @Override
    public ObjectNode printSpendings(int start, int stop, int timestamp) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode transactionsArray = mapper.createArrayNode();

        ArrayList<Pair<String, Double>> commerciants = new ArrayList<>();

        for (Transactions transactions : super.getTransactions()) {
            if (transactions.getDescription().equals("Card payment")  &&
                transactions.getTimestamp() >= start && transactions.getTimestamp() <= stop) {
                ShowTransaction.extract(mapper, transactions, transactionsArray);

                boolean updated = false;
                Pair<String, Double> newPair = new Pair<>(transactions.getCommerciant(), transactions.getAmount());

                for (int i = 0; i < commerciants.size(); i++) {
                    Pair<String, Double> existingPair = commerciants.get(i);
                    if (existingPair.getFirst().equals(newPair.getFirst())) {
                        commerciants.set(i, new Pair<>(existingPair.getFirst(), existingPair.getSecond() + newPair.getSecond()));
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
        reportDetails.put("IBAN", super.getIBAN());
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
