package org.poo.command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.accounts.Account;
import org.poo.management.accounts.AccountType;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public final class SplitPayment implements Order {
    private final Database database;
    private final CommandInput command;
    private final ArrayList<CommandInput> commands;
    private final int st;
    private final ArrayNode output;

    public SplitPayment(final Database database, final CommandInput command,
                        final ArrayList<CommandInput> commands, final int st,
                        final ArrayNode output) {
        this.database = database;
        this.command = command;
        this.commands = commands;
        this.st = st;
        this.output = output;
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
        List<Double> amounts;

        for (String iban : command.getAccounts()) {
            Pair<Integer, Integer> find = database.findBigIBAN(iban);
            int first = find.getFirst();
            int second = find.getSecond();

            userPay.add(database.getUsers().get(first));
            AccountType curr = database.getAccounts().get(first).get(second);
            accountPay.add(curr);
        }

        if (command.getVerify() == -1) {
            for (UserInput user : userPay) {
                for (int idx = st ; idx < commands.size() ; idx++) {
                    if (commands.get(idx).getCommand().equals("acceptSplitPayment")  &&  commands.get(idx).getEmail().equals(user.getEmail())) {
                        if(user != userPay.getLast()) {
                            commands.remove(idx);
                        } else {
                            command.setVerify(1);
                            commands.set(idx, command);
                            return;
                        }
                        break;
                    } else if (commands.get(idx).getCommand().equals("rejectSplitPayment")  &&  commands.get(idx).getEmail().equals(user.getEmail())){
                        command.setVerify(0);
                        commands.set(idx, command);
                        return;
                    }
                }
            }
            return;
        }

        if (command.getVerify() == 0) {
            if (command.getSplitPaymentType().equals("equal")) {

                double amount = command.getAmount() / command.getAccounts().size();

                for (int idx = 0 ; idx < userPay.size() ; idx++) {
                    String outAmount = String.format("%.2f", command.getAmount());
                    userPay.get(idx).addTransaction(new Transactions("Split payment of "
                            + outAmount + " " + command.getCurrency(), timestamp,
                            command.getCurrency(), amount, command.getAccounts(),
                            "One user rejected the payment.","equal"));

                    ((Account) accountPay.get(idx)).addTransaction(new Transactions("Split payment of "
                            + outAmount + " " + command.getCurrency(), timestamp,
                            command.getCurrency(), amount, command.getAccounts(),
                            "One user rejected the payment.","equal"));
                }

            } else {
                for (int idx = 0; idx < userPay.size(); idx++) {
                    String outAmount = String.format("%.2f", command.getAmount());
                    userPay.get(idx).addTransaction(new Transactions("Split payment of "
                            + outAmount + " " + command.getCurrency(), timestamp,
                            command.getCurrency(), -1, command.getAccounts(),
                            "One user rejected the payment.", command.getAmountForUsers(), "custom"));

                    ((Account) accountPay.get(idx)).addTransaction(new Transactions("Split payment of "
                            + outAmount + " " + command.getCurrency(), timestamp,
                            command.getCurrency(), -1, command.getAccounts(),
                            "One user rejected the payment.", command.getAmountForUsers(), "custom"));
                }
            }

            return;
        }

        if (command.getSplitPaymentType().equals("equal")) {

            double amount = command.getAmount() / command.getAccounts().size();

            for (AccountType curr : accountPay) {
                double currAmount = amount * Database.getRate(command.getCurrency(),
                                                    ((Account) curr).getCurrency());

                if (curr.getBalance() < currAmount) {
                    ok = 0;
                    error = ((Account) curr).getIban();
                    break;
                }
            }

            if (ok == 0) {
                for (UserInput user : userPay) {
                    String outAmount = String.format("%.2f", command.getAmount());
                    user.addTransaction(new Transactions("Split payment of "
                            + outAmount + " " + command.getCurrency(), timestamp,
                            command.getCurrency(), amount, command.getAccounts(),
                            "Account " + error + " has insufficient funds for a split payment.", "equal"));
                }

                for (AccountType account : accountPay) {
                    String outAmount = String.format("%.2f", command.getAmount());
                    ((Account) account).addTransaction(new Transactions("Split payment of "
                            + outAmount + " " + command.getCurrency(), timestamp,
                            command.getCurrency(), amount, command.getAccounts(),
                            "Account " + error + " has insufficient funds for a split payment.", "equal"));
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
                        amount, command.getAccounts(),"equal");

                userPay.get(st).addTransaction(transactions);
                ((Account) account).addTransaction(transactions);
                st++;
            }
        } else {
            amounts = command.getAmountForUsers();

            for (int idx = 0 ; idx < command.getAccounts().size() ; idx++) {
                double currAmount = amounts.get(idx) * Database.getRate(command.getCurrency(),
                            ((Account) accountPay.get(idx)).getCurrency());

                if (accountPay.get(idx).getBalance() < currAmount) {
                    ok = 0;
                    error = ((Account) accountPay.get(idx)).getIban();
                    break;
                }
            }

            if (ok == 0) {
                for (int idx = 0 ; idx < userPay.size() ; idx++) {
                    String outAmount = String.format("%.2f", command.getAmount());
                    userPay.get(idx).addTransaction(new Transactions("Split payment of "
                            + outAmount + " " + command.getCurrency(), timestamp,
                            command.getCurrency(), -1, command.getAccounts(),
                            "Account " + error + " has insufficient funds for a split payment.",  command.getAmountForUsers(),"custom"));

                    ((Account) accountPay.get(idx)).addTransaction(new Transactions("Split payment of "
                            + outAmount + " " + command.getCurrency(), timestamp,
                            command.getCurrency(), -1, command.getAccounts(),
                            "Account " + error + " has insufficient funds for a split payment.", command.getAmountForUsers(), "custom"));
                }

                return;
            }

            int st = 0;
            for (AccountType account : accountPay) {
                double currAmount = amounts.get(st) * Database.getRate(command.getCurrency(),
                        ((Account) account).getCurrency());
                account.pay(currAmount);
                String outAmount = String.format("%.2f", command.getAmount());
                Transactions transactions = new Transactions("Split payment of " + outAmount + " "
                        + command.getCurrency(), timestamp, command.getCurrency(),
                        -1, command.getAccounts(), command.getAmountForUsers(), "custom");

                userPay.get(st).addTransaction(transactions);
                ((Account) account).addTransaction(transactions);
                st++;
            }
        }
    }
}
