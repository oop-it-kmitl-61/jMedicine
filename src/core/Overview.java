package core;

import static GUI.GUIHelper.formatHM;
import static GUI.GUIHelper.formatTimestamp;
import static GUI.GUIHelper.makeBoldLabel;
import static GUI.GUIHelper.makeGreyToBlueButton;
import static GUI.GUIHelper.makeGreyToRedButton;
import static GUI.GUIHelper.makeLabel;
import static GUI.GUIHelper.makeSmallerLabel;
import static GUI.GUIHelper.makeSubTitleLabel;
import static GUI.GUIHelper.newCardBorder;
import static GUI.GUIHelper.newFlowLayout;
import static GUI.GUIHelper.newPanelLoop;
import static GUI.GUIHelper.setPadding;
import static core.Core.getUser;
import static core.Utils.*;
import static core.MedicineUtil.getMedIcon;

import api.MedicineDB;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class Overview {

  private TreeMap<String, ArrayList> overviewItem;
  private int overviewCount;

  private void initTreeMap() {
    overviewItem = new TreeMap<>();
  }

  private void appendMedicine(String time, Medicine medicine) {
    ArrayList<Medicine> medData = (ArrayList<Medicine>) overviewItem.get(time);
    if (medData == null) {
      medData = new ArrayList<>();
    }
    medData.add(medicine);
    overviewItem.put(time, medData);
  }

  public JPanel renderOverview() {
    initTreeMap();
    LocalTime now = LocalTime.now();
    Boolean morning, afternoon, evening, bed;
    if (now.minusHours(1).getHour() == 7 && now.plusHours(1).getHour() == 9) {
      morning = true;
    } else if (now.minusHours(1).getHour() == 11 && now.plusHours(1).getHour() == 13) {
      afternoon = true;
    } else if (now.minusHours(1).getHour() == 17 && now.plusHours(1).getHour() == 19) {
      evening = true;
    } else if (now.minusHours(1).getHour() == 21 && now.plusHours(1).getHour() == 23) {
      bed = true;
    }

    JPanel panelMain = newPanelLoop();
    //Date now = new Date();

    // Fetches all medicines
    ArrayList<Medicine> userMedicines = null;
    try {
      userMedicines = MedicineDB.getAllMedicine(getUser().getUserId());
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Filters only active medicines
    for (Medicine medicine : userMedicines) {
      if (medicine.getMedRemaining() > 0 && medicine.getMedEXP().after(new Date())) {
        if (medicine.getMedTime().contains("เช้า")) {
          appendMedicine(getUser().getUserTime()[0] + " น.", medicine);
        }
        if (medicine.getMedTime().contains("กลางวัน")) {
          appendMedicine(getUser().getUserTime()[1] + " น.", medicine);
        }
        if (medicine.getMedTime().contains("เย็น")) {
          appendMedicine(getUser().getUserTime()[2] + " น.", medicine);
        }
        if (medicine.getMedTime().contains("ก่อนนอน")) {
          appendMedicine(getUser().getUserTime()[3] + " น.", medicine);
        }
        if (medicine.getMedTime().contains("ทุก ๆ ")) {
          if (medicine.getLastTaken() == null) {
            String startTime = timestampToTime(medicine.getDateStart()) + " น.";
            appendMedicine(startTime, medicine);
          } else {
            String lastTaken = getNextInterval(medicine.getLastTaken(), Integer.valueOf(medicine.getMedDoseStr()));
            appendMedicine(lastTaken, medicine);
          }
        }
      }
    }

    overviewCount = 0;

    for (String key : overviewItem.keySet()) {
      overviewCount++;
      JPanel panelSub = newFlowLayout();
      panelMain.add(getTimePanel(key));
      for (Medicine med : (ArrayList<Medicine>) overviewItem.get(key)) {
        panelSub.add(getDetailsPanel(med));
      }
      panelMain.add(panelSub);
    }

    if (overviewCount == 0) {
      JPanel panelSub = newFlowLayout();
      JLabel labelNothing = makeLabel("คุณยังไม่มียาที่ต้องรับประทานในขณะนี้");
      setPadding(labelNothing, 10, 0, 0, 4);
      panelSub.add(labelNothing);
      panelMain.add(panelSub);
    }

    return panelMain;
  }

  private JPanel getTimePanel(String time) {
    //JPanels
    JPanel panelMain = new JPanel(new BorderLayout());
    JPanel panelTime = newFlowLayout();

    // JLabels
    JLabel labelTime = makeSubTitleLabel(time);

    // Styling
    setPadding(panelMain, 18, 0, -2);

    panelTime.add(labelTime);
    panelMain.add(panelTime);
    return panelMain;
  }

  private JPanel getDetailsPanel(Medicine medicine) {

    // JPanels
    JPanel panelLoopInfo = new JPanel(new BorderLayout());
    JPanel panelCard = new JPanel();
    JPanel panelMed = newFlowLayout();
    JPanel panelMedInfo = new JPanel();
    JPanel panelBtn = newFlowLayout();

    String doseStr;
    if (medicine.getMedDoseStr().equals("")) {
      doseStr = "ก่อนนอน";
    } else {
      doseStr = medicine.getMedDoseStr();
    }

    // JLabels
    JLabel labelPic = getMedIcon(medicine);
    JLabel labelMedName = makeBoldLabel(medicine.getMedName());
    JLabel labelAmount;
    if (medicine.getMedTime().get(0).equals("ทุก ๆ ")) {
      labelAmount = makeSmallerLabel(
          medicine.getMedDose() + " " + medicine.getMedUnit() + " " + medicine.getMedTime().get(0)
              + doseStr);
    } else {
      labelAmount = makeSmallerLabel(
          medicine.getMedDose() + " " + medicine.getMedUnit() + " " + doseStr);
    }

    // JButtons
    JButton btnAte = makeGreyToBlueButton("ทานแล้ว");
    JButton btnSkip = makeGreyToRedButton("ข้ามเวลานี้");

    // Styling
    panelMedInfo.setLayout(new BoxLayout(panelMedInfo, BoxLayout.PAGE_AXIS));
    panelCard.setLayout(new BoxLayout(panelCard, BoxLayout.PAGE_AXIS));
    panelCard.setBorder(newCardBorder());
    setPadding(labelPic, 6, 0, 0, 8);
    setPadding(labelMedName, 7, 0, -12, 0);
    setPadding(labelAmount, 0, 0, 2, 0);
    setPadding(panelLoopInfo, 0, 20, 4, 0);
    setPadding(panelMed, 6, 36, 12, 0);
    setPadding(panelBtn, 0, 0, -6, -3);

    panelMedInfo.add(labelMedName);
    panelMedInfo.add(labelAmount);

    panelMed.add(labelPic);
    panelMed.add(panelMedInfo);
    panelCard.add(panelMed);

    panelBtn.add(btnAte);
    panelBtn.add(btnSkip);
    panelCard.add(new JSeparator(SwingConstants.HORIZONTAL));
    panelCard.add(panelBtn);

    panelLoopInfo.add(panelCard);
    return panelLoopInfo;
  }

  public int getOverviewCount() {
    return overviewCount;
  }

  public static String getNextInterval(String timestamp, int nextHour) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
    LocalDateTime date = LocalDateTime.parse(timestamp, formatter);
    LocalDateTime next = date.plusHours(nextHour);
    return next.getHour() + ":" + next.getMinute() + " น.";
  }
}
