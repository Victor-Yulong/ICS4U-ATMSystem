/* Developed By: Kevin Wong
 * Revised Date: Mar 3, 2026
 * Version     : 1.0 */

/* DO NOT MODIFY THIS CLASS. USE IT AS IS! */
import java.util.Scanner;
import java.io.File;        
import java.io.IOException; 

public class Database {
   // Global Variables      
   public final static int ACCT_ID_POS           = 0;
   public final static int ACCT_NAME_POS         = 1;
   public final static int ACCT_PASSWORD_POS     = 2;
   public final static int ACCT_BAL_POS          = 3;
   public final static int ACCT_HIST_START_POS   = 4;
   
   public final static int HIST_DATE_POS         = 0;
   public final static int HIST_AMT_POS          = 1;
   public final static int NUM_HISTORY           = 3;
   
   public final static int INITIAL_ID            = 10000;
   
   // Instance Variables
   private String[] allAcct;
   
   //============ CONSTRUCTOR ============//
   /* Initialize a Database object */
   public Database() {
      allAcct = new String[0];
   }
      
   //=========== PUBLIC METHOD ===========//
   /* Import accounts into the database from a textfile.
    * @filename - The textfile that contains the accounts info to be imported.
    *             The textfile must be in the same folder as the code.
    * @return   - The number of accounts imported */ 
   public int ImportAcct(String filename) {
      int numNewAcct = 0;
      try {
         // pass the path to the file as a parameter 
         File file = new File(filename); 
         Scanner sc = new Scanner(file); 
         String infoStr;
         
         int latestId;
         if( allAcct.length > 0 ) {
            String lastAcctInfo = allAcct[ allAcct.length - 1 ];
            latestId = Integer.parseInt(lastAcctInfo.substring(0, lastAcctInfo.indexOf(",")));
         }
         else {
            latestId = INITIAL_ID;
         }
         
         while (sc.hasNextLine()) {
            infoStr = sc.nextLine();
            if( !infoStr.equals("") ) {
               String[] tempArr = new String[ allAcct.length + 1 ];
               for( int i = 0; i < allAcct.length; i++ ){
                  tempArr[i] = allAcct[i];
               }
               tempArr[ allAcct.length ] = (latestId + numNewAcct) + "," + infoStr;
               allAcct = tempArr;
               numNewAcct++;
            }
         }
         sc.close();
      }
      catch (IOException ex) {
         System.out.println("General I/O exception: " + ex.getMessage());
      }   
      return numNewAcct;
   }
 
   /* Create a new account in the Database. Will add a block inside the array that stores 
    * all the Info-String. The next account ID will be given to the new account 
    * and the new account will have a blanace of rm 0.00.
    * @param name - The name for the new account
    * @param pass - The password for the new account
    * @return     - Returns the account ID of created account */
   public int CreateNewAcct(String name, String pass) {
      int newId;
      String[] tempArr;
      if( allAcct.length == 0 ) {
         newId = INITIAL_ID;
      }
      else {
         String lastAcctInfo = allAcct[ allAcct.length - 1 ];
         newId = Integer.parseInt(lastAcctInfo.substring(0, lastAcctInfo.indexOf(","))) + 1; 
      }
      tempArr =  new String[ allAcct.length + 1 ];
      
      for( int i = 0; i < allAcct.length; i++ ) {
         tempArr[i] = allAcct[i];
      }
      
      tempArr[ allAcct.length ] = newId + "," + name + "," + pass + "," + "0.00, , , ";
      allAcct = tempArr;
      
      return newId;
   }
   
   /* Get the whole Info-String for the account at position 'pos'.
    * The string has the following format:
    * [ID],[NAME],[PASSWORD],[BALANCE],[HISTORY1:AMOUNT],[HISTORY2:AMOUNT],[HISTORY3:AMOUNT]
    * Example)
    * 10000,Kevin Wong,12345,116.10,2020-07-27:-50.0,2020-07-27:-25.0,2020-07-27:150.55
    * @param pos - The position in the array
    * @return    - Returns the string that contains all of the account's information at 
    *              position 'pos', if successful
    *              Returns null if pos doesn't exist. */
   public String GetAcctByPos( int pos ) {
      if( 0 <= pos && pos < allAcct.length ) {
         return allAcct[ pos ];
      }
      else {
         return null;
      }
   }   
 
