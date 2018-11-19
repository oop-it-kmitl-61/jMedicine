package core;

import java.util.ArrayList;
import java.util.Date;

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
  private String medEXP;
  private Date dateAdded;
  private String medUnit;

  public Medicine(String medName, String medType, String medColor, String medDescription,
      ArrayList<String> medTime, ArrayList<String> medDoseStr, int medDose, int medTotal,
      String medEXP) {
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
      String medEXP) {
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

  public ArrayList<String> getMedDoseStr() {
    return medDoseStr;
  }

  public void setMedDoseStr(ArrayList<String> medDoseStr) {
    this.medDoseStr = medDoseStr;
  }

  public int getMedDose() {
    return medDose;
  }

  public void setMedDose(int medDose) {
    this.medDose = medDose;
  }

  public int getMedRemaining() {
    return medRemaining;
  }

  public void setMedRemaining(int medRemaining) {
    this.medRemaining = medRemaining;
  }

  public String getMedEXP() {
    return medEXP;
  }

  public void setMedEXP(String medEXP) {
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

  public int getMedTotal() {
    return medTotal;
  }
}
