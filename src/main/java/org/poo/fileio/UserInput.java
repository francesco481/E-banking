package org.poo.fileio;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.management.Transactions;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public final class UserInput {
    private String firstName;
    private String lastName;
    private String email;

    private ArrayList<Transactions> transactions = new ArrayList<>();
}
