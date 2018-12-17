package GUI.components;

import static GUI.GUI.frameMain;
import static GUI.GUI.frameWelcome;
import static GUI.GUI.panelRight;
import static GUI.GUI.reloadOverview;
import static GUI.GUIHelper.backTo;
import static GUI.GUIHelper.editSwitcher;
import static GUI.GUIHelper.fireConfirmDialog;
import static GUI.GUIHelper.fireDBErrorDialog;
import static GUI.GUIHelper.fireErrorDialog;
import static GUI.GUIHelper.fireInfoDialog;
import static GUI.GUIHelper.fireSuccessDialog;
import static GUI.GUIHelper.formatDatePicker;
import static GUI.GUIHelper.formatHM;
import static GUI.GUIHelper.formatYMD;
import static GUI.GUIHelper.getInfoPic;
import static GUI.GUIHelper.getRemoveFailedMessage;
import static GUI.GUIHelper.getRemoveSuccessfulMessage;
import static GUI.GUIHelper.imgPath;
import static GUI.GUIHelper.mainBlue;
import static GUI.GUIHelper.makeBackButton;
import static GUI.GUIHelper.makeBlueButton;
import static GUI.GUIHelper.makeBoldLabel;
import static GUI.GUIHelper.makeCheckBox;
import static GUI.GUIHelper.makeComboBox;
import static GUI.GUIHelper.makeDatePicker;
import static GUI.GUIHelper.makeLabel;
import static GUI.GUIHelper.makeNewButton;
import static GUI.GUIHelper.makeNumberField;
import static GUI.GUIHelper.makeRadioButton;
import static GUI.GUIHelper.makeRedButton;
import static GUI.GUIHelper.makeRemoveButton;
import static GUI.GUIHelper.makeScrollPane;
import static GUI.GUIHelper.makeSmallerLabel;
import static GUI.GUIHelper.makeTextField;
import static GUI.GUIHelper.makeTimeNowPicker;
import static GUI.GUIHelper.makeTitleLabel;
import static GUI.GUIHelper.makeTodayPicker;
import static GUI.GUIHelper.newFlowLayout;
import static GUI.GUIHelper.newPanelLoop;
import static GUI.GUIHelper.setPadding;
import static core.Core.getUser;
import static core.MedicineUtil.getLiquidColor;
import static core.MedicineUtil.getLiquidColorIndex;
import static core.MedicineUtil.getMedDoseStr;
import static core.MedicineUtil.getMedIcon;
import static core.MedicineUtil.getMedTime;
import static core.MedicineUtil.getMedType;
import static core.MedicineUtil.getTabletColor;
import static core.MedicineUtil.getTabletColorIndex;
import static core.MedicineUtil.tableSpoonCalc;
import static core.MedicineUtil.teaSpoonCalc;
import static core.Utils.timestampToString;

import GUI.GUIHelper;
import api.MedicineDB;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import core.Medicine;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * All UIs and handler methods about a medicine will be written here.
 *
 * @author jMedicine
 * @version 0.8.0
 * @since 0.7.0
 */

public class MedicineUI {

