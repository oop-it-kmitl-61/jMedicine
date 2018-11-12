package main;

import java.util.ArrayList;

public class Doctor {

  private String prefix;
  private String name;
  private String ward;
  private String hospital;
  private ArrayList<String> workTime;

  public Doctor(String prefix, String name, String ward, String hospital,
      ArrayList<String> workTime) {
    this.prefix = prefix;
    this.name = name;
    this.ward = ward;
    this.hospital = hospital;
    this.workTime = workTime;
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
