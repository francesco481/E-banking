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

import java.util.*;

@Getter
@Setter
public class Database {
    static Database db = new Database();

    private ArrayList<UserInput> Users = new ArrayList<>();
    private ArrayList<ArrayList<AccountType>> Accounts = new ArrayList<>();
    private ArrayList<Alias> Aliases= new ArrayList<>();
    @Getter
    private static ArrayList<ExchangeInput> Exchange = new ArrayList<>();

    private Database() {}

    public static Database getInstance() {
        return db;
    }

    public void addUsers(UserInput[] Users) {
        for (int i = 0; i < Users.length; i++) {
            this.Users.add(Users[i]);
            this.Accounts.add(new ArrayList<>());
        }
    }

    public void addExchanges(ExchangeInput[] Exchanges) {
        Exchange.addAll(Arrays.asList(Exchanges));
        int n = Exchange.size();

        for (int i = 0; i < n; i++) {
            ExchangeInput rev = this.Exchange.get(i).ExchangeInputRev();
            this.Exchange.add(rev);
        }
    }

    public static double getRate(String from, String to) {
        if(from.equals(to))
            return 1;

        if (from.equals(to)) return 1;

        Map<String, List<ExchangeInput>> graph = new HashMap<>();
        for (ExchangeInput exchange : Exchange) {
            graph.putIfAbsent(exchange.getFrom(), new ArrayList<>());
            graph.get(exchange.getFrom()).add(exchange);
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

    public int findUser(String email)
    {
        for (int i = 0 ; i < Users.size() ; i++){
            if (Users.get(i).getEmail().equals(email)) {
                return i;
            }
        }

        return -1;
    }

    public void addAccount(CommandInput command) {
        int i = findUser(command.getEmail());
        String type = command.getAccountType();
        AccountType account = AccountFactory.getAccount(type, command);
        this.getAccounts().get(i).add(account);

        UserInput user = this.getUsers().get(i);
        user.addTransaction(new Transactions("New account created", command.getTimestamp()));
        ((Account) account).addTransaction(new Transactions("New account created", command.getTimestamp()));
    }

    public void addCard(CommandInput command) {
        int i = findUser(command.getEmail());
        int ok = 1;

        if (i == -1)
            return;

        for(AccountType account : this.getAccounts().get(i)) {
            Account curr = (Account)account;
            if(curr.getIBAN().equals(command.getAccount())) {
                ((Account) account).addCard(command);
                ok = 0;
                UserInput user = this.getUsers().get(i);
                user.addTransaction(new Transactions("New card created", command.getTimestamp(), ((Card) ((Account) account).getCards().getLast()).getCardNumber(), user.getEmail(), ((Account) account).getIBAN()));
                ((Account) account).addTransaction(new Transactions("New card created", command.getTimestamp(), ((Card) ((Account) account).getCards().getLast()).getCardNumber(), user.getEmail(), ((Account) account).getIBAN()));
                return;
            }
        }

        if (ok == 1){

        }
    }

    public static int findIBAN(ArrayList<AccountType> accounts, String IBAN) {
        for (int i = 0 ; i < accounts.size() ; i++) {
            Account curr = (Account)accounts.get(i);
            if(curr.getIBAN().equals(IBAN)) {
                return i;
            }
        }

        return -1;
    }

    public Pair<Integer, Integer> findBigIBAN(String IBAN) {
        int i = -1;
        int j = -1;

        for (int idxU = 0 ; idxU < Users.size() ; idxU++) {
            int idxA = 0;
            for (AccountType account : Accounts.get(idxU))
            {
                if (((Account) account).getIBAN().equals(IBAN)) {
                    return new Pair<>(idxU, idxA);
                }
                idxA++;
            }
        }

        return new Pair<>(i, j);
    }

    public void addFunds(CommandInput command) {
        for (ArrayList<AccountType> accounts : this.getAccounts()) {
            int i = findIBAN(accounts, command.getAccount());
            if (i != -1) {
                ((Account) accounts.get(i)).addFunds(command.getAmount());
            }
        }
    }

    public int deleteAccount(CommandInput command) {
        int i = findUser(command.getEmail());
        if (i == -1)
            return -1;

        int j = findIBAN(db.getAccounts().get(i), command.getAccount());
        if (j == -1)
            return -1;

        Account curr = (Account) db.getAccounts().get(i).get(j);
        if (curr.getBalance() == 0){
            db.getAccounts().get(i).remove(j);
            return 1;
        }

        UserInput user = this.getUsers().get(i);
        user.addTransaction(new Transactions("Account couldn't be deleted - there are funds remaining", command.getTimestamp()));
        curr.addTransaction(new Transactions("Account couldn't be deleted - there are funds remaining", command.getTimestamp()));
        return -1;
    }

    private int findCard(ArrayList<AccountType> accounts, String cardNumber) {
        int st = 0;
        for (AccountType account : accounts) {
            Account curr = (Account)account;
            for(CardType card : curr.getCards()) {
                if (((Card) card).getCardNumber().equals(cardNumber)) {
                    curr.getCards().remove(card);
                    return st;
                }
            }
            st++;
        }

        return -1;
    }

    public int deleteCard(CommandInput command) {
        int idx = 0;
        for (ArrayList<AccountType> accounts : this.getAccounts()) {
            int i = findCard(accounts, command.getCardNumber());
            if (i != -1) {
                UserInput user = this.getUsers().get(idx);
                user.addTransaction(new Transactions("The card has been destroyed", command.getTimestamp(), command.getCardNumber(), user.getEmail(), ((Account) accounts.get(i)).getIBAN()));
                ((Account) accounts.get(i)).addTransaction(new Transactions("The card has been destroyed", command.getTimestamp(), command.getCardNumber(), user.getEmail(), ((Account) accounts.get(i)).getIBAN()));
                return 1;
            }
            idx++;
        }

        return -1;
    }

    public void addAlias(Alias alias) {
        this.Aliases.add(alias);
    }

    public void clear(){
        Users.clear();
        Accounts.clear();
        Exchange.clear();
    }
}
