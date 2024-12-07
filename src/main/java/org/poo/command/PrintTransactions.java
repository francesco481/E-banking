package org.poo.command;

public class PrintTransactions implements Order {
    @Override
    public void execute(int timestamp) {
        System.out.println("Printing transactions");
    }
}
