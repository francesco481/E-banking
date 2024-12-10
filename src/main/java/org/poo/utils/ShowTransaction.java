package org.poo.utils;

import com.fasterxml.jackson.core.TreeCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.management.Transactions;

public class ShowTransaction {
    public static void extract(ObjectMapper mapper, Transactions transaction, ArrayNode transactionsArray) {
        ObjectNode transactionNode = mapper.createObjectNode();
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        if (!transaction.getAccounts().isEmpty())
        {
            transactionNode.put("currency", transaction.getCurrency());
        }
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
        if (transaction.getAmount() != -1 && transaction.getCurrency() != null  &&  transaction.getAccounts().isEmpty()) {
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

        if(!transaction.getAccounts().isEmpty()  &&  transaction.getError() != null) {
            ArrayNode involvedAccounts = mapper.createArrayNode();
            for (String account : transaction.getAccounts()) {
                involvedAccounts.add(account);
            }
            transactionNode.set("involvedAccounts", involvedAccounts);
            transactionNode.put("error", transaction.getError());
        }

        if(!transaction.getAccounts().isEmpty()  &&  transaction.getError() == null) {
            ArrayNode involvedAccounts = mapper.createArrayNode();
            for (String account : transaction.getAccounts()) {
                involvedAccounts.add(account);
            }
            transactionNode.set("involvedAccounts", involvedAccounts);
        }

        transactionsArray.add(transactionNode);
    }
}
