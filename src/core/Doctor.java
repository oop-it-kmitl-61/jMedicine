package core;

import java.util.ArrayList;

/**
 * Stores a doctor information
 * Use a constructor to new a doctor.
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
  private String name;
  private String ward;
  private String hospital;
  private ArrayList<String> workTime;

  public Doctor(String id, String prefix, String name, String ward, String hospital,
      ArrayList<String> workTime) {
    this.id = id;
    this.prefix = prefix;
    this.name = name;
    this.ward = ward;
    this.hospital = hospital;
    this.workTime = workTime;
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

  public String getName() {
    return name;
  }

  public String getWard() {
    return ward;
  }

  public String getHospital() {
    return hospital;
  }

  public ArrayList<String> getWorkTime() {
    return workTime;
  }
}
