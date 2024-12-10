package org.poo.management.Accounts;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface AccountType {
    void pay(double amount);

    ObjectNode printTransaction(int start, int stop, int timestamp);

    ObjectNode printSpendings(int start, int stop, int timestamp);

    double getBalance();
}
