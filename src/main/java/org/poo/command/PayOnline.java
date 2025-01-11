package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.UserInput;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.management.accounts.Account;
import org.poo.management.accounts.AccountType;
import org.poo.management.cards.Card;

import java.util.ArrayList;

public final class PayOnline implements Order {
    private final Database database;
    private final CommandInput command;
    private final ObjectMapper mapper;
    private final ArrayNode output;

    private static final double STANDARD_COMISION = 1.002;
    private static final double SILVER_COMISION = 1.001;
    private static final int SILVER_AMOUNT = 500;
    private static final int GOLD_AMOUNT = 300;
    private static final int PRAG = 5;
    private static final double DISCOUNT_STEP_ONE = 0.0025;
    private static final double DISCOUNT_STEP_TWO = 0.002;
    private static final double DISCOUNT_STEP_THREE = 0.001;
    private static final double SILVER_STEP_ONE = 0.005;
    private static final double SILVER_STEP_TWO = 0.004;
    private static final double SILVER_STEP_THREE = 0.003;
    private static final double GOLD_STEP_ONE = 0.007;
    private static final double GOLD_STEP_TWO = 0.0055;
    private static final double GOLD_STEP_THREE = 0.005;
    private static final int DISCOUNT_THRESHOLD_ONE = 100;
    private static final int DISCOUNT_THRESHOLD_TWO = 300;
    private static final int DISCOUNT_THRESHOLD_THREE = 500;
    private static final double FOOD_DISCOUNT = 0.02;
    private static final double CLOTHES_DISCOUNT = 0.05;
    private static final double TECH_DISCOUNT = 0.1;
    private static final int TECH_THRESHOLD = 10;
    private static final int CLOTHES_THRESHOLD = 5;


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
     * card replacement or insufficient funds.
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
                        Transactions transactions = new Transactions("The card is frozen",
                                timestamp);
                        user.addTransaction(transactions);
                        ((Account) account).addTransaction(transactions);
                        return;
                    }
                    double amount = command.getAmount() * Database.getRate(command.getCurrency(),
                            ((Account) account).getCurrency());
                    double ronAmount = command.getAmount()
                                        * Database.getRate(command.getCurrency(), "RON");

                    double comision = 1;

                    UserInput user = database.getUsers().get(i);
                    if (user.getPlan().equals("standard")) {
                        comision = STANDARD_COMISION;
                    }

                    if (user.getPlan().equals("silver") && ronAmount >= SILVER_AMOUNT) {
                        comision = SILVER_COMISION;
                    }

                    if (account.getBalance() - ((Account) account).getMinimum() >= amount * comision
                            && card.getStatus().equals("active")) {

                        account.pay(amount * comision);
                        card.execPay();

                        String commerciant = command.getCommerciant();

                        for (CommerciantInput commerciantInput : database.getCommerciants()) {
                            if (commerciantInput.getCommerciant().equals(commerciant)) {

                                String type = commerciantInput.getType();
                                String strategy = commerciantInput.getCashbackStrategy();
                                if (strategy.equals("nrOfTransactions")) {
                                    extracted((Account) account, commerciant);
                                }

                                double discount = 0;
                                if (type.equals("Food")
                                        && ((Account) account).getFood() == 1) {
                                    discount = FOOD_DISCOUNT;
                                    ((Account) account).setFood(2);
                                }

                                if (type.equals("Clothes")
                                        && ((Account) account).getClothes() == 1) {
                                    discount = CLOTHES_DISCOUNT;
                                    ((Account) account).setClothes(2);
                                }

                                if (type.equals("Tech") && ((Account) account).getTech() == 1) {
                                    discount = TECH_DISCOUNT;
                                    ((Account) account).setTech(2);
                                }

                                if (strategy.equals("spendingThreshold")) {
                                    double cash = ((Account) account).getTotal() + ronAmount;
                                    ((Account) account).setTotal(cash);

                                    discount = getDiscount(cash, user, discount);
                                }

                                ((Account) account).addFunds(amount * discount);
                            }
                        }

                        Transactions transactions = new Transactions("Card payment",
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

                        changePlan((Account) account, ronAmount, user, command);

                        return;
                    }

                    if (account.getBalance() - ((Account) account).getMinimum() < amount * comision
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

    private static void extracted(final Account account, final String commerciant) {
        int nr;
        if (!account.getCounts().containsKey(commerciant)) {
            nr = 1;
            account.getCounts().put(commerciant, nr);
        } else {
            nr = account.getCounts().get(commerciant) + 1;
            account.getCounts().put(commerciant, nr);
        }

        if (nr > 2 && account.getFood() == 0) {
            account.setFood(1);
        }

        if (nr > CLOTHES_THRESHOLD && account.getClothes() == 0) {
            account.setClothes(1);
        }

        if (nr > TECH_THRESHOLD && account.getTech() == 0) {
            account.setTech(1);
        }
    }

    private static double getDiscount(final double cash, final UserInput user,
                                      final double dis) {
        double discount = dis;
        if (cash >= DISCOUNT_THRESHOLD_THREE) {
            if (user.getPlan().equals("standard") || user.getPlan().equals("student")) {
                discount += DISCOUNT_STEP_ONE;
            } else if (user.getPlan().equals("silver")) {
                discount += SILVER_STEP_ONE;
            } else {
                discount += GOLD_STEP_ONE;
            }
        } else if (cash >= DISCOUNT_THRESHOLD_TWO) {
            if (user.getPlan().equals("standard")
                    || user.getPlan().equals("student")) {
                discount += DISCOUNT_STEP_TWO;
            } else if (user.getPlan().equals("silver")) {
                discount += SILVER_STEP_TWO;
            } else {
                discount += GOLD_STEP_TWO;
            }
        } else if (cash >= DISCOUNT_THRESHOLD_ONE) {
            if (user.getPlan().equals("standard") || user.getPlan().equals("student")) {
                discount += DISCOUNT_STEP_THREE;
            } else if (user.getPlan().equals("silver")) {
                discount += SILVER_STEP_THREE;
            } else {
                discount += GOLD_STEP_THREE;
            }
        }
        return discount;
    }

    static void changePlan(final Account account, final double ronAmount,
                           final UserInput user, final CommandInput command) {
        if (user.getPlan().equals("silver") && ronAmount >= GOLD_AMOUNT) {
            user.increaseGold();

            if (user.getGold() >= PRAG) {
                user.setPlan("gold");
                String iban = account.getIban();
                Transactions transactions = new Transactions("Upgrade plan",
                        command.getTimestamp(), iban, "gold");
                user.addTransaction(transactions);
                account.addTransaction(transactions);
            }
        }
    }
}
