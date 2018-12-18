package core;

import static GUI.GUIHelper.fireConfirmDialog;
import static GUI.GUIHelper.fireDBErrorDialog;
import static GUI.GUIHelper.formatTimestamp;
import static GUI.GUIHelper.formatYMD;
import static GUI.GUIHelper.getSuccessIcon;
import static GUI.GUIHelper.makeBoldLabel;
import static GUI.GUIHelper.makeGreyToBlueButton;
import static GUI.GUIHelper.makeGreyToRedButton;
import static GUI.GUIHelper.makeLabel;
import static GUI.GUIHelper.makeSmallerLabel;
import static GUI.GUIHelper.makeSubTitleLabel;
import static GUI.GUIHelper.newCardBorder;
import static GUI.GUIHelper.newFlowLayout;
import static GUI.GUIHelper.newPanelLoop;
import static GUI.GUIHelper.secondaryBlue;
import static GUI.GUIHelper.setPadding;
import static GUI.components.AppointmentUI.getAppointmentIcon;
import static GUI.components.MedicineUI.reloadMedicines;
import static core.Core.getUser;
import static core.MedicineUtil.getMedIcon;
import static core.MedicineUtil.tableSpoonCalc;
import static core.MedicineUtil.teaSpoonCalc;
import static core.Utils.stringToTimestamp;
import static core.Utils.timestampToTime;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import notification.NotificationFactory;

/**
 * All methods about rendering overview components is here
 *
 * @author jMedicine
 * @version 0.9.1
 * @since 0.7.12
 */

public class Overview {

  private TreeMap<String, ArrayList> overviewItem;
  private int overviewCount;

