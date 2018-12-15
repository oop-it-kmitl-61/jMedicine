package core;

import java.util.ArrayList;

/**
 * Stores all user's information.
 *
 * @author jMedicine
 * @version 0.7.14
 * @since 0.3.0
 */

public class User {

  private String userId, userName, userPrefix, userFirstName, userLastName;
  private String userEmail, userGender;
  private int userAge;
  private double userWeight, userHeight;
  private String[] userTime;
  private ArrayList<Medicine> userMedicines;
  private ArrayList<Doctor> userDoctors;
  private ArrayList<Appointment> userAppointments;


  public User(String userId, String userName, String userPrefix, String userFirstName,
      String userLastName, String userEmail, String userGender, int userAge,
      double userWeight, double userHeight, String[] userTime) {
    this.userId = userId;
    this.userName = userName;
    this.userPrefix = userPrefix;
    this.userFirstName = userFirstName;
    this.userLastName = userLastName;
    this.userEmail = userEmail;
    this.userGender = userGender;
    this.userAge = userAge;
    this.userWeight = userWeight;
    this.userHeight = userHeight;
    this.userTime = userTime;
    this.userMedicines = new ArrayList<>();
    this.userDoctors = new ArrayList<>();
    this.userAppointments = new ArrayList<>();
  }

  public User(String userName) {
    this.userName = userName;
    this.userPrefix = "";
    this.userFirstName = "";
    this.userLastName = "";
    this.userEmail = "";
    this.userGender = "";
    this.userAge = 0;
    this.userWeight = 0;
    this.userHeight = 0;
    this.userTime = new String[]{"08:30", "12:30", "18:30", "22:30"};
    this.userMedicines = new ArrayList<>();
    this.userDoctors = new ArrayList<>();
    this.userAppointments = new ArrayList<>();
  }

  public String getUserPrefix() {
    return userPrefix;
  }

  public void setUserPrefix(String userPrefix) {
    this.userPrefix = userPrefix;
  }

  public String getUserFirstName() {
    return userFirstName;
  }

  public void setUserFirstName(String userFirstName) {
    this.userFirstName = userFirstName;
  }

  public String getUserLastName() {
    return userLastName;
  }

  public void setUserLastName(String userLastName) {
    this.userLastName = userLastName;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserGender() {
    return userGender;
  }

  public void setUserGender(String userGender) {
    this.userGender = userGender;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getUserAge() {
    return userAge;
  }

  public void setUserAge(int userAge) {
    this.userAge = userAge;
  }

  public double getUserWeight() {
    return userWeight;
  }

  public void setUserWeight(double userWeight) {
    this.userWeight = userWeight;
  }

  public double getUserHeight() {
    return userHeight;
  }

  public void setUserHeight(double userHeight) {
    this.userHeight = userHeight;
  }

  public String[] getUserTime() {
    return userTime;
  }

  public void setUserTime(String[] userTime) {
    this.userTime = userTime;
  }

  public ArrayList<Appointment> getUserAppointments() {
    return userAppointments;
  }

  public void setUserAppointments(ArrayList<Appointment> userAppointments) {
    this.userAppointments = userAppointments;
  }

  public ArrayList<Medicine> getUserMedicines() {
    return userMedicines;
  }

  public void setUserMedicines(ArrayList<Medicine> userMedicines) {
    this.userMedicines = userMedicines;
  }

  public ArrayList<Doctor> getUserDoctors() {
    return userDoctors;
  }

  public void setUserDoctors(ArrayList<Doctor> userDoctors) {
    this.userDoctors = userDoctors;
  }

  public void addUserAppointment(Appointment appointment) {
    this.userAppointments.add(appointment);
  }

  public void addUserMedicine(Medicine medicine) {
    this.userMedicines.add(medicine);
  }

  public void addUserDoctor(Doctor doctor) {
    this.userDoctors.add(doctor);
  }

  public boolean removeUserAppointment(Appointment appointment) {
    try {
      this.userAppointments.remove(appointment);
    } catch (NullPointerException ignored) {
      return false;
    }
    return true;
  }

  public boolean removeUserDoctor(Doctor doctor) {
    try {
      this.userDoctors.remove(doctor);
    } catch (NullPointerException ignored) {
      return false;
    }
    return true;
  }

  public boolean removeUserMedicine(Medicine medicine) {
    try {
      this.userMedicines.remove(medicine);
    } catch (NullPointerException ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "User{" +
        "userPrefix='" + userPrefix + '\'' +
        "userFirstName='" + userFirstName + '\'' +
        "userLastName='" + userLastName + '\'' +
        ", userGender='" + userGender + '\'' +
        ", userAge='" + userAge + '\'' +
        ", userWeight='" + userWeight + '\'' +
        ", userHeight='" + userHeight + '\'' +
        ", userMedicines=" + userMedicines +
        ", userDoctors=" + userDoctors +
        '}';
  }
}
