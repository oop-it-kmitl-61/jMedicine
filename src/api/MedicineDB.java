package api;

import static GUI.GUIHelper.formatDMYHM;

import core.Database;
import core.Medicine;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
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
      if (result.getArray("time") == null) {
        throw new MedicineException("Time is null");
      }

      ArrayList<String> time;
      time = Arrays.stream((Object[]) result.getArray("time").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      if (result.getArray("doseStr") == null) {
        throw new MedicineException("Dose is null");
      }

      ArrayList<String> doseStr;
      doseStr = Arrays.stream((Object[]) result.getArray("doseStr").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      results.add(
          new Medicine(result.getString("id"), result.getString("name"), result.getString("type"),
              result.getString("color"), result.getString("description"), time,
              doseStr, result.getInt("dose"), result.getInt("total"),
              result.getDate("expire")));
    }

    pStatement.close();

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

  public static Medicine addMedicine(Medicine medicine, String userId) throws SQLException {

    String SQLCommand = "WITH ROW AS ( INSERT INTO medicine (\"user\", name, type, color, description, dose, total,\"time\", \"doseStr\", expire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id ) SELECT id FROM ROW";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);
    pStatement.setString(2, medicine.getMedName());
    pStatement.setString(3, medicine.getMedType());
    pStatement.setString(4, medicine.getMedColor());
    pStatement.setString(5, medicine.getMedDescription());
    pStatement.setInt(6, medicine.getMedDose());
    pStatement.setInt(7, medicine.getMedTotal());
    pStatement.setArray(8, connection.createArrayOf("text", medicine.getMedTime().toArray()));
    pStatement.setArray(9, connection.createArrayOf("text", medicine.getMedDoseStr().toArray()));
    pStatement.setString(10, new SimpleDateFormat("yyyy-MM-dd").format(medicine.getMedEXP()));

    ResultSet result = pStatement.executeQuery();

    result.next();

    medicine.setId(result.getString("id"));

    pStatement.close();

    return medicine;
  }


  public static Medicine updateMedicine(Medicine medicine) throws SQLException {
    String SQLCommand = "UPDATE medicine SET name = ?, type = ?, color = ?, description = ?, dose = ?, total = ?, \"doseStr\" = ?, expire = ?, \"time\" = ? WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setString(1, medicine.getMedName());
    pStatement.setString(2, medicine.getMedType());
    pStatement.setString(3, medicine.getMedColor());
    pStatement.setString(4, medicine.getMedDescription());
    pStatement.setInt(5, medicine.getMedDose());
    pStatement.setInt(6, medicine.getMedTotal());
    pStatement.setArray(7, connection.createArrayOf("text", medicine.getMedDoseStr().toArray()));
    pStatement.setString(8, formatDMYHM.format(medicine.getMedEXP()));
    pStatement.setArray(9, connection.createArrayOf("text", medicine.getMedTime().toArray()));
    pStatement.setObject(10, medicine.getId(), Types.OTHER);

    pStatement.executeUpdate();

    return medicine;
  }


  public static void removeMedicine(Medicine medicine) throws SQLException {
    String SQLCommand = "DELETE FROM medicine WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);

    pStatement.setObject(1, medicine.getId(), Types.OTHER);

    pStatement.executeUpdate();

    pStatement.close();
  }
}
