package api;


import core.Database;
import core.Medicine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MedicineDB {

  private static Connection connection;

  static {
    try {
      connection = Database.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static ArrayList<Medicine> getAllMedicine(String userId)
      throws SQLException {

    String SQLCommand = "SELECT * FROM medicine WHERE \"user\" = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);

    ResultSet result = pStatement.executeQuery();

    ArrayList<Medicine> results = new ArrayList<>();

    while (result.next()) {
      ArrayList<String> time = Arrays.stream((Object[]) result.getArray("time").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      ArrayList<String> doseStr = Arrays.stream((Object[]) result.getArray("doseStr").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      results.add(
          new Medicine(result.getString("id"), result.getString("name"), result.getString("type"),
              result.getString("color"), result.getString("description"), time,
              doseStr, result.getInt("dose"), result.getInt("total"),
              result.getDate("expire")));
    }

    pStatement.close();
    connection.close();

    return results;
  }

  public static Medicine getMedicineInfo(String id) throws SQLException {
    String SQLCommand = "SELECT * FROM medicine WHERE \"id\" = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, id, Types.OTHER);

    ResultSet result = pStatement.executeQuery();

    result.next();
    ArrayList<String> time = Arrays.stream((Object[]) result.getArray("time").getArray())
        .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

    ArrayList<String> doseStr = Arrays.stream((Object[]) result.getArray("doseStr").getArray())
        .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

    return new Medicine(result.getString("id"), result.getString("name"), result.getString("type"),
        result.getString("color"), result.getString("description"), time,
        doseStr, result.getInt("dose"), result.getInt("total"),
        result.getDate("expire"));
  }

  public static Medicine addMedicine(Medicine medicine) {
    
    return medicine;
  }

  public static void removeMedicine(String id) {

  }

  public static Medicine updateMedicine(Medicine medicine) {
    return medicine;
  }
}
