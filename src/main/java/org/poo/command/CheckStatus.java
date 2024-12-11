package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Cards.Card;
import org.poo.management.Cards.CardType;
import org.poo.management.Database;
import org.poo.management.Transactions;

import java.util.ArrayList;

public final class CheckStatus implements Order {
    private final Database database;
    private final CommandInput command;
    private final ObjectMapper mapper;
    private final ArrayNode output;

    public CheckStatus(final Database database, final CommandInput command,
                       final ObjectMapper mapper, final ArrayNode output) {
        this.database = database;
        this.command = command;
        this.mapper = mapper;
        this.output = output;
    }

    /**
     * Executes the command to check the status of a card and update its status if necessary.
     * This method iterates through the accounts in the database to locate the card specified
     * in the command. If the card is found and is active, and the associated account's balance
     * falls below the minimum allowed, the card is marked as "frozen". Additionally, a
     * transaction is recorded for both the user and the account. If the card is not found,
     * an error message is added to the output.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Typically used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        int st = 0;
        int ok = 1;

        Transactions transactions = new Transactions("You have reached the minimum"
                + " amount of funds, the card will be frozen", timestamp);

        for (ArrayList<AccountType> accounts : database.getAccounts()) {
            for (AccountType account : accounts) {
                Account currAcc = (Account) account;
                for (CardType card : currAcc.getCards()) {
                    Card curr = (Card) card;
                    if (curr.getCardNumber().equals(command.getCardNumber())) {
                        ok = 0;
                        if (curr.getStatus().equals("active")) {
                            if (currAcc.getBalance() <= currAcc.getMinimum()) {
                                curr.setStatus("frozen");
                                UserInput user = database.getUsers().get(st);
                                user.addTransaction(transactions);
                                currAcc.addTransaction(transactions);
                            }
                        }
                    }
                }
            }
            st++;
        }

        if (ok == 1) {
            ObjectNode outputMessage = mapper.createObjectNode();
            outputMessage.put("command", "checkCardStatus");
            ObjectNode outputDetails = mapper.createObjectNode();
            outputDetails.put("timestamp", timestamp);
            outputDetails.put("description", "Card not found");
            outputMessage.set("output", outputDetails);
            outputMessage.put("timestamp", timestamp);
            output.add(outputMessage);
        }
    }
}