   /* Get the whole Info-String for the account ID
    * The string has the following format:
    * [ID],[NAME],[PASSWORD],[BALANCE],[HISTORY1:AMOUNT],[HISTORY2:AMOUNT],[HISTORY3:AMOUNT]
    * Example)
    * 10000,Kevin Wong,12345,116.10,2020-07-27:-50.0,2020-07-27:-25.0,2020-07-27:150.55
    * @param id  - The account ID 
    * @return    - Returns the Info-String that contains all of the account's information
    *              Returns null if ID doesn't exist. */   
   public String GetAcctById( int id ) {
      String result = null;
      for( int i = 0; i < allAcct.length; i++ ) {
         String[] infoStr = allAcct[i].split(",");
         if( Integer.parseInt(infoStr[ACCT_ID_POS]) == id ) {
            result = allAcct[i];
            break;
         }
      }
      return result;
   }
   
   /* Get the number of accounts in the database
    * @return - The number of accounts */   
   public int NumAcct() {
      return allAcct.length;
   }   
   
   /* Check if the specified account ID exist in the database
    * @param id - The account ID that needs to be checked 
    * @return   - Returns true if the account ID exist
    *             Returns false otherwise. */
   public boolean IsAcctExist(int id) {
      boolean status = false;
      for( int i = 0; i < allAcct.length; i++ ) {
         String[] infoStr = allAcct[i].split(",");
         if( Integer.parseInt(infoStr[ACCT_ID_POS]) == id ) {
            status = true;
            break;
         }
      }
      return status;
   }    
   
   /* Get the password from the specified account id
    * @param id - The account ID that needs to be checked 
    * @return   - Returns the password for the specified account id if successful.
    *             Returns null if ID doesn't exist. */  
   public String GetAcctPass(int id) {
      String result = null;
      for( int i = 0; i < allAcct.length; i++ ) {
         String[] infoStr = allAcct[i].split(",");
         if( Integer.parseInt(infoStr[ACCT_ID_POS]) == id ) {
            result = infoStr[ACCT_PASSWORD_POS];
            break;
         }
      }   
      return result;      
   }
   
   /* Set a password in the specified account id
    * @param id     - The account ID that needs to be checked 
    * @param newBal - The new password for the specified account
    * @return       - Returns true if successful.
    *                 Returns false ID doesn't exist. */   
   public boolean SetAcctPass( int id, String newPass ) {
      boolean status = false;
      for( int i = 0; i < allAcct.length; i++ ) {
         String[] infoStr = allAcct[i].split(",");
         if( Integer.parseInt(infoStr[ACCT_ID_POS]) == id ) {
            String newInfo = "";
            for( int j = 0; j < infoStr.length; j++ ) {
               if( j != ACCT_PASSWORD_POS ) {
                  newInfo += infoStr[ j ];
               }
               else {
                  newInfo += newPass;
               }
               
               if( j < infoStr.length - 1 ) {
                  newInfo += ",";
               }
            }
            allAcct[i] = newInfo;
            status = true;
            break;
         }
      }   
      return status;
   }   
   
   /* Get the balance from the specified account id
    * @param id - The account ID that needs to be checked 
    * @return   - Returns the amount for the specified account id if successful.
    *             Returns -1 if ID doesn't exist. */
   public double GetAcctBal( int id ) {
      double result = -1;
      for( int i = 0; i < allAcct.length; i++ ) {
         String[] infoStr = allAcct[i].split(",");
         if( Integer.parseInt(infoStr[ACCT_ID_POS]) == id ) {
            result = Double.parseDouble( infoStr[ACCT_BAL_POS] );
            break;
         }
      }   
      return result;
   }   
   
   /* Set a balance in the specified account id
    * @param id     - The account ID that needs to be checked 
    * @param newBal - The new balance for the specified account
    * @return       - Returns true if successful.
    *                 Returns false if ID doesn't exist. */   
   public boolean SetAcctBal( int id, double newBal ) {
      boolean status = false;
      for( int i = 0; i < allAcct.length; i++ ) {
         String[] infoStr = allAcct[i].split(",");
         if( Integer.parseInt(infoStr[ACCT_ID_POS]) == id ) {
            String newInfo = "";
            for( int j = 0; j < infoStr.length; j++ ) {
               if( j != ACCT_BAL_POS ) {
                  newInfo += infoStr[ j ];
               }
               else {
                  newInfo += String.format("%.2f", Math.round(newBal * 100.0) / 100.0 ).toString();
               }
               
               if( j < infoStr.length - 1 ) {
                  newInfo += ",";
               }
            }
            allAcct[i] = newInfo;
            status = true;
            break;
         }
      }   
      return status;
   }
    