  private void initTreeMap() {
    overviewItem = new TreeMap<>();
    overviewCount = 0;
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
    } catch (SQLException | ParseException ignored) {
    }
    // Filters only upcoming appointments
    for (Appointment app : userAppointments) {
      LocalDate appDate = LocalDate.parse(app.getDate().toString());
      LocalTime appTime = LocalTime.parse(app.getTimeStop());
      if ((today.equals(appDate) && now.isBefore(appTime)) || today.plusDays(1).equals(appDate)) {
        overviewCount++;
        panelMain.add(getTimePanel("app", true));
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

    // User's time range
    int morningStart = Integer.valueOf(getUser().getUserTime()[0].split(":")[0]);
    int afternoonStart = Integer.valueOf(getUser().getUserTime()[1].split(":")[0]);
    int eveningStart = Integer.valueOf(getUser().getUserTime()[2].split(":")[0]);
    int bed = Integer.valueOf(getUser().getUserTime()[3].split(":")[0]);

    // Filters only active medicines && not skipped
    for (Medicine medicine : userMedicines) {
      //LocalDateTime currentLastTaken = medicine.getLastTaken().toLocalDateTime();
      if (medicine.getMedRemaining() > 0 && medicine.getMedEXP().after(new Date())) {
        if (medicine.getMedTime().contains("เช้า")) {
          boolean willBeAdded = true;
          if (medicine.getSkipped().size() > 0) {
            // Check skipped
            for (String skipped : medicine.getSkipped()) {
              LocalDate skippedDate = LocalDate.parse(skipped.split(" ")[0]);
              int skippedHour = Integer.valueOf(skipped.split(" ")[1].split(":")[0]);
              if (today.equals(skippedDate) && (skippedHour >= morningStart
                  && skippedHour < afternoonStart)) {
                willBeAdded = false;
              }
            }
          } else if (medicine.getTaken().size() > 0) {
            // Check taken
            for (String taken : medicine.getTaken()) {
              LocalDate takenDate = LocalDate.parse(taken.split(" ")[0]);
              int takenHour = Integer.valueOf(taken.split(" ")[1].split(":")[0]);
              if (today.equals(takenDate) && (takenHour >= morningStart
                  && takenHour < afternoonStart)) {
                willBeAdded = false;
              }
            }
          }
          if (willBeAdded) {
            appendMedicine(getUser().getUserTime()[0] + " น.", medicine);
          }
        }

        if (medicine.getMedTime().contains("กลางวัน")) {
          boolean willBeAdded = true;
          if (medicine.getSkipped().size() > 0) {
            // Check skipped
            for (String skipped : medicine.getSkipped()) {
              LocalDate skippedDate = LocalDate.parse(skipped.split(" ")[0]);
              int skippedHour = Integer.valueOf(skipped.split(" ")[1].split(":")[0]);
              if (today.equals(skippedDate) && (skippedHour >= afternoonStart
                  && skippedHour < eveningStart)) {
                willBeAdded = false;
              }
            }
          } else if (medicine.getTaken().size() > 0) {
            // Check taken
            for (String taken : medicine.getTaken()) {
              LocalDate takenDate = LocalDate.parse(taken.split(" ")[0]);
              int takenHour = Integer.valueOf(taken.split(" ")[1].split(":")[0]);
              if (today.equals(takenDate) && (takenHour >= afternoonStart
                  && takenHour < eveningStart)) {
                willBeAdded = false;
              }
            }
          }
          if (willBeAdded) {
            appendMedicine(getUser().getUserTime()[1] + " น.", medicine);
          }
        }

        if (medicine.getMedTime().contains("เย็น")) {
          boolean willBeAdded = true;
          if (medicine.getSkipped().size() > 0) {
            // Check skipped
            for (String skipped : medicine.getSkipped()) {
              LocalDate skippedDate = LocalDate.parse(skipped.split(" ")[0]);
              int skippedHour = Integer.valueOf(skipped.split(" ")[1].split(":")[0]);
              if (today.equals(skippedDate) && (skippedHour >= eveningStart
                  && skippedHour < bed)) {
                willBeAdded = false;
              }
            }
          } else if (medicine.getTaken().size() > 0) {
            // Check taken
            for (String taken : medicine.getTaken()) {
              LocalDate takenDate = LocalDate.parse(taken.split(" ")[0]);
              int takenHour = Integer.valueOf(taken.split(" ")[1].split(":")[0]);
              if (today.equals(takenDate) && (takenHour >= eveningStart
                  && takenHour < bed)) {
                willBeAdded = false;
              }
            }
          }
          if (willBeAdded) {
            appendMedicine(getUser().getUserTime()[2] + " น.", medicine);
          }
        }

        if (medicine.getMedTime().contains("ก่อนนอน")) {
          boolean willBeAdded = true;
          if (medicine.getSkipped().size() > 0) {
            // Check skipped
            for (String skipped : medicine.getSkipped()) {
              LocalDate skippedDate = LocalDate.parse(skipped.split(" ")[0]);
              int skippedHour = Integer.valueOf(skipped.split(" ")[1].split(":")[0]);
              if (today.equals(skippedDate) && (skippedHour >= bed)) {
                willBeAdded = false;
              }
            }
          } else if (medicine.getTaken().size() > 0) {
            // Check taken
            for (String taken : medicine.getTaken()) {
              LocalDate takenDate = LocalDate.parse(taken.split(" ")[0]);
              int takenHour = Integer.valueOf(taken.split(" ")[1].split(":")[0]);
              if (today.equals(takenDate) && (takenHour >= bed)) {
                willBeAdded = false;
              }
            }
          }
          if (willBeAdded) {
            appendMedicine(getUser().getUserTime()[3] + " น.", medicine);
          }
        }

        if (medicine.getMedTime().contains("ทุก ๆ ")) {
          String lastTaken = getNextInterval(medicine.getLastTaken(),
              Integer.valueOf(medicine.getMedDoseStr().split(" ")[0]));
          appendMedicine(lastTaken, medicine);
        }
      }
    }

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
          panelSub.add(getMedPanel(med, key.split(" ")[0], true));
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
          panelSub.add(getMedPanel(med, key.split(" ")[0], false));
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
      setPadding(labelNothing, 10, 0, 0, 0);
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
    JLabel labelAppName = makeBoldLabel(
        app.getDate() + " ตั้งแต่เวลา " + app.getTimeStart() + " น. - " + app.getTimeStop()
            + " น.");
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

  private JPanel getMedPanel(Medicine medicine, String time, boolean now) {

    // JPanels
    JPanel panelLoopInfo = new JPanel(new BorderLayout());
    JPanel panelCard = new JPanel();
    JPanel panelMed = newFlowLayout();
    JPanel panelMedInfo = new JPanel();
    JPanel panelBtn = newFlowLayout();
    JPanel panelSuccess = newFlowLayout();

    String doseStr;
    if (medicine.getMedDoseStr().equals("")) {
      doseStr = "ก่อนนอน";
    } else {
      doseStr = medicine.getMedDoseStr();
    }

    String medHour = time.split(":")[0];
    String medMinute = time.split(":")[1];

    // JLabels
    JLabel labelMedPic = getMedIcon(medicine);
    JLabel labelMedName = makeBoldLabel(medicine.getMedName());
    JLabel labelAmount;
    JLabel labelSuccessPic = getSuccessIcon();
    JLabel labelSuccess = makeLabel(" ");

    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);

    panelSuccess.add(labelSuccessPic);
    panelSuccess.add(labelSuccess);

    LocalDate today = LocalDate.now();
    LocalTime currentTime = LocalTime.now();

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
    panelSuccess.setVisible(false);
    setPadding(labelMedPic, 6, 0, 0, 8);
    setPadding(labelMedName, 7, 0, -12, 0);
    setPadding(labelAmount, 0, 0, 2, 0);
    setPadding(panelLoopInfo, 0, 20, 4, 0);
    setPadding(panelMed, 6, 36, 12, 0);
    setPadding(panelBtn, 0, 0, -6, -3);
    setPadding(labelSuccessPic, -4, -12, -4, -4);
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

    // Listeners
    btnAte.addActionListener(e -> {
      String medTime = medHour + ":" + medMinute;
      medicine.setMedRemaining(medicine.getMedRemaining() - medDose);
      medicine.appendTaken(today + " " + medTime);
      try {
        MedicineDB.updateMedicine(medicine);
        labelSuccess
            .setText("ทานยาเรียบร้อยแล้ว ยาคงเหลือ " + medicine.getMedRemaining() + " " + medUnit);
        panelBtn.setVisible(false);
        panelSuccess.setVisible(true);
        reloadMedicines();

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//          @Override
//          public void run() {
//            panelLoopInfo.setVisible(false);
//            btnAte.setVisible(false);
//            btnSkip.setVisible(false);
//          }
//        }, 3*1000);

      } catch (SQLException e1) {
        e1.printStackTrace();
        fireDBErrorDialog();
      }
    });
    btnSkip.addActionListener(skip -> {
      int result = fireConfirmDialog(
          "ต้องการข้ามการทานยา " + medicine.getMedName() + " ในเวลานี้ใช่หรือไม่");
      if (result == JOptionPane.YES_OPTION) {
        String medTime = medHour + ":" + medMinute;
        medicine.appendSkipped(today + " " + medTime);
        if (medicine.getMedTime().get(0).equals("ทุก ๆ ")) {
          medicine.setLastTaken(new Timestamp(stringToTimestamp(today + " " + medTime)));
        }
        try {
          MedicineDB.updateMedicine(medicine);
          labelMedName.setText(medicine.getMedName() + " (ข้ามแล้ว)");
          labelMedName.setForeground(Color.GRAY);
          labelAmount.setForeground(Color.GRAY);
          separator.setVisible(false);
          panelBtn.setVisible(false);
          btnAte.setVisible(false);
          btnSkip.setVisible(false);
          reloadMedicines();
        } catch (SQLException e1) {
          e1.printStackTrace();
          fireDBErrorDialog();
        }
      }
    });

    panelMedInfo.add(labelMedName);
    panelMedInfo.add(labelAmount);

    panelMed.add(labelMedPic);
    panelMed.add(panelMedInfo);
    panelCard.add(panelMed);

    panelBtn.add(btnAte);
    panelBtn.add(btnSkip);
    panelCard.add(separator);
    panelCard.add(panelBtn);
    panelCard.add(panelSuccess);

    panelLoopInfo.add(panelCard);
    return panelLoopInfo;
  }

  public int getOverviewCount() {
    return overviewCount;
  }

  private static String getNextInterval(Timestamp timestamp, int nextHour) {
    LocalDateTime date = timestamp.toLocalDateTime();
    LocalDateTime next = date.plusHours(nextHour);
    return String.format("%02d:%02d น.", next.getHour(), next.getMinute());
  }
}
