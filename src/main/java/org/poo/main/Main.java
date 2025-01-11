package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.command.*;
import org.poo.fileio.CommandInput;
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

                if (!delete) {
                    System.out.println("Delete failed");
                }
            }
            boolean delete = resultFile.delete();
            if (!delete) {
                System.out.println("Delete failed");
            }
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
        db.addCommerciants(inputData.getCommerciants());
        db.addCommands(inputData.getCommands());

        for (int idx = 0; idx < db.getCommands().size(); idx++)  {
            CommandInput commands = db.getCommands().get(idx);
            String currCommand = commands.getCommand();
            switch (currCommand) {
                case "printUsers" -> {
                    PrintUsers printUsers = new PrintUsers(db, objectMapper, output);
                    printUsers.execute(commands.getTimestamp());
                }
                case "printTransactions" -> {
                    PrintTransactions printTransactions = new PrintTransactions(db, objectMapper,
                            output, commands);
                    printTransactions.execute(commands.getTimestamp());
                }
                case "addAccount" -> {
                    AddAccount addAccount = new AddAccount(db, commands);
                    addAccount.execute(commands.getTimestamp());
                }
                case "createCard", "createOneTimeCard" -> {
                    CreateCard createCard = new CreateCard(db, commands);
                    createCard.execute(commands.getTimestamp());
                }
                case "addFunds" -> {
                    AddFunds addFunds = new AddFunds(db, commands);
                    addFunds.execute(commands.getTimestamp());
                }
                case "deleteAccount" -> {
                    DeleteAccount deleteAccount = new DeleteAccount(db, commands,
                            objectMapper, output);
                    deleteAccount.execute(commands.getTimestamp());
                }
                case "deleteCard" -> {
                    DeleteCard deleteCard = new DeleteCard(db, commands);
                    deleteCard.execute(commands.getTimestamp());
                }
                case "payOnline" -> {
                    PayOnline payOnline = new PayOnline(db, commands, objectMapper, output);
                    payOnline.execute(commands.getTimestamp());
                }
                case "sendMoney" -> {
                    SendMoney sendMoney = new SendMoney(db, commands, output);
                    sendMoney.execute(commands.getTimestamp());
                }
                case "checkCardStatus" -> {
                    CheckStatus checkStatus = new CheckStatus(db, commands, objectMapper, output);
                    checkStatus.execute(commands.getTimestamp());
                }
                case "setMinimumBalance" -> {
                    SetMinimum setMinimum = new SetMinimum(db, commands);
                    setMinimum.execute(commands.getTimestamp());
                }
                case "setAlias" -> {
                    SetAlias setAlias = new SetAlias(db, commands);
                    setAlias.execute(commands.getTimestamp());
                }
                case "splitPayment" -> {
                    SplitPayment splitPayment = new SplitPayment(db, commands, idx + 1);
                    splitPayment.execute(commands.getTimestamp());
                }
                case "addInterest" -> {
                    AddInterest addInterest = new AddInterest(db, commands, objectMapper, output);
                    addInterest.execute(commands.getTimestamp());
                }
                case "changeInterestRate" -> {
                    ChangeInterest changeInterest = new ChangeInterest(db, commands,
                            objectMapper, output);
                    changeInterest.execute(commands.getTimestamp());
                }
                case "report" -> {
                    Report report = new Report(db, commands, objectMapper, output);
                    report.execute(commands.getTimestamp());
                }
                case "spendingsReport" -> {
                    ReportSpending reportSpending = new ReportSpending(db, commands,
                            objectMapper, output);
                    reportSpending.execute(commands.getTimestamp());
                }
                case "withdrawSavings" -> {
                    WithDraw withDraw = new WithDraw(commands, db);
                    withDraw.execute(commands.getTimestamp());
                }
                case "upgradePlan" -> {
                    UpgradePlan upgradePlan = new UpgradePlan(db, commands, output);
                    upgradePlan.execute(commands.getTimestamp());
                }
                case "cashWithdrawal" -> {
                    CashDraw cashDraw = new CashDraw(db, commands, output);
                    cashDraw.execute(commands.getTimestamp());
                }
                case "rejectSplitPayment", "acceptSplitPayment" -> {
                    NoSplit noSplit = new NoSplit(commands, output);
                    noSplit.execute(commands.getTimestamp());
                }
                default -> {
                }

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
                .replaceAll(CheckerConstants.DIGIT_REGEX,
                        CheckerConstants.EMPTY_STR);
        return Integer.parseInt(fileName.substring(0, 2));
    }
}
