package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.management.accounts.Account;
import org.poo.management.accounts.AccountType;
import org.poo.management.Database;

public final class ReportSpending implements Order {
    private final Database database;
    private final CommandInput command;
    private final ObjectMapper mapper;
    private final ArrayNode output;

    public ReportSpending(final Database database, final CommandInput command,
                          final ObjectMapper mapper, final ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    /**
     * Executes the command to print spending details for a specific account within a given time
     * range. This method searches for the specified account by its IBAN and retrieves the
     * spending details that fall within the start and end timestamps. If the account is
     * found, the spending details are printed; otherwise, an error message is generated.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        for (int i = 0; i < database.getUsers().size(); i++) {
            for (AccountType account : database.getAccounts().get(i)) {
                if (((Account) account).getIban().equals(command.getAccount())) {
                    ObjectNode outputNode = account.printSpendings(command.getStartTimestamp(),
                                            command.getEndTimestamp(), command.getTimestamp());
                    output.add(outputNode);
                    return;
                }
            }
        }

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("command", "spendingsReport");

        ObjectNode errorOutput = mapper.createObjectNode();
        errorOutput.put("description", "Account not found");
        errorOutput.put("timestamp", timestamp);

        outputNode.set("output", errorOutput);
        outputNode.put("timestamp", timestamp);
        output.add(outputNode);
    }
}
