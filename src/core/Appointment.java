package core;

import java.util.Date;

public class Appointment {

  private String id;
  private Date timeStart;
  private Date timeStop;
  private Doctor doctor;
  private String hospitalName;

  public Appointment(Date timeStart, Date timeEnd, Doctor doctor,
      String hospitalName) {
    this.timeStart = timeStart;
    this.timeStop = timeEnd;
    this.doctor = doctor;
    this.hospitalName = hospitalName;
  }

  public Appointment(String id, Date timeStart, Date timeStop, Doctor doctor,
      String hospitalName) {
    this.id = id;
    this.timeStart = timeStart;
    this.timeStop = timeStop;
    this.doctor = doctor;
    this.hospitalName = hospitalName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getTimeStart() {
    return timeStart;
  }

  public void setTimeStart(Date timeStart) {
    this.timeStart = timeStart;
  }

  public Date getTimeStop() {
    return timeStop;
  }

  public void setTimeStop(Date timeStop) {
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
}
