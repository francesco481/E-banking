package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;
import org.poo.management.Transactions;

public class AddInterest implements Order {
    Database database;
    CommandInput command;
    ObjectMapper mapper;
    ArrayNode output;

    public AddInterest(Database database, CommandInput command, ObjectMapper mapper, ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    @Override
    public void execute(int timestamp) {
        int i = 0;
        for (UserInput user : database.getUsers()) {
            for (AccountType account : database.getAccounts().get(i)) {
                if (((Account) account).getIBAN().equals(command.getAccount()))
                {
                    if (((Account) account).getInterest() != -1) {
                        ((Account) account).setBalance((command.getInterestRate() + 1) * account.getBalance());
                    }
                    else {
                        ObjectNode outputNode = mapper.createObjectNode();
                        outputNode.put("command", "addInterest");
                        ObjectNode errorOutput = mapper.createObjectNode();
                        errorOutput.put("timestamp", timestamp);
                        errorOutput.put("description", "This is not a savings account");
                        outputNode.set("output", errorOutput);
                        outputNode.put("timestamp", timestamp);
                        output.add(outputNode);
                    }
                    return;
                }
            }
            i++;
        }
    }
}
