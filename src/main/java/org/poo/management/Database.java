package org.poo.management;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.UserInput;
import org.poo.management.Accounts.Account;
import org.poo.management.Accounts.AccountFactory;
import org.poo.management.Cards.Card;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class Database {
    static Database db = new Database();

    private ArrayList<UserInput> Users = new ArrayList<>();
    private ArrayList<ArrayList<Account>> Accounts = new ArrayList<>();
    private ArrayList<ExchangeInput> Exchange = new ArrayList<>();

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

//    private boolean findExchange(String from, String to) {
//
//    }

    public void addExchanges(ExchangeInput[] Exchanges) {
        this.Exchange.addAll(Arrays.asList(Exchanges));
//
//        while (true) {
//            int ok = 1;
//            for (int i = 0; i < this.Exchange.size(); i++) {
//                for (int j = i; j < this.Exchange.size(); j++) {
//                    if (this.Exchange.get(i).getTo().equals(this.Exchange.get(j).getFrom()) &&
//                        !this.Exchange.get(i).getFrom().equals(this.Exchange.get(j).getTo()) &&
//                        !findExchange(this.Exchange.get(i).getFrom(), this.Exchange.get(j).getTo())) {
//                        ok = 0;
//                    }
//                }
//            }
//
//            if(ok == 1)
//            {
//                break;
//            }
//        }
    }

    public double getRate(String from, String to) {
        for (ExchangeInput exchange : Exchange) {
            if (exchange.getFrom().equals(from) && exchange.getTo().equals(to)) {
                return exchange.getRate();
            }
            else if (exchange.getFrom().equals(to) && exchange.getTo().equals(from)) {
                return 1/exchange.getRate();
            }
        }

        return -1;
    }

    private int findUser(String email)
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
        Account account = AccountFactory.getAccount(type, command);
        this.getAccounts().get(i).add(account);
    }

    public void addCard(CommandInput command) {
        int i = findUser(command.getEmail());
        int ok = 1;

        if (i == -1)
            return;

        for(Account account : this.getAccounts().get(i)) {
            if(account.getIBAN().equals(command.getAccount())) {
                account.addCard(command);
                ok = 0;
            }
        }

        if(ok == 1){

        }
    }

    private int findIBAN(ArrayList<Account> accounts, String IBAN) {
        for (int i = 0 ; i < accounts.size() ; i++) {
            if(accounts.get(i).getIBAN().equals(IBAN)) {
                return i;
            }
        }

        return -1;
    }

    public void addFunds(CommandInput command) {
        for (ArrayList<Account> accounts : this.getAccounts()) {
            int i = findIBAN(accounts, command.getAccount());
            if (i != -1) {
                accounts.get(i).addFunds(command.getAmount());
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

        Account curr = db.getAccounts().get(i).get(j);
        if (curr.getBalance() == 0){
            db.getAccounts().get(i).remove(j);
        }

        return 1;
    }

    private int findCard(ArrayList<Account> accounts, String cardNumber) {
        for (Account account : accounts) {
            for(Card card : account.getCards()) {
                if (card.getCardNumber().equals(cardNumber)) {
                    account.getCards().remove(card);
                    return 1;
                }
            }
        }

        return -1;
    }

    public int deleteCard(CommandInput command) {
        for (ArrayList<Account> accounts : this.getAccounts()) {
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
