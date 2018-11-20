package core;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath.Step;
import java.util.ArrayList;

/**
 * Stores user information.
 * Use a constructor to new a user.
 */

public class User {

  private String userId;
  private String userTitle;
  private String userFirstName;
  private String userLastName;
  private String userName = "(ไม่ได้ตั้งชื่อ)";
  private String userEmail;
  private String userGender;
  private String userAge;
  private String userWeight;
  private String userHeight;
  private ArrayList<Medicine> userMedicines;
  private ArrayList<Doctor> userDoctors;
  public User(String userName) {
    this.userName = userName;
    this.userMedicines = new ArrayList<>();
    this.userDoctors = new ArrayList<>();
  }

  public User(String userId, String userTitle, String userFirstName, String userLastName,
      String userEmail, String userGender, String userAge, String userWeight,
      String userHeight) {
    this.userId = userId;
    this.userTitle = userTitle;
    this.userFirstName = userFirstName;
    this.userLastName = userLastName;
    this.userEmail = userEmail;
    this.userGender = userGender;
    this.userAge = userAge;
    this.userWeight = userWeight;
    this.userHeight = userHeight;
  }

  public User(String userName, String userEmail, String userGender, String userAge,
      String userWeight, String userHeight, String userId) {
    this.userEmail = userEmail;
    this.userName = userName;
    this.userGender = userGender;
    this.userAge = userAge;
    this.userWeight = userWeight;
    this.userHeight = userHeight;
    this.userId = userId;
  }

  public String getUserTitle() {
    return userTitle;
  }

  public void setUserTitle(String userTitle) {
    this.userTitle = userTitle;
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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserGender() {
    return userGender;
  }

  public void setUserGender(String userGender) {
    this.userGender = userGender;
  }

  public String getUserAge() {
    return userAge;
  }

  public void setUserAge(String userAge) {
    this.userAge = userAge;
  }

  public String getUserWeight() {
    return userWeight;
  }

  public void setUserWeight(String userWeight) {
    this.userWeight = userWeight;
  }

  public String getUserHeight() {
    return userHeight;
  }

  public void setUserHeight(String userHeight) {
    this.userHeight = userHeight;
  }

  public ArrayList<Medicine> getUserMedicines() {
    return userMedicines;
  }

  public ArrayList<Doctor> getUserDoctors() {
    return userDoctors;
  }

  public void addUserMedicine(Medicine medicine) {
    this.userMedicines.add(medicine);
  }

  public void addUserDoctor(Doctor doctor) {
    this.userDoctors.add(doctor);
  }

  @Override
  public String toString() {
    return "User{" +
        "userName='" + userName + '\'' +
        ", userGender='" + userGender + '\'' +
        ", userAge='" + userAge + '\'' +
        ", userWeight='" + userWeight + '\'' +
        ", userHeight='" + userHeight + '\'' +
        ", userMedicines=" + userMedicines +
        ", userDoctors=" + userDoctors +
        '}';
  }
}
