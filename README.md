# MARTINUT FRANCESCO 324CA

# e-banking

**Description**:
The `e-banking` project is a simple e-banking system designed to handle different types of
commands. It aims to provide a basic interface for managing bank accounts, creating cards,
performing transactions, and managing user commands efficiently.

## Features
- **Account Creation**:
    - **Savings Account**: Create a savings account with a unique ID.
    - **Classic Account**: Create a standard checking account with a unique ID.
- **Card Creation**:
    - **One-Time Card**: Generate a one-time use card for online transactions.
    - **Classic Card**: Issue a regular card for general use.
- **Transaction History**: View a history of transactions for each account.
- **Check Card Status**: Check whether a card is active or blocked.
- **Pay Online**: Make payments for online services directly from the bank account.
- **Send Money Between Accounts**: Transfer money between different bank accounts.

### Implementation Details

- **Data Structures**:
    - **Accounts**: An `ArrayList<ArrayList<AccountType>>` holds multiple lists of accounts,
      categorized by account type (`savings`, `classic`). Each nested list contains accounts
      specific to that type.
    - **Cards**: An `ArrayList<CardType>` is used to store all cards. Each card has an ID and an
      associated status (active or blocked) and type (`one-time` or `classic`).
    - **Transactions**: A list maintains a log of all transactions for each account, recording
      details such as transaction type (deposit, online payment, transfer), amount, and timestamp.

- **Commands and User Interface**:
    - The system supports a command-line interface where users input commands to interact with the
      banking system. Each command is parsed, validated, and executed accordingly. Examples include
      creating accounts, generating cards, checking balances, making payments, and transferring
      money. For example, to create a new savings account, a user might input:
      `create account savings`. To check the balance of an account, the command would be:
      `balance <account_id>`.

- **Error Handling**:
    - The implementation includes error handling to manage invalid inputs, insufficient funds, and
      non-existent accounts or cards. Appropriate error messages are displayed to guide users and
      maintain system integrity.

- **Security**:
    - The implementation aims to ensure the security of financial transactions. This includes
      validation checks for account and card details, and mechanisms to prevent unauthorized
      access to accounts or cards.

## Core Functionality

The `e-banking` project provides essential banking services to manage accounts, cards,
transactions, and payments effectively. Below are the core functionalities supported
by the system:

### Account Management:
- **Account Creation**: The system supports creating both savings and classic checking accounts.
  Each account is identified by a unique account ID and stores essential information such as
  account balance and account type.
- **Deposit and Withdrawal**: Users can deposit and transfer funds from their accounts. The
  system validates transactions to ensure sufficient funds are available for withdrawals.
- **Check Balance**: Users can check their current account balance, helping them track their
  financial status at any time.
- **Change Interest Rate for Savings Accounts**: The system allows administrators to set and
  update interest rates for savings accounts, helping users grow their savings over time.
- **Update Plan**: The system allows users to have a plan for their accounts : standard/student/silver/gold

### Card Management:
- **Card Creation**: Users can create one-time use or classic cards. Each card has an ID and an
  associated status (active or blocked) and type (`one-time` or `classic`).
- **Check Card Status**: Users can verify the status of a specific card to ensure it is usable for
  transactions. Cards can be blocked if necessary for security reasons.

### Transaction History:
- The system maintains a comprehensive history of all transactions for each account, including
- deposits, withdrawals, online payments, and money transfers. Users can view their transaction
- history to track their financial activity and understand their spending patterns.

### Online Payments:
- Users can make online payments directly from their accounts. This functionality supports seamless
  transactions with merchants and ensures payments are processed only if there is sufficient balance
  in the account. Depending on their plan the user must pay comisions
- The commerciant is giving a cashback depending on the user plan and also the type of cashback the commerciant
choose

### Money Transfers:
- Users have the ability to transfer money between different bank accounts. The system checks that
  both the source and destination accounts exist and that the transfer amount does not exceed the
  available balance in the source account. This functionality supports inter-account transfers and
  enables users to move funds between their accounts or to other users.

### Split Payment:
- Users can split a payment across multiple bank accounts. This feature allows users to allocate
  parts of a payment to different accounts, facilitating flexible budgeting and spending management
  The interesting part is that the users also need to accept/reject the payment.

### Cash withdraw:
- Users can withdraw money from a ATM in Romania the machine is always giving them RON.

## Design Patterns

- **Factory Pattern for Accounts**:
    - A **factory pattern** is used to create instances of `Account`. This pattern abstracts the creation
  process and ensures that the right type of account is created based on specific criteria.
    - For instance, the `AccountFactory` can produce both savings and classic accounts depending on
  the user's requirements. This ensures flexibility and maintains clean separation of concerns in the
  account creation process.

- **Strategy Pattern for Cards**:
    - A **strategy pattern** is used for defining different behaviors of cards
    (such as one-time or classic cards).
        - The `Card` class delegates its behavior to a `CardStrategy` interface, which defines
        the specific functionality of the card.
        - Concrete classes implementing the `CardStrategy` interface, such as `OneTimeCardStrategy`
        and `ClassicCardStrategy`, encapsulate the card's unique behaviors.
        - The `CardFactory` creates a `Card` object and assigns the appropriate `CardStrategy`
        based on user requirements, allowing dynamic changes in card behavior without altering the core `Card` class.

By combining these patterns, the design achieves high cohesion and low coupling, making it easier to extend or modify both accounts and cards independently.

- **Command Pattern for Commands**:
    - The `Command Pattern` is used to encapsulate the different commands (like creating an account,
      generating a card, processing a payment, and transferring money) into command objects. Each
      command object executes a specific action on the bank system. This pattern makes the system
      extensible and allows for easy addition of new commands without changing the existing ones.
    - For example, a `CreateAccountCommand` object would encapsulate the details of account creation,
      validating the type and details before creating the account.

- **Database Management**:
    - **Singleton Instance**: A singleton instance is used for managing the database
      (`Database db = new Database();`). This ensures there is only one instance of the database across
      the application, preventing duplication and potential issues with data management.
    - **User Inputs**: An array list (`ArrayList<UserInput> users`) is used to store user input
      data. This list tracks all user activities and interactions with the banking system.
    - **Accounts**: An array list (`ArrayList<ArrayList<AccountType>> accounts`) holds multiple lists
      of accounts, categorized by account type (`savings`, `classic`). Each list contains accounts
      specific to that type.
    - **Aliases**: An array list (`ArrayList<Alias> aliases`) is used to manage aliases for account
      holders, providing an additional layer of identification for users.
    - **Exchange Inputs**: A static array list (`ArrayList<ExchangeInput> exchange`)
      maintains data related to currency exchange transactions.

```java
private static Database db = new Database();

private ArrayList<UserInput> users = new ArrayList<>();
private ArrayList<ArrayList<AccountType>> accounts = new ArrayList<>();
private ArrayList<Alias> aliases = new ArrayList<>();
private ArrayList<CommerciantInput> commerciants = new ArrayList<>();
private ArrayList<CommandInput> commands = new ArrayList<>();
private static ArrayList<ExchangeInput> exchange = new ArrayList<>();