package main;

import java.sql.*;
import java.util.Properties;

public class Database {

  public static Connection getConnection() throws SQLException {
    String url = "jdbc:postgresql://jmed.wiput.me/jmedicine";
    Properties props = new Properties();
    props.setProperty("user", "jmedicine");
    props.setProperty("password", "Jmedicine#2018");
    props.setProperty("ssl", "false");
    return DriverManager.getConnection(url, props);
  }

  public static void main(String[] args) throws SQLException {
    getConnection();
  }
}
