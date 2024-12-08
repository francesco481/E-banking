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

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class Database {
    static Database db = new Database();

    private ArrayList<UserInput> Users = new ArrayList<>();
    private ArrayList<ArrayList<AccountType>> Accounts = new ArrayList<>();
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

        for (ExchangeInput exchange : Exchange) {
            if (exchange.getFrom().equals(from) && exchange.getTo().equals(to))
                return exchange.getRate();
        }

        for (ExchangeInput exchange1 : Exchange) {
            for (ExchangeInput exchange2 : Exchange) {
                if (exchange1.getFrom().equals(from) &&
                    exchange1.getTo().equals(exchange2.getFrom()) &&
                    exchange2.getTo().equals(to)) {

                    return exchange1.getRate() * exchange2.getRate();
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
            }
        }

        if(ok == 1){

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
        }

        return 1;
    }

    private int findCard(ArrayList<AccountType> accounts, String cardNumber) {
        for (AccountType account : accounts) {
            Account curr = (Account)account;
            for(CardType card : curr.getCards()) {
                if (((Card) card).getCardNumber().equals(cardNumber)) {
                    curr.getCards().remove(card);
                    return 1;
                }
            }
        }

        return -1;
    }

    public int deleteCard(CommandInput command) {
        for (ArrayList<AccountType> accounts : this.getAccounts()) {
            int i = findCard(accounts, command.getCardNumber());
            if (i != -1) {
                return 1;
            }
        }

        return -1;
    }

    public void clear(){
        Users.clear();
        Accounts.clear();
        Exchange.clear();
    }
}
