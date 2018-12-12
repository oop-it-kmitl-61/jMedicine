package GUI.components;

import static GUI.GUI.*;
import static GUI.GUIHelper.*;
import static core.MedicineUtil.*;
import static core.Core.*;
import static core.Utils.timestampToString;

import GUI.GUIHelper;
import api.MedicineDB;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import core.Medicine;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * All UIs and handler methods about a medicine will be written here.
 *
 * @author jMedicine
 * @version 0.7.12
 * @since 0.7.0
 */

public class MedicineUI {

  private static JPanel panelMedicines, panelColor, panelTabletColor, panelCapsuleColor, panelLiquidColor;

  private static JLabel labelUnit1, labelUnit2;
  private static JTextField tfMedName, tfMedDescription, tfAmount, tfTotalMeds, tfEvery;
  private static JComboBox cbMedType, cbTabletColor, cbLiquidColor, cbCapsuleColor01, cbCapsuleColor02;
  private static JCheckBox cbMorning, cbAfternoon, cbEvening, cbBed, cbEvery;
  private static JRadioButton rbBefore, rbAfter, rbImmediately;
  private static ButtonGroup bgMedDoseStr;
  private static DatePicker pickerStart, pickerEXP;
  private static TimePicker pickerStartTime;

  public static void panelAllMedicines() {
    /*
      Creates GUI displaying all medicines that user has had input.
      All medicines will be displayed in a card with a medicine icon,
      a name and a short summary.
     */

    // JPanels
    JPanel panelTitle = new JPanel(new BorderLayout());
    panelMedicines = new JPanel(new BorderLayout());
    JPanel panelLoop = newPanelLoop();

    JLabel labelTitle = makeTitleLabel("ยาทั้งหมด");
    panelTitle.add(labelTitle);

    // Fetch all medicines from the records
    ArrayList<Medicine> userMedicines = null;
    try {
      userMedicines = MedicineDB.getAllMedicine(getUser().getUserId());
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Init panel loop
    panelLoop.add(makeNewButton("เพิ่มยาใหม่"));

    if (userMedicines.isEmpty()) {
      labelTitle.setText("คุณยังไม่มียาที่บันทึกไว้");
    } else {
      labelTitle.setText("ยาทั้งหมด");
      for (Medicine medCurrent : userMedicines) {
        JPanel cardLoop = makeMedCard(medCurrent);
        panelLoop.add(cardLoop);
      }
    }

    JScrollPane scrollPane = makeScrollPane(panelLoop);

    // Add all panels into the main panel
    panelMedicines.add(panelTitle, BorderLayout.NORTH);
    panelMedicines.add(scrollPane);

    panelRight.add(panelMedicines, "ยาทั้งหมด");
    panelRight.add(panelAddMedicine(), "เพิ่มยาใหม่");
  }

  private static JPanel panelViewMedicine(Medicine medicine) {
    /* Creates GUI displaying all information of a single medicine */

    // JPanels
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelButtons = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());

    // JLabels
    JLabel labelPic = getMedIcon(medicine);
    String medName = medicine.getMedName();

    // JButtons
    JButton btnEdit = makeBlueButton("แก้ไขข้อมูล");
    JButton btnRemove = makeRemoveButton();
    JButton labelTitle = makeBackButton(medName, "ยาทั้งหมด");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    //panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
    setPadding(labelPic, 0, 0, 10);
    setPadding(panelTitle, -6, 0, 20, 8);
    setPadding(panelBody, 0, 0, 0, 38);
    setPadding(panelView, 0, 0, 0, -20);

    // Listeners
    btnEdit.addActionListener(e -> editSwitcher(panelRight, panelEditMedicine(medicine)));
    btnRemove.addActionListener(e -> {
      int dialogResult = fireConfirmDialog(
          "ต้องการลบยานี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");
      System.out.println(dialogResult);
      if (dialogResult == JOptionPane.YES_OPTION) {
        String labelMessage;
        try {
          MedicineDB.removeMedicine(medicine);
          labelMessage = getRemoveSuccessfulMessage("ยา");
          fireSuccessDialog(labelMessage);
          reloadOverview();
        } catch (SQLException ex) {
          labelMessage = getRemoveFailedMessage("ยา");
          fireErrorDialog(labelMessage);
        }

        panelRight.remove(panelMedicines);
        panelAllMedicines();
        backTo("ยาทั้งหมด");
        panelRight.remove(panelViewMedicine(medicine));
      }
    });

    panelTitle.add(labelTitle);
    panelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("ลักษณะยา"));
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = newFlowLayout();
    panelSub.add(labelPic);
    setPadding(panelSub, 6, 0, 10);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("ข้อมูลพื้นฐาน"));
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("ชื่อยา: "));
    panelSub.add(makeLabel(medName));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("คำอธิบาย: "));
    panelSub.add(makeLabel(medicine.getMedDescription()));
    setPadding(panelSub, -10, 0, 0);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("จำนวนยาที่เหลือ: "));
    panelSub.add(makeLabel(String.valueOf(medicine.getMedRemaining())));
    setPadding(panelSub, -10, 0, 10);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("เวลาที่ต้องรับประทาน"));
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    if (medicine.getMedTime().get(0).equals("ทุก ๆ ")) {
      panelSub = newFlowLayout();
      panelSub.add(makeLabel(medicine.getMedTime().get(0)));
      panelSub.add(makeLabel(medicine.getMedDoseStr() + " ครั้งละ " + medicine.getMedDose() + " " + medicine.getMedUnit()));
      panelBody.add(panelSub);
    } else {
      panelSub = newFlowLayout();
      for (int i = 0; i < medicine.getMedTime().size(); i++) {
        panelSub.add(makeLabel(medicine.getMedTime().get(i)));
      }
      setPadding(panelSub, 0, 0, -10);
      panelBody.add(panelSub);

      panelSub = newFlowLayout();
      String space = " ";
      if (medicine.getMedDoseStr().equals("")) {
        space = "";
      }
      panelSub.add(makeLabel(
          medicine.getMedDoseStr() + space + "ครั้งละ " + medicine.getMedDose() + " " + medicine.getMedUnit()
      ));
      panelBody.add(panelSub);
    }

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("ข้อมูลอื่น ๆ"));
    setPadding(panelSub, 20, 0, 0);
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("จำนวนยาเริ่มต้น: "));
    panelSub.add(makeLabel(String.valueOf(medicine.getMedTotal())));
    setPadding(panelSub, 0, 0, -10);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("วันที่เพิ่มยา: "));
    panelSub.add(makeLabel(formatYMD.format(medicine.getDateAdded())));
    setPadding(panelSub, 0, 0, -10);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("วันที่เริ่มทานยา: "));
    panelSub.add(makeLabel(timestampToString(medicine.getDateStart())));
    setPadding(panelSub, 0, 0, -10);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("วันหมดอายุ: "));
    panelSub.add(makeLabel(GUIHelper.formatYMD.format(medicine.getMedEXP())));
    panelBody.add(panelSub);

    panelButtons.add(btnEdit, BorderLayout.CENTER);
    panelButtons.add(btnRemove, BorderLayout.EAST);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(scrollPane, BorderLayout.CENTER);
    panelView.add(panelButtons, BorderLayout.SOUTH);

    return panelView;
  }

  private static JPanel panelAddMedicine() {
    /* Creates outer GUI when user add a new medicine from all medicines page. */

    // JPanels
    JPanel panelAddMedicine = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มยาใหม่", "ยาทั้งหมด");
    JButton btnSave = makeBlueButton("บันทึกยา");

    // Styling
    setPadding(panelAddMedicine, -2, 0, 0, -16);
    setPadding(panelBody, 0, 0, 20, 40);
    setPadding(panelTitle, 0, 0, 20);

    // Listeners
    btnSave.addActionListener(e -> {
      String selectedMedType = getMedType()[cbMedType.getSelectedIndex()];
      String type;
      String selectedColor = "";
      if (selectedMedType.equals("ยาแคปซูล")) {
        type = "capsule";
        selectedColor = getTabletColor()[cbCapsuleColor01.getSelectedIndex()];
        selectedColor += "-";
        selectedColor += getTabletColor()[cbCapsuleColor02.getSelectedIndex()];
      } else if (selectedMedType.equals("ยาเม็ด")) {
        type = "tablet";
        selectedColor = getTabletColor()[cbTabletColor.getSelectedIndex()];
      } else if (selectedMedType.equals("ยาน้ำ")) {
        type = "liquid";
        selectedColor = getLiquidColor()[cbLiquidColor.getSelectedIndex()];
      } else {
        type = "spray";
      }

      // Dose Information
      ArrayList<String> selectedMedTime = new ArrayList<>();
      String selectedDoseStr = "";
      if (cbMorning.isSelected()) {
        selectedMedTime.add("เช้า");
      }
      if (cbAfternoon.isSelected()) {
        selectedMedTime.add("กลางวัน");
      }
      if (cbEvening.isSelected()) {
        selectedMedTime.add("เย็น");
      }
      if (cbBed.isSelected()) {
        selectedMedTime.add("ก่อนนอน");
      }
      if (cbEvery.isSelected()) {
        selectedMedTime.add("ทุก ๆ ");
        selectedDoseStr = tfEvery.getText() + " ชั่วโมง";
      }

      if (rbBefore.isSelected()) {
        selectedDoseStr = "ก่อนอาหาร";
      } else if (rbAfter.isSelected()) {
        selectedDoseStr = "หลังอาหาร";
      } else if (rbImmediately.isSelected()) {
        selectedDoseStr = "หลังอาหารทันที / พร้อมอาหาร";
      }

      Date exp = Date.from(pickerEXP.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
      String dateStart = pickerStart.getDate() + " " + pickerStartTime.getTime();
      Medicine med = new Medicine(tfMedName.getText(), type, selectedColor,
          tfMedDescription.getText(), selectedMedTime, selectedDoseStr,
          Integer.valueOf(tfAmount.getText()),
          Integer.valueOf(tfTotalMeds.getText()), exp, dateStart);
      try {
        MedicineDB.addMedicine(med, getUser().getUserId());
        fireSuccessDialog("ยา " + med.getMedName() + " ได้ถูกเพิ่มเรียบร้อยแล้ว");
        panelRight.remove(panelMedicines);
        panelAllMedicines();
        backTo("ยาทั้งหมด");
        panelRight.remove(panelAddMedicine());
        panelRight.remove(panelBody);
        panelRight.add(panelAddMedicine(), "เพิ่มยาใหม่");
        reloadOverview();
      } catch (SQLException e1) {
        fireDBErrorDialog();
        e1.printStackTrace();
      }
    });

    panelTitle.add(btnBack);
    panelBody.add(form(), BorderLayout.CENTER);
    panelBody.add(btnSave, BorderLayout.SOUTH);

    panelAddMedicine.add(panelTitle, BorderLayout.NORTH);
    panelAddMedicine.add(panelBody, BorderLayout.CENTER);

    return panelAddMedicine;
  }

  public static JPanel panelFirstMedicine() {

    // JPanels
    JPanel panelFirstMed = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());
    JPanel panelBtn = new JPanel(new BorderLayout());

    // JButton
    JButton btnSave = makeBlueButton("บันทึกยา");
    JButton btnSkip = makeRedButton("ข้ามขั้นตอนนี้");

    panelTitle.add(makeTitleLabel("เพิ่มยาตัวแรกของคุณ"));

    panelBtn.add(btnSave, BorderLayout.CENTER);
    panelBtn.add(btnSkip, BorderLayout.EAST);

    panelBody.add(form(), BorderLayout.CENTER);
    panelBody.add(panelBtn, BorderLayout.SOUTH);

    // Styling
    setPadding(panelFirstMed, 20, 20, 0);
    setPadding(panelBody, 0, 0, 20, 20);
    setPadding(panelTitle, 20, 0, 20, 20);

    // Listeners
    btnSave.addActionListener(e -> {
      String selectedMedType = getMedType()[cbMedType.getSelectedIndex()];
      String type;
      String selectedColor = "";
      if (selectedMedType.equals("ยาแคปซูล")) {
        type = "capsule";
        selectedColor = getTabletColor()[cbCapsuleColor01.getSelectedIndex()];
        selectedColor += "-";
        selectedColor += getTabletColor()[cbCapsuleColor02.getSelectedIndex()];
      } else if (selectedMedType.equals("ยาเม็ด")) {
        type = "tablet";
        selectedColor = getTabletColor()[cbTabletColor.getSelectedIndex()];
      } else if (selectedMedType.equals("ยาน้ำ")) {
        type = "liquid";
        selectedColor = getLiquidColor()[cbLiquidColor.getSelectedIndex()];
      } else {
        type = "spray";
      }

      // Dose Information
      ArrayList<String> selectedMedTime = new ArrayList<>();
      String selectedDoseStr = "";
      if (cbMorning.isSelected()) {
        selectedMedTime.add("เช้า");
      }
      if (cbAfternoon.isSelected()) {
        selectedMedTime.add("กลางวัน");
      }
      if (cbEvening.isSelected()) {
        selectedMedTime.add("เย็น");
      }
      if (cbBed.isSelected()) {
        selectedMedTime.add("ก่อนนอน");
      }
      if (cbEvery.isSelected()) {
        selectedMedTime.add("ทุก ๆ ");
        selectedDoseStr = tfEvery.getText() + " ชั่วโมง";
      }

      if (rbBefore.isSelected()) {
        selectedDoseStr = "ก่อนอาหาร";
      } else if (rbAfter.isSelected()) {
        selectedDoseStr = "หลังอาหาร";
      } else if (rbImmediately.isSelected()) {
        selectedDoseStr = "หลังอาหารทันที / พร้อมอาหาร";
      }
      System.out.println(pickerEXP.getDate());
      Date exp = Date.from(pickerEXP.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
      String dateStart = pickerStart.getDate() + " " + pickerStartTime.getTime();
      Medicine med = new Medicine(tfMedName.getText(), type, selectedColor,
          tfMedDescription.getText(), selectedMedTime, selectedDoseStr,
          Integer.valueOf(tfAmount.getText()),
          Integer.valueOf(tfTotalMeds.getText()), exp, dateStart);
      try {
        MedicineDB.addMedicine(med, getUser().getUserId());
        fireSuccessDialog("ยา " + med.getMedName() + " ได้ถูกเพิ่มเรียบร้อยแล้ว");
        panelRight.remove(panelMedicines);
        panelAllMedicines();
        backTo("ยาทั้งหมด");
        panelRight.remove(panelAddMedicine());
        panelRight.remove(panelBody);
        panelRight.add(panelAddMedicine(), "เพิ่มยาใหม่");
        frameWelcome.setVisible(false);
        frameMain.setVisible(true);
        frameWelcome = null;
        reloadOverview();
      } catch (SQLException e1) {
        fireDBErrorDialog();
        e1.printStackTrace();
      }
    });
    btnSkip.addActionListener(e -> {
      if (frameWelcome == null) {
        CardLayout cl = (CardLayout) (panelRight.getLayout());
        cl.show(panelRight, "ยาทั้งหมด");
      } else {
        frameWelcome.setVisible(false);
        frameMain.setVisible(true);
        frameWelcome = null;
      }
    });

    panelFirstMed.add(panelTitle, BorderLayout.NORTH);
    panelFirstMed.add(panelBody, BorderLayout.CENTER);

    return panelFirstMed;
  }

  private static JPanel panelEditMedicine(Medicine medicine) {
    /* Creates GUI of the form for editing a new medicine. */

    // JPanels
    JPanel panelEditMed = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel(new BorderLayout());
    JScrollPane form = form();

    // JButtons
    JButton btnBack = makeBackButton("แก้ไขยา", medicine.getMedName());
    JButton btnEdit = makeBlueButton("บันทึก");

    panelTitle.add(btnBack);
    panelBody.add(form, BorderLayout.CENTER);
    panelBody.add(btnEdit, BorderLayout.SOUTH);

    // Styling
    setPadding(form, 0, 0, 10, 16);

    // Set existed info
    tfAmount.setText(String.valueOf(medicine.getMedDose()));
    tfMedName.setText(medicine.getMedName());
    tfMedDescription.setText(medicine.getMedDescription());
    tfTotalMeds.setText(String.valueOf(medicine.getMedTotal()));

    String[] dateStart = medicine.getDateStart().split(" ");
    String startDate = "";
    String startTime = "";
    try {
      Date date = formatYMD.parse(dateStart[0]);
      startDate = formatDatePicker.format(date);
      Date time = formatHM.parse(dateStart[1]);
      startTime = formatHM.format(time);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    pickerStart.setText(startDate);
    pickerStartTime.setText(startTime);
    pickerEXP.setText(formatDatePicker.format(medicine.getMedEXP()));

    switch (medicine.getMedType()) {
      case "tablet":
        cbMedType.setSelectedIndex(0);
        cbTabletColor.setSelectedIndex(getTabletColorIndex(medicine.getMedColor()));
        panelColor.setVisible(true);
        panelTabletColor.setVisible(true);
        panelCapsuleColor.setVisible(false);
        panelLiquidColor.setVisible(false);
        break;
      case "capsule":
        cbMedType.setSelectedIndex(1);
        String[] currentColors = medicine.getMedColor().split("-");
        cbCapsuleColor01.setSelectedIndex(getTabletColorIndex(currentColors[0]));
        cbCapsuleColor02.setSelectedIndex(getTabletColorIndex(currentColors[1]));
        panelColor.setVisible(true);
        panelTabletColor.setVisible(false);
        panelCapsuleColor.setVisible(true);
        panelLiquidColor.setVisible(false);
        break;
      case "liquid":
        cbMedType.setSelectedIndex(2);
        cbLiquidColor.setSelectedIndex(getLiquidColorIndex(medicine.getMedColor()));
        panelColor.setVisible(true);
        panelTabletColor.setVisible(false);
        panelCapsuleColor.setVisible(false);
        panelLiquidColor.setVisible(true);
        break;
      case "spray":
        cbMedType.setSelectedIndex(3);
    }

    for (int i = 0; i < medicine.getMedTime().size(); i++) {
      switch (medicine.getMedTime().get(i)) {
        case "เช้า":
          cbMorning.setSelected(true);
          break;
        case "กลางวัน":
          cbAfternoon.setSelected(true);
          break;
        case "เย็น":
          cbEvening.setSelected(true);
          break;
        case "ก่อนนอน":
          cbBed.setSelected(true);
          break;
        case "ทุก ๆ ":
          cbEvery.setSelected(true);
          tfEvery.setText(medicine.getMedDoseStr().split(" ")[0]);
          cbMorning.setEnabled(false);
          cbMorning.setSelected(false);
          cbAfternoon.setEnabled(false);
          cbAfternoon.setSelected(false);
          cbEvening.setEnabled(false);
          cbEvening.setSelected(false);
          cbBed.setEnabled(false);
          cbBed.setSelected(false);
          bgMedDoseStr.clearSelection();
          rbBefore.setEnabled(false);
          rbAfter.setEnabled(false);
          rbImmediately.setEnabled(false);
      }
    }

    // Listeners
    medTimeRadioHandler(medicine);
    btnEdit.addActionListener(e -> {
      String selectedMedType = getMedType()[cbMedType.getSelectedIndex()];
      String type;
      String selectedColor = "";
      if (selectedMedType.equals("ยาแคปซูล")) {
        type = "capsule";
        selectedColor = getTabletColor()[cbCapsuleColor01.getSelectedIndex()];
        selectedColor += "-";
        selectedColor += getTabletColor()[cbCapsuleColor02.getSelectedIndex()];
      } else if (selectedMedType.equals("ยาเม็ด")) {
        type = "tablet";
        selectedColor = getTabletColor()[cbTabletColor.getSelectedIndex()];
      } else if (selectedMedType.equals("ยาน้ำ")) {
        type = "liquid";
        selectedColor = getLiquidColor()[cbLiquidColor.getSelectedIndex()];
      } else {
        type = "spray";
      }

      // DOSE INFORMATION
      ArrayList<String> selectedMedTime = new ArrayList<>();
      String selectedDoseStr = "";
      if (cbMorning.isSelected()) {
        selectedMedTime.add("เช้า");
      }
      if (cbAfternoon.isSelected()) {
        selectedMedTime.add("กลางวัน");
      }
      if (cbEvening.isSelected()) {
        selectedMedTime.add("เย็น");
      }
      if (cbBed.isSelected()) {
        selectedMedTime.add("ก่อนนอน");
      }
      if (cbEvery.isSelected()) {
        selectedMedTime.add("ทุก ๆ ");
        selectedDoseStr = tfEvery.getText() + " ชั่วโมง";
      }

      if (rbBefore.isSelected()) {
        selectedDoseStr = "ก่อนอาหาร";
      } else if (rbAfter.isSelected()) {
        selectedDoseStr = "หลังอาหาร";
      } else if (rbImmediately.isSelected()) {
        selectedDoseStr = "หลังอาหารทันที / พร้อมอาหาร";
      }
      Date exp = Date.from(pickerEXP.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
      String newDateStart = pickerStart.getDate() + " " + pickerStartTime.getTime();
      medicine.setMedName(tfMedName.getText());
      medicine.setMedType(type);
      medicine.setMedColor(selectedColor);
      medicine.setMedDescription(tfMedDescription.getText());
      medicine.setMedTime(selectedMedTime);
      medicine.setMedDoseStr(selectedDoseStr);
      medicine.setMedDose(Integer.valueOf(tfAmount.getText()));
      medicine.setMedTotal(Integer.valueOf(tfTotalMeds.getText()));
      medicine.setMedEXP(exp);
      medicine.setDateStart(newDateStart);
      try {
        MedicineDB.updateMedicine(medicine);
        fireSuccessDialog("แก้ไขยา " + medicine.getMedName() + " เรียบร้อยแล้ว");
        panelRight.remove(panelViewMedicine(medicine));
        panelRight.add(panelViewMedicine(medicine));
        reload();
        backTo("ยาทั้งหมด");
        panelRight.remove(panelEditMedicine(medicine));
        reloadOverview();
      } catch (SQLException e1) {
        fireDBErrorDialog();
        e1.printStackTrace();
      }
    });

    panelEditMed.add(panelTitle, BorderLayout.NORTH);
    panelEditMed.add(panelBody, BorderLayout.CENTER);
    return panelEditMed;
  }

  private static JScrollPane form() {
    /* Creates GUI of the form for adding a new medicine. */
    String medUnit = "เม็ด";

    // JPanels
    JPanel panelBody = new JPanel();
    JPanel panelSub = newFlowLayout();
    panelColor = newFlowLayout();
    panelTabletColor = newFlowLayout();
    panelCapsuleColor = newFlowLayout();
    panelLiquidColor = newFlowLayout();

    // JTextFields
    tfMedName = makeTextField(16);
    tfMedDescription = makeTextField(16);
    tfAmount = makeNumberField(2);
    tfTotalMeds = makeNumberField(2);
    tfEvery = makeNumberField(2);

    // JLabels
    labelUnit1 = makeLabel(medUnit);
    labelUnit2 = makeLabel(medUnit);
    JLabel labelHeading1 = makeBoldLabel("ข้อมูลพื้นฐาน");
    JLabel labelHeading2 = makeBoldLabel("ขนาดและเวลาที่ต้องรับประทาน");
    JLabel labelHeading3 = makeBoldLabel("ข้อมูลอื่น ๆ");

    // Arrays
    String[] medType = getMedType();
    String[] tabletColor = getTabletColor();
    String[] liquidColor = getLiquidColor();
    String[] medTime = getMedTime();
    String[] medDoseStr = getMedDoseStr();
    ArrayList<ImageIcon> tabletColorIcons = new ArrayList<>();
    ArrayList<ImageIcon> liquidColorIcons = new ArrayList<>();

    for (String color : tabletColor) {
      tabletColorIcons.add(new ImageIcon(imgPath + "/colors/" + color + ".png"));
    }
    for (String color : liquidColor) {
      liquidColorIcons.add(new ImageIcon(imgPath + "/colors/" + color + ".png"));
    }

    // JComboBoxes
    cbMedType = makeComboBox(medType);
    cbTabletColor = makeComboBox(tabletColorIcons);
    cbLiquidColor = makeComboBox(liquidColorIcons);
    cbCapsuleColor01 = makeComboBox(tabletColorIcons);
    cbCapsuleColor02 = makeComboBox(tabletColorIcons);

    // JCheckBoxes
    cbMorning = makeCheckBox(medTime[0]);
    cbAfternoon = makeCheckBox(medTime[1]);
    cbEvening = makeCheckBox(medTime[2]);
    cbBed = makeCheckBox(medTime[3]);
    cbEvery = makeCheckBox("ทุก ๆ ");

    // JRadioButtons
    rbBefore = makeRadioButton(medDoseStr[0]);
    rbAfter = makeRadioButton(medDoseStr[1]);
    rbImmediately = makeRadioButton(medDoseStr[2]);

    // Radio Groups
    bgMedDoseStr = new ButtonGroup();
    bgMedDoseStr.add(rbBefore);
    bgMedDoseStr.add(rbAfter);
    bgMedDoseStr.add(rbImmediately);

    // Pickers
    pickerStart = makeTodayPicker();
    pickerEXP = makeDatePicker();
    pickerStartTime = makeTimeNowPicker();

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelBody, 0, 0, 40);
    setPadding(labelHeading2, 20, 0, 0);
    setPadding(labelHeading3, 20, 0, 0);
    panelCapsuleColor.setVisible(false);
    panelLiquidColor.setVisible(false);

    // Listeners
    medTypeUIHandler();
    toggleCheckBox();

    panelSub.add(labelHeading1);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("ชื่อยา"));
    panelSub.add(tfMedName);
    panelSub.add(makeLabel("คำอธิบายยา (เช่น ยาแก้ปวด)"));
    panelSub.add(tfMedDescription);
    panelBody.add(panelSub);

    JPanel panelMedSettings = newFlowLayout();

    JPanel panelType = newFlowLayout();
    panelType.add(makeLabel("ประเภท"));
    panelType.add(cbMedType);
    panelMedSettings.add(panelType);

    panelColor.add(makeLabel("สีของยา"));
    panelMedSettings.add(panelColor);

    panelTabletColor.add(cbTabletColor);
    panelMedSettings.add(panelTabletColor);

    panelCapsuleColor.add(makeLabel("สีที่ 1"));
    panelCapsuleColor.add(cbCapsuleColor01);
    panelCapsuleColor.add(makeLabel("สีที่ 2"));
    panelCapsuleColor.add(cbCapsuleColor02);
    panelMedSettings.add(panelCapsuleColor);

    panelLiquidColor.add(cbLiquidColor);
    panelMedSettings.add(panelLiquidColor);

    panelBody.add(panelMedSettings);

    panelSub = newFlowLayout();
    panelSub.add(labelHeading2);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbMorning);
    panelSub.add(cbAfternoon);
    panelSub.add(cbEvening);
    panelSub.add(cbBed);
    panelSub.add(cbEvery);
    panelSub.add(tfEvery);
    panelSub.add(makeLabel("ชั่วโมง"));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(rbBefore);
    panelSub.add(rbAfter);
    panelSub.add(rbImmediately);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("จำนวน"));
    panelSub.add(tfAmount);
    panelSub.add(labelUnit1);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelHeading3);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("จำนวนยาทั้งหมด"));
    panelSub.add(tfTotalMeds);
    panelSub.add(labelUnit2);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("วันและเวลาที่เริ่มทานยา"));
    panelSub.add(pickerStart);
    panelSub.add(pickerStartTime);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("วันหมดอายุ"));
    panelSub.add(pickerEXP);
    panelBody.add(panelSub);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    return scrollPane;
  }

  private static JPanel makeMedCard(Medicine medicine) {
    /* Creates a card that will be used on the All medicines panel only. */

    // Strings
    Date medEXP = medicine.getMedEXP();
    String medTitle = medicine.getMedName() + " (" + medicine.getMedDescription() + ")";
    String medShortInfo =
        "เหลืออยู่ " + medicine.getMedRemaining() + " " + medicine.getMedUnit() + " หมดอายุ "
            + GUIHelper.formatDMY
            .format(medEXP);

    // JPanels
    JPanel panelPic = new JPanel();
    JPanel panelInfo = new JPanel();
    JPanel panelLoopInfo = new JPanel();

    // JLabels
    JLabel labelTitle = makeBoldLabel(medTitle);
    JLabel labelShortInfo = makeSmallerLabel(medShortInfo);
    JLabel labelPic = getMedIcon(medicine);

    // Styling
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 0, 0, 20, 0);
    panelPic.setLayout(new BoxLayout(panelPic, BoxLayout.X_AXIS));
    panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.PAGE_AXIS));

    panelPic.add(labelPic);
    panelInfo.add(labelTitle);
    panelInfo.add(labelShortInfo);

    setPadding(labelTitle, 10, 0, -10, 0);
    setPadding(labelPic, 5, 10, 0, 0);

    panelLoopInfo.add(panelPic);
    panelLoopInfo.add(panelInfo);
    panelLoopInfo.add(Box.createHorizontalGlue());

    panelLoopInfo.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        panelRight.add(panelViewMedicine(medicine), medicine.getMedName());
        CardLayout cl = (CardLayout) (panelRight.getLayout());
        cl.show(panelRight, medicine.getMedName());
      }
    });

    return panelLoopInfo;
  }

  private static void medTimeRadioHandler(Medicine medicine) {
    if (medicine.getMedDoseStr().equals("ก่อนอาหาร")) {
      rbBefore.setSelected(true);
    } else if (medicine.getMedDoseStr().equals("หลังอาหาร")) {
      rbAfter.setSelected(true);
    } else if (medicine.getMedDoseStr().equals("หลังอาหารทันที / พร้อมอาหาร")) {
      rbImmediately.setSelected(true);
    }
  }

  private static void medTypeUIHandler() {
    cbMedType.addActionListener(e -> {
      switch (cbMedType.getSelectedIndex()) {
        case 0:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(true);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelUnit1.setText("เม็ด");
          labelUnit2.setText("เม็ด");
          break;
        case 1:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(true);
          panelLiquidColor.setVisible(false);
          labelUnit1.setText("แคปซูล");
          labelUnit2.setText("แคปซูล");
          break;
        case 2:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(true);
          labelUnit1.setText("มิลลิลิตร");
          labelUnit2.setText("แคปซูล");
          break;
        default:
          panelColor.setVisible(false);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelUnit1.setText("มิลลิลิตร");
          labelUnit2.setText("แคปซูล");
          break;
      }
    });
  }

  private static void toggleCheckBox() {
    cbEvery.addActionListener(e -> {
      if (cbEvery.isSelected()) {
        cbMorning.setEnabled(false);
        cbMorning.setSelected(false);
        cbAfternoon.setEnabled(false);
        cbAfternoon.setSelected(false);
        cbEvening.setEnabled(false);
        cbEvening.setSelected(false);
        cbBed.setEnabled(false);
        cbBed.setSelected(false);
        bgMedDoseStr.clearSelection();
        rbBefore.setEnabled(false);
        rbAfter.setEnabled(false);
        rbImmediately.setEnabled(false);
      } else {
        cbMorning.setEnabled(true);
        cbAfternoon.setEnabled(true);
        cbEvening.setEnabled(true);
        cbBed.setEnabled(true);
        rbBefore.setEnabled(true);
        rbAfter.setEnabled(true);
        rbImmediately.setEnabled(true);
      }
    });
    cbBed.addActionListener(e -> {
      if (cbBed.isSelected() && !cbMorning.isSelected() && !cbAfternoon.isSelected() && !cbEvening.isSelected()) {
        bgMedDoseStr.clearSelection();
        rbBefore.setEnabled(false);
        rbAfter.setEnabled(false);
        rbImmediately.setEnabled(false);
      } else {
        rbBefore.setEnabled(true);
        rbAfter.setEnabled(true);
        rbImmediately.setEnabled(true);
      }
    });
    cbEvening.addActionListener(e -> {
      if (cbBed.isSelected() && !cbMorning.isSelected() && !cbAfternoon.isSelected() && !cbEvening.isSelected()) {
        bgMedDoseStr.clearSelection();
        rbBefore.setEnabled(false);
        rbAfter.setEnabled(false);
        rbImmediately.setEnabled(false);
      } else {
        rbBefore.setEnabled(true);
        rbAfter.setEnabled(true);
        rbImmediately.setEnabled(true);
      }
    });
    cbAfternoon.addActionListener(e -> {
      if (cbBed.isSelected() && !cbMorning.isSelected() && !cbAfternoon.isSelected() && !cbEvening.isSelected()) {
        bgMedDoseStr.clearSelection();
        rbBefore.setEnabled(false);
        rbAfter.setEnabled(false);
        rbImmediately.setEnabled(false);
      } else {
        rbBefore.setEnabled(true);
        rbAfter.setEnabled(true);
        rbImmediately.setEnabled(true);
      }
    });
    cbMorning.addActionListener(e -> {
      if (cbBed.isSelected() && !cbMorning.isSelected() && !cbAfternoon.isSelected() && !cbEvening.isSelected()) {
        bgMedDoseStr.clearSelection();
        rbBefore.setEnabled(false);
        rbAfter.setEnabled(false);
        rbImmediately.setEnabled(false);
      } else {
        rbBefore.setEnabled(true);
        rbAfter.setEnabled(true);
        rbImmediately.setEnabled(true);
      }
    });
  }

  private static void reload() {
    panelRight.remove(panelMedicines);
    panelAllMedicines();
  }
}
