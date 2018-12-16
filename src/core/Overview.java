package core;

import static GUI.GUIHelper.*;
import static GUI.components.AppointmentUI.getAppointmentIcon;
import static core.Core.getUser;
import static core.MedicineUtil.tableSpoonCalc;
import static core.MedicineUtil.teaSpoonCalc;
import static core.Utils.*;
import static core.MedicineUtil.getMedIcon;

import api.AppointmentDB;
import api.MedicineDB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import notification.NotificationFactory;

/**
 * All methods about rendering overview components is here
 *
 * @author jMedicine
 * @version 0.8.1
 * @since 0.7.12
 */

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
    LocalDate today = LocalDate.now();
    LocalTime now = LocalTime.now();

    JPanel panelMain = newPanelLoop();

    // Fetches all appointments
    ArrayList<Appointment> userAppointments = null;
    try {
      userAppointments = AppointmentDB.getAllAppointment(getUser().getUserId());
    } catch (SQLException | ParseException e) {
      e.printStackTrace();
    }
    // Filters only upcoming appointments
    for (Appointment app : userAppointments) {
      LocalDate appDate = LocalDate.parse(app.getDate().toString());
      LocalTime appTime = LocalTime.parse(app.getTimeStop());
      if ((today.equals(appDate) && now.isBefore(appTime)) || today.plusDays(1).equals(appDate)) {
        panelMain.add(getTimePanel("app",true));
        JPanel panelSub = newFlowLayout();
        panelSub.add(getAppPanel(app));
        panelSub.setBackground(secondaryBlue);
        setPadding(panelSub, 10, 20, 20);
        panelMain.add(panelSub);
        if (getUser().isShowNotification() && !app.isNotified()) {
          try {
              NotificationFactory.showNotification("You have an upcoming appointment.");
              app.setNotified(true);
              AppointmentDB.updateAppointment(app);
          } catch (UnsatisfiedLinkError | SQLException ignored) {
          }
        }
      }
    }

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
      LocalTime currentTime = LocalTime.parse(key.split(" ")[0]);

      Timestamp timestamp = new Timestamp(0);
      try {
        Date currentTimeDate = formatTimestamp.parse(today + " " + currentTime);
        timestamp = new Timestamp(currentTimeDate.getTime());
      } catch (ParseException e) {
        e.printStackTrace();
      }

      boolean focus = false;

      // Active period starts before the actual time for 10 minutes and ends after the actual time for 1 hour.
      int curMinuteTime = currentTime.getHour() * 60 + currentTime.getMinute();
      int checkTime = now.getHour() * 60 + now.getMinute();
      int diff = curMinuteTime - checkTime;

      if (diff <= 10 && diff >= -60) {
        focus = true;
        panelMain.add(getTimePanel(key, true));
        boolean sendNotification = false;
        for (Medicine med : (ArrayList<Medicine>) overviewItem.get(key)) {
          if (!med.getLastNotified().equals(timestamp)) {
            sendNotification = true;
            med.setLastNotified(timestamp);
            try {
              MedicineDB.updateMedicine(med);
            } catch (SQLException e) {
              e.printStackTrace();
            }
          }
          panelSub.add(getMedPanel(med, true));
        }
        if (getUser().isShowNotification() && sendNotification) {
          try {
            NotificationFactory.showNotification("It's your med time!");
          } catch (UnsatisfiedLinkError ignored) {
          }
        }
        panelSub.setBackground(secondaryBlue);
      } else {
        panelMain.add(getTimePanel(key, false));
        for (Medicine med : (ArrayList<Medicine>) overviewItem.get(key)) {
          panelSub.add(getMedPanel(med, false));
        }
      }
      if (focus) {
        setPadding(panelSub, 10, 20, 20);
      } else {
        setPadding(panelSub, 10, 0, 20);
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

  private JPanel getTimePanel(String time, boolean now) {
    //JPanels
    JPanel panelMain = new JPanel(new BorderLayout());
    JPanel panelTime = newFlowLayout();

    // JLabels
    JLabel labelTime = makeSubTitleLabel("");

    // Styling
    if (now) {
      if (time.equals("app")) {
        labelTime.setText("คุณมีนัดแพทย์ที่กำลังมาถึง");
      } else {
        labelTime.setText("ได้เวลาทานยารอบเวลา " + time);
      }
      panelMain.setBackground(secondaryBlue);
      panelTime.setBackground(secondaryBlue);
      labelTime.setForeground(Color.WHITE);
      setPadding(panelMain, 14, 20, -6);
    } else {
      labelTime.setText(time);
      setPadding(panelMain, 10, 0, -8);
    }

    panelTime.add(labelTime);
    panelMain.add(panelTime);
    return panelMain;
  }

  private JPanel getAppPanel(Appointment app) {
    // JPanels
    JPanel panelLoopInfo = new JPanel(new BorderLayout());
    JPanel panelCard = new JPanel();
    JPanel panelApp = newFlowLayout();
    JPanel panelAppInfo = new JPanel();

    // JLabels
    JLabel labelPic = getAppointmentIcon();
    JLabel labelAppName = makeBoldLabel(app.getDate() + " ตั้งแต่เวลา " + app.getTimeStart() + " น. - " + app.getTimeStop() + " น.");
    JLabel labelAppSub = makeLabel(app.getDoctor() + " โรงพยาบาล" + app.getDoctor().getHospital());

    // Styling
    panelAppInfo.setLayout(new BoxLayout(panelAppInfo, BoxLayout.PAGE_AXIS));
    panelCard.setLayout(new BoxLayout(panelCard, BoxLayout.PAGE_AXIS));
    setPadding(labelPic, 6, 0, 0, 8);
    setPadding(labelAppName, 7, 0, -12, 0);
    setPadding(labelAppSub, 0, 0, 2, 0);
    setPadding(panelLoopInfo, 0, 20, 4, 0);
    setPadding(panelApp, 6, 36, 12, 0);
      panelLoopInfo.setBackground(secondaryBlue);
      panelCard.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(new Color(166, 166, 166)),
          BorderFactory.createEmptyBorder(10, 10, 10, 10)
      ));


    panelAppInfo.add(labelAppName);
    panelAppInfo.add(labelAppSub);

    panelApp.add(labelPic);
    panelApp.add(panelAppInfo);
    panelCard.add(panelApp);

    panelLoopInfo.add(panelCard);
    return panelLoopInfo;
  }

  private JPanel getMedPanel(Medicine medicine, boolean now) {

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

    int medDose = medicine.getMedDose();
    String medUnit = medicine.getMedUnit();
    String doseInfo = medDose + " " + medUnit;

    if (medicine.getMedType().equals("liquid")) {
      // Convert ml to spoon
      int table;
      int tea;
      try {
        table = tableSpoonCalc(medDose);
        tea = teaSpoonCalc(medDose);
      } catch (NumberFormatException ignored) {
        table = 0;
        tea = 0;
      }

      String labelText = " (";
      if (table > 0) {
        labelText += table;
        labelText += " ช้อนโต๊ะ";
        if (tea > 0) {
          labelText += " ";
          labelText += tea;
          labelText += " ช้อนชา";
        }
      } else {
        labelText += tea;
        labelText += " ช้อนชา";
      }
      labelText += ")";
      doseInfo += labelText;
    }

    if (medicine.getMedTime().get(0).equals("ทุก ๆ ")) {
      labelAmount = makeSmallerLabel(doseInfo + " " + medicine.getMedTime().get(0) + doseStr);
    } else {
      labelAmount = makeSmallerLabel(doseInfo + " " + doseStr);
    }

    // JButtons
    JButton btnAte = makeGreyToBlueButton("ทานแล้ว");
    JButton btnSkip = makeGreyToRedButton("ข้ามเวลานี้");

    // Styling
    panelMedInfo.setLayout(new BoxLayout(panelMedInfo, BoxLayout.PAGE_AXIS));
    panelCard.setLayout(new BoxLayout(panelCard, BoxLayout.PAGE_AXIS));
    setPadding(labelPic, 6, 0, 0, 8);
    setPadding(labelMedName, 7, 0, -12, 0);
    setPadding(labelAmount, 0, 0, 2, 0);
    setPadding(panelLoopInfo, 0, 20, 4, 0);
    setPadding(panelMed, 6, 36, 12, 0);
    setPadding(panelBtn, 0, 0, -6, -3);
    if (now) {
      panelLoopInfo.setBackground(secondaryBlue);
      panelCard.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(new Color(166, 166, 166)),
          BorderFactory.createEmptyBorder(10, 10, 10, 10)
      ));
    } else {
      // Advanced border has problem rendering the background
      panelCard.setBorder(newCardBorder());
    }

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

  private static String getNextInterval(String timestamp, int nextHour) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
    LocalDateTime date = LocalDateTime.parse(timestamp, formatter);
    LocalDateTime next = date.plusHours(nextHour);
    return next.getHour() + ":" + next.getMinute() + " น.";
  }
}
