package api;

import static core.Core.getUser;
import static core.Utils.sha256;

import core.Database;
import core.User;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class UserDB {

  private static Connection connection;

  static {
    try {
      connection = Database.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static User updateUserTime() throws SQLException {
    User user = getUser();
    String SQLCommand = "UPDATE users SET \"time\" = ? WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setArray(1, connection.createArrayOf("text", getUser().getUserTime()));
    pStatement.setObject(2, user.getUserId(), Types.OTHER);

    pStatement.executeUpdate();

    return getUser();
  }

  public static User updateUserData() throws SQLException {
    User user = getUser();
    String SQLCommand = "UPDATE users SET username = ?, email = ?, title = ?, firstname = ?, lastname = ?, gender = ?, weight = ?, height = ?, age = ? WHERE  id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setString(1, user.getUserName());
    pStatement.setString(2, user.getUserEmail());
    pStatement.setString(3, user.getUserPrefix());
    pStatement.setString(4, user.getUserFirstName());
    pStatement.setString(5, user.getUserLastName());
    pStatement.setString(6, user.getUserGender());
    pStatement.setDouble(7, user.getUserWeight());
    pStatement.setDouble(8, user.getUserHeight());
    pStatement.setDouble(9, user.getUserAge());
    pStatement.setObject(10, user.getUserId(), Types.OTHER);

    pStatement.executeUpdate();

    return user;
  }

  public static User updateUserPassword(char[] password)
      throws SQLException, NoSuchAlgorithmException {
    User user = getUser();
    String encrypted = sha256(String.valueOf(password));
    String SQLCommand = "UPDATE users SET \"password\" = ? WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setString(1, encrypted);
    pStatement.setObject(2, getUser().getUserId(), Types.OTHER);

    pStatement.executeUpdate();

    return user;
  }

  public static void deleteUser() throws SQLException {
    User user = getUser();
    String SQLCommand = "DELETE FROM users WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);

    pStatement.setObject(1, user.getUserId(), Types.OTHER);

    pStatement.executeUpdate();

    pStatement.close();
  }
}
