package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.accounts.Account;
import org.poo.management.accounts.AccountType;
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
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode commandNode = mapper.createObjectNode();
            commandNode.put("command", command.getCommand());

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("timestamp", command.getTimestamp());
            outputNode.put("description", "Account not found");

            commandNode.set("output", outputNode);
            commandNode.put("timestamp", timestamp);

            output.add(commandNode);
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
            user.addTransaction(new Transactions("Insufficient funds", command.getTimestamp()));
            ((Account) account).addTransaction(new Transactions("Insufficient funds", command.getTimestamp()));
            return;
        }

        account.pay(amount);

        user.setPlan(command.getNewPlanType());
        user.addTransaction(new Transactions("Upgrade plan", command.getTimestamp(), command.getAccount(), command.getNewPlanType()));
        ((Account) account).addTransaction(new Transactions("Upgrade plan", command.getTimestamp(), command.getAccount(), command.getNewPlanType()));
    }
}
