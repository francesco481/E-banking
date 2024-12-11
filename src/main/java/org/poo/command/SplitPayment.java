package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.utils.Pair;

import java.util.ArrayList;

public final class SplitPayment implements Order {
    private final Database database;
    private final CommandInput command;

    public SplitPayment(final Database database, final CommandInput command) {
        this.database = database;
        this.command = command;
    }

    /**
     * Executes the command to process a split payment across multiple accounts.
     * This method divides the specified amount into smaller amounts, checks the
     * availability of funds in each account, and performs the payment if sufficient funds exist.
     * If an account does not have enough funds, appropriate error transactions are created.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        ArrayList<UserInput> userPay = new ArrayList<>();
        ArrayList<AccountType> accountPay = new ArrayList<>();
        int ok = 1;
        String error = null;

        double amount = command.getAmount() / command.getAccounts().size();

        for (String iban : command.getAccounts()) {
            Pair find = database.findBigIBAN(iban);
            int first = (int) find.getFirst();
            int second = (int) find.getSecond();

            if (first != -1) {
                userPay.add(database.getUsers().get(first));
                AccountType curr = database.getAccounts().get(first).get(second);
                accountPay.add(curr);

                double currAmount = amount * Database.getRate(command.getCurrency(),
                        ((Account) curr).getCurrency());

                if (curr.getBalance() < currAmount) {
                    ok = 0;
                    error = ((Account) curr).getIban();
                }
            }
        }

        if (ok == 0) {
            for (UserInput user : userPay) {
                String outAmount = String.format("%.2f", command.getAmount());
                user.addTransaction(new Transactions("Split payment of "
                        + outAmount + " " + command.getCurrency(), timestamp,
                        command.getCurrency(), amount, command.getAccounts(),
                        "Account " + error + " has insufficient funds for a split payment."));
            }

            for (AccountType account : accountPay) {
                String outAmount = String.format("%.2f", command.getAmount());
                ((Account) account).addTransaction(new Transactions("Split payment of "
                        + outAmount + " " + command.getCurrency(), timestamp,
                        command.getCurrency(), amount, command.getAccounts(),
                        "Account " + error + " has insufficient funds for a split payment."));
            }

            return;
        }

        int st = 0;
        for (AccountType account : accountPay) {
            double currAmount = amount * Database.getRate(command.getCurrency(),
                                                ((Account) account).getCurrency());
            account.pay(currAmount);
            String outAmount = String.format("%.2f", command.getAmount());
            Transactions transactions = new Transactions("Split payment of " + outAmount + " "
                    + command.getCurrency(), timestamp, command.getCurrency(),
                    amount, command.getAccounts());

            userPay.get(st).addTransaction(transactions);
            ((Account) account).addTransaction(transactions);
            st++;
        }
    }
}
