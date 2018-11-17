package api;

import static core.Utils.sha256;

import core.Database;
import core.User;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class Login {

  private Connection connection;

  public Login() throws SQLException {
    Connection connection = Database.getConnection();
  }

  public User doSignIn(String username, String password) throws NoSuchAlgorithmException {
    password = sha256(password);

    return new User("Hello", "male", "112", "120", "120");
  }



}
