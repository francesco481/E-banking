package org.poo.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.management.Accounts.Account;
import org.poo.management.Cards.Card;
import org.poo.management.Database;

public final class PrintUsers implements Order {
    private final Database db;
    private final ObjectMapper mapper;
    private final ArrayNode output;

    public PrintUsers(final Database db, final ObjectMapper mapper, final ArrayNode output) {
        this.db = db;
        this.mapper = mapper;
        this.output = output;
    }

    /**
     * Executes the command to print details of all users and their associated accounts.
     * This method retrieves user information along with their account details, including
     * account balances, currencies, types, and card information. The output is formatted
     * as JSON, making it easy to display and consume programmatically.
     *
     * @param timestamp the timestamp at which the command is executed.
     *                  Used for logging or tracking the operation.
     */
    @Override
    public void execute(final int timestamp) {
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "printUsers");

        ArrayNode users = mapper.createArrayNode();

        for (int i = 0; i < db.getUsers().size(); i++) {
            ObjectNode userNode = mapper.createObjectNode();

            userNode.put("firstName", db.getUsers().get(i).getFirstName());
            userNode.put("lastName", db.getUsers().get(i).getLastName());
            userNode.put("email", db.getUsers().get(i).getEmail());

            ArrayNode accounts = mapper.createArrayNode();
            for (int j = 0; j < db.getAccounts().get(i).size(); j++) {
                ObjectNode accountNode = mapper.createObjectNode();
                Account curr = (Account) db.getAccounts().get(i).get(j);

                accountNode.put("IBAN", curr.getIban());
                accountNode.put("balance", curr.getBalance());
                accountNode.put("currency", curr.getCurrency());
                accountNode.put("type", curr.getType());

                ArrayNode cards = mapper.createArrayNode();
                for (int k = 0; k < curr.getCards().size(); k++) {
                    ObjectNode cardNode = mapper.createObjectNode();
                    Card card = curr.getCards().get(k);
                    cardNode.put("cardNumber", card.getCardNumber());
                    cardNode.put("status", card.getStatus());

                    cards.add(cardNode);
                }

                accountNode.set("cards", cards);
                accounts.add(accountNode);
            }

            userNode.set("accounts", accounts);
            users.add(userNode);
        }
        commandNode.set("output", users);
        commandNode.put("timestamp", timestamp);

        output.add(commandNode);
    }

}
