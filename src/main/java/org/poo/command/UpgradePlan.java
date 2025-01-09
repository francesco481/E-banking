package org.poo.command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.utils.Pair;
import org.poo.utils.Utils;

public class UpgradePlan implements Order {
    Database database;
    CommandInput command;
    ArrayNode output;

    public UpgradePlan(Database database, CommandInput command, ArrayNode output) {
        this.database = database;
        this.command = command;
        this.output = output;
    }

    @Override
    public void execute(int timestamp) {
        Pair< Integer, Integer > idx = database.findBigIBAN(command.getAccount());

        if (idx.getFirst() == -1) {
            //account not found
            return;
        }

        UserInput user = database.getUsers().get(idx.getFirst());
        AccountType account = database.getAccounts().get(idx.getFirst()).get(idx.getSecond());

        if (user.getPlan().equals(command.getNewPlanType())) {
            //same plan
            return;
        }

        if (!Utils.isUp(user.getPlan(), command.getNewPlanType())) {
            //downgrade
            return;
        }

        double amount = Utils.getAmount(user.getPlan(), command.getNewPlanType()) * Database.getRate("RON", ((Account) account).getCurrency());

        if (amount > account.getBalance()) {
            //insf funds
            return;
        }

        account.pay(amount);

        user.setPlan(command.getNewPlanType());
        user.addTransaction(new Transactions("Upgrade plan", command.getTimestamp(), command.getAccount(), command.getNewPlanType()));
        ((Account) account).addTransaction(new Transactions("Upgrade plan", command.getTimestamp(), command.getAccount(), command.getNewPlanType()));
    }
}
