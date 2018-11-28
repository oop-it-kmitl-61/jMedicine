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

  private static String[] medType = {"ยาเม็ด", "ยาแคปซูล", "ยาน้ำ", "สเปรย์"};
  private static String[] tabletColor = {"white", "blue", "green", "yellow", "red", "pink",
      "purple",
      "orange", "brown"};
  private static String[] liquidColor = {"transparent", "white", "blue", "green", "yellow", "red",
      "pink",
      "purple", "orange", "brown", "black"};
  private static String[] medTime = {"เช้า", "กลางวัน", "เย็น", "ก่อนนอน"};
  private static String[] medDoseStr = {"ก่อนอาหาร", "หลังอาหาร", "พร้อมอาหาร/หลังอาหารทันที"};

  public static String[] getMedType() {
    return medType;
  }

  public static String[] getTabletColor() {
    return tabletColor;
  }

  public static int getTabletColorIndex(String color) {
    for (int i = 0; i < tabletColor.length; i++) {
      if (tabletColor[i].equals(color)) {
        return i;
      }
    }
    return -1;
  }

  public static String[] getLiquidColor() {
    return liquidColor;
  }

  public static int getLiquidColorIndex(String color) {
    for (int i = 0; i < liquidColor.length; i++) {
      if (liquidColor[i].equals(color)) {
        return i;
      }
    }
    return -1;
  }

  public static String[] getMedTime() {
    return medTime;
  }

  public static String[] getMedDoseStr() {
    return medDoseStr;
  }

  public static JLabel getMedIcon(Medicine medicine) {
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
      case "spray":
        imgURL += "/spray.png";
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
