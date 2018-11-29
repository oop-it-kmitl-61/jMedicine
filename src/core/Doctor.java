package core;

import java.util.ArrayList;

/**
 * Stores a doctor information Use a constructor to new a doctor.
 *
 * @param prefix a doctor prefix, chosen from a combo box.
 * @param name a doctor name.
 * @param ward a doctor ward.
 * @param hospital a doctor's workplace.
 * @param workTime an ArrayList of a time that doctor will be working.
 */

public class Doctor {

  private String id;
  private String prefix;
  private String firstName;
  private String lastName;
  private String ward;
  private String hospital;
  private ArrayList<ArrayList> workTime;

  public Doctor(String prefix, String firstName, String lastName, String ward,
      String hospital, ArrayList<ArrayList> workTime) {
    this.prefix = prefix;
    this.firstName = firstName;
    this.lastName = lastName;
    this.ward = ward;
    this.hospital = hospital;
    this.workTime = workTime;
  }

  public Doctor(String id, String prefix, String firstName, String lastName, String ward,
      String hospital, ArrayList<ArrayList> workTime) {
    this.id = id;
    this.prefix = prefix;
    this.firstName = firstName;
    this.lastName = lastName;
    this.ward = ward;
    this.hospital = hospital;
    this.workTime = workTime;
  }

  public String toString() {
    return prefix + " " + firstName + " " + lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getWard() {
    return ward;
  }

  public void setWard(String ward) {
    this.ward = ward;
  }

  public String getHospital() {
    return hospital;
  }

  public void setHospital(String hospital) {
    this.hospital = hospital;
  }

  public ArrayList<ArrayList> getWorkTime() {
    return workTime;
  }

  public void setWorkTime(ArrayList<ArrayList> workTime) {
    this.workTime = workTime;
  }
}
