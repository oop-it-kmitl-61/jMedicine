package core;

import core.Medicine;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Helps displaying an attribute of a medicine.
 */

public class MedicineUtil {
  private String[] medType = {"ยาเม็ด", "ยาแคปซูล", "ยาน้ำ", "ยาแบบฉีด"};
  private String[] medColor = {"ขาว", "ใส", "น้ำเงิน", "เขียว", "เหลือง", "ชมพู", "ส้ม", "น้ำตาล",
      "ไม่ระบุ"};
  private String[] medTime = {"เช้า", "กลางวัน", "เย็น", "ก่อนนอน"};
  private String[] medDoseStr = {"ก่อนอาหาร", "หลังอาหาร", "พร้อมอาหาร/หลังอาหารทันที"};

  public String[] getMedType() {
    return medType;
  }

  public String[] getMedColor() {
    return medColor;
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
        imgURL += "tablet-";
        break;
      case "capsule":
        imgURL += "capsule.png";
        urlFinished = true;
        break;
      case "liquid":
        imgURL += "liquid-";
        break;
      case "inject":
        imgURL += "inject.png";
        urlFinished = true;
        break;
    }

    if (!urlFinished) {
      imgURL += medicine.getMedColor();
      imgURL += ".png";
    }

    try {
      Image img = ImageIO.read(new File("src/main/img/" + imgURL));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return labelPic;
  }
}
