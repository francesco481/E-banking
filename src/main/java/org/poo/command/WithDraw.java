package org.poo.command;

import org.poo.fileio.CommandInput;
import org.poo.fileio.UserInput;
import org.poo.management.accounts.Account;
import org.poo.management.accounts.AccountType;
import org.poo.management.Database;
import org.poo.management.Transactions;
import org.poo.utils.Pair;

import java.time.LocalDate;
import java.time.Period;

public final class WithDraw implements Order {
    private final CommandInput command;
    private final Database database;

    private static final int AGE_LIMIT = 21;

    public WithDraw(final CommandInput command, final Database database) {
        this.command = command;
        this.database = database;
    }

    @Override
    public void execute(final int timestamp) {
        Pair<Integer, Integer> idx = database.findBigIBAN(command.getAccount());

        if (idx.getFirst() == -1) {
            //account not found
            return;
        }

        UserInput user = database.getUsers().get(idx.getFirst());
        AccountType account = database.getAccounts().get(idx.getFirst()).get(idx.getSecond());

        LocalDate birth = LocalDate.parse(user.getBirthDate());
        LocalDate currDate = LocalDate.now();

        Period age = Period.between(birth, currDate);
        if (age.getYears() < AGE_LIMIT) {
            Transactions transactions = new Transactions("You don't have the minimum age required.",
                    command.getTimestamp());
            user.addTransaction(transactions);
            ((Account) account).addTransaction(transactions);
            return;
        }

        if (!((Account) account).getType().equals("savings")) {
            //account not savings
            return;
        }

        AccountType receive = null;
        for (AccountType curr : database.getAccounts().get(idx.getFirst())) {
            if (((Account) curr).getCurrency().equals(command.getCurrency())
                    &&  ((Account) curr).getType().equals("classic")) {
                receive = curr;
                break;
            }
        }

        if (receive == null) {
            Transactions transactions = new Transactions("You do not have a classic account.",
                    command.getTimestamp());
            user.addTransaction(transactions);
            ((Account) account).addTransaction(transactions);
            return;
        }

        double amount = Database.getRate(command.getCurrency(), ((Account) receive).getCurrency())
                * command.getAmount();

        if (amount > account.getBalance()) {
            //insf funds
            return;
        }

        account.pay(amount);
        ((Account) receive).addFunds(command.getAmount());
        Transactions transactions = new Transactions("Savings withdrawal", command.getTimestamp(),
                ((Account) receive).getIban(), ((Account) account).getIban(), amount);
        user.addTransaction(transactions);
        user.addTransaction(transactions);

        ((Account) account).addTransaction(transactions);
        ((Account) receive).addTransaction(transactions);
    }
}
