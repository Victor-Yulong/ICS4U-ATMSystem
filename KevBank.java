/* Developed By: Kevin Wong
 * Revised Date: Mar 3, 2026
 * Version     : 1.0 */
import java.text.SimpleDateFormat;  
import java.text.DateFormat;
import java.util.Date;

public class KevBank {
  // (Put your Global variables here)
  public final static String IMPORT_FILENAME       = "KevBankImport.txt";
  public final static int PASSWORD_LENGTH          =  5;
  public final static int SUCCESS                  =  1;
  public final static int ACCT_NOT_FOUND           = -1;
  public final static int INVALID_PASSWORD_LENGTH  = -2;
  public final static int WRONG_PASSWORD           = -3;
  public final static int PASSWORD_MISMATCH        = -4;
  public final static int INVALID_AMOUNT           = -5;
  public final static int NO_ENOUGH_MONEY          = -6;
  public final static int DEST_ACCT_NOT_FOUND      = -7;
  public final static int TWO_ACCT_MISMATCH        = -8;
  
  // DO NOT MODIFY THESE VARIABLE!
  // Provided Instance variables 
  int currLoginId;                             // Keeps track of the currently logged in user's account ID
  // If no user is logged in, it will have a value of 0.
  private Database db;                         // Database object. DO NOT MODIFY THIS VARIABLE!
  
  //============= CONSTRUCTOR =============//
  /* Constructor the KevBank object */
  public KevBank() {
    db = new Database();
    currLoginId = 0;
  }
  
  //=========== PRIVATE METHOD ==========//
  
  /* Get history String at the specified position
   * Example:
   * [ID],[NAME],[PASSWORD],[BALANCE],[HISTORY1:AMOUNT],[HISTORY2:AMOUNT],[HISTORY3:AMOUNT]
   * Then [HISTORY1] is pos = 0
   *      [HISTORY2] is pos = 1
   *      [HISTORY3] is pos = 2
   * @param id  - The account ID
   * @param pos - The position in the history String
   * @return    - Returns the history String at the specified history-String position. 
   *              Returns null if ID doesn't exist, pos is invalid, or history at pos
   *                      is empty.  */ 
  private String getHistStrAt(int id, int pos) {
    // Get logged-in user account history, and split it to array
    String acctHist = db.GetAcctById(id);
     if (acctHist == null) {
       return null;
     }
     String[] acctHistArr = acctHist.split(",");
     // Find the specified info position, and return if valid
     int targetIndex = Database.ACCT_HIST_START_POS + pos;
     if (targetIndex > acctHistArr.length - 1) {  // Check if the target index is within the array range
       return null;
     }
     if (acctHistArr[targetIndex].equals(" ")) {  // A single space " " means there¡¯s no history at the position
      return null; 
    }
     return acctHistArr[targetIndex];
  }
  
