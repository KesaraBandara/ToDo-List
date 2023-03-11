package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    private static DBconnection dBconnection;
    private Connection connection;

    private DBconnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
           connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","password");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("vvvvvvvvvvvv");
            e.printStackTrace();
            //System.out.println("vvvvvvvvvvvv");
        }


    }

    public static DBconnection getInstance(){
    //    System.out.println("nullllllllll");
       return (dBconnection == null) ? dBconnection=new DBconnection() : dBconnection ;

    }

    public Connection getConnection(){
        return connection;
    }

}