   /* Get account's History-String
    * Example:
    * [HISTORY1:AMOUNT],[HISTORY2:AMOUNT],[HISTORY3:AMOUNT]
    * @param id  - The account ID
    * @return    - Returns History-String if successful,
    *              Returns null if ID doesn't exist. */
   public String GetAllHistStr( int id ) {
      String result = null;
      for( int i = 0; i < allAcct.length; i++ ){
         String[] infoStr = allAcct[i].split(",", 5);
         if( Integer.parseInt( infoStr[ ACCT_ID_POS ] ) == id ) {
            // Search for the history specified at pos
            result = infoStr[ ACCT_HIST_START_POS ];
            break;
         }
      }   
      return result;
   }
   
   /* Get account's History-String at the specified position in the databases 
    * History-String section of the info-string acts similar to an array. 
    * Example:
    * [ID],[NAME],[PASSWORD],[BALANCE],[HISTORY1:AMOUNT],[HISTORY2:AMOUNT],[HISTORY3:AMOUNT]
    * Then [HISTORY1] is pos = 0
    *      [HISTORY2] is pos = 1
    *      [HISTORY3] is pos = 2
    * @param id  - The account ID
    * @param pos - The position in the history section that needs to be updated.
    * @return    - Returns History-String in the format [DATE:AMOUNT] if successful,
    *              Returns null if ID doesn't exist or pos is invalid. */
   public String GetHistAt( int id, int pos ) {
      if( pos < NUM_HISTORY ) {
         for( int i = 0; i < allAcct.length; i++ ){
            String[] infoStr = allAcct[i].split(",", 5);
            if( Integer.parseInt( infoStr[ ACCT_ID_POS ] ) == id ) {
               // Search for the history specified at pos
               String[] histInfoStr = infoStr[ ACCT_HIST_START_POS ].split(",");
               return histInfoStr[ pos ] ;
            }
         }
      }
      return null;
   } 
   
   /* Update account's History-String at the specified position in the databases 
    * History-String section of the Info-String acts similar to an array. 
    * Example:
    * [ID],[NAME],[PASSWORD],[BALANCE],[HISTORY1:AMOUNT],[HISTORY2:AMOUNT],[HISTORY3:AMOUNT]
    * Then [HISTORY1] is pos = 0
    *      [HISTORY2] is pos = 1
    *      [HISTORY3] is pos = 2
    * @param id      - The account that needs to be updated. 
    * @param pos     - The position in the History-String section that needs to be updated.
    * @param date    - The date in the format of 'yyyy-mm-dd'
    * @param amount  - The amount changed for the history.
    * @return        - Returns true if changed succesfully 
    *                  Returns false if acount ID doesn't exist. */
   public boolean SetHistAt( int id, int pos, String date, double amt ) {
      for( int i = 0; i < allAcct.length; i++ ){
         String[] infoStr = allAcct[i].split(",", 5);
         if( Integer.parseInt( infoStr[ ACCT_ID_POS ] ) == id ) {
            // Reconstruct Info String
            String[] histInfoStr = infoStr[ ACCT_HIST_START_POS ].split(",");
            String newHist = "";
            for( int j = 0; j < NUM_HISTORY; j++ ) {
               if( j == pos ) {
                  newHist += date + ":" + String.format("%.2f", amt).toString();
               }
               else {
                  newHist += histInfoStr[j];
               }
               
               if( j < NUM_HISTORY - 1 ) {
                  newHist += ",";
               }
            }
            
            // Update Info String History section 
            allAcct[i] = infoStr[0] + "," + infoStr[1] + "," + infoStr[2] + "," + 
                         infoStr[3] + "," + newHist;
            return true;
         }
      }
      return false;
   }   
   
   /* FOR TESTING PURPOSES */
   /* Displays all the accounts and its information
    * on the screen. */
   public void DisplayAllAcct() {
     for(int i = 0; i < allAcct.length; i++ ) {
       System.out.println( allAcct[i] );
     }
   }  
}