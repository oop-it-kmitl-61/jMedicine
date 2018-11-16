package main;

import java.sql.*;
import java.util.Properties;

public class Database {

  public static Connection getConnection() throws SQLException {
    String DB_HOST = System.getenv("DB_HOST");
    String DB_NAME = System.getenv("DB_NAME");
    String DB_USER = System.getenv("DB_USER");
    String DB_PASS = System.getenv("DB_PASS");

    String url = "jdbc:postgresql://" + DB_HOST + "/" + DB_NAME;
    Properties props = new Properties();
    props.setProperty("user", DB_USER);
    props.setProperty("password", DB_PASS);
    props.setProperty("ssl", "false");
    return DriverManager.getConnection(url, props);
  }

  public static void main(String[] args) throws SQLException {
    getConnection();
  }
}
