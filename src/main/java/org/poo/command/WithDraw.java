package org.poo.command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.utils.Pair;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

public class WithDraw implements Order {
    private CommandInput command;
    private Database database;
    private final ArrayNode output;

    public WithDraw(CommandInput command, Database database, ArrayNode output) {
        this.command = command;
        this.database = database;
        this.output = output;
    }

    @Override
    public void execute(int timestamp) {
        Pair < Integer, Integer > idx = database.findBigIBAN(command.getAccount());

        if (idx.getFirst() == -1) {
            //account not found
            return;
        }

        UserInput user = database.getUsers().get(idx.getFirst());
        AccountType account = database.getAccounts().get(idx.getFirst()).get(idx.getSecond());

        LocalDate birth = LocalDate.parse(user.getBirthDate());
        LocalDate currDate = LocalDate.now();

        Period age = Period.between(birth, currDate);
        if (age.getYears() < 21) {
            user.addTransaction(new Transactions("You don't have the minimum age required.", command.getTimestamp()));
            ((Account) account).addTransaction(new Transactions("You don't have the minimum age required.", command.getTimestamp()));
            return;
        }

        if (!((Account) account).getType().equals("savings")) {
            //account not savings
            return;
        }

        AccountType receive = null;
        for (AccountType curr : database.getAccounts().get(idx.getFirst())) {
            if (((Account) curr).getCurrency().equals(command.getCurrency())  &&  ((Account) curr).getType().equals("classic")) {
                receive = curr;
                break;
            }
        }

        if (receive == null) {
            user.addTransaction(new Transactions("You do not have a classic account.", command.getTimestamp()));
            ((Account) account).addTransaction(new Transactions("You do not have a classic account.", command.getTimestamp()));
            return;
        }

        double comision = 0;
        double amount = Database.getRate(command.getCurrency(), ((Account) receive).getCurrency()) * command.getAmount();

        if (amount > account.getBalance()) {
            //insf funds
            return;
        }

        account.pay(amount);
        ((Account) receive).addFunds(command.getAmount());
    }
}