  private static JPanel panelMedicines, panelAddMedicine;
  private static JButton btnSave;
  private static JButton btnEdit;
  private static JButton btnAddFirst;

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
      fireDBErrorDialog();
    }

    // Init panel loop
    panelLoop.add(makeNewButton("เพิ่มยาใหม่"));

    if (userMedicines.isEmpty()) {
      labelTitle.setText("คุณยังไม่มียาที่บันทึกไว้");
    } else {
      labelTitle.setText("ยาทั้งหมด");
      ArrayList<Medicine> activeMedicines = new ArrayList<>();
      ArrayList<Medicine> inActiveMedicines = new ArrayList<>();

      for (Medicine medCurrent : userMedicines) {
        // Check if med is active
        Date now = new Date();
        Date medExp = medCurrent.getMedEXP();
        if (medExp.before(now) || medCurrent.getMedRemaining() == 0) {
          inActiveMedicines.add(medCurrent);
        } else {
          activeMedicines.add(medCurrent);
        }
      }

      // Start making active medicine cards
      JPanel panelHead = newFlowLayout();
      panelHead.add(makeBoldLabel("ยาปัจจุบัน (" + activeMedicines.size() + ")"));
      panelLoop.add(panelHead);
      for (Medicine medCurrent : activeMedicines) {
        JPanel cardLoop = makeMedCard(medCurrent, true);
        panelLoop.add(cardLoop);
      }

      int inactiveCount = inActiveMedicines.size();
      if (inactiveCount > 0) {
        // Start making inactive medicine cards
        panelHead = newFlowLayout();
        panelHead.add(makeBoldLabel("ยาที่หมดหรือหมดอายุแล้ว (" + inactiveCount + ")"));
        panelLoop.add(panelHead);
        for (Medicine medCurrent : inActiveMedicines) {
          JPanel cardLoop = makeMedCard(medCurrent, false);
          panelLoop.add(cardLoop);
        }
      }

    }

    panelMedicines.add(panelTitle, BorderLayout.NORTH);

    if (userMedicines.size() < 4) {
      setPadding(panelLoop, 0, 0, 1000, 10);
      panelMedicines.add(panelLoop);
    } else {
      setPadding(panelLoop, 0, 0, 20, 10);
      panelMedicines.add(makeScrollPane(panelLoop));
    }

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
    JButton btnEditMed = makeBlueButton("แก้ไขข้อมูล");
    JButton btnRemove = makeRemoveButton();
    JButton labelTitle = makeBackButton(medName, "ยาทั้งหมด");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(labelPic, 0, 0, 10);
    setPadding(panelTitle, -6, 0, 20, 8);
    setPadding(panelBody, 0, 0, 0, 38);
    setPadding(panelView, 0, 0, 0, -20);

    // Listeners
    btnEditMed.addActionListener(e -> editSwitcher(panelRight, panelEditMedicine(medicine)));
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
      panelSub.add(makeLabel(
          medicine.getMedDoseStr() + " ครั้งละ " + medicine.getMedDose() + " " + medicine
              .getMedUnit()));
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
          medicine.getMedDoseStr() + space + "ครั้งละ " + medicine.getMedDose() + " " + medicine
              .getMedUnit()
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

    panelButtons.add(btnEditMed, BorderLayout.CENTER);
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
    panelAddMedicine = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มยาใหม่", "ยาทั้งหมด");
    btnSave = makeBlueButton("บันทึกยา");

    // Styling
    setPadding(panelAddMedicine, -2, 0, 0, -16);
    setPadding(panelBody, 0, 0, 20, 40);
    setPadding(panelTitle, 0, 0, 20);

    panelTitle.add(btnBack);
    panelBody.add(form("add", null), BorderLayout.CENTER);
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
    btnAddFirst = makeBlueButton("บันทึกยา");
    JButton btnSkip = makeRedButton("ข้ามขั้นตอนนี้");

    panelTitle.add(makeTitleLabel("เพิ่มยาตัวแรกของคุณ"));

    panelBtn.add(btnAddFirst, BorderLayout.CENTER);
    panelBtn.add(btnSkip, BorderLayout.EAST);

    panelBody.add(form("first", null), BorderLayout.CENTER);
    panelBody.add(panelBtn, BorderLayout.SOUTH);

    // Styling
    setPadding(panelFirstMed, 20, 20, 0);
    setPadding(panelBody, 0, 0, 20, 20);
    setPadding(panelBtn, 0, 0, 0, -8);
    setPadding(panelTitle, 20, 0, 20, 20);

    // Listener
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

    // JButtons
    JButton btnBack = makeBackButton("แก้ไขยา", medicine.getMedName());
    btnEdit = makeBlueButton("บันทึก");

    // JPanels
    JPanel panelEditMed = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel(new BorderLayout());
    JScrollPane form = form("edit", medicine);

    panelTitle.add(btnBack);
    panelBody.add(form, BorderLayout.CENTER);
    panelBody.add(btnEdit, BorderLayout.SOUTH);

    // Styling
    setPadding(form, 0, 0, 10, 16);

    panelEditMed.add(panelTitle, BorderLayout.NORTH);
    panelEditMed.add(panelBody, BorderLayout.CENTER);
    return panelEditMed;
  }

  private static JScrollPane form(String type, Medicine medicine) {
    /* Creates GUI of the form for adding a new medicine. */
    String medUnit = "เม็ด";

    // JPanels
    JPanel panelBody = new JPanel();
    JPanel panelSub = newFlowLayout();
    JPanel panelLiquidHint = new JPanel();
    JPanel panelColor = newFlowLayout();
    JPanel panelTabletColor = newFlowLayout();
    JPanel panelCapsuleColor = newFlowLayout();
    JPanel panelLiquidColor = newFlowLayout();

    // JTextFields
    JTextField tfMedName = makeTextField(16);
    JTextField tfMedDescription = makeTextField(16);
    JTextField tfAmount = makeNumberField(2);
    JTextField tfTotalMeds = makeNumberField(2);
    JTextField tfEvery = makeNumberField(2);

    // JLabels
    JLabel labelUnit1 = makeLabel(medUnit);
    JLabel labelUnit2 = makeLabel(medUnit);
    JLabel labelHeading1 = makeBoldLabel("ข้อมูลพื้นฐาน");
    JLabel labelHeading2 = makeBoldLabel("ขนาดและเวลาที่ต้องรับประทาน*");
    JLabel labelHeading3 = makeBoldLabel("ข้อมูลอื่น ๆ");
    JLabel labelHint1 = makeSmallerLabel("1 ช้อนชา = 5 มิลลิลิตร");
    JLabel labelHint2 = makeSmallerLabel("1 ช้อนโต๊ะ = 15 มิลลิลิตร");
    JLabel labelAutoConvert = makeLabel(" ");

    JButton btnAuto = makeBlueButton("คำนวณยาน้ำอัตโนมัติ");

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
    JCheckBox cbEvery = makeCheckBox("ทุก ๆ ");

    // JRadioButtons
    JRadioButton rbBefore = makeRadioButton(medDoseStr[0]);
    JRadioButton rbAfter = makeRadioButton(medDoseStr[1]);
    JRadioButton rbImmediately = makeRadioButton(medDoseStr[2]);

    // Radio Groups
    ButtonGroup bgMedDoseStr = new ButtonGroup();
    bgMedDoseStr.add(rbBefore);
    bgMedDoseStr.add(rbAfter);
    bgMedDoseStr.add(rbImmediately);

    // Pickers
    DatePicker pickerStart = makeTodayPicker();
    DatePicker pickerEXP = makeDatePicker();
    TimePicker pickerStartTime = makeTimeNowPicker();

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    panelLiquidHint.setLayout(new BoxLayout(panelLiquidHint, BoxLayout.PAGE_AXIS));
    panelLiquidHint.setBackground(mainBlue);
    setPadding(panelBody, 0, 0, 40);
    setPadding(labelHeading2, 20, 0, 0);
    setPadding(labelHeading3, 20, 0, 0);
    setPadding(panelLiquidHint, 16);
    panelLiquidHint.setVisible(false);
    panelCapsuleColor.setVisible(false);
    panelLiquidColor.setVisible(false);
    labelAutoConvert.setVisible(false);
    labelHint1.setForeground(Color.WHITE);
    labelHint2.setForeground(Color.WHITE);

    // Listeners

    if (type.equals("edit")) {
      // ========================= EDIT ==========================
      switch (medicine.getMedDoseStr()) {
        case "ก่อนอาหาร":
          rbBefore.setSelected(true);
          break;
        case "หลังอาหาร":
          rbAfter.setSelected(true);
          break;
        case "หลังอาหารทันที / พร้อมอาหาร":
          rbImmediately.setSelected(true);
          break;
      }
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
      btnEdit.addActionListener(e -> {
        String selectedMedType = getMedType()[cbMedType.getSelectedIndex()];
        String currType;
        String selectedColor = "";
        if (selectedMedType.equals("ยาแคปซูล")) {
          currType = "capsule";
          selectedColor = getTabletColor()[cbCapsuleColor01.getSelectedIndex()];
          selectedColor += "-";
          selectedColor += getTabletColor()[cbCapsuleColor02.getSelectedIndex()];
        } else if (selectedMedType.equals("ยาเม็ด")) {
          currType = "tablet";
          selectedColor = getTabletColor()[cbTabletColor.getSelectedIndex()];
        } else if (selectedMedType.equals("ยาน้ำ")) {
          currType = "liquid";
          selectedColor = getLiquidColor()[cbLiquidColor.getSelectedIndex()];
        } else {
          currType = "spray";
        }

        // DOSE INFORMATION
        ArrayList<String> selectedMedTime = new ArrayList<>();
        boolean isSelectedMedTime = false;
        boolean isSelectedStr = false;
        String selectedDoseStr = "";
        if (cbMorning.isSelected()) {
          selectedMedTime.add("เช้า");
          isSelectedMedTime = true;
        }
        if (cbAfternoon.isSelected()) {
          selectedMedTime.add("กลางวัน");
          isSelectedMedTime = true;
        }
        if (cbEvening.isSelected()) {
          selectedMedTime.add("เย็น");
          isSelectedMedTime = true;
        }
        if (cbBed.isSelected()) {
          selectedMedTime.add("ก่อนนอน");
          isSelectedMedTime = true;
          isSelectedStr = true;
        }
        if (cbEvery.isSelected()) {
          selectedMedTime.add("ทุก ๆ ");
          selectedDoseStr = tfEvery.getText() + " ชั่วโมง";
          isSelectedMedTime = true;
          isSelectedStr = true;
        }

        if (rbBefore.isSelected()) {
          selectedDoseStr = "ก่อนอาหาร";
          isSelectedStr = true;
        } else if (rbAfter.isSelected()) {
          selectedDoseStr = "หลังอาหาร";
          isSelectedStr = true;
        } else if (rbImmediately.isSelected()) {
          selectedDoseStr = "หลังอาหารทันที / พร้อมอาหาร";
          isSelectedStr = true;
        }
        Date exp = null;
        try {
          exp = Date.from(pickerEXP.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (NullPointerException ignored) {
        }

        if (tfMedName.getText().equals("") || tfAmount.getText().equals("") || tfTotalMeds.getText()
            .equals("") || !isSelectedMedTime || !isSelectedStr || pickerStart.getText().equals("")
            || pickerStartTime.getText().equals("")) {
          fireErrorDialog("กรุณากรอกข้อมูลให้ครบตามช่องที่มีเครื่องหมาย *");
        } else {
          String newDateStart = pickerStart.getDate() + " " + pickerStartTime.getTime();
          medicine.setMedName(tfMedName.getText());
          medicine.setMedType(currType);
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
            reloadMedicines();
            backTo("ยาทั้งหมด");
            panelRight.remove(panelEditMedicine(medicine));
            reloadOverview();
          } catch (SQLException e1) {
            fireDBErrorDialog();
            e1.printStackTrace();
          }
        }
      });
      // ===================== END EDIT =====================
    } else if (type.equals("add")) {
      btnSave.addActionListener(e -> {
        String selectedMedType = getMedType()[cbMedType.getSelectedIndex()];
        String currType;
        String selectedColor = "";
        if (selectedMedType.equals("ยาแคปซูล")) {
          currType = "capsule";
          selectedColor = getTabletColor()[cbCapsuleColor01.getSelectedIndex()];
          selectedColor += "-";
          selectedColor += getTabletColor()[cbCapsuleColor02.getSelectedIndex()];
        } else if (selectedMedType.equals("ยาเม็ด")) {
          currType = "tablet";
          selectedColor = getTabletColor()[cbTabletColor.getSelectedIndex()];
        } else if (selectedMedType.equals("ยาน้ำ")) {
          currType = "liquid";
          selectedColor = getLiquidColor()[cbLiquidColor.getSelectedIndex()];
        } else {
          currType = "spray";
        }

        // Dose Information
        ArrayList<String> selectedMedTime = new ArrayList<>();
        boolean isSelectedMedTime = false;
        boolean isSelectedStr = false;
        String selectedDoseStr = "";
        if (cbMorning.isSelected()) {
          selectedMedTime.add("เช้า");
          isSelectedMedTime = true;
        }
        if (cbAfternoon.isSelected()) {
          selectedMedTime.add("กลางวัน");
          isSelectedMedTime = true;
        }
        if (cbEvening.isSelected()) {
          selectedMedTime.add("เย็น");
          isSelectedMedTime = true;
        }
        if (cbBed.isSelected()) {
          selectedMedTime.add("ก่อนนอน");
          isSelectedMedTime = true;
          isSelectedStr = true;
        }
        if (cbEvery.isSelected()) {
          selectedMedTime.add("ทุก ๆ ");
          selectedDoseStr = tfEvery.getText() + " ชั่วโมง";
          isSelectedMedTime = true;
          isSelectedStr = true;
        }

        if (rbBefore.isSelected()) {
          selectedDoseStr = "ก่อนอาหาร";
          isSelectedStr = true;
        } else if (rbAfter.isSelected()) {
          selectedDoseStr = "หลังอาหาร";
          isSelectedStr = true;
        } else if (rbImmediately.isSelected()) {
          selectedDoseStr = "หลังอาหารทันที / พร้อมอาหาร";
          isSelectedStr = true;
        }
        Date exp = null;
        try {
          exp = Date.from(pickerEXP.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (NullPointerException ignored) {
        }

        if (tfMedName.getText().equals("") || tfAmount.getText().equals("") || tfTotalMeds.getText()
            .equals("") || !isSelectedMedTime || !isSelectedStr || pickerStart.getText().equals("")
            || pickerStartTime.getText().equals("")) {
          fireErrorDialog("กรุณากรอกข้อมูลให้ครบตามช่องที่มีเครื่องหมาย *");
        } else {
          String dateStart = pickerStart.getDate() + " " + pickerStartTime.getTime();
          Medicine med = new Medicine(tfMedName.getText(), currType, selectedColor,
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
        }
      });
    } else if (type.equals("first")) {
      btnAddFirst.addActionListener(e -> {
        String selectedMedType = getMedType()[cbMedType.getSelectedIndex()];
        String currType;
        String selectedColor = "";
        if (selectedMedType.equals("ยาแคปซูล")) {
          currType = "capsule";
          selectedColor = getTabletColor()[cbCapsuleColor01.getSelectedIndex()];
          selectedColor += "-";
          selectedColor += getTabletColor()[cbCapsuleColor02.getSelectedIndex()];
        } else if (selectedMedType.equals("ยาเม็ด")) {
          currType = "tablet";
          selectedColor = getTabletColor()[cbTabletColor.getSelectedIndex()];
        } else if (selectedMedType.equals("ยาน้ำ")) {
          currType = "liquid";
          selectedColor = getLiquidColor()[cbLiquidColor.getSelectedIndex()];
        } else {
          currType = "spray";
        }

        // Dose Information
        ArrayList<String> selectedMedTime = new ArrayList<>();
        boolean isSelectedMedTime = false;
        boolean isSelectedStr = false;
        String selectedDoseStr = "";
        if (cbMorning.isSelected()) {
          selectedMedTime.add("เช้า");
          isSelectedMedTime = true;
        }
        if (cbAfternoon.isSelected()) {
          selectedMedTime.add("กลางวัน");
          isSelectedMedTime = true;
        }
        if (cbEvening.isSelected()) {
          selectedMedTime.add("เย็น");
          isSelectedMedTime = true;
        }
        if (cbBed.isSelected()) {
          selectedMedTime.add("ก่อนนอน");
          isSelectedMedTime = true;
          isSelectedStr = true;
        }
        if (cbEvery.isSelected()) {
          selectedMedTime.add("ทุก ๆ ");
          selectedDoseStr = tfEvery.getText() + " ชั่วโมง";
          isSelectedMedTime = true;
          isSelectedStr = true;
        }

        if (rbBefore.isSelected()) {
          selectedDoseStr = "ก่อนอาหาร";
          isSelectedStr = true;
        } else if (rbAfter.isSelected()) {
          selectedDoseStr = "หลังอาหาร";
          isSelectedStr = true;
        } else if (rbImmediately.isSelected()) {
          selectedDoseStr = "หลังอาหารทันที / พร้อมอาหาร";
          isSelectedStr = true;
        }
        Date exp = null;
        try {
          exp = Date.from(pickerEXP.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (NullPointerException ignored) {
        }
        if (tfMedName.getText().equals("") || tfAmount.getText().equals("") || tfTotalMeds.getText()
            .equals("") || !isSelectedMedTime || !isSelectedStr || pickerStart.getText().equals("")
            || pickerStartTime.getText().equals("")) {
          fireErrorDialog("กรุณากรอกข้อมูลให้ครบตามช่องที่มีเครื่องหมาย *");
        } else {
          String dateStart = pickerStart.getDate() + " " + pickerStartTime.getTime();
          Medicine med = new Medicine(tfMedName.getText(), currType, selectedColor,
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
        }
      });
    }

    tfAmount.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateLabel();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateLabel();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        updateLabel();
      }

      private void updateLabel() {
        int table;
        int tea;
        try {
          table = tableSpoonCalc(Integer.valueOf(tfAmount.getText()));
          tea = teaSpoonCalc(Integer.valueOf(tfAmount.getText()));
        } catch (NumberFormatException ignored) {
          table = 0;
          tea = 0;
        }

        String labelText = "(";
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
        labelAutoConvert.setText(labelText);
      }
    });

    cbMedType.addActionListener(e -> {
      switch (cbMedType.getSelectedIndex()) {
        case 0:
          panelLiquidHint.setVisible(false);
          panelColor.setVisible(true);
          panelTabletColor.setVisible(true);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelAutoConvert.setVisible(false);
          labelUnit1.setText("เม็ด");
          labelUnit2.setText("เม็ด");
          break;
        case 1:
          panelLiquidHint.setVisible(false);
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(true);
          panelLiquidColor.setVisible(false);
          labelAutoConvert.setVisible(false);
          labelUnit1.setText("แคปซูล");
          labelUnit2.setText("แคปซูล");
          break;
        case 2:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(true);
          panelLiquidHint.setVisible(true);
          labelAutoConvert.setVisible(true);
          labelUnit1.setText("มิลลิลิตร");
          labelUnit2.setText("มิลลิลิตร");
          break;
        default:
          panelLiquidHint.setVisible(false);
          panelColor.setVisible(false);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelAutoConvert.setVisible(false);
          labelUnit1.setText("มิลลิลิตร");
          labelUnit2.setText("มิลลิลิตร");
          break;
      }
    });

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
      if (cbBed.isSelected() && !cbMorning.isSelected() && !cbAfternoon.isSelected() && !cbEvening
          .isSelected()) {
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
      if (cbBed.isSelected() && !cbMorning.isSelected() && !cbAfternoon.isSelected() && !cbEvening
          .isSelected()) {
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
      if (cbBed.isSelected() && !cbMorning.isSelected() && !cbAfternoon.isSelected() && !cbEvening
          .isSelected()) {
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
      if (cbBed.isSelected() && !cbMorning.isSelected() && !cbAfternoon.isSelected() && !cbEvening
          .isSelected()) {
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

    JFrame frameAuto = new JFrame("ระบบคำนวณปริมาณยาน้ำอัตโนมัติ");

    btnAuto.addActionListener(e -> {
      // JOptionPane is not wrapping text
      String text = "<html><body><p style='text-align: center; width: 400px;'>ระบบคำนวณการรับประทานยาน้ำอัตโนมัติสำหรับเด็กอายุไม่เกิน 14 ปีของโปรแกรม jMedicine เป็นเพียงเครื่องมือช่วยอำนวยความสะดวกในการกำหนดปริมาณยาที่คำนวณจากอายุและน้ำหนักที่ท่านได้บันทึกไว้ โปรดปรึกษาการใช้ยาจากเภสัชกรก่อนทุกครั้ง การใช้ระบบนี้หมายความว่าท่านได้ทำความเข้าใจและศึกษาข้อมูลยาจากเอกสารประกอบตัวยาเรียบร้อยแล้ว</p></body></html>";
      fireInfoDialog(text);

      // JPanels
      JPanel panelMain = new JPanel(new BorderLayout());
      JPanel panelContainer = new JPanel();
      JPanel panel = newFlowLayout();

      // Styling
      panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.PAGE_AXIS));
      setPadding(panelMain, 20);

      // Required information
      int age = getUser().getUserAge();
      double weight = getUser().getUserWeight();

      String[] availableMedicines = {"Amoxicillin Syrup", "Bromhexine Syrup", "Cloxacillin Syrup",
          "CPM Syrup", "Paracetamol Syrup"};
      String[] availableIntensities = {"125 mg / 5 ml", "4 mg / 5 ml", "125 mg / 5 ml",
          "2 mg / 5 ml", "120 mg / 5 ml", "125 mg / 5 ml", "2 mg / 5 ml"};
      String[] medicineDescriptions = {"ยาแก้อักเสบ ฆ่าเชื้อ", "ยาแก้ไอ ละลายเสมหะ", "ยาฆ่าเชื้อ",
          "ยาแก้แพ้", "ยาแก้ปวด"};
      double[] medicineDoses = {20 * weight / 3, 0.7 * weight / 3, 50 * weight / 4,
          0.35 * weight / 6, 10 * weight / 6};
      ArrayList<String[]> medicineTimes = new ArrayList();
      medicineTimes.add(new String[]{"ทุก ๆ "});
      medicineTimes.add(new String[]{"เช้า", "กลางวัน", "เย็น"});
      medicineTimes.add(new String[]{"เช้า", "กลางวัน", "เย็น", "ก่อนนอน"});
      medicineTimes.add(new String[]{"ทุก ๆ "});
      medicineTimes.add(new String[]{"ทุก ๆ "});
      String[] medicineDoseStrs = {"8", "หลังอาหาร", "หลังอาหาร", "4", "4"};

      JLabel labelIntensity = makeLabel(availableIntensities[0]);
      JButton btnSubmit = makeBlueButton("คำนวณยา");

      JComboBox<String> cbMedicines = makeComboBox(availableMedicines);

      btnSubmit.addActionListener(action -> {
        // Reset all
        cbMorning.setSelected(false);
        cbAfternoon.setSelected(false);
        cbEvening.setSelected(false);
        cbBed.setSelected(false);
        cbEvery.setSelected(false);
        rbAfter.setSelected(false);
        rbBefore.setSelected(false);
        rbImmediately.setSelected(false);
        tfEvery.setText("");
        int selectedIndex = cbMedicines.getSelectedIndex();
        tfMedName.setText(availableMedicines[selectedIndex]);
        tfMedDescription.setText(medicineDescriptions[selectedIndex]);
        cbMedType.setSelectedIndex(2);
        tfAmount.setText(String.valueOf((int) medicineDoses[selectedIndex]));
        for (String time : medicineTimes.get(selectedIndex)) {
          if (time.equals("เช้า")) {
            cbMorning.setSelected(true);
          }
          if (time.equals("กลางวัน")) {
            cbAfternoon.setSelected(true);
          }
          if (time.equals("เย็น")) {
            cbEvening.setSelected(true);
          }
          if (time.equals("ก่อนนอน")) {
            cbBed.setSelected(true);
          }
          if (time.equals("ทุก ๆ ")) {
            cbEvery.setSelected(true);
            tfEvery.setText(medicineDoseStrs[selectedIndex]);
          }
        }
        if (medicineDoseStrs[selectedIndex].equals("หลังอาหาร")) {
          rbAfter.setSelected(true);
        }
        frameAuto.setVisible(false);
      });
      cbMedicines.addActionListener(selection -> {
        int current = cbMedicines.getSelectedIndex();
        labelIntensity.setText(availableIntensities[current]);
      });

      panel.add(makeBoldLabel("ข้อมูลปัจจุบันของคุณ: "));
      panel.add(makeLabel("อายุ: " + age + " ปี, น้ำหนัก: " + weight + " กิโลกรัม"));
      panelContainer.add(panel);

      panel = newFlowLayout();
      panel.add(makeBoldLabel("เลือกตัวยา:"));
      panel.add(cbMedicines);
      panelContainer.add(panel);

      panel = newFlowLayout();
      panel.add(makeBoldLabel("ความเข้มข้นของยา:"));
      panel.add(labelIntensity);
      panelContainer.add(panel);

      panel = newFlowLayout();
      panel.add(
          makeLabel("เมื่อกดปุ่มคำนวณยา ระบบจะกรอกปริมาณยาและเวลาที่ต้องรับประทานให้โดยอัตโนมัติ"));
      panelContainer.add(panel);

      panel = newFlowLayout();
      panel.add(makeLabel(
          "อย่างไรก็ตาม ท่านต้องกรอกข้อมูลเพิ่มเติมอื่น ๆ ให้ครบถ้วนในช่องที่มีเครื่องหมาย *"));
      panelContainer.add(panel);

      panelMain.add(panelContainer, BorderLayout.CENTER);
      panelMain.add(btnSubmit, BorderLayout.SOUTH);

      frameAuto.add(panelMain);
      frameAuto.setSize(new Dimension(660, 380));
      frameAuto.setMinimumSize(new Dimension(620, 380));
      frameAuto.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      frameAuto.setLocationRelativeTo(null);
      frameAuto.setVisible(true);
    });

    panelLiquidHint.add(getInfoPic());
    panelLiquidHint.add(labelHint1);
    panelLiquidHint.add(labelHint2);

    if (getUser().getUserAge() > 0 && getUser().getUserAge() < 15) {
      panelSub = newFlowLayout();
      panelSub.add(btnAuto);
      panelBody.add(panelSub);
    }

    panelSub = newFlowLayout();
    panelSub.add(labelHeading1);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("ชื่อยา*"));
    panelSub.add(tfMedName);
    panelSub.add(makeLabel("คำอธิบายยา (เช่น ยาแก้ปวด)"));
    panelSub.add(tfMedDescription);
    panelBody.add(panelSub);

    JPanel panelMedSettings = newFlowLayout();

    JPanel panelType = newFlowLayout();
    panelType.add(makeLabel("ประเภท*"));
    panelType.add(cbMedType);
    panelMedSettings.add(panelType);

    panelColor.add(makeLabel("สีของยา*"));
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
    panelSub.add(labelAutoConvert);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(panelLiquidHint);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelHeading3);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("จำนวนยาทั้งหมด*"));
    panelSub.add(tfTotalMeds);
    panelSub.add(labelUnit2);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("วันและเวลาที่เริ่มทานยา*"));
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

  private static JPanel makeMedCard(Medicine medicine, boolean isActive) {
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
    if (!isActive) {
      labelTitle.setForeground(Color.GRAY);
      labelShortInfo.setForeground(Color.GRAY);
    }

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

  public static void reloadMedicines() {
    panelRight.remove(panelMedicines);
    panelRight.remove(panelAddMedicine);
    panelRight.add(panelAddMedicine(), "เพิ่มยาใหม่");
    panelAllMedicines();
  }
}
