package org.poo.management;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountFactory;
import org.poo.management.Accounts.AccountType;
import org.poo.management.Cards.Card;
import org.poo.management.Cards.CardType;
import org.poo.utils.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

@Getter
@Setter
public final class Database {
    private static Database db = new Database();

    private ArrayList<UserInput> users = new ArrayList<>();
    private ArrayList<ArrayList<AccountType>> accounts = new ArrayList<>();
    private ArrayList<Alias> aliases = new ArrayList<>();
    @Getter
    private static ArrayList<ExchangeInput> exchange = new ArrayList<>();

    private Database() {

    }

    public static Database getInstance() {
        return db;
    }

    /**
     * Adds multiple users to the collection of users.
     * This method iterates through the provided array of `UserInput` objects and adds each user to
     * the internal list of users. Additionally, it initializes a new empty `ArrayList`
     * for each user to hold associated accounts.
     *
     * @param userInputs an array of `UserInput` objects representing the users to be added.
     */
    public void addUsers(final UserInput[] userInputs) {
        for (int i = 0; i < userInputs.length; i++) {
            this.users.add(userInputs[i]);
            this.accounts.add(new ArrayList<>());
        }
    }

    /**
     * Finds the index of an account with the specified IBAN in a list of accounts.
     * This method iterates through the list of `AccountType` objects and checks if the IBAN
     * of each account matches the provided IBAN. It returns the index of the matching account
     * or `-1` if no match is found.
     *
     * @param accounts a list of `AccountType` objects to search through.
     * @param iban the IBAN of the account to search for.
     * @return the index of the account with the specified IBAN, or `-1` if not found.
     */
    public static int findIBAN(final ArrayList<AccountType> accounts, final String iban) {
        for (int i = 0; i < accounts.size(); i++) {
            Account curr = (Account) accounts.get(i);
            if (curr.getIban().equals(iban)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Retrieves the exchange rate between two specified currencies.
     * This method uses a breadth-first search (BFS) algorithm to find the path and calculate
     * the rate from the starting currency to the target currency using a series of exchange
     * rates provided in the `exchange` list. If a direct or indirect path exists between the
     * currencies, it returns the calculated rate; otherwise, it returns `-1` indicating no
     * valid conversion path.
     *
     * @param from the source currency.
     * @param to the target currency.
     * @return the exchange rate from `from` to `to`, or `-1` if no conversion path exists.
     */
    public static double getRate(final String from, final String to) {
        if (from.equals(to)) {
            return 1;
        }

        Map<String, List<ExchangeInput>> graph = new HashMap<>();
        for (ExchangeInput exchangeInput : exchange) {
            graph.putIfAbsent(exchangeInput.getFrom(), new ArrayList<>());
            graph.get(exchangeInput.getFrom()).add(exchangeInput);
        }

        Queue<Pair<String, Double>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(new Pair<>(from, 1.0));

        while (!queue.isEmpty()) {
            Pair<String, Double> current = queue.poll();
            String currentCurrency = current.getFirst();
            double currentRate = current.getSecond();

            visited.add(currentCurrency);

            if (graph.containsKey(currentCurrency)) {
                for (ExchangeInput neighbor : graph.get(currentCurrency)) {
                    String neighborCurrency = neighbor.getTo();
                    double newRate = currentRate * neighbor.getRate();

                    if (neighborCurrency.equals(to)) {
                        return newRate;
                    }

                    if (!visited.contains(neighborCurrency)) {
                        queue.add(new Pair<>(neighborCurrency, newRate));
                    }
                }
            }
        }

        return -1;
    }

    /**
     * Finds the index of a user with the specified email address in the `users` list.
     *
     * @param email the email address of the user to search for.
     * @return the index of the user with the specified email, or `-1` if not found.
     */
    public int findUser(final String email) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(email)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Creates and adds a new account to the specified user.
     *
     * @param command the command input containing the user’s email, account type, and timestamp.
     */
    public void addAccount(final CommandInput command) {
        int i = findUser(command.getEmail());
        String type = command.getAccountType();
        AccountType account = AccountFactory.getAccount(type, command);
        this.getAccounts().get(i).add(account);

        UserInput user = this.getUsers().get(i);
        Transactions transactions = new Transactions("New account created", command.getTimestamp());
        user.addTransaction(transactions);
        ((Account) account).addTransaction(transactions);
    }

    /**
     * Adds a set of exchange inputs to the exchange list and creates reverse exchanges.
     * This method takes an array of `ExchangeInput` objects, adds them to the existing
     * exchange list, and creates reverse exchanges for each input.
     *
     * @param exchangeInputs an array of `ExchangeInput` objects to be added to the exchange list.
     */
    public void addExchanges(final ExchangeInput[] exchangeInputs) {
        exchange.addAll(Arrays.asList(exchangeInputs));
        int n = exchange.size();

        for (int i = 0; i < n; i++) {
            ExchangeInput rev = exchange.get(i).exchangeRev();
            exchange.add(rev);
        }
    }

    /**
     * Adds a new card to an account associated with the specified user.
     * This method associates a new card with the user's specified account, creates the card using
     * the `CardFactory`, and records a "New card created" transaction.
     *
     * @param command the command input containing details such as the user's email, account IBAN,
     * and card creation timestamp.
     */
    public void addCard(final CommandInput command) {
        int i = findUser(command.getEmail());
        if (i == -1) {
            return;
        }

        for (AccountType account : this.getAccounts().get(i)) {
            Account curr = (Account) account;
            if (curr.getIban().equals(command.getAccount())) {
                ((Account) account).addCard(command);
                UserInput user = this.getUsers().get(i);
                Transactions transactions = new Transactions("New card created",
                        command.getTimestamp(),
                        ((Card) ((Account) account).getCards().getLast()).getCardNumber(),
                        user.getEmail(), ((Account) account).getIban());

                user.addTransaction(transactions);
                ((Account) account).addTransaction(transactions);
                return;
            }
        }
    }

    /**
     * Finds and returns the indices of a user and their associated account that
     * match the specified IBAN.
     *
     * @param iban the IBAN of the account to search for.
     * @return a pair containing the user index and account index, or `-1` if not found.
     */
    public Pair<Integer, Integer> findBigIBAN(final String iban) {
        int i = -1;
        int j = -1;

        for (int idxU = 0; idxU < users.size(); idxU++) {
            int idxA = 0;
            for (AccountType account : accounts.get(idxU)) {
                if (((Account) account).getIban().equals(iban)) {
                    return new Pair<>(idxU, idxA);
                }
                idxA++;
            }
        }

        return new Pair<>(i, j);
    }

    /**
     * Adds funds to the specified account across all users.
     *
     * @param command the command input containing the account IBAN and amount.
     */
    public void addFunds(final CommandInput command) {
        for (ArrayList<AccountType> accountTypes : this.getAccounts()) {
            int i = findIBAN(accountTypes, command.getAccount());
            if (i != -1) {
                ((Account) accountTypes.get(i)).addFunds(command.getAmount());
            }
        }
    }

    /**
     * Deletes an account if it has no remaining balance.
     *
     * @param command the command input containing the user's email and account IBAN.
     * @return `1` if the account was successfully deleted, `-1` otherwise.
     */
    public int deleteAccount(final CommandInput command) {
        int i = findUser(command.getEmail());
        if (i == -1) {
            return -1;
        }

        int j = findIBAN(db.getAccounts().get(i), command.getAccount());
        if (j == -1) {
            return -1;
        }

        Account curr = (Account) db.getAccounts().get(i).get(j);
        if (curr.getBalance() == 0) {
            db.getAccounts().get(i).remove(j);
            return 1;
        }

        UserInput user = this.getUsers().get(i);
        Transactions transactions = new Transactions("Account couldn't be deleted "
                + "- there are funds remaining", command.getTimestamp());

        user.addTransaction(transactions);
        curr.addTransaction(transactions);
        return -1;
    }

    /**
     * Finds and removes a card from the specified account and returns its index.
     *
     * @param accountTypes the list of account types.
     * @param cardNumber the card number to search for.
     * @return the index of the removed card, or `-1` if not found.
     */
    private int findCard(final ArrayList<AccountType> accountTypes, final String cardNumber) {
        int st = 0;
        for (AccountType account : accountTypes) {
            Account curr = (Account) account;
            for (CardType card : curr.getCards()) {
                if (((Card) card).getCardNumber().equals(cardNumber)) {
                    curr.getCards().remove(card);
                    return st;
                }
            }
            st++;
        }

        return -1;
    }

    /**
     * Deletes a card based on the specified command.
     *
     * @param command the command input containing the card number and timestamp.
     * @return `1` if the card was successfully deleted, `-1` otherwise.
     */
    public int deleteCard(final CommandInput command) {
        int idx = 0;
        for (ArrayList<AccountType> accountTypes : this.getAccounts()) {
            int i = findCard(accountTypes, command.getCardNumber());
            if (i != -1) {
                UserInput user = this.getUsers().get(idx);
                Transactions transactions = new Transactions("The card has been destroyed",
                        command.getTimestamp(), command.getCardNumber(), user.getEmail(),
                        ((Account) accountTypes.get(i)).getIban());

                user.addTransaction(transactions);
                ((Account) accountTypes.get(i)).addTransaction(transactions);
                return 1;
            }
            idx++;
        }

        return -1;
    }

    /**
     * Adds a new alias to the list of aliases.
     *
     * @param alias the alias to be added.
     */
    public void addAlias(final Alias alias) {
        this.aliases.add(alias);
    }

    /**
     * Clears all data from the lists of users, accounts, and exchanges.
     * This method removes all elements from the respective collections,
     * effectively resetting them to an empty state.
     */
    public void clear() {
        users.clear();
        accounts.clear();
        exchange.clear();
    }
}
