package core;

import java.util.Date;

public class Appointment {

  private String id;
  private Date date;
  private String timeStart;
  private String timeStop;
  private Doctor doctor;
  private String hospitalName;
  private String note;

  public Appointment(String id, Date date, String timeStart, String timeStop, Doctor doctor,
      String hospitalName, String note) {
    this.id = id;
    this.date = date;
    this.timeStart = timeStart;
    this.timeStop = timeStop;
    this.doctor = doctor;
    this.hospitalName = hospitalName;
    this.note = note;
  }

  public Appointment(Date date, String timeStart, String timeStop, Doctor doctor,
      String hospitalName, String note) {
    this.date = date;
    this.timeStart = timeStart;
    this.timeStop = timeStop;
    this.doctor = doctor;
    this.hospitalName = hospitalName;
    this.note = note;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getTimeStart() {
    return timeStart;
  }

  public void setTimeStart(String timeStart) {
    this.timeStart = timeStart;
  }

  public String getTimeStop() {
    return timeStop;
  }

  public void setTimeStop(String timeStop) {
    this.timeStop = timeStop;
  }

  public Doctor getDoctor() {
    return doctor;
  }

  public void setDoctor(Doctor doctor) {
    this.doctor = doctor;
  }

  public String getHospitalName() {
    return hospitalName;
  }

  public void setHospitalName(String hospitalName) {
    this.hospitalName = hospitalName;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}
