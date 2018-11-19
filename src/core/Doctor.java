package core;

import java.util.ArrayList;

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
