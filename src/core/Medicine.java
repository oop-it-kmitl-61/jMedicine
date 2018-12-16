package core;

import static core.Utils.stringToTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Stores a medicine information. Use a constructor to new a medicine.
 *
 * @param medName a name of medicine.
 * @param medType a string to classify the medicine, must be "tablet", "capsule", "liquid" or
 * "spray".
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
  private String medDoseStr;
  private int medDose;
  private int medTotal;
  private int medRemaining;
  private Date medEXP;
  private Date dateAdded;
  private String medUnit;
  private String dateStart;
  private Timestamp lastTaken;
  private ArrayList<String> taken;
  private ArrayList<String> skipped;
  private Timestamp lastNotified;

  public Medicine(String medName, String medType, String medColor, String medDescription,
      ArrayList<String> medTime, String medDoseStr, int medDose, int medTotal,
      Date medEXP, String dateStart) {
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
    this.dateStart = dateStart;
    this.lastTaken = new Timestamp(stringToTimestamp(dateStart));
    this.taken = new ArrayList<>();
    this.skipped = new ArrayList<>();
    this.lastNotified = new Timestamp(0);
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
      case "spray":
        this.medUnit = "มิลลิลิตร";
        break;
    }
  }

  public Medicine(String id, String medName, String medType, String medColor, String medDescription,
      ArrayList<String> medTime, String medDoseStr, int medDose, int medTotal,
      Date medEXP, String dateStart, Timestamp lastTaken, ArrayList<String> taken,
      ArrayList<String> skipped, Timestamp lastNotified) {
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
    this.dateStart = dateStart;
    this.lastTaken = lastTaken;
    this.taken = taken;
    this.skipped = skipped;
    this.lastNotified = lastNotified;
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
      case "spray":
        this.medUnit = "มิลลิลิตร";
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

  public void setMedName(String medName) {
    this.medName = medName;
  }

  public String getMedType() {
    return medType;
  }

  public void setMedType(String medType) {
    this.medType = medType;
  }

  public String getMedColor() {
    return medColor;
  }

  public void setMedColor(String medColor) {
    this.medColor = medColor;
  }

  public String getMedDescription() {
    return medDescription;
  }

  public void setMedDescription(String medDescription) {
    this.medDescription = medDescription;
  }

  public ArrayList<String> getMedTime() {
    return medTime;
  }

  public void setMedTime(ArrayList<String> medTime) {
    this.medTime = medTime;
  }

  public String getMedDoseStr() {
    return medDoseStr;
  }

  public void setMedDoseStr(String medDoseStr) {
    this.medDoseStr = medDoseStr;
  }

  public int getMedDose() {
    return medDose;
  }

  public void setMedDose(int medDose) {
    this.medDose = medDose;
  }

  public int getMedTotal() {
    return medTotal;
  }

  public void setMedTotal(int medTotal) {
    this.medTotal = medTotal;
  }

  public int getMedRemaining() {
    return medRemaining;
  }

  public void setMedRemaining(int medRemaining) {
    this.medRemaining = medRemaining;
  }

  public Date getMedEXP() {
    return medEXP;
  }

  public void setMedEXP(Date medEXP) {
    this.medEXP = medEXP;
  }

  public Date getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(Date dateAdded) {
    this.dateAdded = dateAdded;
  }

  public String getMedUnit() {
    return medUnit;
  }

  public void setMedUnit(String medUnit) {
    this.medUnit = medUnit;
  }

  public Timestamp getLastTaken() {
    return lastTaken;
  }

  public void setLastTaken(Timestamp lastTaken) {
    this.lastTaken = lastTaken;
  }

  public String getDateStart() {
    return dateStart;
  }

  public void setDateStart(String dateStart) {
    this.dateStart = dateStart;
  }

  public ArrayList<String> getTaken() {
    return taken;
  }

  public void setTaken(ArrayList<String> taken) {
    this.taken = taken;
  }

  public void appendTaken(String taken) {
    this.taken.add(taken);
  }

  public ArrayList<String> getSkipped() {
    return skipped;
  }

  public void setSkipped(ArrayList<String> skipped) {
    this.skipped = skipped;
  }

  public void appendSkipped(String skipped) {
    this.skipped.add(skipped);
  }

  public Timestamp getLastNotified() {
    return lastNotified;
  }

  public void setLastNotified(Timestamp lastNotified) {
    this.lastNotified = lastNotified;
  }
}
