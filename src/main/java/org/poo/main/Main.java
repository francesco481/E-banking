package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.command.*;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.ObjectInput;
import org.poo.management.Database;
import org.poo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                boolean delete = file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        var sortedFiles = Arrays.stream(Objects.requireNonNull(directory.listFiles())).
                sorted(Comparator.comparingInt(Main::fileConsumer))
                .toList();

        for (File file : sortedFiles) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(CheckerConstants.TESTS_PATH + filePath1);
        ObjectInput inputData = objectMapper.readValue(file, ObjectInput.class);

        ArrayNode output = objectMapper.createArrayNode();
        Database db = Database.getInstance();

        db.addUsers(inputData.getUsers());
        db.addExchanges(inputData.getExchangeRates());

        for (CommandInput commands : inputData.getCommands())
        {
            String currCommand = commands.getCommand();
            if (currCommand.equals("printUsers")) {
                PrintUsers printUsers = new PrintUsers(db, objectMapper, output);
                printUsers.execute(commands.getTimestamp());
            }
            else if (currCommand.equals("printTransactions")) {
                PrintTransactions printTransactions = new PrintTransactions();
                printTransactions.execute(commands.getTimestamp());
            }
            else if (currCommand.equals("addAccount")) {
                AddAccount addAccount = new AddAccount(db, commands);
                addAccount.execute(commands.getTimestamp());
            }
            else if (currCommand.equals("createCard")) {
                CreateCard createCard = new CreateCard(db, commands);
                createCard.execute(commands.getTimestamp());
            }
            else if (currCommand.equals("createOneTimeCard")) {
                CreateCard createCard = new CreateCard(db, commands);
                createCard.execute(commands.getTimestamp());
            }
            else if (currCommand.equals("addFunds")) {
                AddFunds addFunds = new AddFunds(db, commands);
                addFunds.execute(commands.getTimestamp());
            }
            else if (currCommand.equals("deleteAccount")) {
                DeleteAccount deleteAccount = new DeleteAccount(db, commands, objectMapper, output);
                deleteAccount.execute(commands.getTimestamp());
            }
            else if (currCommand.equals("deleteCard")) {
                DeleteCard deleteCard = new DeleteCard(db, commands, objectMapper, output);
                deleteCard.execute(commands.getTimestamp());
            }
            else if (currCommand.equals("payOnline")) {
                PayOnline payOnline = new PayOnline(db, commands, objectMapper, output);
                payOnline.execute(commands.getTimestamp());
            }
            else if (currCommand.equals("sendMoney")) {
                SendMoney sendMoney = new SendMoney(db, commands);
                sendMoney.execute(commands.getTimestamp());
            }
        }

        db.clear();
        Utils.resetRandom();

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }

    /**
     * Method used for extracting the test number from the file name.
     *
     * @param file the input file
     * @return the extracted numbers
     */
    public static int fileConsumer(final File file) {
        String fileName = file.getName()
                .replaceAll(CheckerConstants.DIGIT_REGEX, CheckerConstants.EMPTY_STR);
        return Integer.parseInt(fileName.substring(0, 2));
    }
}
