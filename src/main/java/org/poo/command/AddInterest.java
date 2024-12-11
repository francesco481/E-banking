package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;

public final class AddInterest implements Order {
    private final Database database;
    private final CommandInput command;
    private final ObjectMapper mapper;
    private final ArrayNode output;

    public AddInterest(final Database database, final CommandInput command,
                       final ObjectMapper mapper, final ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    /**
     * Executes the command to add interest to a specified account in the database.
     * This method iterates through the users and their associated accounts to find
     * the account specified in the command. If the account is eligible for interest,
     * its balance is updated based on the provided interest rate. If the account is
     * not eligible, an error message is added to the output.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Typically used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        int i = 0;
        for (UserInput ignored : database.getUsers()) {
            for (AccountType account : database.getAccounts().get(i)) {
                if (((Account) account).getIban().equals(command.getAccount())) {
                    if (((Account) account).getInterest() != -1) {
                        ((Account) account).setBalance((command.getInterestRate() + 1)
                                                        * account.getBalance());
                    } else {
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
