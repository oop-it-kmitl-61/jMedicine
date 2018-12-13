package api;

import static core.Core.getUser;

import core.Database;
import core.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    String SQLCommand = "UPDATE users SET \"time\" = ? WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setArray(1, connection.createArrayOf("text", getUser().getUserTime()));
    pStatement.setObject(2, getUser().getUserId(), Types.OTHER);

    pStatement.executeUpdate();

    return getUser();
  }

  public static User updateUserData(User user) throws SQLException {

    // TODO: Update user data in DB

    return user;
  }
}
