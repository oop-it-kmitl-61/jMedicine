package api;

import static api.DoctorDB.getDoctorInfo;

import core.Appointment;
import core.Database;
import core.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class AppointmentDB {

  private static Connection connection;
  private static DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

  static {
    try {
      connection = Database.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static ArrayList<Appointment> getAllAppointment(String userId)
      throws SQLException, ParseException {
    String SQLCommand = "SELECT * FROM doctors WHERE \"user\" = ?";

    PreparedStatement pStatement = connection.prepareStatement(SQLCommand);
    pStatement.setObject(1, userId, Types.OTHER);

    ResultSet result = pStatement.executeQuery();

    ArrayList<Appointment> results = new ArrayList<>();

    while (result.next()) {

      ArrayList<String> time = Arrays.stream((Object[]) result.getArray("time").getArray())
          .map(Object::toString).collect(Collectors.toCollection(ArrayList::new));

      Date timeStart = dateFormatter.parse(time.get(0));
      Date timeEnd = dateFormatter.parse(time.get(1));
      Doctor doctor = getDoctorInfo(result.getString("doctor"));

      results.add(
          new Appointment(result.getString("id"), timeStart, timeEnd, doctor, doctor.getHospital())
      );
    }

    return results;
  }


}
