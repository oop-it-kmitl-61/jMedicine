package core;

import static GUI.GUIHelper.makeBoldLabel;
import static GUI.GUIHelper.makeGreyToBlueButton;
import static GUI.GUIHelper.makeGreyToRedButton;
import static GUI.GUIHelper.makeSmallerLabel;
import static GUI.GUIHelper.makeSubTitleLabel;
import static GUI.GUIHelper.newCardBorder;
import static GUI.GUIHelper.newFlowLayout;
import static GUI.GUIHelper.newPanelLoop;
import static GUI.GUIHelper.setPadding;
import static core.Core.getUser;
import static core.MedicineUtil.getMedIcon;

import api.MedicineDB;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class Overview {

  public JPanel renderOverview() {
    // JPanels
    JPanel panelMain = newPanelLoop();
    JPanel panelSub = newFlowLayout();

    Date now = new Date();

    // Fetches all medicines
    ArrayList<Medicine> userMedicines = null;
    try {
      userMedicines = MedicineDB.getAllMedicine(getUser().getUserId());
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Filters only active medicines
    ArrayList<Medicine> morningMedicines = new ArrayList<>();
    ArrayList<Medicine> afternoonMedicines = new ArrayList<>();
    ArrayList<Medicine> eveningMedicines = new ArrayList<>();
    ArrayList<Medicine> bedMedicines = new ArrayList<>();
    // TODO : Intervals
    ArrayList<Medicine> intervalMedicines = new ArrayList<>();

    for (Medicine medicine : userMedicines) {
      if (medicine.getMedRemaining() > 0) {
        if (medicine.getMedTime().contains("เช้า")) {
          morningMedicines.add(medicine);
        }
        if (medicine.getMedTime().contains("กลางวัน")) {
          afternoonMedicines.add(medicine);
        }
        if (medicine.getMedTime().contains("เย็น")) {
          eveningMedicines.add(medicine);
        }
        if (medicine.getMedTime().contains("ก่อนนอน")) {
          bedMedicines.add(medicine);
        }
      }
    }

    // Morning Medicines
    if (morningMedicines.size() > 0 ) {
      // TODO : GET TIME FORM THE USER'S SETTINGS
      panelMain.add(getTimePanel("08.30 น."));
      for (Medicine med : morningMedicines) {
        panelSub.add(getDetailsPanel(med));
      }
      panelMain.add(panelSub);
    }

    // Afternoon Medicines
    if (afternoonMedicines.size() > 0 ) {
      // TODO : GET TIME FORM THE USER'S SETTINGS
      panelMain.add(getTimePanel("12.30 น."));
      panelSub = newFlowLayout();
      for (Medicine med : afternoonMedicines) {
        panelSub.add(getDetailsPanel(med));
      }
      panelMain.add(panelSub);
    }

    // Evening Medicines
    if (eveningMedicines.size() > 0 ) {
      // TODO : GET TIME FORM THE USER'S SETTINGS
      panelMain.add(getTimePanel("18.30 น."));
      panelSub = newFlowLayout();
      for (Medicine med : eveningMedicines) {
        panelSub.add(getDetailsPanel(med));
      }
      panelMain.add(panelSub);
    }

    // Bed Medicines
    if (bedMedicines.size() > 0 ) {
      // TODO : GET TIME FORM THE USER'S SETTINGS
      panelMain.add(getTimePanel("22.30 น."));
      panelSub = newFlowLayout();
      for (Medicine med : bedMedicines) {
        panelSub.add(getDetailsPanel(med));
      }
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
    JLabel labelAmount = makeSmallerLabel(medicine.getMedDose() + " " + medicine.getMedUnit() + " " + doseStr);

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
}
