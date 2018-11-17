package core;

import java.util.ArrayList;

public class User {

  private String userName = "(ไม่ได้ตั้งชื่อ)";
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

  public User(String userName, String userGender, String userAge, String userWeight,
      String userHeight) {
    this.userName = userName;
    this.userGender = userGender;
    this.userAge = userAge;
    this.userWeight = userWeight;
    this.userHeight = userHeight;
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
}
