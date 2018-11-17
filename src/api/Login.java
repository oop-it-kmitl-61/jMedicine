package api;

import static core.Utils.sha256;

import core.Database;
import core.User;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Login {

  private Connection connection;

  public Login() throws SQLException {
    connection = Database.getConnection();
  }

  public User doSignIn(String username, String password)
      throws NoSuchAlgorithmException, SQLException, LoginException {
    password = sha256(password);

    String SQLCommand = "SELECT * FROM users WHERE username = ? AND password = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setString(1, username);
    pStatement.setString(2, password);

    ResultSet result = pStatement.executeQuery();

    if (result.next()) {
      return new User(result.getString("title") + result.getString("firstname") + " " + result
          .getString("lastname"), result.getString("gender"), String.valueOf(result.getInt("age")),
          String.valueOf(result.getFloat("weight")),
          String.valueOf(result.getFloat("height")), result.getString("id"));
    }

    throw new LoginException("Login failed");
  }

  public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
    Login l = new Login();
    System.out.println(l.doSignIn("wiput", "123456"));
  }

}
