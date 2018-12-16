package core;

import java.util.Date;

public class Appointment {

  private String id;
  private Date date;
  private String timeStart;
  private String timeStop;
  private Doctor doctor;
  private String note;
  private boolean notified;

  public Appointment(String id, Date date, String timeStart, String timeStop, Doctor doctor,
      String note, boolean notified) {
    this.id = id;
    this.date = date;
    this.timeStart = timeStart;
    this.timeStop = timeStop;
    this.doctor = doctor;
    this.note = note;
    this.notified = notified;
  }

  public Appointment(Date date, String timeStart, String timeStop, Doctor doctor, String note) {
    this.date = date;
    this.timeStart = timeStart;
    this.timeStop = timeStop;
    this.doctor = doctor;
    this.note = note;
    this.notified = false;
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

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public boolean isNotified() {
    return notified;
  }

  public void setNotified(boolean notified) {
    this.notified = notified;
  }
}
