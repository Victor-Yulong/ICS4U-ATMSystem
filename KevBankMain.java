import java.util.Scanner;

public class KevBankMain {
  public static void main(String[] args) {
    // Declaration of variables
    KevBank kb = new KevBank();
    kb.ImportFileToDB();
    Scanner input = new Scanner(System.in);
    
    int menuOption, acctOption, currLoginId;
    String retypePass;
    boolean isRunning = true;
    
    // Start running MAIN MENU loop until user calls to exit (option 3)
    while(isRunning) {
      
      // Display main menu
      System.out.println("\n|====================================|");
      System.out.println("|         WELCOME TO KEVBANK         |");
      System.out.println("|====================================|");
      System.out.println("1. New User");
      System.out.println("2. Returning User");
      System.out.println("3. Exit");
      System.out.print("Pick an option:");
      menuOption = input.nextInt();
      input.nextLine();
      
      switch(menuOption) {
        
        case 1:      // 1.  ---------- New User Account Registration ----------
          int newId;
          newId = kb.GetNextNewId();
          
          // User input new account information
          System.out.println("\n|------------------------------------|");
          System.out.println("|              NEW USER              |");
          System.out.println("|------------------------------------|");
          System.out.println("Account ID      : " + newId);
          System.out.print("Your name       :");
          String name = input.nextLine();
          System.out.print("New password    :");
          String password = input.nextLine();
          System.out.print("Retype password :");
          retypePass = input.nextLine();
          System.out.print("Initial Balance :");
          double initialBal = input.nextDouble();
          input.nextLine();
          
          // Create, store new account and display warning when there is error
          newId = kb.CreateNewAcct(name, password, retypePass, initialBal);
          if (newId == KevBank.INVALID_PASSWORD_LENGTH) {
            System.out.println("Your new password is not 5 digits, please try again!");
          }
          else if (newId == KevBank.PASSWORD_MISMATCH) {
            System.out.println("Retyped password does not match the original one, please try again!");
          }
          else if (newId == KevBank.INVALID_AMOUNT) {
            System.out.println("Initial deposit can not be negative, please try again!");
          }
          else {
            System.out.println("Successfully created account " + "'" + newId + "'.");
          }
          break;
          
        case 2:       // 2.  ---------- Returning User Login ----------
          int loginResult;
          
          // User input his/her account information
          System.out.println("\n|------------------------------------|");
          System.out.println("|        RETURNING USER LOGIN        |");
          System.out.println("|------------------------------------|");
          System.out.print("Account ID:");
          currLoginId = input.nextInt();
          input.nextLine();
          System.out.print("Password  :");
          password = input.nextLine();
          
          // Check user login result and display warning when there is error
          loginResult = kb.Login(currLoginId, password);
          if (loginResult == KevBank.ACCT_NOT_FOUND) {
            System.out.println("Account ID does not exist, please login again!");
          }
          else if (loginResult == KevBank.WRONG_PASSWORD) {
            System.out.println("The password is wrong, please login again!");
          }
  
          // If user logged in successfully, go into inner (Returning User Login) loop 
          else {
            System.out.println("Successfully logged in.");
            System.out.println("\nWelcome back, " + kb.GetAcctName() + "!");
            
            // Start running RETURNING USER ACCOUNT OPTION MENU loop until user calls to exit (option 6)
            boolean isLoggedIn = true;
            while (isLoggedIn) {
              
              // Display ACCOUNT OPTIONS and let user choose service #
              System.out.println("============================================");
              System.out.println("|              ACCOUNT OPTIONS             |");
              System.out.println("============================================");
              System.out.println("|  1. ACCOUNT INFO   |  4. TRANSFER        |");
              System.out.println("|  2. DEPOSIT        |  5. CHANGE PASSWORD |");
              System.out.println("|  3. WITHDRAW       |  6. LOGOUT          |");
              System.out.println("============================================");
              System.out.print("Pick an option:");
              acctOption = input.nextInt();
              input.nextLine();
            
              switch (acctOption) {
                
                case 1:       // 1.  ---------- ACCOUNT INFO ----------
                  int numHist;
                  
                  // Display user account info and transaction history
                  System.out.println("\n====================================");
                  System.out.println("|       ACCOUNT DETAIL INFO        |");
                  System.out.println("====================================");
                  System.out.printf("%-10s:%19d\n", "Account ID", currLoginId);
                  System.out.printf("%-10s:%19s\n", "Name", kb.GetAcctName());
                  System.out.printf("%-10s:%3s%16.2f\n\n", "Balance", "RM", kb.GetAcctBal());
                  System.out.println("Transaction History");
                  System.out.println("-----------------------------------");
                  
                  // Access the number of logged-in user account history
                  numHist = kb.NumHistory(currLoginId);
                  if (numHist == 0) {
                    System.out.println("No transaction history at the moment!\n");
                  }
                  // If there is history, display one by one
                  else {
                    for (int i = 0; i < numHist; i++) {
                      System.out.printf("%s%20.2f\n", kb.GetHistDateAt(currLoginId, i), kb.GetHistAmountAt(currLoginId, i));
                    }
                    System.out.println("");
                  }
                  break;
                         
                case 2:         // 2.  ---------- DEPOSIT ----------
                  double depositAmt;
                  int depositResult;
                  
                  // User input deposit amount
                  System.out.println("\n====================================");
                  System.out.println("|             DEPOSIT              |");
                  System.out.println("====================================");
                  System.out.print("Enter amount to deposit:");
                  depositAmt = input.nextDouble();
                  input.nextLine();
                  // Execute deposit transcation and display result if valid
                  depositResult = kb.Deposit(currLoginId, depositAmt); 
                  if (depositResult == KevBank.ACCT_NOT_FOUND) {
                    System.out.println("Account ID does not exist!\n");
                  }
                  else if (depositResult == KevBank.INVALID_AMOUNT) {
                    System.out.println("You can't deposit negative or 0 amount, please try again!\n");
                  }
                  else {
                    System.out.printf("%s%.2f\n", "Successfully deposited rm ", depositAmt);
                    System.out.println("");
                  }
                  break;
                  
                case 3:         // 3.  ---------- WITHDRAW ----------
                  double withdrawAmt;
                  int withdrawResult;
                  
                  // User input withdraw amount
                  System.out.println("\n====================================");
                  System.out.println("|             WITHDRAW             |");
                  System.out.println("====================================");
                  System.out.print("Enter amount to withdraw:");
                  withdrawAmt = input.nextDouble();
                  input.nextLine();
                  // Execute withdraw transaction and display result if valid
                  withdrawResult = kb.Withdraw(currLoginId, withdrawAmt);
                  if (withdrawResult == KevBank.ACCT_NOT_FOUND) {
                    System.out.println("Account ID does not exist, please try again!\n");
                  }
                  else if (withdrawResult == KevBank.INVALID_AMOUNT) {
                    System.out.println("You can't withdraw negative or 0 amount, please try again!\n");
                  }
                  else if (withdrawResult == KevBank.NO_ENOUGH_MONEY) {
                    System.out.println("Your account money is insufficient, please try again!\n");
                  }
                  else {
                    System.out.printf("%s%.2f\n", "Successfully withdrawn rm ", withdrawAmt);
                    System.out.println("");
                  }
                  break;
                  
                case 4:         // 4.  ---------- TRANSFER ----------
                  int destId, transResult;
                  double transAmt;
                  
                  // User input transfer amount
                  System.out.println("\n====================================");
                  System.out.println("|             TRANSFER             |");
                  System.out.println("====================================");
                  System.out.print("Enter destination account ID :");
                  destId = input.nextInt();
                  System.out.print("Enter amount to transfer     :");
                  transAmt = input.nextDouble();
                  input.nextLine();
                  // Execute transfer transaction and display result if valid
                  transResult = kb.Transfer(destId, transAmt);
                  if (transResult == KevBank.ACCT_NOT_FOUND) {
                    System.out.println("Account ID does not exist, please try again!\n");
                  }
                  else if (transResult == KevBank.INVALID_AMOUNT) {
                    System.out.println("You can't transfer negative or 0 amount, please try again!\n");
                  }
                  else if (transResult == KevBank.NO_ENOUGH_MONEY) {
                    System.out.println("Your account money is insufficient, please try again!\n");
                  }
                  else if (transResult == KevBank.DEST_ACCT_NOT_FOUND) {
                    System.out.println("The destination account doesn't exist, please try again!\n");
                  }
                  else if (transResult == KevBank.TWO_ACCT_MISMATCH) {
                    System.out.println("You can't transfer money to yourself, please try again!\n");
                  }
                  else {
                    System.out.printf("%s%.2f%s%d%s\n", "Successfully transferred rm ", transAmt, " to account '", destId, "'");
                    System.out.println("");
                  }
                  break;
                  
                case 5:         // 5.  ---------- CHANGE PASSWORD ----------
                  String oldPassword, newPassword;
                  int passChangeResult;
                  
                  // User change password
                  System.out.println("\n====================================");
                  System.out.println("|          CHANGE PASSWORD         |");
                  System.out.println("====================================");
                  System.out.print("Old Password :");
                  oldPassword = input.nextLine();
                  System.out.print("New Password :");
                  newPassword = input.nextLine();
                  System.out.print("Retype Password :");
                  retypePass = input.nextLine();
                  // Store the new password and display result if valid
                  passChangeResult = kb.ChangePass(oldPassword, newPassword, retypePass);
                  if (passChangeResult == KevBank.WRONG_PASSWORD) {
                    System.out.println("The old password is wrong, please try again!\n");
                  }
                  else if (passChangeResult == KevBank.PASSWORD_MISMATCH) {
                    System.out.println("The password you retyped does not match the original one, please try again!\n");
                  }
                  else {
                    System.out.println("Successfully changed the password.\n");
                  }
                  break;
                  
                case 6:          // 6.  ---------- LOGOUT ----------
                  kb.Logout();
                  isLoggedIn = false; // Modify the sentinel value and break the inner loop (Returing User Account)
                  break;
                   
                // Inner loop (Returning User Account) erorr-checking
                default:
                  System.out.println("Invalid option, please select again among 1-6.\n");
              }
            }
          }
            break;

            case 3:     // 3.  ---------- Exit ----------
              System.out.println("Thank you for using KevBank!");
              isRunning = false; // Modify the sentinel value, break the outer loop (Main Menu), and exit the program
              break;

            // Outer loop (Main Menu) error-checking
            default:
              System.out.println("Invalid option, please select again among 1-3.\n");
          }
      }
      input.close();
    }
  }