package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Database;
import org.poo.management.Transactions;

public class PrintTransactions implements Order {
    Database database;
    ObjectMapper mapper;
    ArrayNode output;
    CommandInput command;

    public PrintTransactions(Database database, ObjectMapper mapper, ArrayNode output, CommandInput command) {
        this.database = database;
        this.mapper = mapper;
        this.output = output;
        this.command = command;
    }

    @Override
    public void execute(int timestamp) {
        for (UserInput user : database.getUsers()) {
            if (user.getEmail().equals(command.getEmail())) {
                ArrayNode transactionsArray = mapper.createArrayNode();

                for (Transactions transaction : user.getTransactions()) {
                    ObjectNode transactionNode = mapper.createObjectNode();
                    transactionNode.put("timestamp", transaction.getTimestamp());
                    transactionNode.put("description", transaction.getDescription());

                    if (transaction.getAmount() != -1) {
                        transactionNode.put("amount", transaction.getAmount());
                    }
                    if (transaction.getCommerciant() != null) {
                        transactionNode.put("commerciant", transaction.getCommerciant());
                    }

                    if (transaction.getSenderIBAN() != null) {
                        transactionNode.put("senderIBAN", transaction.getSenderIBAN());
                    }
                    if (transaction.getReceiverIBAN() != null) {
                        transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
                    }
                    if (transaction.getAmount() != -1 && transaction.getCurrency() != null) {
                        transactionNode.put("amount", transaction.getAmount() + " " + transaction.getCurrency());
                    }
                    if (transaction.getTransferType() != null) {
                        transactionNode.put("transferType", transaction.getTransferType());
                    }

                    if (transaction.getCard() != null) {
                        transactionNode.put("card", transaction.getCard());
                    }
                    if (transaction.getCardHolder() != null) {
                        transactionNode.put("cardHolder", transaction.getCardHolder());
                    }
                    if (transaction.getIBAN() != null) {
                        transactionNode.put("account", transaction.getIBAN());
                    }

                    transactionsArray.add(transactionNode);
                }

                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("command", "printTransactions");
                outputNode.set("output", transactionsArray);
                outputNode.put("timestamp", timestamp);

                output.add(outputNode);
                return;
            }
        }
    }

}
