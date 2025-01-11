package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.UserInput;
import org.poo.management.accounts.Account;
import org.poo.management.accounts.AccountType;
import org.poo.management.cards.Card;
import org.poo.management.Database;
import org.poo.management.Transactions;

import java.util.ArrayList;

public final class PayOnline implements Order {
    private final Database database;
    private final CommandInput command;
    private final ObjectMapper mapper;
    private final ArrayNode output;

    public PayOnline(final Database database, final CommandInput command,
                     final ObjectMapper mapper, final ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    /**
     * Executes the command to process an online payment using a specified card.
     * The method performs the following actions:
     * - Validates the user's existence in the database.
     * - Checks if the specified card exists and its status (active or frozen).
     * - Processes the payment if the account has sufficient funds and the card is active.
     * - Records transactions for the user and the account, including special cases such as
     *   card replacement or insufficient funds.
     * - Generates an error response if the card is not found in the database.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        int i = database.findUser(command.getEmail());
        if (i == -1) {
            return;
        }

        if (command.getAmount() == 0) {
            return;
        }

        ArrayList<AccountType> curr = database.getAccounts().get(i);
        int ok = 1;
        for (AccountType account : curr) {
            for (Card card : ((Account) account).getCards()) {
                if (card.getCardNumber().equals(command.getCardNumber())) {
                    ok = 0;
                    if (card.getStatus().equals("frozen")) {
                        UserInput user = database.getUsers().get(i);
                        Transactions transactions  = new Transactions("The card is frozen",
                                                                                timestamp);
                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);
                        return;
                    }
                    double amount = command.getAmount() * Database.getRate(command.getCurrency(),
                                                            ((Account) account).getCurrency());
                    double ronAmount = command.getAmount() * Database.getRate(command.getCurrency(), "RON");

                    double comision = 1;

                    UserInput user = database.getUsers().get(i);
                    if (user.getPlan().equals("standard")) {
                        comision = 1.002;
                    }

                    if (user.getPlan().equals("silver")  &&  ronAmount >= 500) {
                        comision = 1.001;
                    }

                    if (account.getBalance() - ((Account) account).getMinimum() >= amount*comision
                            && card.getStatus().equals("active")) {

                        account.pay(amount*comision);
                        card.execPay();

                        String commerciant = command.getCommerciant();

                        for (CommerciantInput commerciantInput : database.getCommerciants()) {
                            if (commerciantInput.getCommerciant().equals(commerciant)) {

                                String type = commerciantInput.getType();
                                if (commerciantInput.getCashbackStrategy().equals("nrOfTransactions")) {
                                    int nr;
                                    if (!((Account) account).getNumber().containsKey(commerciant)) {
                                        nr = 1;
                                        ((Account) account).getNumber().put(commerciant, nr);
                                    } else {
                                        nr = ((Account) account).getNumber().get(commerciant) + 1;
                                        ((Account) account).getNumber().put(commerciant, nr);
                                    }

                                    if (nr > 2 && ((Account) account).getFood() == 0) {
                                        ((Account) account).setFood(1);
                                    }

                                    if (nr > 5 && ((Account) account).getClothes() == 0) {
                                        ((Account) account).setClothes(1);
                                    }

                                    if (nr > 10 && ((Account) account).getTech() == 0) {
                                        ((Account) account).setTech(1);
                                    }
                                }

                                double discount = 0;
                                if (type.equals("Food") && ((Account) account).getFood() == 1) {
                                    discount = 0.02;
                                    ((Account) account).setFood(2);
                                }

                                if (type.equals("Clothes") && ((Account) account).getClothes() == 1) {
                                    discount = 0.05;
                                    ((Account) account).setClothes(2);
                                }

                                if (type.equals("Tech")  && ((Account) account).getTech() == 1) {
                                    discount = 0.1;
                                    ((Account) account).setTech(2);
                                }

                                if (commerciantInput.getCashbackStrategy().equals("spendingThreshold")) {
                                    double cash = ((Account) account).getTotal() + ronAmount;
                                    ((Account) account).setTotal(cash);

                                    if (cash >= 500) {
                                        if (user.getPlan().equals("standard") || user.getPlan().equals("student")) {
                                            discount += 0.0025;
                                        } else if (user.getPlan().equals("silver")) {
                                            discount += 0.005;
                                        } else {
                                            discount += 0.007;
                                        }
                                    } else if (cash >= 300) {
                                        if (user.getPlan().equals("standard") || user.getPlan().equals("student")) {
                                            discount += 0.002;
                                        } else if (user.getPlan().equals("silver")) {
                                            discount += 0.004;
                                        } else {
                                            discount += 0.0055;
                                        }
                                    } else if (cash >= 100) {
                                        if (user.getPlan().equals("standard") || user.getPlan().equals("student")) {
                                            discount += 0.001;
                                        } else if (user.getPlan().equals("silver")) {
                                            discount += 0.003;
                                        } else {
                                            discount += 0.005;
                                        }
                                    }
                                }

                                ((Account) account).addFunds(amount * discount);
                            }
                        }

                        ok = 2;
                        Transactions transactions  = new Transactions("Card payment",
                                            timestamp, command.getCommerciant(), amount);
                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);

                        if (!card.getCardNumber().equals(command.getCardNumber())) {
                            Transactions transactions1 = new Transactions("The card has been "
                                    + "destroyed", timestamp, command.getCardNumber(),
                                                     user.getEmail(),
                                                    ((Account) account).getIban());

                            ((Account) account).addTransaction(transactions1);
                            user.addTransaction(transactions1);

                            Transactions transactions2 = new Transactions("New card created",
                                    timestamp, card.getCardNumber(), user.getEmail(),
                                    ((Account) account).getIban());

                            ((Account) account).addTransaction(transactions2);
                            ((Account) account).addUsed(command.getCardNumber());
                            user.addTransaction(transactions2);
                        }

                        if (user.getPlan().equals("silver")  &&  ronAmount >= 300) {
                            user.increaseGold();

                            if (user.getGold() >= 5) {
                                user.setPlan("gold");
                                String iban = ((Account) account).getIban();
                                user.addTransaction(new Transactions("Upgrade plan", command.getTimestamp(), iban, "gold"));
                                ((Account) account).addTransaction(new Transactions("Upgrade plan", command.getTimestamp(), iban, "gold"));
                            }
                        }

                        return;
                    }

                    if (account.getBalance() - ((Account) account).getMinimum() < amount*comision
                            && card.getStatus().equals("active")) {
                        Transactions transactions = new Transactions("Insufficient funds",
                                                                    timestamp);

                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);
                    }
                }
            }
        }

        if (ok == 1) {
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Card not found");

            ObjectNode responseNode = mapper.createObjectNode();
            responseNode.put("command", "payOnline");
            responseNode.set("output", outputNode);
            responseNode.put("timestamp", timestamp);

            output.add(responseNode);
        }
    }
}
