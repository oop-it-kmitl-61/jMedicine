package GUI.components;

import static GUI.GUI.*;
import static GUI.GUIHelper.*;
import static core.MedicineUtil.*;
import static core.Core.*;

import GUI.GUIHelper;
import api.MedicineDB;
import com.github.lgooddatepicker.components.DatePicker;
import core.Medicine;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
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
 * @version 0.7.0
 * @since 0.7.0
 */

public class MedicineUI {

  private static JPanel panelMedicines;

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

  public static JPanel panelViewMedicine(Medicine medicine) {
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
        } catch (SQLException ex) {
          labelMessage = getRemoveFailedMessage("ยา");
          fireErrorDialog(labelMessage);
        }

        panelRight.remove(panelMedicines);
        panelAllMedicines();
        backTo("ยาทั้งหมด");
        panelRight.remove(panelViewMedicine(medicine));
      } else {

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

    for (int i = 0; i < medicine.getMedTime().size(); i++) {
      panelSub = newFlowLayout();
      JLabel labelMedTime = makeLabel(medicine.getMedTime().get(i));
      JLabel labelMedDoseStr = makeLabel(medicine.getMedDoseStr().get(i));
      panelSub.add(labelMedDoseStr);
      if (!medicine.getMedTime().get(i).equals("")) {
        panelSub.add(labelMedTime);
      }
      panelSub.add(makeLabel(medicine.getMedDose() + " " + medicine.getMedUnit()));
      setPadding(panelSub, 0, 0, -10);
      panelBody.add(panelSub);
    }

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("ข้อมูลอื่น ๆ"));
    setPadding(panelSub, 20, 0, 0);
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("วันที่เพิ่มยา: "));
    panelSub.add(makeLabel(GUIHelper.formatDMY.format(medicine.getDateAdded())));
    setPadding(panelSub, 0, 0, -10);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("จำนวนยาเริ่มต้น: "));
    panelSub.add(makeLabel(String.valueOf(medicine.getMedTotal())));
    setPadding(panelSub, 0, 0, -10);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("วันหมดอายุ: "));
    panelSub.add(makeLabel(GUIHelper.formatDMY.format(medicine.getMedEXP())));
    panelBody.add(panelSub);

    panelButtons.add(btnEdit, BorderLayout.CENTER);
    panelButtons.add(btnRemove, BorderLayout.EAST);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(scrollPane, BorderLayout.CENTER);
    panelView.add(panelButtons, BorderLayout.SOUTH);

    return panelView;
  }

  public static JPanel panelAddMedicine() {
    /* Creates outer GUI when user add a new medicine from all medicines page. */

    // JPanels
    JPanel panelAddMedicine = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มยาใหม่", "ยาทั้งหมด");

    // Styling
    setPadding(panelAddMedicine, -2, 0, 40, -16);
    setPadding(panelBody, 0, 0, 20, 40);
    setPadding(panelTitle, 0, 0, 20);

    panelTitle.add(btnBack);
    panelBody.add(addMedGUI(), BorderLayout.CENTER);

    panelAddMedicine.add(panelTitle, BorderLayout.NORTH);
    panelAddMedicine.add(panelBody, BorderLayout.CENTER);

    return panelAddMedicine;
  }

  public static JScrollPane addMedGUI() {
    /* Creates GUI of the form for adding a new medicine. */
    String medUnit = "เม็ด";

    // JPanels
    JPanel panelAddMedGUI = new JPanel();
    JPanel panelSub = newFlowLayout();
    JPanel panelSubMorning = newFlowLayout();
    JPanel panelSubAfternoon = newFlowLayout();
    JPanel panelSubEvening = newFlowLayout();
    JPanel panelSubBed = newFlowLayout();
    JPanel panelColor = newFlowLayout();
    JPanel panelTabletColor = newFlowLayout();
    JPanel panelCapsuleColor = newFlowLayout();
    JPanel panelLiquidColor = newFlowLayout();

    // JTextFields
    JTextField tfMedName = makeTextField(20);
    JTextField tfMedDescription = makeTextField(20);
    JTextField tfAmountMorning = makeTextField(2);
    JTextField tfAmountAfternoon = makeTextField(2);
    JTextField tfAmountEvening = makeTextField(2);
    JTextField tfAmountBed = makeTextField(2);
    JTextField tfTotalMeds = makeTextField(2);

    // JLabels
    JLabel labelUnit = makeLabel(medUnit);
    JLabel labelUnitMorning = makeLabel(medUnit);
    JLabel labelUnitAfternoon = makeLabel(medUnit);
    JLabel labelUnitEvening = makeLabel(medUnit);
    JLabel labelUnitBed = makeLabel(medUnit);

    // JButtons
    JButton btnSave = makeBlueButton("บันทึกยา");

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
    JComboBox cbMedType = makeComboBox(medType);
    JComboBox cbTabletColor = makeComboBox(tabletColorIcons);
    JComboBox cbLiquidColor = makeComboBox(liquidColorIcons);
    JComboBox cbCapsuleColor01 = makeComboBox(tabletColorIcons);
    JComboBox cbCapsuleColor02 = makeComboBox(tabletColorIcons);

    // JCheckBoxes
    JCheckBox cbMorning = makeCheckBox(medTime[0]);
    JCheckBox cbAfternoon = makeCheckBox(medTime[1]);
    JCheckBox cbEvening = makeCheckBox(medTime[2]);
    JCheckBox cbBed = makeCheckBox(medTime[3]);

    // JRadioButtons
    JRadioButton rbMorningBefore = makeRadioButton(medDoseStr[0]);
    JRadioButton rbMorningAfter = makeRadioButton(medDoseStr[1]);
    JRadioButton rbMorningImme = makeRadioButton(medDoseStr[2]);

    JRadioButton rbAfternoonBefore = makeRadioButton(medDoseStr[0]);
    JRadioButton rbAfternoonAfter = makeRadioButton(medDoseStr[1]);
    JRadioButton rbAfternoonImme = makeRadioButton(medDoseStr[2]);

    JRadioButton rbEveningBefore = makeRadioButton(medDoseStr[0]);
    JRadioButton rbEveningAfter = makeRadioButton(medDoseStr[1]);
    JRadioButton rbEveningImme = makeRadioButton(medDoseStr[2]);

    // Radio Groups
    ButtonGroup bgMorning = new ButtonGroup();
    ButtonGroup bgAfternoon = new ButtonGroup();
    ButtonGroup bgEvening = new ButtonGroup();

    bgMorning.add(rbMorningBefore);
    bgMorning.add(rbMorningAfter);
    bgMorning.add(rbMorningImme);

    bgAfternoon.add(rbAfternoonBefore);
    bgAfternoon.add(rbAfternoonAfter);
    bgAfternoon.add(rbAfternoonImme);

    bgEvening.add(rbEveningBefore);
    bgEvening.add(rbEveningAfter);
    bgEvening.add(rbEveningImme);

    DatePicker picker = new DatePicker();

    // Styling
    panelAddMedGUI.setLayout(new BoxLayout(panelAddMedGUI, BoxLayout.PAGE_AXIS));
    setPadding(panelAddMedGUI, 0, 0, 40);
    panelCapsuleColor.setVisible(false);
    panelLiquidColor.setVisible(false);
    panelSubMorning.setVisible(false);
    panelSubAfternoon.setVisible(false);
    panelSubEvening.setVisible(false);
    panelSubBed.setVisible(false);

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
      int dose = 0;
      ArrayList<String> selectedMedTime = new ArrayList<>();
      ArrayList<String> selectedDoseStr = new ArrayList<>();
      if (cbMorning.isSelected()) {
        selectedMedTime.add("เช้า");
        medTimeAdder(rbMorningBefore, rbMorningAfter, rbMorningImme, selectedDoseStr);
        dose = Integer.valueOf(tfAmountMorning.getText());
      }
      if (cbAfternoon.isSelected()) {
        selectedMedTime.add("กลางวัน");
        medTimeAdder(rbAfternoonBefore, rbAfternoonAfter, rbAfternoonImme, selectedDoseStr);
        dose = Integer.valueOf(tfAmountAfternoon.getText());
      }
      if (cbEvening.isSelected()) {
        selectedMedTime.add("เย็น");
        medTimeAdder(rbEveningBefore, rbEveningAfter, rbEveningImme, selectedDoseStr);
        dose = Integer.valueOf(tfAmountEvening.getText());
      }
      if (cbBed.isSelected()) {
        selectedMedTime.add("ก่อนนอน");
        selectedDoseStr.add("");
        dose = Integer.valueOf(tfAmountBed.getText());
      }
      Date exp = Date.from(picker.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
      Medicine med = new Medicine(tfMedName.getText(), type, selectedColor,
          tfMedDescription.getText(), selectedMedTime, selectedDoseStr, dose,
          Integer.valueOf(tfTotalMeds.getText()), exp);
      try {
        MedicineDB.addMedicine(med, getUser().getUserId());
        fireSuccessDialog("ยา " + med.getMedName() + " ได้ถูกเพิ่มเรียบร้อยแล้ว");
        panelRight.remove(panelMedicines);
        panelAllMedicines();
        backTo("ยาทั้งหมด");
        panelRight.remove(panelAddMedicine());
        panelRight.remove(panelAddMedGUI);
        panelRight.add(panelAddMedicine(), "เพิ่มยาใหม่");
      } catch (SQLException e1) {
        fireDBErrorDialog();
        e1.printStackTrace();
      }
    });

    medTypeUIHandler(panelColor, panelTabletColor, panelCapsuleColor, panelLiquidColor, labelUnit,
        labelUnitMorning, labelUnitAfternoon, labelUnitEvening, labelUnitBed, cbMedType);
    cbAfternoon.addActionListener(e -> {
      if (cbAfternoon.isSelected()) {
        panelSubAfternoon.setVisible(true);
      } else {
        panelSubAfternoon.setVisible(false);
      }
    });
    cbEvening.addActionListener(e -> {
      if (cbEvening.isSelected()) {
        panelSubEvening.setVisible(true);
      } else {
        panelSubEvening.setVisible(false);
      }
    });
    cbBed.addActionListener(e -> {
      if (cbBed.isSelected()) {
        panelSubBed.setVisible(true);
      } else {
        panelSubBed.setVisible(false);
      }
    });
    cbMorning.addActionListener(e -> {
      if (cbMorning.isSelected()) {
        panelSubMorning.setVisible(true);
      } else {
        panelSubMorning.setVisible(false);
      }
    });

    panelSub.add(makeLabel("ชื่อยา"));
    panelSub.add(tfMedName);
    panelSub.add(makeLabel("ประเภท"));
    panelSub.add(cbMedType);
    panelAddMedGUI.add(panelSub);

    panelColor.add(makeBoldLabel("สีของยา"));
    panelAddMedGUI.add(panelColor);

    panelTabletColor.add(cbTabletColor);
    panelAddMedGUI.add(panelTabletColor);

    panelCapsuleColor.add(makeLabel("สีที่ 1"));
    panelCapsuleColor.add(cbCapsuleColor01);
    panelCapsuleColor.add(makeLabel("สีที่ 2"));
    panelCapsuleColor.add(cbCapsuleColor02);
    panelAddMedGUI.add(panelCapsuleColor);

    panelLiquidColor.add(cbLiquidColor);
    panelAddMedGUI.add(panelLiquidColor);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("คำอธิบายยา (เช่น ยาแก้ปวด)"));
    panelSub.add(tfMedDescription);
    panelAddMedGUI.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("เวลาที่ต้องรับประทาน"));
    panelAddMedGUI.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbMorning);
    panelSubMorning.add(rbMorningBefore);
    panelSubMorning.add(rbMorningAfter);
    panelSubMorning.add(rbMorningImme);
    panelSubMorning.add(makeLabel("จำนวน"));
    panelSubMorning.add(tfAmountMorning);
    panelSubMorning.add(labelUnitMorning);
    panelSub.add(panelSubMorning);
    panelAddMedGUI.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbAfternoon);
    panelSubAfternoon.add(rbAfternoonBefore);
    panelSubAfternoon.add(rbAfternoonAfter);
    panelSubAfternoon.add(rbAfternoonImme);
    panelSubAfternoon.add(makeLabel("จำนวน"));
    panelSubAfternoon.add(tfAmountAfternoon);
    panelSubAfternoon.add(labelUnitAfternoon);
    panelSub.add(panelSubAfternoon);
    panelAddMedGUI.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbEvening);
    panelSubEvening.add(rbEveningBefore);
    panelSubEvening.add(rbEveningAfter);
    panelSubEvening.add(rbEveningImme);
    panelSubEvening.add(makeLabel("จำนวน"));
    panelSubEvening.add(tfAmountEvening);
    panelSubEvening.add(labelUnitEvening);
    panelSub.add(panelSubEvening);
    panelAddMedGUI.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbBed);
    panelSubBed.add(makeLabel("จำนวน"));
    panelSubBed.add(tfAmountBed);
    panelSubBed.add(labelUnitBed);
    panelSub.add(panelSubBed);
    panelAddMedGUI.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("จำนวนยาทั้งหมด"));
    panelSub.add(tfTotalMeds);
    panelSub.add(labelUnit);
    panelAddMedGUI.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("วันหมดอายุ"));
    panelSub.add(picker);
    panelAddMedGUI.add(panelSub);

    panelSub = new JPanel();
    panelSub.add(btnSave);
    panelAddMedGUI.add(panelSub);

    JScrollPane scrollPane = makeScrollPane(panelAddMedGUI);

    return scrollPane;
  }

  public static JPanel panelEditMedicine(Medicine medicine) {
    /* Creates GUI of the form for editing a new medicine. */
    String medUnit = medicine.getMedUnit();

    // JPanels
    JPanel panelEditMed = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelButtons = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());
    JPanel panelSub = newFlowLayout();
    JPanel panelSubMorning = newFlowLayout();
    JPanel panelSubAfternoon = newFlowLayout();
    JPanel panelSubEvening = newFlowLayout();
    JPanel panelSubBed = newFlowLayout();
    JPanel panelColor = newFlowLayout();
    JPanel panelTabletColor = newFlowLayout();
    JPanel panelCapsuleColor = newFlowLayout();
    JPanel panelLiquidColor = newFlowLayout();

    // JButtons
    JButton btnBack = makeBackButton("แก้ไขยา", medicine.getMedName());
    JButton btnSave = makeBlueButton("บันทึก");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    panelCapsuleColor.setVisible(false);
    panelLiquidColor.setVisible(false);
    panelSubMorning.setVisible(false);
    panelSubAfternoon.setVisible(false);
    panelSubEvening.setVisible(false);
    panelSubBed.setVisible(false);
    setPadding(panelEditMed, -4, 0, 10, -18);
    setPadding(panelBody, 0, 0, 20, 40);
    setPadding(panelTitle, 0, 0, 20);

    // JTextFields
    JTextField tfMedName = makeTextField(20);
    JTextField tfMedDescription = makeTextField(20);
    JTextField tfAmountMorning = makeTextField(2);
    JTextField tfAmountAfternoon = makeTextField(2);
    JTextField tfAmountEvening = makeTextField(2);
    JTextField tfAmountBed = makeTextField(2);
    JTextField tfTotalMeds = makeTextField(2);

    DatePicker picker = new DatePicker();

    tfMedName.setText(medicine.getMedName());
    tfMedDescription.setText(medicine.getMedDescription());
    tfTotalMeds.setText(String.valueOf(medicine.getMedTotal()));
    picker.setText(formatDatePicker.format(medicine.getMedEXP()));

    // JLabels
    JLabel labelUnit = makeLabel(medUnit);
    JLabel labelUnitMorning = makeLabel(medUnit);
    JLabel labelUnitAfternoon = makeLabel(medUnit);
    JLabel labelUnitEvening = makeLabel(medUnit);
    JLabel labelUnitBed = makeLabel(medUnit);

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
    JComboBox cbMedType = makeComboBox(medType);
    JComboBox cbTabletColor = makeComboBox(tabletColorIcons);
    JComboBox cbLiquidColor = makeComboBox(liquidColorIcons);
    JComboBox cbCapsuleColor01 = makeComboBox(tabletColorIcons);
    JComboBox cbCapsuleColor02 = makeComboBox(tabletColorIcons);

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

    // JCheckBoxes
    JCheckBox cbMorning = makeCheckBox(medTime[0]);
    JCheckBox cbAfternoon = makeCheckBox(medTime[1]);
    JCheckBox cbEvening = makeCheckBox(medTime[2]);
    JCheckBox cbBed = makeCheckBox(medTime[3]);

    // JRadioButtons
    JRadioButton rbMorningBefore = makeRadioButton(medDoseStr[0]);
    JRadioButton rbMorningAfter = makeRadioButton(medDoseStr[1]);
    JRadioButton rbMorningImme = makeRadioButton(medDoseStr[2]);

    JRadioButton rbAfternoonBefore = makeRadioButton(medDoseStr[0]);
    JRadioButton rbAfternoonAfter = makeRadioButton(medDoseStr[1]);
    JRadioButton rbAfternoonImme = makeRadioButton(medDoseStr[2]);

    JRadioButton rbEveningBefore = makeRadioButton(medDoseStr[0]);
    JRadioButton rbEveningAfter = makeRadioButton(medDoseStr[1]);
    JRadioButton rbEveningImme = makeRadioButton(medDoseStr[2]);

    // Radio Groups
    ButtonGroup bgMorning = new ButtonGroup();
    ButtonGroup bgAfternoon = new ButtonGroup();
    ButtonGroup bgEvening = new ButtonGroup();

    bgMorning.add(rbMorningBefore);
    bgMorning.add(rbMorningAfter);
    bgMorning.add(rbMorningImme);

    bgAfternoon.add(rbAfternoonBefore);
    bgAfternoon.add(rbAfternoonAfter);
    bgAfternoon.add(rbAfternoonImme);

    bgEvening.add(rbEveningBefore);
    bgEvening.add(rbEveningAfter);
    bgEvening.add(rbEveningImme);

    for (int i = 0; i < medicine.getMedTime().size(); i++) {
      switch (medicine.getMedTime().get(i)) {
        case "เช้า":
          cbMorning.setSelected(true);
          medTimeRadioHandler(medicine, rbMorningBefore, rbMorningAfter, i);
          tfAmountMorning.setText(String.valueOf(medicine.getMedDose()));
          panelSubMorning.setVisible(true);
          break;
        case "กลางวัน":
          cbAfternoon.setSelected(true);
          medTimeRadioHandler(medicine, rbAfternoonBefore, rbAfternoonAfter, i);
          tfAmountAfternoon.setText(String.valueOf(medicine.getMedDose()));
          panelSubAfternoon.setVisible(true);
          break;
        case "เย็น":
          cbEvening.setSelected(true);
          medTimeRadioHandler(medicine, rbEveningBefore, rbEveningAfter, i);
          tfAmountEvening.setText(String.valueOf(medicine.getMedDose()));
          panelSubEvening.setVisible(true);
          break;
        case "ก่อนนอน":
          cbBed.setSelected(true);
          panelSubBed.setVisible(true);
          tfAmountBed.setText(String.valueOf(medicine.getMedDose()));
          break;
      }
    }

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
      int dose = 0;
      ArrayList<String> selectedMedTime = new ArrayList<>();
      ArrayList<String> selectedDoseStr = new ArrayList<>();
      if (cbMorning.isSelected()) {
        selectedMedTime.add("เช้า");
        medTimeAdder(rbMorningBefore, rbMorningAfter, rbMorningImme, selectedDoseStr);
        dose = Integer.valueOf(tfAmountMorning.getText());
      }
      if (cbAfternoon.isSelected()) {
        selectedMedTime.add("กลางวัน");
        medTimeAdder(rbAfternoonBefore, rbAfternoonAfter, rbAfternoonImme, selectedDoseStr);
        dose = Integer.valueOf(tfAmountAfternoon.getText());
      }
      if (cbEvening.isSelected()) {
        selectedMedTime.add("เย็น");
        medTimeAdder(rbEveningBefore, rbEveningAfter, rbEveningImme, selectedDoseStr);
        dose = Integer.valueOf(tfAmountEvening.getText());
      }
      if (cbBed.isSelected()) {
        selectedMedTime.add("ก่อนนอน");
        selectedDoseStr.add("");
        dose = Integer.valueOf(tfAmountBed.getText());
      }
      Date exp = Date.from(picker.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
      Medicine med = new Medicine(tfMedName.getText(), type, selectedColor,
          tfMedDescription.getText(), selectedMedTime, selectedDoseStr, dose,
          Integer.valueOf(tfTotalMeds.getText()), exp);
      try {
        MedicineDB.updateMedicine(med);
        fireSuccessDialog("แก้ไขยา " + med.getMedName() + " เรียบร้อยแล้ว");
        panelRight.remove(panelViewMedicine(medicine));
        panelRight.add(panelViewMedicine(medicine));
        backTo("ยาทั้งหมด");
        panelRight.remove(panelEditMedicine(medicine));
      } catch (SQLException e1) {
        fireDBErrorDialog();
        e1.printStackTrace();
      }
    });

    medTypeUIHandler(panelColor, panelTabletColor, panelCapsuleColor, panelLiquidColor, labelUnit,
        labelUnitMorning, labelUnitAfternoon, labelUnitEvening, labelUnitBed, cbMedType);
    cbAfternoon.addActionListener(e -> {
      if (cbAfternoon.isSelected()) {
        panelSubAfternoon.setVisible(true);
      } else {
        panelSubAfternoon.setVisible(false);
      }
    });
    cbEvening.addActionListener(e -> {
      if (cbEvening.isSelected()) {
        panelSubEvening.setVisible(true);
      } else {
        panelSubEvening.setVisible(false);
      }
    });
    cbBed.addActionListener(e -> {
      if (cbBed.isSelected()) {
        panelSubBed.setVisible(true);
      } else {
        panelSubBed.setVisible(false);
      }
    });
    cbMorning.addActionListener(e -> {
      if (cbMorning.isSelected()) {
        panelSubMorning.setVisible(true);
      } else {
        panelSubMorning.setVisible(false);
      }
    });

    panelSub.add(btnBack);
    panelTitle.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("ชื่อยา"));
    panelSub.add(tfMedName);
    panelSub.add(makeLabel("ประเภท"));
    panelSub.add(cbMedType);
    panelBody.add(panelSub);

    panelColor.add(makeBoldLabel("สีของยา"));
    panelBody.add(panelColor);

    panelTabletColor.add(cbTabletColor);
    panelBody.add(panelTabletColor);

    panelCapsuleColor.add(makeLabel("สีที่ 1"));
    panelCapsuleColor.add(cbCapsuleColor01);
    panelCapsuleColor.add(makeLabel("สีที่ 2"));
    panelCapsuleColor.add(cbCapsuleColor02);
    panelBody.add(panelCapsuleColor);

    panelLiquidColor.add(cbLiquidColor);
    panelBody.add(panelLiquidColor);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("คำอธิบายยา (เช่น ยาแก้ปวด)"));
    panelSub.add(tfMedDescription);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("เวลาที่ต้องรับประทาน"));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbMorning);
    panelSubMorning.add(rbMorningBefore);
    panelSubMorning.add(rbMorningAfter);
    panelSubMorning.add(rbMorningImme);
    panelSubMorning.add(makeLabel("จำนวน"));
    panelSubMorning.add(tfAmountMorning);
    panelSubMorning.add(labelUnitMorning);
    panelSub.add(panelSubMorning);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbAfternoon);
    panelSubAfternoon.add(rbAfternoonBefore);
    panelSubAfternoon.add(rbAfternoonAfter);
    panelSubAfternoon.add(rbAfternoonImme);
    panelSubAfternoon.add(makeLabel("จำนวน"));
    panelSubAfternoon.add(tfAmountAfternoon);
    panelSubAfternoon.add(labelUnitAfternoon);
    panelSub.add(panelSubAfternoon);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbEvening);
    panelSubEvening.add(rbEveningBefore);
    panelSubEvening.add(rbEveningAfter);
    panelSubEvening.add(rbEveningImme);
    panelSubEvening.add(makeLabel("จำนวน"));
    panelSubEvening.add(tfAmountEvening);
    panelSubEvening.add(labelUnitEvening);
    panelSub.add(panelSubEvening);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbBed);
    panelSubBed.add(makeLabel("จำนวน"));
    panelSubBed.add(tfAmountBed);
    panelSubBed.add(labelUnitBed);
    panelSub.add(panelSubBed);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("จำนวนยาทั้งหมด"));
    panelSub.add(tfTotalMeds);
    panelSub.add(labelUnit);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("วันหมดอายุ"));
    panelSub.add(picker);
    panelBody.add(panelSub);

    panelSub = new JPanel();
    panelSub.add(btnSave);
    panelBody.add(panelSub);

    panelButtons.add(btnSave);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    panelEditMed.add(panelTitle, BorderLayout.NORTH);
    panelEditMed.add(scrollPane, BorderLayout.CENTER);
    panelEditMed.add(panelButtons, BorderLayout.SOUTH);
    return panelEditMed;
  }

  private static JPanel makeMedCard(Medicine medicine) {
    /* Creates a card that will be used on the All medicines panel only. */
    Date medEXP = medicine.getMedEXP();
    String medTitle = medicine.getMedName() + " (" + medicine.getMedDescription() + ")";
    String medShortInfo =
        "เหลืออยู่ " + medicine.getMedRemaining() + " " + medicine.getMedUnit() + " หมดอายุ "
            + GUIHelper.formatDMY
            .format(medEXP);
    JLabel labelTitle = makeBoldLabel(medTitle);
    JLabel labelShortInfo = makeLabel(medShortInfo);
    JLabel labelPic = getMedIcon(medicine);
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 0, 0, 20, 0);

    JPanel panelPic = new JPanel();
    panelPic.setLayout(new BoxLayout(panelPic, BoxLayout.X_AXIS));

    JPanel panelInfo = new JPanel();
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

  private static void medTimeRadioHandler(Medicine medicine, JRadioButton rbMorningBefore,
      JRadioButton rbMorningAfter, int i) {
    if (medicine.getMedDoseStr().get(i).equals("ก่อนอาหาร")) {
      rbMorningBefore.setSelected(true);
    } else if (medicine.getMedDoseStr().get(i).equals("หลังอาหาร")) {
      rbMorningAfter.setSelected(true);
    } else {
      rbMorningBefore.setSelected(true);
    }
  }

  private static void medTypeUIHandler(JPanel panelColor, JPanel panelTabletColor,
      JPanel panelCapsuleColor, JPanel panelLiquidColor, JLabel labelUnit, JLabel labelUnitMorning,
      JLabel labelUnitAfternoon, JLabel labelUnitEvening, JLabel labelUnitBed,
      JComboBox cbMedType) {
    cbMedType.addActionListener(e -> {
      switch (cbMedType.getSelectedIndex()) {
        case 0:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(true);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelUnit.setText("เม็ด");
          labelUnitMorning.setText("เม็ด");
          labelUnitAfternoon.setText("เม็ด");
          labelUnitEvening.setText("เม็ด");
          labelUnitBed.setText("เม็ด");
          break;
        case 1:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(true);
          panelLiquidColor.setVisible(false);
          labelUnit.setText("แคปซูล");
          labelUnitMorning.setText("แคปซูล");
          labelUnitAfternoon.setText("แคปซูล");
          labelUnitEvening.setText("แคปซูล");
          labelUnitBed.setText("แคปซูล");
          break;
        case 2:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(true);
          labelUnit.setText("มิลลิลิตร");
          labelUnitMorning.setText("มิลลิลิตร");
          labelUnitAfternoon.setText("มิลลิลิตร");
          labelUnitEvening.setText("มิลลิลิตร");
          labelUnitBed.setText("มิลลิลิตร");
          break;
        default:
          panelColor.setVisible(false);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelUnit.setText("มิลลิลิตร");
          labelUnitMorning.setText("ครั้ง");
          labelUnitAfternoon.setText("ครั้ง");
          labelUnitEvening.setText("ครั้ง");
          labelUnitBed.setText("ครั้ง");
          break;
      }
    });
  }
}
