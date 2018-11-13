package main;

import java.sql.*;
import java.util.Properties;

public class Database {

  public static void main(String[] args) throws SQLException {
    String url = "jdbc:postgresql://jmed.wiput.me/jmedicine";
    Properties props = new Properties();
    props.setProperty("user", "jmedicine");
    props.setProperty("password", "Jmedicine#2018");
    props.setProperty("ssl", "false");
    Connection conn = DriverManager.getConnection(url, props);

    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery("SELECT * FROM test");

    while (rs.next()) {
      System.out.println(rs.getString(1));
    }
    
    rs.close();
    st.close();
  }
}
