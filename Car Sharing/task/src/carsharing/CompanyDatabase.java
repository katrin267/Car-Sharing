package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDatabase {

    // JDBC driver name and database URL
    private final String JDBC_DRIVER = "org.h2.Driver";
    private String DB_URL;
    private Connection conn = null;
    private Statement stmt = null;


    public CompanyDatabase(String dbName) {
        this.DB_URL = "jdbc:h2:file:C:/Users/Георгий/IdeaProjects/Car Sharing/Car Sharing/task/src/carsharing/db/" + dbName;
        createDatabase();
        createTableCompany();
        createTableCars();
        createTableCustomer();
    }

    private void createDatabase() {

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
//            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(true);
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }

    private void createTableCompany() {
        String sql = "CREATE TABLE IF NOT EXISTS COMPANY " +
                "(ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                " NAME VARCHAR(255) UNIQUE NOT NULL)";
        executeQuery(sql);
        sql = "ALTER TABLE COMPANY ALTER COLUMN ID RESTART WITH 1";
        executeQuery(sql);
    }

    private void createTableCars() {
        String sql = "CREATE TABLE IF NOT EXISTS CAR" +
                "(ID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "NAME VARCHAR(255) UNIQUE NOT NULL," +
                "COMPANY_ID INTEGER NOT NULL," +
                "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (ID))";
        executeQuery(sql);
        sql = "ALTER TABLE CAR ALTER COLUMN ID RESTART WITH 1";
        executeQuery(sql);
    }

    private void createTableCustomer() {
        String sql = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
                "(ID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "NAME VARCHAR(255) UNIQUE NOT NULL," +
                "RENTED_CAR_ID INTEGER," +
                "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR (ID))";
        executeQuery(sql);
//        sql = "ALTER TABLE CUSTOMER ALTER COLUMN ID RESTART WITH 2";
//        executeQuery(sql);
    }

    void addCompany(String companyName) {
        String sql = "INSERT INTO COMPANY (NAME) VALUES ( '" + companyName + "' )";
        executeQuery(sql);
        System.out.println("The company was created!");
    }

    void addCar(String carName, String companyId) {
        String sql = "INSERT INTO CAR (NAME, COMPANY_ID) VALUES ( '" + carName + "', " + companyId + " )";
        executeQuery(sql);
        System.out.println("The car was added!");
    }

    void addCustomer(String customerName) {
        String sql = "INSERT INTO CUSTOMER (NAME) VALUES ( '" + customerName + "')";
        executeQuery(sql);
        System.out.println("The customer was added!");
    }

    void rentCar(String carId, String customerId) {
        String sqlRent = "UPDATE CUSTOMER SET RENTED_CAR_ID = " + carId + " WHERE ID = " + customerId;
        executeQuery(sqlRent);
        System.out.println("You rented '" + getCarNameById(carId) + "'");
    }

    boolean isCarRented(String customerId) {
        String sql = "SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID = " + customerId;
        ResultSet rs = executeSelect(sql);
        try {
            rs.next();
            if (rs.getString("RENTED_CAR_ID") == null) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    void showRentedCar(String customerId) {
        String sql = "SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID = " + customerId;
        ResultSet rs = executeSelect(sql);
        try {
            while (rs.next()) {
                String carId = rs.getString("RENTED_CAR_ID");
                if (carId == null) {
                    System.out.println("You didn't rent a car!");
                    break;
                } else {

                    System.out.println("Your rented car:");
                    System.out.println(getCarNameById(carId));
                    System.out.println("Company:");
                    System.out.println(getCompanyByCarId(carId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void returnRentedCar(String customerId) {
        String sql = "SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID = " + customerId;
        ResultSet rs = executeSelect(sql);
        try {
            while (rs.next()) {
                if (rs.getString("RENTED_CAR_ID") == null) {
                    System.out.println("You didn't rent a car!");
                    break;
                } else {
                    String sqlReturn = "UPDATE CUSTOMER SET RENTED_CAR_ID = NULL WHERE ID = " + customerId;
                    executeQuery(sqlReturn);
                    System.out.println("You've returned a rented car!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean showCompaniesList() {
        String sql = "SELECT ID, NAME FROM COMPANY ORDER BY ID";
        ResultSet rs = executeSelect(sql);
        // Extract data from result set
        int i = 0;
        try {
            while (rs.next()) {
                if (i == 0) {
                    System.out.println("Choose the company:");
                }
                // Retrieve by column name
                int compId = rs.getInt("ID");
                String compName = rs.getString("NAME");

                // Display values
                System.out.println(compId + ". " + compName);
//                System.out.println((i + 1) + ". " + compName);
                i++;
            }
            rs.close();
            if (i == 0) {
                System.out.println("The company list is empty!");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean showCustomerList() {
        String sql = "SELECT ID, NAME FROM CUSTOMER ORDER BY ID";
        ResultSet rs = executeSelect(sql);
        // Extract data from result set
        int i = 0;
        try {
            while (rs.next()) {
                if (i == 0) {
                    System.out.println("Choose a customer:");
                }
                // Retrieve by column name
                int id = rs.getInt("ID");
                String custName = rs.getString("NAME");

                // Display values
                System.out.println(id + ". " + custName);
//                System.out.println((i + 1) + ". " + compName);
                i++;
            }
            rs.close();
            if (i == 0) {
                System.out.println("The customer list is empty!");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    List<Integer> showCarsList(String fkCompanyId) {
        String sqlRentedCars = "SELECT RENTED_CAR_ID FROM CUSTOMER";
        ResultSet rsRentedCars = executeSelect(sqlRentedCars);
        List<Integer> rentedIdList = new ArrayList<>();

        String sql = "SELECT ID, NAME FROM CAR WHERE COMPANY_ID = " + fkCompanyId + " ORDER BY ID";
        ResultSet rs = executeSelect(sql);
        List<Integer> indexList = new ArrayList<>();
        int i = 0;
        try {
            while (rsRentedCars.next()) {
                rentedIdList.add(rsRentedCars.getInt("RENTED_CAR_ID"));
            }
            while (rs.next()) {
                int carId = rs.getInt("ID");
                String carName = rs.getString("NAME");
                if (rentedIdList.contains(carId)) {
                    continue;
                }
                if (i == 0) {
                    System.out.println("'" + getCompanyNameById(fkCompanyId) + "'  cars:");
                }

                // Display values
                System.out.println((i + 1) + ". " + carName);
                i++;
                indexList.add(carId);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return indexList;
    }

    String getCompanyNameById(String compId) {
        return getNameById(compId, "COMPANY");
    }

    String getCarNameById(String carId) {
        return getNameById(carId, "CAR");
    }

    String getCompanyByCarId(String carId) {
        String sql = "SELECT COMPANY_ID FROM CAR WHERE ID = " + carId;
        String companyName = "";
        ResultSet rs = executeSelect(sql);
        try {
            while (rs.next()) {
                companyName = getCompanyNameById(rs.getString("COMPANY_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companyName;
    }

    private String getNameById(String id, String tableName) {
        String sqlCompName = "SELECT NAME FROM " + tableName + " WHERE ID = " + id;
        ResultSet rs = executeSelect(sqlCompName);
        String name = "";
        try {
            while (rs.next()) {
                name = rs.getString("NAME");
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    private void executeQuery(String sql) {
//        Connection conn = null;
//        Statement stmt = null;

        try {
//            // STEP 1: Register JDBC driver
//            Class.forName(JDBC_DRIVER);
//
//            //STEP 2: Open a connection
//            System.out.println("Connecting to database...");
//            conn = DriverManager.getConnection(DB_URL);
//            conn.setAutoCommit(true);

            //STEP 3: Execute a query
//            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
//            System.out.println("Created table in given database...");

            // STEP 4: Clean-up environment
//            stmt.close();
//            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
//        } finally {
//            //finally block used to close resources
//            try {
//                if (stmt != null) stmt.close();
//            } catch (SQLException se2) {
//            } // nothing we can do
//            try {
//                if (conn != null) conn.close();
//            } catch (SQLException se) {
//                se.printStackTrace();
//            } //end finally try
        } //end try
    }

    private ResultSet executeSelect(String sql) {
        ResultSet rs = null;

        try {
//            // STEP 1: Register JDBC driver
//            Class.forName(JDBC_DRIVER);
//
//            //STEP 2: Open a connection
//            System.out.println("Connecting to database...");
//            conn = DriverManager.getConnection(DB_URL);
//            conn.setAutoCommit(true);

            //STEP 3: Execute a query
//            System.out.println("Creating query in given database...");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
//            System.out.println("Created select in given database...");

            // STEP 4: Clean-up environment
//            stmt.close();
//            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
//        } finally {
//            //finally block used to close resources
//            try {
//                if (stmt != null) stmt.close();
//            } catch (SQLException se2) {
//            } // nothing we can do
//            try {
//                if (conn != null) conn.close();
//            } catch (SQLException se) {
//                se.printStackTrace();
//            } //end finally try
        } //end try

        return rs;
    }

}


