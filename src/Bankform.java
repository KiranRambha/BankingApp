package bankform;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author kiran
 */
public class Bankform {
    private Connection con;
    private String url;
    private String serverName;
    private String portNumber;
    private String dataBase;
    private String userName;
    private String password;
    private String sql;
    private int accountNumber;
    private String customername = "";
    private String customeraddress;
    private int customerphone;
    private int branchid;
    private String sex;
    private double accountbalance;
    private double loanbalance;
    private ResultSet rs, rs1;
    private String branchaddress;
    
   
    public Bankform(){
        url = "jdbc:postgresql://";
        serverName = "localhost";
        portNumber = "5432";
        dataBase = "bank";
        userName = "postgres";
        password = "postgres";
    }
    
    /**
     * 
     * @return returns the connection url
     */
    private String getConnectionUrl(){
        return url + serverName + ":" + portNumber + "/" + dataBase;
    }
    
    /**
     * 
     * @return Loads the driver makes the connection and returns it
     */
    private Connection getConnection(){
        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(getConnectionUrl(), userName, password);
            if(con != null){
                System.out.println("Connection successful");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error trace in getConnection() : " + e.getMessage());
        }
        return con;
    }
    
    /**
     * 
     * @param customername to send the customer name to the database aquired from the form
     * @param customeraddress to send the customer address to the database aquired from the form
     * @param phone to send the phone number to the database aquired from the form
     * @param branchname to send the branch name to the database aquired from the form 
     * @param sex to send the customer sex to the database aquired from the form
     * @param accountbalance to send the account balance to the database aquired from the form
     * @param loanbalance to send the loan balance to the database aquired from the form
     * @return returns weather the update was successful or not
     */
    public String input(String customername, String customeraddress, Long phone, String branchname, String sex, double accountbalance, double loanbalance){
        String s = "Unable To Update";
        int code = getBranchCode(branchname), id = nextId();
        id++;
        try {
            con = getConnection();
            sql = "insert into account(id, customername, address, phone, branchid, sex, accountbalance, loanbalance) values(?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement pstmt = con.prepareStatement(sql);
            
            //TODO get the values from the form and insert into the database
            pstmt.setInt(1, id);
            pstmt.setString(2, customername);
            pstmt.setString(3, customeraddress);
            pstmt.setLong(4, phone);
            pstmt.setInt(5, code);
            pstmt.setString(6, sex);
            pstmt.setDouble(7, accountbalance);
            pstmt.setDouble(8, loanbalance);
            pstmt.executeUpdate();
            con.close();
            s = "Update Successful";
            System.out.println(s);
        } catch (SQLException ex) {
            System.out.println(ex);
            System.out.println(s);
        }
        return s;
    }
    
    /**
     * 
     * @param accountnumber sending in the account number aquired from the form to get the bank detail of this account number
     */
    public void output(int accountnumber){
        try{
            con = getConnection();
            sql = "select * from account, branch where account.branchid = branch.code and account.id = " + accountnumber + ";";
            PreparedStatement pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            con.close();
            while(rs.next()){
                this.accountNumber = rs.getInt(1);
                this.customername = rs.getString(2);
                this.customeraddress = rs.getString(3);
                this.customerphone = (int) rs.getLong(4);
                this.branchid = rs.getInt(5);
                this.sex = rs.getString(6);
                this.accountbalance = rs.getFloat(7);
                this.loanbalance = rs.getFloat(8);
                this.branchaddress = rs.getString(10);
            }
        }catch (SQLException e){
            System.out.println(e);
        }
    }
    
    /**
     * 
     * @return returns the maximum account number in the database
     */
    public int nextId(){
        int i = 0;
        try{
            con = getConnection();
            PreparedStatement pstatement = con.prepareStatement("select max(id) from account;");
            rs = pstatement.executeQuery();
            while(rs.next()){
                i = rs.getInt(1);
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        return i;
    }
    
    /**
     * 
     * @return returns the account number
     */
    public int getAccountNumber(){
        return accountNumber;
    }
    
    /**
     * 
     * @return returns the customer name
     */
    public String getCustomerName(){
        return customername;
    }
    
    /**
     * 
     * @return returns the customer address
     */
    public String getCustomerAddress(){
        return customeraddress;
    }
    
    /**
     * 
     * @return returns the customer phone number
     */
    public int getCustomerPhone(){
        return customerphone;
    }
    
    /**
     * 
     * @return returns the branch id
     */
    public int getBranchId(){
        return branchid;
    }
    
    /**
     * 
     * @return returns the branch address
     */
    public String getBranchAddress(){
        return branchaddress;
    }
    
    /**
     * 
     * @return returns the sex of the customer
     */
    public String getSex(){
        return sex;
    }
    
    /**
     * 
     * @return returns the account balance
     */
    public double getAccountBalace(){
        return accountbalance;
    }
    
    /**
     * 
     * @return returns the loan balance
     */
    public double getLoanBalance(){
        return loanbalance;
    }
    
    /**
     * 
     * @param branchaddress we send in the branch address in to get the branch id of it
     * @return returns the branch id of the branch address sent in
     */
    public int getBranchCode(String branchaddress){
        int code = 0;
        try{
            con = getConnection();
            PreparedStatement pstmt1 = con.prepareStatement("Select branch.code from branch where branch.address = \'" + branchaddress + "\';");
            rs1 = pstmt1.executeQuery();
            while(rs1.next()){
                code = rs1.getInt(1);
            }
        }catch(SQLException e){
            System.out.println(e);
        }
        return code;
    }
    
    /**
     * 
     * @return returns the total value of all the banks combined
     */
    public ResultSet getBankValue(){
        ResultSet resultset = null;
        Double sum = 0.0;
        try{
            con = getConnection();
            PreparedStatement s = con.prepareStatement("select sum(accountbalance) from account;");
            resultset = s.executeQuery();
        }catch (SQLException e){
            System.out.println(e);
        }
        return resultset;
    }
    
    /**
     * 
     * @return returns the amount of money each bank holds 
     */
    public String eachBankDetail(){
        ResultSet resultset = null;
        String s = "";
        try{
            con = getConnection();
            PreparedStatement s1 = con.prepareStatement("select branch.address, sum(accountbalance) from branch, account where branch.code = account.branchid group by branch.address;");
            resultset = s1.executeQuery();
            while(resultset.next()){
                s += resultset.getString(1) + " : $" + Math.round(resultset.getDouble(2)) + "\n";
            }
        }catch (SQLException e){
            System.out.println(e);
        }
        return s;
    }
}
    

