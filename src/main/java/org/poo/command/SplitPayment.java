package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.utils.Pair;


import java.util.ArrayList;

public class SplitPayment implements Order {
    Database database;
    CommandInput command;
    ObjectMapper mapper;
    ArrayNode output;

    public SplitPayment(Database database, CommandInput command, ObjectMapper mapper, ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    @Override
    public void execute(int timestamp) {
        ArrayList<UserInput> userPay = new ArrayList<>();
        ArrayList<AccountType> accountPay = new ArrayList<>();
        int ok = 1;
        String error = null;

        double amount = command.getAmount() / command.getAccounts().size();

        for (String IBAN : command.getAccounts()) {
            Pair find = database.findBigIBAN(IBAN);
            int first = (int) find.getFirst();
            int second = (int) find.getSecond();

            if(first != -1) {
                userPay.add(database.getUsers().get(first));
                AccountType curr = database.getAccounts().get(first).get(second);
                accountPay.add(curr);

                double currAmount = amount * Database.getRate(command.getCurrency(), ((Account) curr).getCurrency());
                if (curr.getBalance() < currAmount) {
                    ok = 0;
                    error = ((Account) curr).getIBAN();
                }
            }
        }

        if (ok == 0) {
            for (UserInput user : userPay) {
                String outAmount = String.format("%.2f", command.getAmount());
                user.addTransaction(new Transactions("Split payment of " + outAmount + " " + command.getCurrency(), timestamp, command.getCurrency(), amount, command.getAccounts(), "Account " + error + " has insufficient funds for a split payment."));
            }
            for (AccountType account : accountPay) {
                String outAmount = String.format("%.2f", command.getAmount());
                ((Account) account).addTransaction(new Transactions("Split payment of " + outAmount + " " + command.getCurrency(), timestamp, command.getCurrency(), amount, command.getAccounts(), "Account " + error + " has insufficient funds for a split payment."));
            }

            return;
        }

        int st = 0;
        for (AccountType account : accountPay) {
            double currAmount = amount * Database.getRate(command.getCurrency(), ((Account) account).getCurrency());
            account.pay(currAmount);
            String outAmount = String.format("%.2f", command.getAmount());
            userPay.get(st).addTransaction(new Transactions("Split payment of " + outAmount + " " + command.getCurrency(), timestamp, command.getCurrency(), amount, command.getAccounts()));
            ((Account) account).addTransaction(new Transactions("Split payment of " + outAmount + " " + command.getCurrency(), timestamp, command.getCurrency(), amount, command.getAccounts(),"Account " + error + " has insufficient funds for a split payment."));
            st++;
        }
    }
}
