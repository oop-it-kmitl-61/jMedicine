package core;

import static GUI.GUIHelper.imgPath;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Helps displaying an attribute of a medicine.
 */

public class MedicineUtil {

  private String[] medType = {"ยาเม็ด", "ยาแคปซูล", "ยาน้ำ", "ยาแบบฉีด"};
  private String[] tabletColor = {"white", "blue", "green", "yellow", "red", "pink", "purple",
      "orange",
      "brown"};
  private String[] liquidColor = {"transparent", "white", "blue", "green", "yellow", "red", "pink",
      "purple",
      "orange", "brown", "black"};
  private String[] medTime = {"เช้า", "กลางวัน", "เย็น", "ก่อนนอน"};
  private String[] medDoseStr = {"ก่อนอาหาร", "หลังอาหาร", "พร้อมอาหาร/หลังอาหารทันที"};

  public String[] getMedType() {
    return medType;
  }

  public String[] getTabletColor() {
    return tabletColor;
  }

  public String[] getLiquidColor() {
    return liquidColor;
  }

  public String[] getMedTime() {
    return medTime;
  }

  public String[] getMedDoseStr() {
    return medDoseStr;
  }

  public JLabel getMedIcon(Medicine medicine) {
    String imgURL = "";
    JLabel labelPic = new JLabel();
    boolean urlFinished = false;
    switch (medicine.getMedType()) {
      case "tablet":
        imgURL += "/tablets/tablet-";
        break;
      case "capsule":
        imgURL += "/capsules/capsule-";
        break;
      case "liquid":
        imgURL += "/liquids/liquid-";
        break;
      case "inject":
        imgURL += "/inject.png";
        urlFinished = true;
        break;
    }

    if (!urlFinished) {
      imgURL += medicine.getMedColor();
      imgURL += ".png";
    }

    try {
      Image img = ImageIO.read(new File(imgPath + imgURL));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ex) {
      try {
        Image img = ImageIO.read(new File(imgPath + "/system/med-not-found.png"));
        labelPic.setIcon(new ImageIcon(img));
      } catch (IOException ignored) {
      }

    }

    return labelPic;
  }
}
