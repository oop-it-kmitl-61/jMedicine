package api;

import static core.Utils.stringToTimestamp;

import core.Database;
import core.Medicine;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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

      if (result.getArray("doseStr") == null) {
        throw new MedicineException("Dose is null");
      }

      ArrayList<String> time;
      time = Arrays.stream((Object[]) result.getArray("time").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      ArrayList<String> taken;
      if (result.getArray("taken") != null) {
        taken = Arrays.stream((Object[]) result.getArray("taken").getArray())
            .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));
      } else {
        taken = new ArrayList<>();
      }

      ArrayList<String> skipped;
      if (result.getArray("skipped") != null) {
        skipped = Arrays.stream((Object[]) result.getArray("skipped").getArray())
            .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));
      } else {
        skipped = new ArrayList<>();
      }

      results.add(
          new Medicine(result.getString("id"), result.getString("name"), result.getString("type"),
              result.getString("color"), result.getString("description"), time,
              result.getString("doseStr"), result.getInt("dose"), result.getInt("total"),
              result.getDate("expire"), result.getString("startDate"),
              result.getTimestamp("lastTaken"), taken, skipped,
              result.getTimestamp("lastnotified")));
    }

    pStatement.close();

    return results;
  }

  public static Medicine addMedicine(Medicine medicine, String userId) throws SQLException {

    String SQLCommand = "WITH ROW AS ( INSERT INTO medicine (\"user\", name, type, color, description, dose, total,\"time\", \"doseStr\", expire, \"startDate\", \"lastNotified\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id ) SELECT id FROM ROW";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);
    pStatement.setString(2, medicine.getMedName());
    pStatement.setString(3, medicine.getMedType());
    pStatement.setString(4, medicine.getMedColor());
    pStatement.setString(5, medicine.getMedDescription());
    pStatement.setInt(6, medicine.getMedDose());
    pStatement.setInt(7, medicine.getMedTotal());
    pStatement.setArray(8, connection.createArrayOf("text", medicine.getMedTime().toArray()));
    pStatement.setString(9, medicine.getMedDoseStr());
    if (medicine.getMedEXP() == null) {
      pStatement.setDate(10, null);
    } else {
      pStatement.setDate(10, new Date(medicine.getMedEXP().getTime()));
    }
    pStatement.setTimestamp(11, new java.sql.Timestamp(stringToTimestamp(medicine.getDateStart())));
    pStatement.setTimestamp(12, medicine.getLastNotified());

    ResultSet result = pStatement.executeQuery();

    result.next();

    medicine.setId(result.getString("id"));

    pStatement.close();

    return medicine;
  }


  public static Medicine updateMedicine(Medicine medicine) throws SQLException {
    String SQLCommand = "UPDATE medicine SET name = ?, type = ?, color = ?, description = ?, dose = ?, total = ?, \"doseStr\" = ?, expire = ?, \"time\" = ?, \"startDate\" = ?, \"lastTaken\" = ?, \"taken\" = ?, \"skipped\" = ?, \"lastnotified\" = ? WHERE id = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setString(1, medicine.getMedName());
    pStatement.setString(2, medicine.getMedType());
    pStatement.setString(3, medicine.getMedColor());
    pStatement.setString(4, medicine.getMedDescription());
    pStatement.setInt(5, medicine.getMedDose());
    pStatement.setInt(6, medicine.getMedTotal());
    pStatement.setString(7, medicine.getMedDoseStr());
    pStatement.setDate(8, new Date(medicine.getMedEXP().getTime()));
    pStatement.setArray(9, connection.createArrayOf("text", medicine.getMedTime().toArray()));
    pStatement.setTimestamp(10, new java.sql.Timestamp(stringToTimestamp(medicine.getDateStart())));
    pStatement.setTimestamp(11, medicine.getLastTaken());
    pStatement.setArray(12, connection.createArrayOf("text", medicine.getTaken().toArray()));
    pStatement.setArray(13, connection.createArrayOf("text", medicine.getSkipped().toArray()));
    pStatement.setTimestamp(14, medicine.getLastNotified());
    pStatement.setObject(15, medicine.getId(), Types.OTHER);

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
