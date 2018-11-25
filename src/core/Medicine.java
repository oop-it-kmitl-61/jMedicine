package core;

import java.util.ArrayList;
import java.util.Date;

/**
 * Stores a medicine information.
 * Use a constructor to new a medicine.
 *
 * @param medName a name of medicine.
 * @param medType a string to classify the medicine,
 *        must be "tablet", "capsule", "liquid" or "Inject".
 * @param medColor a string of color.
 * @param medDescription a description of medicine.
 * @param medTime an ArrayList of String, containing a time for having a medicine.
 * @param medDoseStr an ArrayList of String, containing how to have a medicine.
 * @param medDose a number of medicine per one dose.
 * @param medTotal a total numbers of medicine have been prescribed.
 * @param medEXP a medicine expired date.
 */

public class Medicine {
  private String id;
  private String medName;
  private String medType;
  private String medColor;
  private String medDescription;
  private ArrayList<String> medTime;
  private ArrayList<String> medDoseStr;
  private int medDose;
  private int medTotal;
  private int medRemaining;
  private Date medEXP;
  private Date dateAdded;
  private String medUnit;

  public Medicine(String medName, String medType, String medColor, String medDescription,
      ArrayList<String> medTime, ArrayList<String> medDoseStr, int medDose, int medTotal,
      Date medEXP) {
    this.medName = medName;
    this.medType = medType;
    this.medColor = medColor;
    this.medDescription = medDescription;
    this.medTime = medTime;
    this.medDoseStr = medDoseStr;
    this.medDose = medDose;
    this.medTotal = medTotal;
    this.medRemaining = medTotal;
    this.medEXP = medEXP;
    this.dateAdded = new Date();
    switch (medType) {
      case "tablet":
        this.medUnit = "เม็ด";
        break;
      case "capsule":
        this.medUnit = "แคปซูล";
        break;
      case "liquid":
        this.medUnit = "มิลลิลิตร";
        break;
      case "inject":
        this.medUnit = "เข็ม";
        break;
    }
  }

  public Medicine(String id, String medName, String medType, String medColor, String medDescription,
      ArrayList<String> medTime, ArrayList<String> medDoseStr, int medDose, int medTotal,
      Date medEXP) {
    this.id = id;
    this.medName = medName;
    this.medType = medType;
    this.medColor = medColor;
    this.medDescription = medDescription;
    this.medTime = medTime;
    this.medDoseStr = medDoseStr;
    this.medDose = medDose;
    this.medTotal = medTotal;
    this.medRemaining = medTotal;
    this.medEXP = medEXP;
    this.dateAdded = new Date();
    switch (medType) {
      case "tablet":
        this.medUnit = "เม็ด";
        break;
      case "capsule":
        this.medUnit = "แคปซูล";
        break;
      case "liquid":
        this.medUnit = "มิลลิลิตร";
        break;
      case "inject":
        this.medUnit = "เข็ม";
        break;
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMedName() {
    return medName;
  }

  public String getMedType() {
    return medType;
  }

  public String getMedColor() {
    return medColor;
  }

  public String getMedDescription() {
    return medDescription;
  }

  public ArrayList<String> getMedTime() {
    return medTime;
  }

  public ArrayList<String> getMedDoseStr() {
    return medDoseStr;
  }

  public int getMedDose() {
    return medDose;
  }

  public int getMedRemaining() {
    return medRemaining;
  }

  public Date getMedEXP() {
    return medEXP;
  }

  public Date getDateAdded() {
    return dateAdded;
  }

  public String getMedUnit() {
    return medUnit;
  }

  public int getMedTotal() { return medTotal; }
}
