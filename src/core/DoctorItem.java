package core;

/**
 * Being used by JComboBox to contain a doctor name along with Doctor object
 *
 * @author jMedicine
 * @version 0.7.3
 * @since 0.7.3
 */

public class DoctorItem {

  private String doctorName;
  private Doctor doctor;

  public DoctorItem(String doctorName, Doctor doctor) {
    this.doctorName = doctorName;
    this.doctor = doctor;
  }

  public String getDoctorName() {
    return doctorName;
  }

  public Doctor getDoctor() {
    return doctor;
  }

  @Override
  public String toString() {
    return doctorName;
  }
}