  /* Get today's date
   * @return - today's date in the form of "yyyy-MM-dd".  */
  private String getTodayDate() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
    Date date = new Date(); 
    return dateFormat.format(date);
  }
  
  /* Update transaction history array: move each block of history information
   * to next block, and insert the new history at pos 0.
   * Example:
   * [ID],[NAME],[PASSWORD],[BALANCE],[HISTORY1:AMOUNT],[HISTORY2:AMOUNT],[HISTORY3:AMOUNT]
   * Then [HISTORY1] is pos = 0
   *      [HISTORY2] is pos = 1
   *      [HISTORY3] is pos = 2
   * @param id  - The account ID
   * @param newAmt - The new amount to be updated
                  (positive for deposit, negative for withdrawal or transfer)  */
  private void updateTransHist(int id, double newAmt) {
    // Use for-loop to shift history record by 1 position (eg: [HISTORY 0] to [HISTORY 1], [HISTORY 1] to [HISTORY 2])
    for (int i = Database.NUM_HISTORY - 1; i > 0; i--) {
      String histDate = GetHistDateAt(id, (i - 1));
      if (histDate != null) {
        double histAmt = GetHistAmountAt(id, (i - 1));
        db.SetHistAt(id, i, histDate, histAmt);
      }
    }
    // Store & Replace the newest information at [HISTORY 0] 
    db.SetHistAt(id, 0, getTodayDate(), newAmt);
  }
  
  //=========== PUBLIC METHOD ===========//
  /* PROVIDED METHOD 
   * Import the dummy data from the textfile into the database, so that
   * you can have some data to work with while working on other
   * parts of the program.
   * @return - The number of accounts that was imported from
   *           the textfile. */
  public int ImportFileToDB(){
    return db.ImportAcct( IMPORT_FILENAME );
  }
  
  /* Create a new account in the database
   * @param name       - The name for the new account 
   * @param pass       - The password for the new account
   * @param retypePass - The retyped password for verification
   * @param bal        - The initial balance for the new account
   * @return           - Returns the new account ID.
   *                     Returns -2 if the password is not 5 digits (characters)
   *                     Returns -4 if retype password doesn't match the original password
   *                     Returns -5 if balance is negative */
  public int CreateNewAcct(String name, String pass, String retypePass, double bal) {
    // Check if input from front-end is valid
    if (pass.length() != PASSWORD_LENGTH) {
      return INVALID_PASSWORD_LENGTH;
    }
    if (!retypePass.equals(pass)) {
      return PASSWORD_MISMATCH;
    }
    if (bal < 0) {
      return INVALID_AMOUNT;
    }
    // Create and store new account in database and set up initial balance
    int newId = db.CreateNewAcct(name, pass);  
    db.SetAcctBal(newId, bal);
    return newId;
  }
  
  /* Get the next new ID that will be used for a new account 
   * @return - The new account ID */
  public int GetNextNewId() {
    return Database.INITIAL_ID + db.NumAcct();
  }
  
  /* Login into the system
   * @param id   - The account ID 
   * @param pass - The password 
   * @return     - Returns 1 if login successfully. 
   *               Returns -1 if account ID doesn't exist
   *               Returns -3 if password is wrong */     
  public int Login(int id, String pass) {
    // Check if input from front-end is valid
    boolean isIdExist = db.IsAcctExist(id);
    if (!isIdExist) {
      return ACCT_NOT_FOUND;
    }
    String corrPassword = db.GetAcctPass(id);
    if (!pass.equals(corrPassword)) {
      return WRONG_PASSWORD;
    }
    currLoginId = id;   // set user's curent login id to instance variable for using it afterward
    return SUCCESS;
  }
  
  /* Logout the currently logged-in's account from
   * the system. */
  public void Logout() {
    currLoginId = 0;   // If no user is logged in, it will have a value of 0.
  }
  
  /* Get the currently logged-in account's name
   * @return - Returns name of the currently logged in account
   *         - Returns null otherwise. */
  public String GetAcctName() {
    // Split the whole info String for the logged-in user account and access the position of account name
    String userInfo = db.GetAcctById(currLoginId);
    if (userInfo != null) {
      String[] userInfoArr = userInfo.split(",");
      return userInfoArr[Database.ACCT_NAME_POS];
    }
    return null;
  }
  
  /* Get the currently logged-in account's balance
   * @return - Returns the balance in the account.
   *         - Returns -1 if ID doesn't exist. */
  public double GetAcctBal() {
    return db.GetAcctBal(currLoginId);  
  }
  
  /* Get the number of history the logged-in account has
   * @return - Returns the number of history if successful. 
   *           Returns -1 if account ID is not found in
   *                   the database. */
  public int NumHistory( int id ) {
    int count = 0;   // Variable for tracking the number of history
    // Check if account history exists
    String acctHist = db.GetAcctById(id);
    if (acctHist == null) {
      return ACCT_NOT_FOUND;
    }
    // Check each history block after splitting the info String. Add 1 to 'count' variable if it contains data.
    String[] histArr = acctHist.split(",");
    for (int i = Database.ACCT_HIST_START_POS; i < histArr.length; i++) {
      if (!histArr[i].equals(" ")) {   // A single space " " means there¡¯s no history at the position
        count++;
      }
    }
      return count; 
  }
  
  /* Get the history 'date' at the specified position
   * Example:
   * [ID],[NAME],[PASSWORD],[BALANCE],[HISTORY1:AMOUNT],[HISTORY2:AMOUNT],[HISTORY3:AMOUNT]
   * Then [HISTORY1] is pos = 0
   *      [HISTORY2] is pos = 1
   *      [HISTORY3] is pos = 2
   * @param id  - The account ID 
   * @param pos - The position in the History-String
   * @return    - Returns the 'DATE' at the specified History-String position. 
   *              Returns null if ID doesn't exist, pos is invalid, or history at pos
   *                      is empty. */ 
  public String GetHistDateAt(int id, int pos ) {
    // Use helper method to get the history String at the specified position
    String histStr = getHistStrAt(id, pos);
    if (histStr == null) {
      return null;
    }
    // Split the String and return the date at the date position
    String[] dateHisArr = histStr.split(":");
    return dateHisArr[Database.HIST_DATE_POS];
  }
  
  /* Get the history 'amount' at the specified position
   * Example:
   * [ID],[NAME],[PASSWORD],[BALANCE],[HISTORY1:AMOUNT],[HISTORY2:AMOUNT],[HISTORY3:AMOUNT]
   * Then [HISTORY1] is pos = 0
   *      [HISTORY2] is pos = 1
   *      [HISTORY3] is pos = 2
   * @param id  - The account ID
   * @param pos - The position in the History-String
   * @return    - Returns 'AMOUNT' at the specified History-String position */
  public double GetHistAmountAt(int id, int pos ){
    // Use helper method to get the history String at the specified position
    String histStr = getHistStrAt(id, pos);
    // Split the String and return the amount at the amount position
    String[] amtHistArr = histStr.split(":");
    return Double.parseDouble(amtHistArr[Database.HIST_AMT_POS]);
  }
  
  /* Depositing money into an account.
   * @param id  - The account ID
   * @param amt - The amount to be deposited into the account
   * @return    - Returns 1 if deposit successfully.
   *              Returns -1 if account ID does not exist.
   *              Returns -5 if amount depositing is invalid (negative and zero) */  
  public int Deposit(int id, double amt ){
    // Check if input from front-end is valid
    boolean isIdExist = db.IsAcctExist(id);
    if (!isIdExist) {
      return ACCT_NOT_FOUND;
    }
    if (amt <= 0) {
      return INVALID_AMOUNT;
    }
    // Get current account balance before deposit, and set new balance
    double currentBal = db.GetAcctBal(id);
    db.SetAcctBal(id, (currentBal + amt));
    updateTransHist(id, amt);  // Use helper method to update transaction history
    return SUCCESS;
  }
  
  /* Withdrawing money from an account.
   * @param id  - The account ID
   * @param amt - The amount to be withdrawn from the account
   * @return    - Returns 1 if withdraw successfully.
   *              Returns -1 if account ID does not exist.
   *              Returns -5 if amount withdrawing is invalid (negative and zero)
   *              Returns -6 if account does not have enough money */
  public int Withdraw( int id, double amt ) {
    // Check if input from front-end is valid
    boolean isIdExist = db.IsAcctExist(id);
    if (!isIdExist) {
      return ACCT_NOT_FOUND;
    }
    if (amt <= 0) {
      return INVALID_AMOUNT;
    }
    // Get current account balance before withdrawing and check if it's insufficient
    double currentBal = db.GetAcctBal(id);
    if (currentBal < amt) {
      return NO_ENOUGH_MONEY;
    }
    // Subtract the amount and update transcation history
    db.SetAcctBal(id, (currentBal - amt));
    updateTransHist(id, (-amt));  // Use helper method to update transaction history. '-amt' means negative transaction record
    return SUCCESS;
  }
  
  /* Transfering money from an origin account to a destination account.
   * @param destId  - The destination account ID that is transferring to
   * @param amt     - The amount to be transferred
   * @return        - Returns 1 if transferred successfully.
   *                  Returns -1 if origin account ID (Currently logged-in account) does not exist.
   *                  Returns -5 if amount transferring is invalid (negative and zero) 
   *                  Returns -6 if origin account (Currently logged-in account) does not have enough money 
   *                  Returns -7 if destination account ID does not exist. 
   *                  Returns -8 if origin ID and destination ID are the same. */  
  public int Transfer( int destId, double amt ){
    // Check if input from front-end is valid for original account
    boolean isUserIdExist = db.IsAcctExist(currLoginId);
    if (!isUserIdExist) {
      return ACCT_NOT_FOUND;
    }
    // Check if input from front-end is valid for destination account
    boolean isDestIdExist = db.IsAcctExist(destId);
    if (!isDestIdExist) {
      return DEST_ACCT_NOT_FOUND;
    }
    // Check if original ID and destination ID are the same
    if (currLoginId == destId) {
      return TWO_ACCT_MISMATCH;
    }
    if (amt <= 0) {
      return INVALID_AMOUNT;
    }
    double userAcctBal = db.GetAcctBal(currLoginId); 
    if (userAcctBal < amt) {
      return NO_ENOUGH_MONEY;
    }
    double destAcctBal = db.GetAcctBal(destId); // Get destination account balance
    // Subtract the amount from the original account and update transcation history
    db.SetAcctBal(currLoginId, (userAcctBal - amt));
    updateTransHist(currLoginId, (-amt));  // '-amt' means negative transaction record
    // Add the amount to destination account and update transcation history
    db.SetAcctBal(destId, (destAcctBal + amt));
    updateTransHist(destId, amt);
    return SUCCESS;
  }
  
  /* Change the password for the logged in user's account
   * @param oldPass    - The account's original password
   * @param newPass    - The password the user wants to change to
   * @param retypePass - The retyped password to verify that it is correct
   * @return           - Returns 1 if password changes successful                     
   *                     Returns -3 if original password is incorrect 
   *                     Returns -4 if retyped password is incorrect */
  public int ChangePass(String oldPass, String newPass, String retypePass) {
    // Check if old/new password are valid
    if (!oldPass.equals(db.GetAcctPass(currLoginId))) {
      return WRONG_PASSWORD;
    }
    if (!retypePass.equals(newPass)) {
      return PASSWORD_MISMATCH;
    }
    // Store & Set new password in the database
    db.SetAcctPass(currLoginId, newPass);
    return SUCCESS;
  }        
        
  /* FOR TESTING PURPOSES */
  /* Displays all the accounts and its information
   * on the screen. */
  public void DisplayAllAcct() {
    db.DisplayAllAcct();
  }
}