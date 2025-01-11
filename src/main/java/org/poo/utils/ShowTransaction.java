package org.poo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.management.Transactions;

public final class ShowTransaction {
    private ShowTransaction() {

    }
    /**
     * Extracts transaction details and adds them to an array node.
     * This method converts a transaction object into a structured JSON node, including
     * various transaction details such as timestamps, descriptions, amounts, and involved accounts.
     * It also handles errors if any are present.
     *
     * @param mapper the `ObjectMapper` used to create JSON nodes.
     * @param transaction the `Transactions` object containing transaction details.
     * @param transactionsArray the `ArrayNode` to which the transaction details are added.
     */
    public static void extract(final ObjectMapper mapper, final Transactions transaction,
                               final ArrayNode transactionsArray) {
        ObjectNode transactionNode = mapper.createObjectNode();
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionNode.put("description", transaction.getDescription());

        if (transaction.getSplit() != null) {
            transactionNode.put("splitPaymentType", transaction.getSplit());
        }
        if (!transaction.getAccounts().isEmpty()  ||  transaction.getDescription().equals("Interest rate income")) {
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
        if (transaction.getClassicIban() != null) {
            transactionNode.put("classicAccountIBAN", transaction.getClassicIban());
        }
        if (transaction.getSavingsIban() != null) {
            transactionNode.put("savingsAccountIBAN", transaction.getSavingsIban());
        }
        if (transaction.getReceiverIBAN() != null) {
            transactionNode.put("receiverIBAN", transaction.getReceiverIBAN());
        }
        if (!transaction.getDescription().equals("Interest rate income")  && transaction.getAmount() != -1 && transaction.getCurrency() != null
                &&  transaction.getAccounts().isEmpty()) {
            transactionNode.put("amount", transaction.getAmount() + " "
                    + transaction.getCurrency());
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


        if (transaction.getNewPlan() != null) {
            transactionNode.put("accountIBAN", transaction.getIban());
            transactionNode.put("newPlanType", transaction.getNewPlan());
        }

        if (transaction.getIban() != null  &&  transaction.getNewPlan() == null) {
            transactionNode.put("account", transaction.getIban());
        }

        if (!transaction.getAccounts().isEmpty() && !transaction.getAmounts().isEmpty()  &&  transaction.getError() != null) {
            ArrayNode involvedAccounts = mapper.createArrayNode();
            for (String account : transaction.getAccounts()) {
                involvedAccounts.add(account);
            }
            transactionNode.set("involvedAccounts", involvedAccounts);

            ArrayNode amountForUsers = mapper.createArrayNode();
            for (Double amount : transaction.getAmounts()) {
                amountForUsers.add(amount);
            }
            transactionNode.set("amountForUsers", amountForUsers);

            transactionNode.put("error", transaction.getError());
        }

        if (!transaction.getAccounts().isEmpty()  &&  !transaction.getAmounts().isEmpty()  &&  transaction.getError() == null) {
            ArrayNode involvedAccounts = mapper.createArrayNode();
            for (String account : transaction.getAccounts()) {
                involvedAccounts.add(account);
            }
            transactionNode.set("involvedAccounts", involvedAccounts);


            ArrayNode amountForUsers = mapper.createArrayNode();
            for (Double amount : transaction.getAmounts()) {
                amountForUsers.add(amount);
            }
            transactionNode.set("amountForUsers", amountForUsers);
        }

        if (!transaction.getAccounts().isEmpty() && transaction.getAmounts().isEmpty()  &&  transaction.getError() != null) {
            ArrayNode involvedAccounts = mapper.createArrayNode();
            for (String account : transaction.getAccounts()) {
                involvedAccounts.add(account);
            }
            transactionNode.set("involvedAccounts", involvedAccounts);

            transactionNode.put("error", transaction.getError());
        }

        if (!transaction.getAccounts().isEmpty()  &&  transaction.getAmounts().isEmpty()  &&  transaction.getError() == null) {
            ArrayNode involvedAccounts = mapper.createArrayNode();
            for (String account : transaction.getAccounts()) {
                involvedAccounts.add(account);
            }
            transactionNode.set("involvedAccounts", involvedAccounts);
        }

        transactionsArray.add(transactionNode);
    }
}
