package GUI;

import static GUI.GUIHelper.*;
import static api.Login.*;

import api.LoginException;
import api.MedicineDB;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PermissionHandler;
import com.teamdev.jxbrowser.chromium.PermissionRequest;
import com.teamdev.jxbrowser.chromium.PermissionStatus;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import core.Appointment;
import core.AppointmentUtil;
import core.Doctor;
import core.DoctorUtil;
import core.Medicine;
import core.MedicineUtil;
import core.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import core.LocationHelper;
import mdlaf.shadows.DropShadowBorder;


/**
 * GUI class creates all graphic user interface, all in javax.swing.
 */

public class GUI implements ActionListener, KeyListener {

  private JFrame frameWelcome, frameMain;
  private JPanel panelLeft;
  private JPanel panelRight;
  private JPanel panelWelcome;
  private JPanel panelSub01, panelSub02, panelSub03, panelSub04, panelSub05, panelSub06;
  private JPanel panelTitle, panelLoop, cardLoop;
  private JPanel panelSignIn, panelLoadingSignIn, panelErrorSignIn;
  private JTextField tfUserName;
  private JPasswordField tfPassword, tfPasswordConfirm;
  private static JButton buttons[];
  private Dimension windowSize, minSize;
  private static Color mainBlue;
  private User user;
  private MedicineUtil medUtil;

  public GUI(Dimension windowSize) {
    this.medUtil = new MedicineUtil();
    this.windowSize = windowSize;
    this.minSize = new Dimension(800, 600);
    Locale locale = new Locale("th", "TH");
    mainBlue = new Color(20, 101, 155);
    JOptionPane.setDefaultLocale(locale);
    GUIHelper.setup();
  }

  private void main() {
    /* Creates the main frame including left navigation and 6 sub panels on the right */

    // Init main panels
    JPanel panelMain = new JPanel(new BorderLayout());
    panelLeft = new JPanel(new GridBagLayout());
    panelRight = new JPanel(new CardLayout());
    setPadding(panelLeft, 20, 0, 5, 0);
    setPadding(panelRight, 25, 20, 10, 20);

    // Init panels that will be switched inside the right panel
    panelSub01 = new JPanel(new BorderLayout());
    panelSub02 = new JPanel(new BorderLayout());
    panelSub03 = new JPanel(new BorderLayout());
    panelSub04 = new JPanel(new BorderLayout());
    panelSub05 = new JPanel(new BorderLayout());
    panelSub06 = new JPanel(new BorderLayout());

    // Make left navigation
    makeLeftNavigation();

    // Add all sub panels into the right panel
    panelRight.add(panelOverview(), "ภาพรวม");
    panelRight.add(panelAllMedicines(), "ยาทั้งหมด");
    panelRight.add(panelAllAppointments(), "นัดแพทย์");
    panelRight.add(panelAllDoctors(), "แพทย์");
    panelRight.add(panelNearbyHospitals(), "โรงพยาบาลใกล้เคียง");
    panelRight.add(panelSettings(), "การตั้งค่า");
    panelRight.add(panelAddMedicine(), "เพิ่มยาใหม่");
    panelRight.add(panelAddDoctor(), "เพิ่มแพทย์ใหม่");
    panelRight.add(panelAddAppointment(), "เพิ่มนัดใหม่");

    // Add left navigation and right panel into the main panel
    panelMain.add(panelLeft, BorderLayout.WEST);
    panelMain.add(panelRight, BorderLayout.CENTER);

    // Init main frame
    frameMain = new JFrame("jMedicine");
    frameMain.add(panelMain);
    frameMain.setMinimumSize(this.minSize);
    frameMain.setSize(this.windowSize);
    frameMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  private JPanel panelOverview() {
    /*
      Creates GUI of overview panel, displaying a summary of upcoming events,
      including medication reminders and doctor appointments.
     */

    // Init title panel displaying title label
    panelTitle = newFlowLayout();
    String today = GUIHelper.formatDMYFull.format(new Date());
    panelTitle.add(makeTitleLabel(today));
    setPadding(panelTitle, 0, 0, 0, 2);

    // Init card loop
    panelLoop = newPanelLoop();
    // TODO: Fetch these upcoming events from the database
    // Sample loop
    cardLoop = makeOverviewCard("12.30 น. (อีก 1 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)",
        "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    cardLoop = makeOverviewCard("18.30 น. (อีก 7 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)",
        "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    // End sample loop
    setPadding(panelLoop, 0, 0, 600, 0);

    // Add all panels into the main panel
    panelSub01.add(panelTitle, BorderLayout.NORTH);
    panelSub01.add(panelLoop);

    return panelSub01;
  }

  private JPanel panelAllMedicines() {
    /*
      Creates GUI displaying all medicines that user has had input.
      All medicines will be displayed in a card with a medicine icon,
      a name and a short summary.
     */

    // Init title panel displaying title label
    JLabel labelTitle = makeTitleLabel("ยาทั้งหมด");
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle);

    // Fetch all medicines from the records
    ArrayList<Medicine> userMedicines = user.getUserMedicines();

    // Init panel loop
    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มยาใหม่"));

    if (userMedicines.isEmpty()) {
      labelTitle.setText("คุณยังไม่มียาที่บันทึกไว้");
    } else {
      labelTitle.setText("ยาทั้งหมด");
      for (Medicine medCurrent : userMedicines) {
        cardLoop = makeMedCard(medCurrent);
        panelLoop.add(cardLoop);
      }
    }

    // Add all panels into the main panel
    panelSub02.add(panelTitle, BorderLayout.NORTH);
    panelSub02.add(panelLoop);

    return panelSub02;
  }

  private JPanel panelAllAppointments() {
    /*
      Creates GUI displaying all appointments that user has had input.
      All appointments will be displayed in a card with a default icon,
      a date and a short summary.
     */

    // Init title panel displaying title label
    JLabel labelTitle = makeTitleLabel("นัดแพทย์");
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle);

    // Init panel loop
    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มนัดใหม่"));

    // Fetch all medicines from the records
    ArrayList<Appointment> userAppointment = user.getUserAppointments();

    if (userAppointment.isEmpty()) {
      labelTitle.setText("คุณยังไม่มีนัดแพทย์ที่บันทึกไว้");
    } else {
      labelTitle.setText("นัดแพทย์");
      for (Appointment appCurrent : userAppointment) {
        cardLoop = makeAppointmentCard(appCurrent);
        panelLoop.add(cardLoop);
      }
    }

    // Add all panels into the main panel
    panelSub03.add(panelLoop);
    panelSub03.add(panelTitle, BorderLayout.NORTH);
    panelSub03.setBackground(Color.WHITE);

    return panelSub03;
  }

  private JPanel panelAllDoctors() {
    /*
      Creates GUI displaying all doctors that user has had input.
      All doctors will be displayed in a card with a default icon,
      a name and a short summary.
     */

    // Init title panel displaying title label
    JLabel labelTitle = makeTitleLabel("แพทย์");
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle);

    // Fetch all doctors
    ArrayList<Doctor> userDoctors = user.getUserDoctors();

    // Init panel loop
    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มแพทย์ใหม่"));

    if (userDoctors.isEmpty()) {
      labelTitle.setText("คุณยังไม่มีแพทย์ที่บันทึกไว้");
    } else {
      labelTitle.setText("แพทย์");
      for (Doctor doctorCurrent : userDoctors) {
        cardLoop = makeDoctorCard(doctorCurrent);
        panelLoop.add(cardLoop);
      }
    }

    // Add all panels into the main panel
    panelSub04.add(panelLoop);
    panelSub04.add(panelTitle, BorderLayout.NORTH);
    panelSub04.setBackground(Color.WHITE);

    return panelSub04;
  }

  private JPanel panelNearbyHospitals() {
    /*
      Creates GUI displaying Google LocationHelper that is showing the current position
      of the user, fetched from a public IP address, queried nearby hospitals.
     */

    // Init title panel displaying title label
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeTitleLabel("โรงพยาบาลใกล้เคียง"));

    // Fetch current location into an array of double,
    // containing latitude and longitude.
    double[] location = LocationHelper.getLocation();

    // Init web browser
    Browser browser = new Browser();
    BrowserView view = new BrowserView(browser);
    // Try to grant the geolocation permission
    browser.setPermissionHandler(request -> PermissionStatus.GRANTED);
    // Load URL that query the hospital around the current position
    browser.loadURL(
        "https://www.google.co.th/maps/search/hospitals/@" + location[0] + "," + location[1]
            + ",12z");

    // Add all sub panels into the main panel
    panelSub05.add(panelTitle, BorderLayout.NORTH);
    panelSub05.add(view);

    return panelSub05;
  }

  private JPanel panelSettings() {
    /* Creates GUI displaying user's settings */

    // Init title panel displaying title label

    // JPanels
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeTitleLabel("การตั้งค่า"));
    JPanel panelBody = new JPanel();

    // JToggle
    JToggleButton toggleNoti = makeToggle("เปิดการแจ้งเตือน (macOS เท่านั้น)", true);

    // JLabels
    JLabel labelEdit = makeLabel("แก้ไขข้อมูลส่วนตัว");
    JLabel labelSignOut = makeLabel("ออกจากระบบ");
    JLabel labelUserName = makeTitleLabel(user.getUserName());

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(labelSignOut, 0, 0, 20);
    setPadding(panelBody, 20, 0, 180);
    setPadding(labelUserName, 0, 0, 20, 0);

    makeLabelClickable(labelSignOut, "ยังไม่ได้เข้าสู่ระบบ");

    JPanel panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("ผู้ใช้งานปัจจุบัน"));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelUserName);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("ตั้งค่าผู้ใช้งาน"));
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = newFlowLayout();
    panelSub.add(labelEdit);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelSignOut);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("ตั้งค่าโปรแกรม"));
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = newFlowLayout();
    panelSub.add(toggleNoti);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("เวอร์ชั่น 0.6.3"));
    panelBody.add(panelSub);

    // Add all sub panels into the main panel
    panelSub06.add(panelTitle, BorderLayout.NORTH);
    panelSub06.add(panelBody);

    return panelSub06;
  }

  private JPanel panelAddMedicine() {
    /* Creates outer GUI when user add a new medicine from all medicines page. */

    // JPanels
    JPanel panelAddMedicine = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel(new BorderLayout());
    panelTitle = new JPanel(new BorderLayout());

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

  private JPanel panelEditMedicine(Medicine medicine) {
    /* Creates GUI of the form for editing a new medicine. */
    String medUnit = medicine.getMedUnit();

    // JPanels
    JPanel panelEditMed = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelButtons = new JPanel(new BorderLayout());
    panelTitle = new JPanel(new BorderLayout());
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
    JButton btnSave = makeButton("บันทึก");

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
    JTextField tfMedEXP = makeTextField(10);

    tfMedName.setText(medicine.getMedName());
    tfMedDescription.setText(medicine.getMedDescription());
    tfTotalMeds.setText(String.valueOf(medicine.getMedTotal()));
    tfMedEXP.setText(formatDMY.format(medicine.getMedEXP()));

    // JLabels
    JLabel labelUnit = makeLabel(medUnit);
    JLabel labelUnitMorning = makeLabel(medUnit);
    JLabel labelUnitAfternoon = makeLabel(medUnit);
    JLabel labelUnitEvening = makeLabel(medUnit);
    JLabel labelUnitBed = makeLabel(medUnit);

    // Arrays
    String[] medType = medUtil.getMedType();
    String[] tabletColor = medUtil.getTabletColor();
    String[] liquidColor = medUtil.getLiquidColor();
    String[] medTime = medUtil.getMedTime();
    String[] medDoseStr = medUtil.getMedDoseStr();
    ArrayList<ImageIcon> tabletColorIcons = new ArrayList<>();
    ArrayList<ImageIcon> liquidColorIcons = new ArrayList<>();

    for (String color : tabletColor) {
      tabletColorIcons.add(new ImageIcon(GUIHelper.imgPath + "/colors/" + color + ".png"));
    }
    for (String color : liquidColor) {
      liquidColorIcons.add(new ImageIcon(GUIHelper.imgPath + "/colors/" + color + ".png"));
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
        cbTabletColor.setSelectedIndex(medUtil.getTabletColorIndex(medicine.getMedColor()));
        panelColor.setVisible(true);
        panelTabletColor.setVisible(true);
        panelCapsuleColor.setVisible(false);
        panelLiquidColor.setVisible(false);
        break;
      case "capsule":
        cbMedType.setSelectedIndex(1);
        String[] currentColors = medicine.getMedColor().split("-");
        cbCapsuleColor01.setSelectedIndex(medUtil.getTabletColorIndex(currentColors[0]));
        cbCapsuleColor02.setSelectedIndex(medUtil.getTabletColorIndex(currentColors[1]));
        panelColor.setVisible(true);
        panelTabletColor.setVisible(false);
        panelCapsuleColor.setVisible(true);
        panelLiquidColor.setVisible(false);
        break;
      case "liquid":
        cbMedType.setSelectedIndex(2);
        cbLiquidColor.setSelectedIndex(medUtil.getLiquidColorIndex(medicine.getMedColor()));
        panelColor.setVisible(true);
        panelTabletColor.setVisible(false);
        panelCapsuleColor.setVisible(false);
        panelLiquidColor.setVisible(true);
        break;
      case "inject":
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
      saveSwitcher(panelRight, panelEditMedicine(medicine), panelViewMedicine(medicine),
          medicine.getMedName());
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
    panelSub.add(tfMedEXP);
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

  private JPanel panelViewMedicine(Medicine medicine) {
    /* Creates GUI displaying all information of a single medicine */

    // JPanels
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelButtons = new JPanel(new BorderLayout());
    panelTitle = new JPanel(new BorderLayout());

    // JLabels
    JLabel labelPic = medUtil.getMedIcon(medicine);
    String medName = medicine.getMedName();

    // JButtons
    JButton btnEdit = makeButton("แก้ไขข้อมูล");
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
    btnEdit.addActionListener(e -> {
      editSwitcher(panelRight, panelEditMedicine(medicine));
    });
    btnRemove.addActionListener(e -> {
      int dialogResult = 0;
      JLabel labelConfirm = makeLabel(
          "ต้องการลบยานี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");
      setPadding(labelConfirm, 0, 16, 0, 0);

      beep("warning");
      try {
        Image img = ImageIO.read(new File(GUIHelper.imgWarningSrc));
        Icon icon = new ImageIcon(img);
        dialogResult = JOptionPane
            .showConfirmDialog(null, labelConfirm, "คุณกำลังทำการลบยา", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE, icon);
      } catch (Exception ignored) {
        dialogResult = JOptionPane
            .showConfirmDialog(null, labelConfirm, "คุณกำลังทำการลบยา", JOptionPane.YES_NO_OPTION);
      }

      if (dialogResult == JOptionPane.YES_OPTION) {
        String labelMessage;
        if (user.removeUserMedicine(medicine)) {
          labelMessage = getRemoveSuccessfulMessage("ยา");
          fireSuccessDialog(labelMessage);
        } else {
          labelMessage = getRemoveFailedMessage("ยา");
          fireErrorDialog(labelMessage);
        }
        panelRight.remove(panelAllMedicines());
        panelSub02 = null;
        panelSub02 = new JPanel(new BorderLayout());
        panelRight.add(panelAllMedicines(), "ยาทั้งหมด");
        backTo("ยาทั้งหมด");
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
      panelSub.add(labelMedTime);
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

  private JPanel panelAddDoctor() {
    /* Creates GUI of the form for adding a new doctor. */

    // JPanels
    JPanel panelAddDoctor = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    panelTitle = newFlowLayout();

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มแพทย์ใหม่", "แพทย์");
    JButton btnAdd = makeButton("บันทึกแพทย์");

    // JCheckBoxes
    JCheckBox cbSunday = makeCheckBox("วันอาทิตย์");
    JCheckBox cbMonday = makeCheckBox("วันจันทร์");
    JCheckBox cbTuesday = makeCheckBox("วันอังคาร");
    JCheckBox cbWednesday = makeCheckBox("วันพุธ");
    JCheckBox cbThursday = makeCheckBox("วันพฤหัสบดี");
    JCheckBox cbFriday = makeCheckBox("วันศุกร์");
    JCheckBox cbSaturday = makeCheckBox("วันเสาร์");

    // TimePickers
    TimePicker sundayStartPicker = new TimePicker();
    TimePicker sundayEndPicker = new TimePicker();
    TimePicker mondayStartPicker = new TimePicker();
    TimePicker mondayEndPicker = new TimePicker();
    TimePicker tuesStartPicker = new TimePicker();
    TimePicker tuesEndPicker = new TimePicker();
    TimePicker wedStartPicker = new TimePicker();
    TimePicker wedEndPicker = new TimePicker();
    TimePicker thurStartPicker = new TimePicker();
    TimePicker thurEndPicker = new TimePicker();
    TimePicker fridayStartPicker = new TimePicker();
    TimePicker fridayEndPicker = new TimePicker();
    TimePicker satStartPicker = new TimePicker();
    TimePicker satEndPicker = new TimePicker();
    TimePicker[] timePickers = {sundayStartPicker, sundayEndPicker, mondayStartPicker,
        mondayEndPicker, tuesStartPicker, tuesEndPicker, wedStartPicker, wedEndPicker,
        thurStartPicker, thurEndPicker, fridayStartPicker, fridayEndPicker, satStartPicker,
        satEndPicker};

    // JLabels
    JLabel sundayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel sundayEndLabel = makeLabel("จนถึงเวลา");
    JLabel mondayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel mondayEndLabel = makeLabel("จนถึงเวลา");
    JLabel tuesStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel tuesEndLabel = makeLabel("จนถึงเวลา");
    JLabel wedStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel wedEndLabel = makeLabel("จนถึงเวลา");
    JLabel thurStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel thurEndLabel = makeLabel("จนถึงเวลา");
    JLabel fridayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel fridayEndLabel = makeLabel("จนถึงเวลา");
    JLabel satStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel satEndLabel = makeLabel("จนถึงเวลา");
    JLabel[] labels = {sundayStartLabel, sundayEndLabel, mondayStartLabel, mondayEndLabel,
        tuesStartLabel, tuesEndLabel, wedStartLabel, wedEndLabel, thurStartLabel, thurEndLabel,
        fridayStartLabel, fridayEndLabel, satStartLabel, satEndLabel};

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelTitle, 0, 0, 20);
    setPadding(panelAddDoctor, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 10, 28);
    for (JLabel label : labels) {
      label.setVisible(false);
    }
    for (TimePicker tp : timePickers) {
      tp.setVisible(false);
    }

    // Listeners
    workTimeCheckBoxUIHandler(cbSunday, sundayStartPicker, sundayEndPicker, sundayStartLabel,
        sundayEndLabel);
    workTimeCheckBoxUIHandler(cbMonday, mondayStartPicker, mondayEndPicker, mondayStartLabel,
        mondayEndLabel);
    workTimeCheckBoxUIHandler(cbTuesday, tuesStartPicker, tuesEndPicker, tuesStartLabel,
        tuesEndLabel);
    workTimeCheckBoxUIHandler(cbWednesday, wedStartPicker, wedEndPicker, wedStartLabel,
        wedEndLabel);
    workTimeCheckBoxUIHandler(cbThursday, thurStartPicker, thurEndPicker, thurStartLabel,
        thurEndLabel);
    workTimeCheckBoxUIHandler(cbFriday, fridayStartPicker, fridayEndPicker, fridayStartLabel,
        fridayEndLabel);
    workTimeCheckBoxUIHandler(cbSaturday, satStartPicker, satEndPicker, satStartLabel, satEndLabel);

    JTextField tfDoctorName = makeTextField(20);
    JTextField tfDoctorSurName = makeTextField(20);
    JTextField tfDoctorWard = makeTextField(16);
    JTextField tfDoctorHospital = makeTextField(18);
    String[] prefixes = {"นพ.", "พญ.", "ศ.นพ", "ผศ.นพ"};
    JComboBox cbPrefix = makeComboBox(prefixes);

    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(makeLabel("คำนำหน้า"));
    panelSub.add(cbPrefix);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("ชื่อ"));
    panelSub.add(tfDoctorName);
    panelSub.add(makeLabel("นามสกุล"));
    panelSub.add(tfDoctorSurName);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("แผนก"));
    panelSub.add(tfDoctorWard);
    panelSub.add(makeLabel("ชื่อสถานพยาบาล"));
    panelSub.add(tfDoctorHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("วันและเวลาที่เข้าตรวจ"));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbSunday);
    panelSub.add(sundayStartLabel);
    panelSub.add(sundayStartPicker);
    panelSub.add(sundayEndLabel);
    panelSub.add(sundayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbMonday);
    panelSub.add(mondayStartLabel);
    panelSub.add(mondayStartPicker);
    panelSub.add(mondayEndLabel);
    panelSub.add(mondayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbTuesday);
    panelSub.add(tuesStartLabel);
    panelSub.add(tuesStartPicker);
    panelSub.add(tuesEndLabel);
    panelSub.add(tuesEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbWednesday);
    panelSub.add(wedStartLabel);
    panelSub.add(wedStartPicker);
    panelSub.add(wedEndLabel);
    panelSub.add(wedEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbThursday);
    panelSub.add(thurStartLabel);
    panelSub.add(thurStartPicker);
    panelSub.add(thurEndLabel);
    panelSub.add(thurEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbFriday);
    panelSub.add(fridayStartLabel);
    panelSub.add(fridayStartPicker);
    panelSub.add(fridayEndLabel);
    panelSub.add(fridayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbSaturday);
    panelSub.add(satStartLabel);
    panelSub.add(satStartPicker);
    panelSub.add(satEndLabel);
    panelSub.add(satEndPicker);
    panelBody.add(panelSub);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    panelAddDoctor.add(panelTitle, BorderLayout.NORTH);
    panelAddDoctor.add(scrollPane, BorderLayout.CENTER);
    panelAddDoctor.add(btnAdd, BorderLayout.SOUTH);

    return panelAddDoctor;
  }

  private JPanel panelEditDoctor(Doctor doctor) {
    /* Creates GUI of the form for editing doctor. */

    // JPanels
    JPanel panelAddDoctor = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    panelTitle = newFlowLayout();

    // JButtons
    JButton btnBack = makeBackButton("แก้ไขแพทย์", DoctorUtil.getDoctorFullName(doctor));
    JButton btnSave = makeButton("บันทึกแพทย์");

    // JCheckBoxes
    JCheckBox cbSunday = makeCheckBox("วันอาทิตย์");
    JCheckBox cbMonday = makeCheckBox("วันจันทร์");
    JCheckBox cbTuesday = makeCheckBox("วันอังคาร");
    JCheckBox cbWednesday = makeCheckBox("วันพุธ");
    JCheckBox cbThursday = makeCheckBox("วันพฤหัสบดี");
    JCheckBox cbFriday = makeCheckBox("วันศุกร์");
    JCheckBox cbSaturday = makeCheckBox("วันเสาร์");

    // TimePickers
    TimePicker sundayStartPicker = new TimePicker();
    TimePicker sundayEndPicker = new TimePicker();
    TimePicker mondayStartPicker = new TimePicker();
    TimePicker mondayEndPicker = new TimePicker();
    TimePicker tuesStartPicker = new TimePicker();
    TimePicker tuesEndPicker = new TimePicker();
    TimePicker wedStartPicker = new TimePicker();
    TimePicker wedEndPicker = new TimePicker();
    TimePicker thurStartPicker = new TimePicker();
    TimePicker thurEndPicker = new TimePicker();
    TimePicker fridayStartPicker = new TimePicker();
    TimePicker fridayEndPicker = new TimePicker();
    TimePicker satStartPicker = new TimePicker();
    TimePicker satEndPicker = new TimePicker();
    TimePicker[] timePickers = {sundayStartPicker, sundayEndPicker, mondayStartPicker,
        mondayEndPicker, tuesStartPicker, tuesEndPicker, wedStartPicker, wedEndPicker,
        thurStartPicker, thurEndPicker, fridayStartPicker, fridayEndPicker, satStartPicker,
        satEndPicker};

    // JLabels
    JLabel sundayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel sundayEndLabel = makeLabel("จนถึงเวลา");
    JLabel mondayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel mondayEndLabel = makeLabel("จนถึงเวลา");
    JLabel tuesStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel tuesEndLabel = makeLabel("จนถึงเวลา");
    JLabel wedStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel wedEndLabel = makeLabel("จนถึงเวลา");
    JLabel thurStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel thurEndLabel = makeLabel("จนถึงเวลา");
    JLabel fridayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel fridayEndLabel = makeLabel("จนถึงเวลา");
    JLabel satStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel satEndLabel = makeLabel("จนถึงเวลา");
    JLabel[] labels = {sundayStartLabel, sundayEndLabel, mondayStartLabel, mondayEndLabel,
        tuesStartLabel, tuesEndLabel, wedStartLabel, wedEndLabel, thurStartLabel, thurEndLabel,
        fridayStartLabel, fridayEndLabel, satStartLabel, satEndLabel};

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelTitle, 0, 0, 20);
    setPadding(panelAddDoctor, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 10, 28);
    for (JLabel label : labels) {
      label.setVisible(false);
    }
    for (TimePicker tp : timePickers) {
      tp.setVisible(false);
    }

    for (ArrayList<String> timeArray : doctor.getWorkTime()) {
      switch (timeArray.get(0)) {
        case "วันอาทิตย์":
          cbSunday.setSelected(true);
          sundayStartLabel.setVisible(true);
          sundayEndLabel.setVisible(true);
          sundayStartPicker.setVisible(true);
          sundayEndPicker.setVisible(true);
          sundayStartPicker.setText(timeArray.get(1));
          sundayEndPicker.setText(timeArray.get(2));
          break;

        case "วันจันทร์":
          cbMonday.setSelected(true);
          mondayStartLabel.setVisible(true);
          mondayEndLabel.setVisible(true);
          mondayStartPicker.setVisible(true);
          mondayEndPicker.setVisible(true);
          mondayStartPicker.setText(timeArray.get(1));
          mondayEndPicker.setText(timeArray.get(2));
          break;

        case "วันอังคาร":
          cbTuesday.setSelected(true);
          tuesStartLabel.setVisible(true);
          tuesEndLabel.setVisible(true);
          tuesStartPicker.setVisible(true);
          tuesEndPicker.setVisible(true);
          tuesStartPicker.setText(timeArray.get(1));
          tuesEndPicker.setText(timeArray.get(2));
          break;

        case "วันพุธ":
          cbWednesday.setSelected(true);
          wedStartLabel.setVisible(true);
          wedEndLabel.setVisible(true);
          wedStartPicker.setVisible(true);
          wedEndPicker.setVisible(true);
          wedStartPicker.setText(timeArray.get(1));
          wedEndPicker.setText(timeArray.get(2));
          break;

        case "วันพฤหัสบดี":
          cbThursday.setSelected(true);
          thurStartLabel.setVisible(true);
          thurEndLabel.setVisible(true);
          thurStartPicker.setVisible(true);
          thurEndPicker.setVisible(true);
          thurStartPicker.setText(timeArray.get(1));
          thurEndPicker.setText(timeArray.get(2));
          break;

        case "วันศุกร์":
          cbFriday.setSelected(true);
          fridayStartLabel.setVisible(true);
          fridayEndLabel.setVisible(true);
          fridayStartPicker.setVisible(true);
          fridayEndPicker.setVisible(true);
          fridayStartPicker.setText(timeArray.get(1));
          fridayEndPicker.setText(timeArray.get(2));
          break;

        case "วันเสาร์":
          cbSaturday.setSelected(true);
          satStartLabel.setVisible(true);
          satEndLabel.setVisible(true);
          satStartPicker.setVisible(true);
          satEndPicker.setVisible(true);
          satStartPicker.setText(timeArray.get(1));
          satEndPicker.setText(timeArray.get(2));
          break;
      }
    }

    // Listeners
    btnSave.addActionListener(e -> {
      saveSwitcher(panelRight, panelEditDoctor(doctor), panelViewDoctor(doctor),
          DoctorUtil.getDoctorFullName(doctor));
    });

    workTimeCheckBoxUIHandler(cbSunday, sundayStartPicker, sundayEndPicker, sundayStartLabel,
        sundayEndLabel);
    workTimeCheckBoxUIHandler(cbMonday, mondayStartPicker, mondayEndPicker, mondayStartLabel,
        mondayEndLabel);
    workTimeCheckBoxUIHandler(cbTuesday, tuesStartPicker, tuesEndPicker, tuesStartLabel,
        tuesEndLabel);
    workTimeCheckBoxUIHandler(cbWednesday, wedStartPicker, wedEndPicker, wedStartLabel,
        wedEndLabel);
    workTimeCheckBoxUIHandler(cbThursday, thurStartPicker, thurEndPicker, thurStartLabel,
        thurEndLabel);
    workTimeCheckBoxUIHandler(cbFriday, fridayStartPicker, fridayEndPicker, fridayStartLabel,
        fridayEndLabel);
    workTimeCheckBoxUIHandler(cbSaturday, satStartPicker, satEndPicker, satStartLabel, satEndLabel);

    JTextField tfDoctorName = makeTextField(20);
    JTextField tfDoctorSurName = makeTextField(20);
    JTextField tfDoctorWard = makeTextField(16);
    JTextField tfDoctorHospital = makeTextField(18);
    String[] prefixes = DoctorUtil.getPrefixes();
    JComboBox cbPrefix = makeComboBox(prefixes);

    tfDoctorName.setText(doctor.getFirstName());
    tfDoctorSurName.setText(doctor.getLastName());
    tfDoctorWard.setText(doctor.getWard());
    tfDoctorHospital.setText(doctor.getHospital());
    cbPrefix.setSelectedIndex(DoctorUtil.getPrefixIndex(doctor.getPrefix()));

    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(makeLabel("คำนำหน้า"));
    panelSub.add(cbPrefix);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("ชื่อ"));
    panelSub.add(tfDoctorName);
    panelSub.add(makeLabel("นามสกุล"));
    panelSub.add(tfDoctorSurName);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("แผนก"));
    panelSub.add(tfDoctorWard);
    panelSub.add(makeLabel("ชื่อสถานพยาบาล"));
    panelSub.add(tfDoctorHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("วันและเวลาที่เข้าตรวจ"));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbSunday);
    panelSub.add(sundayStartLabel);
    panelSub.add(sundayStartPicker);
    panelSub.add(sundayEndLabel);
    panelSub.add(sundayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbMonday);
    panelSub.add(mondayStartLabel);
    panelSub.add(mondayStartPicker);
    panelSub.add(mondayEndLabel);
    panelSub.add(mondayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbTuesday);
    panelSub.add(tuesStartLabel);
    panelSub.add(tuesStartPicker);
    panelSub.add(tuesEndLabel);
    panelSub.add(tuesEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbWednesday);
    panelSub.add(wedStartLabel);
    panelSub.add(wedStartPicker);
    panelSub.add(wedEndLabel);
    panelSub.add(wedEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbThursday);
    panelSub.add(thurStartLabel);
    panelSub.add(thurStartPicker);
    panelSub.add(thurEndLabel);
    panelSub.add(thurEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbFriday);
    panelSub.add(fridayStartLabel);
    panelSub.add(fridayStartPicker);
    panelSub.add(fridayEndLabel);
    panelSub.add(fridayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbSaturday);
    panelSub.add(satStartLabel);
    panelSub.add(satStartPicker);
    panelSub.add(satEndLabel);
    panelSub.add(satEndPicker);
    panelBody.add(panelSub);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    panelAddDoctor.add(panelTitle, BorderLayout.NORTH);
    panelAddDoctor.add(scrollPane, BorderLayout.CENTER);
    panelAddDoctor.add(btnSave, BorderLayout.SOUTH);

    return panelAddDoctor;
  }

  private void workTimeCheckBoxUIHandler(JCheckBox checkBox, TimePicker startPicker,
      TimePicker endPicker, JLabel startLabel, JLabel endLabel) {
    checkBox.addActionListener(e -> {
      if (checkBox.isSelected()) {
        startLabel.setVisible(true);
        endLabel.setVisible(true);
        startPicker.setVisible(true);
        endPicker.setVisible(true);
      } else {
        startLabel.setVisible(false);
        endLabel.setVisible(false);
        startPicker.setVisible(false);
        endPicker.setVisible(false);
      }
    });
  }

  private JPanel panelViewDoctor(Doctor doctor) {
    /* Creates GUI displaying all information of a single doctor. */

    // JPanels
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelButtons = new JPanel(new BorderLayout());
    panelTitle = new JPanel(new BorderLayout());

    String doctorName = DoctorUtil.getDoctorFullName(doctor);

    // JButtons
    JButton btnEdit = makeButton("แก้ไขข้อมูลแพทย์");
    JButton btnRemove = makeRemoveButton();
    JButton btnBack = makeBackButton(doctorName, "แพทย์");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelTitle, -6, 0, 20, 8);
    setPadding(panelBody, 0, 0, 0, 38);
    setPadding(panelView, 0, 0, 0, -20);

    // Listeners
    btnEdit.addActionListener(e -> {
      editSwitcher(panelRight, panelEditDoctor(doctor));
    });

    btnRemove.addActionListener(e -> {
      JLabel labelConfirm = makeLabel(
          "ต้องการลบแพทย์คนนี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");
      setPadding(labelConfirm, 0, 16, 0, 0);
      int dialogResult = 0;

      beep("warning");
      try {
        Image img = ImageIO.read(new File(GUIHelper.imgWarningSrc));
        Icon icon = new ImageIcon(img);
        dialogResult = JOptionPane.showConfirmDialog(null, labelConfirm, "คุณกำลังทำการลบแพทย์",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icon);
      } catch (Exception ignored) {
        dialogResult = JOptionPane.showConfirmDialog(null, labelConfirm, "คุณกำลังทำการลบแพทย์",
            JOptionPane.YES_NO_OPTION);
      }

      if (dialogResult == JOptionPane.YES_OPTION) {
        String labelMessage;
        if (user.removeUserDoctor(doctor)) {
          labelMessage = getRemoveSuccessfulMessage("แพทย์");
        } else {
          labelMessage = getRemoveFailedMessage("แพทย์");
        }
        panelRight.remove(panelAllDoctors());
        panelSub04 = null;
        panelSub04 = new JPanel(new BorderLayout());
        panelRight.add(panelAllDoctors(), "แพทย์");
        backTo("แพทย์");
        fireSuccessDialog(labelMessage);
      }
    });

    panelTitle.add(btnBack);
    panelBody.add(makeLabel("ชื่อแพทย์: " + doctorName));
    if (doctor.getWard() != null) {
      panelBody.add(makeLabel("แผนก: " + doctor.getWard()));
    }
    panelBody.add(makeLabel("โรงพยาบาล: " + doctor.getHospital()));
    if (doctor.getWorkTime() != null) {
      panelBody.add(makeLabel("เวลาเข้าตรวจ:"));
      // WorkTime is an ArrayList, convert it to a printable format
      for (ArrayList<String> workTime : doctor.getWorkTime()) {
        JLabel labelWorkTime = makeLabel(
            workTime.get(0) + " เวลา " + workTime.get(1) + " น. - " + workTime.get(2) + " น.");
        setPadding(labelWorkTime, 0, 20);
        panelBody.add(labelWorkTime);
      }
    }

    panelButtons.add(btnEdit, BorderLayout.CENTER);
    panelButtons.add(btnRemove, BorderLayout.EAST);

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(panelBody, BorderLayout.CENTER);
    panelView.add(panelButtons, BorderLayout.SOUTH);

    return panelView;
  }

  private JPanel panelViewAppointment(Appointment appointment) {
    /* Creates GUI displaying information of a single appointment. */

    // JPanels
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelButtons = new JPanel(new BorderLayout());
    panelTitle = new JPanel(new BorderLayout());

    Doctor appointmentDr = appointment.getDoctor();
    String doctorName = appointmentDr.getPrefix() + " " + appointmentDr.getName();

    String title = formatDMY.format(appointment.getTimeStart())
        + " เวลา " + formatHM.format(appointment.getTimeStart()) + " น. - "
        + formatHM.format(appointment.getTimeStop()) + " น.";

    // JButtons
    JButton btnEdit = makeButton("แก้ไขนัดแพทย์");
    JButton btnRemove = makeRemoveButton();
    JButton btnBack = makeBackButton(title, "นัดแพทย์");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelBody, 20, 0, 0, 45);

    // Listeners
    btnEdit.addActionListener(e -> {
      editSwitcher(panelRight, panelEditAppointment(appointment));
    });

    btnRemove.addActionListener(e -> {
      JLabel labelConfirm = makeLabel(
          "ต้องการลบนัดแพทย์นี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");
      setPadding(labelConfirm, 0, 16, 0, 0);
      int dialogResult = 0;

      beep("warning");
      try {
        Image img = ImageIO.read(new File(GUIHelper.imgWarningSrc));
        Icon icon = new ImageIcon(img);
        dialogResult = JOptionPane.showConfirmDialog(null, labelConfirm, "คุณกำลังทำการลบนัดแพทย์",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icon);
      } catch (Exception ignored) {
        dialogResult = JOptionPane.showConfirmDialog(null, labelConfirm, "คุณกำลังทำการลบนัดแพทย์",
            JOptionPane.YES_NO_OPTION);
      }

      if (dialogResult == JOptionPane.YES_OPTION) {
        String labelMessage;
        if (user.removeUserAppointment(appointment)) {
          labelMessage = getRemoveSuccessfulMessage("นัดแพทย์");
        } else {
          labelMessage = getRemoveFailedMessage("นัดแพทย์");
        }
        panelRight.remove(panelAllAppointments());
        panelSub03 = null;
        panelSub03 = new JPanel(new BorderLayout());
        panelRight.add(panelAllAppointments(), "นัดแพทย์");
        backTo("นัดแพทย์");
        fireSuccessDialog(labelMessage);
      }
    });

    // Init web browser
    Browser browser = new Browser();
    BrowserView view = new BrowserView(browser);
    // Load URL that query the hospital around the current position
    browser.loadURL("https://www.google.co.th/maps/search/" + appointment.getHospitalName());

    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(makeLabel("แพทย์ผู้นัด: " + doctorName));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("โรงพยาบาล: " + appointment.getHospitalName()));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("กำหนดนัด: " + title));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelButtons.add(btnEdit, BorderLayout.CENTER);
    panelButtons.add(btnRemove, BorderLayout.EAST);
    panelSub.add(panelButtons);
    panelBody.add(panelSub);

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(view, BorderLayout.CENTER);
    panelView.add(panelBody, BorderLayout.SOUTH);

    return panelView;
  }

  private JPanel panelEditAppointment(Appointment app) {

    // JPanels
    JPanel panelEditAppointment = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelBody = new JPanel();

    // JButtons
    JButton btnBack = makeBackButton("แก้ไขนัด", AppointmentUtil.getTitle(app));
    JButton btnSave = makeButton("บันทึก");

    // JTextFields
    // TODO: tfDoctor @ editAppointment
    JTextField tfDoctor = makeTextField(30);
    JTextField tfHospital = makeTextField(30);
    JTextField tfNote = makeTextField(40);

    tfDoctor.setText(DoctorUtil.getDoctorFullName(app.getDoctor()));
    tfHospital.setText(app.getHospitalName());

    // JLabels
    JLabel labelDateTitle = makeLabel("วันที่นัด");
    JLabel labelTimeStart = makeLabel("ตั้งแต่เวลา");
    JLabel labelTimeEnd = makeLabel("จนถึงเวลา");
    JLabel labelDoctor = makeLabel("แพทย์ที่นัด");
    JLabel labelHospital = makeLabel("ชื่อสถานพยาบาล");
    JLabel labelNote = makeLabel("หมายเหตุการนัด");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelEditAppointment, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 260, 28);
    setPadding(panelTitle, 0, 0, 20);
    setPadding(labelDoctor, 10, 0, -10, 0);
    setPadding(labelHospital, 10, 0, -10, 0);
    setPadding(labelNote, 10, 0, -10, 0);

    // Listener
    btnSave.addActionListener(e -> {
      saveSwitcher(panelRight, panelEditAppointment(app), panelViewAppointment(app),
          AppointmentUtil.getTitle(app));
    });

    // Panel Title
    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(labelDateTitle);
    DatePicker datePicker1 = new DatePicker();
    panelSub.add(datePicker1);
    panelSub.add(labelTimeStart);
    TimePicker timePicker1 = new TimePicker();
    panelSub.add(timePicker1);
    panelSub.add(labelTimeEnd);
    TimePicker timePicker2 = new TimePicker();
    panelSub.add(timePicker2);
    setPadding(panelSub, 0, 0, 10, 0);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelNote);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfNote);
    panelBody.add(panelSub);

    panelEditAppointment.add(panelTitle, BorderLayout.NORTH);
    panelEditAppointment.add(panelBody, BorderLayout.CENTER);
    panelEditAppointment.add(btnSave, BorderLayout.SOUTH);

    return panelEditAppointment;
  }

  private JPanel panelAddAppointment() {

    // JPanels
    JPanel panelAddAppointment = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelBody = new JPanel();

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มนัดใหม่", "นัดแพทย์");
    JButton btnAdd = makeButton("บันทึกนัด");

    // JTextFields
    // TODO: tfDoctor @ addAppointment
    JTextField tfDoctor = makeTextField(30);
    JTextField tfHospital = makeTextField(30);
    JTextField tfNote = makeTextField(40);

    // JLabels
    JLabel labelDateTitle = makeLabel("วันที่นัด");
    JLabel labelTimeStart = makeLabel("ตั้งแต่เวลา");
    JLabel labelTimeEnd = makeLabel("จนถึงเวลา");
    JLabel labelDoctor = makeLabel("แพทย์ที่นัด");
    JLabel labelHospital = makeLabel("ชื่อสถานพยาบาล");
    JLabel labelNote = makeLabel("หมายเหตุการนัด");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelAddAppointment, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 260, 28);
    setPadding(panelTitle, 0, 0, 20);
    setPadding(labelDoctor, 10, 0, -10, 0);
    setPadding(labelHospital, 10, 0, -10, 0);
    setPadding(labelNote, 10, 0, -10, 0);

    // Panel Title
    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(labelDateTitle);
    DatePicker datePicker1 = new DatePicker();
    panelSub.add(datePicker1);
    panelSub.add(labelTimeStart);
    TimePicker timePicker1 = new TimePicker();
    panelSub.add(timePicker1);
    panelSub.add(labelTimeEnd);
    TimePicker timePicker2 = new TimePicker();
    panelSub.add(timePicker2);
    setPadding(panelSub, 0, 0, 10, 0);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelNote);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfNote);
    panelBody.add(panelSub);

    panelAddAppointment.add(panelTitle, BorderLayout.NORTH);
    panelAddAppointment.add(panelBody, BorderLayout.CENTER);
    panelAddAppointment.add(btnAdd, BorderLayout.SOUTH);

    return panelAddAppointment;
  }

  public void initWelcome() {
    /*
      Creates a very first GUI. This GUI will be displayed if the program is being
      run for the first time or the user is not logged in.
     */

    // Frame
    frameWelcome = new JFrame("jMedicine: เข้าสู่ระบบ");

    // Panels
    panelLoadingSignIn = getLoadingPanel(false);
    panelErrorSignIn = getErrorPanel("ชื่อผู้ใช้งานหรือรหัสผ่านไม่ถูกต้อง");
    panelWelcome = new JPanel(new CardLayout());
    panelSignIn = new JPanel(new GridBagLayout());
    JPanel panelFirstMed = new JPanel();

    // JLabels
    JLabel space = new JLabel();
    JLabel labelWelcome = makeTitleLabel("ยินดีต้อนรับ");
    JLabel labelWelcomeSub = makeLabel("เข้าสู่ระบบเพื่อ Sync ข้อมูลของคุณทุกที่ ทุกเวลา");
    JLabel labelRegister = makeLabel("ยังไม่มีบัญชี? ลงทะเบียนที่นี่");
    JLabel labelSignIn = makeLabel("มีบัญชีอยู่แล้ว? เข้าสู่ระบบที่นี่");
    JLabel labelUsername = makeBoldLabel("Username");
    JLabel labelPassword = makeBoldLabel("Password");
    JLabel labelPasswordConfirm = makeBoldLabel("กรอก Password อีกครั้ง");

    // JTextFields
    tfUserName = makeTextField(20);
    tfPassword = makePasswordField(20);
    tfPasswordConfirm = makePasswordField(20);

    // JButtons
    JButton btnSignIn = makeButton("เข้าสู่ระบบ");
    JButton btnSignUp = makeButton("ลงทะเบียน");

    // Styling
    setPadding(labelUsername, 0, 0, -16, 0);
    setPadding(labelPassword, 0, 0, -10, 0);
    setPadding(labelPasswordConfirm, 0, 0, -10, 0);
    setPadding(labelRegister, 20, 60);
    setPadding(labelSignIn, 20, 60);
    panelLoadingSignIn.setVisible(false);
    panelErrorSignIn.setVisible(false);
    labelPasswordConfirm.setVisible(false);
    tfPasswordConfirm.setVisible(false);
    btnSignUp.setVisible(false);
    labelSignIn.setVisible(false);
    makeLabelCenter(labelWelcome);
    makeLabelCenter(labelWelcomeSub);

    // Listeners
    btnSignIn.addActionListener(this);
    tfUserName.addKeyListener(this);
    tfPassword.addKeyListener(this);
    labelRegister.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        labelPasswordConfirm.setVisible(true);
        tfPasswordConfirm.setVisible(true);
        btnSignIn.setVisible(false);
        labelRegister.setVisible(false);
        btnSignUp.setVisible(true);
        labelSignIn.setVisible(true);
        labelWelcomeSub.setVisible(false);
        labelWelcome.setText("ลงทะเบียน");
      }
    });
    labelSignIn.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        labelPasswordConfirm.setVisible(false);
        tfPasswordConfirm.setVisible(false);
        btnSignIn.setVisible(true);
        labelRegister.setVisible(true);
        btnSignUp.setVisible(false);
        labelSignIn.setVisible(false);
        labelWelcomeSub.setVisible(true);
        labelWelcome.setText("ยินดีต้อนรับ");
      }
    });

    // Welcome Panel
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.weighty = 1000;
    gbc.gridy = 0;
    panelSignIn.add(space, gbc);
    gbc.weightx = 0.0;
    gbc.weighty = 2;
    gbc.gridwidth = 11;
    gbc.ipady = 8;
    gbc.gridx = 0;
    gbc.gridy = 1;
    JPanel panelBox = new JPanel();
    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.Y_AXIS));
    setPadding(labelWelcome, 0, 0, 20);
    setPadding(labelWelcomeSub, 0, 0, 10);
    setPadding(btnSignIn, 10, 0, 0);
    panelBox.add(labelWelcome);
    panelBox.add(labelWelcomeSub);
    panelSignIn.add(panelBox, gbc);
    gbc.gridy++;
    panelSignIn.add(labelUsername, gbc);
    gbc.gridy++;
    panelSignIn.add(tfUserName, gbc);
    gbc.gridy++;
    panelSignIn.add(labelPassword, gbc);
    gbc.gridy++;
    panelSignIn.add(tfPassword, gbc);
    gbc.gridy++;
    panelSignIn.add(tfPassword, gbc);
    gbc.gridy++;
    panelSignIn.add(labelPasswordConfirm, gbc);
    gbc.gridy++;
    panelSignIn.add(tfPasswordConfirm, gbc);
    gbc.gridy++;
    panelSignIn.add(panelLoadingSignIn, gbc);
    gbc.gridy++;
    panelSignIn.add(panelErrorSignIn, gbc);
    gbc.gridy++;
    panelSignIn.add(btnSignUp, gbc);
    gbc.gridy++;
    panelSignIn.add(btnSignIn, gbc);
    gbc.gridy++;
    panelSignIn.add(labelRegister, gbc);
    gbc.gridy++;
    panelSignIn.add(labelSignIn, gbc);
    space = new JLabel();
    gbc.weighty = 300;
    panelSignIn.add(space, gbc);

    // FirstMed Panel
    JLabel labelTitle = makeTitleLabel("เพิ่มยาตัวแรกของคุณ");
    JButton btnSkip = makeButton("ข้ามขั้นตอนนี้");
    setPadding(labelTitle, 0, 0, 30);

    btnSkip.addActionListener(this);
    btnSkip.setAlignmentX(Component.CENTER_ALIGNMENT);

    panelFirstMed.setLayout(new BoxLayout(panelFirstMed, BoxLayout.PAGE_AXIS));
    setPadding(panelFirstMed, 80, 0, 40, 0);

    JPanel panelInline = new JPanel(new FlowLayout());
    panelInline.add(labelTitle);
    panelFirstMed.add(panelInline);
    panelFirstMed.add(addMedGUI());
    panelFirstMed.add(btnSkip);

    panelWelcome.add(panelSignIn, "ยังไม่ได้เข้าสู่ระบบ");
    panelWelcome.add(panelFirstMed, "เพิ่มยาตัวแรก");

    frameWelcome.add(panelWelcome);
    frameWelcome.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frameWelcome.setMinimumSize(this.minSize);
    frameWelcome.setSize(this.windowSize);
    frameWelcome.setVisible(true);
  }

  private void makeLeftNavigation() {
    /* Creates GUI of the left navigation. */
    buttons = new JButton[]{
        makeLeftNavigationButton("ภาพรวม"),
        makeLeftNavigationButton("ยาทั้งหมด"),
        makeLeftNavigationButton("นัดแพทย์"),
        makeLeftNavigationButton("แพทย์"),
        makeLeftNavigationButton("โรงพยาบาลใกล้เคียง"),
        makeLeftNavigationButton("การตั้งค่า"),
    };

    int buttonY = 0;
    JLabel space = new JLabel();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.ipady = 14;
    gbc.ipadx = 20;
    gbc.weightx = 0.0;
    gbc.weighty = 1;
    gbc.gridwidth = 8;
    gbc.gridx = 0;

    int tempCount = 0;
    for (JButton button : buttons) {
      button.setHorizontalAlignment(SwingConstants.LEFT);
      paintButton();
      if (tempCount == 0) {
        paintCurrentTabButton(button);
      }
      // Switch between sub panels
      button.addActionListener(e -> {
        CardLayout cl = (CardLayout) (panelRight.getLayout());
        cl.show(panelRight, e.getActionCommand());
        paintButton();
        if (e.getActionCommand().equals(button.getText())) {
          paintCurrentTabButton(button);
        }
      });
      gbc.gridy = buttonY;
      buttonY++;
      panelLeft.add(button, gbc);
      tempCount++;
    }

    gbc.weighty = 1000;
    panelLeft.add(space, gbc);
    panelLeft.setBackground(mainBlue);
  }

  private JPanel makeOverviewCard(String time, String medName, String dose) {
    /* Creates a card that will be used on the Overview panel only. */

    // JPanels
    JPanel panelLoopInfo = new JPanel(new BorderLayout());
    JPanel panelCard = new JPanel();
    JPanel panelTime = newFlowLayout();
    JPanel panelMed = newFlowLayout();
    JPanel panelMedInfo = new JPanel();

    // JLabels
    JLabel labelPic = medUtil.getMedIcon(user.getUserMedicines().get(0));
    JLabel labelTime = makeBoldLabel(time);
    JLabel labelMedName = makeLabel(medName);
    JLabel labelAmount = makeLabel(dose);

    // Styling
    panelMedInfo.setLayout(new BoxLayout(panelMedInfo, BoxLayout.PAGE_AXIS));
    panelCard.setLayout(new BoxLayout(panelCard, BoxLayout.PAGE_AXIS));
    panelCard.setBorder(
        new DropShadowBorder(UIManager.getColor("Control"), 1, 5, .3f, 16, true, true, true, true));
    setPadding(labelPic, 6, 0, 0, 8);
    setPadding(labelMedName, 6, 0, -10, 0);
    setPadding(labelAmount, 0, 0, 2, 0);
    setPadding(panelLoopInfo, 0, 0, 20);

    panelMedInfo.add(labelMedName);
    panelMedInfo.add(labelAmount);

    panelMed.add(labelPic);
    panelMed.add(panelMedInfo);

    panelCard.add(panelMed);
    panelTime.add(labelTime);

    panelLoopInfo.add(panelTime, BorderLayout.NORTH);
    panelLoopInfo.add(panelCard);
    return panelLoopInfo;
  }

  private void makeLabelClickable(JLabel label, String href) {
    /* Works like <a> */
    label.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (href.equals("ลงทะเบียน") || href.equals("ยังไม่ได้เข้าสู่ระบบ")) {
          if (frameWelcome == null) {
            initWelcome();
          }
          frameWelcome.setVisible(true);
          CardLayout cl = (CardLayout) (panelWelcome.getLayout());
          cl.show(panelWelcome, href);
          try {
            frameMain.setVisible(false);
          } catch (NullPointerException ignored) {
          }
        } else {
          CardLayout cl = (CardLayout) (panelRight.getLayout());
          cl.show(panelRight, href);
        }
      }
    });
  }

  private JButton makeBackButton(String buttonText, String backTo) {
    /* Creates a button that will be used on the nested page. */
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.BOLD, 42));
    button.setHorizontalAlignment(SwingConstants.LEFT);
    try {
      Image img = ImageIO.read(new File(imgPath + "/system/back.png"));
      button.setIcon(new ImageIcon(img));
    } catch (Exception ignored) {
    }
    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.addActionListener(e -> {
      backTo(backTo);
    });
    return button;
  }

  private JPanel makeNewButton(String btnName) {
    /* Creates a new button. */
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, -16);

    JButton btnNew = new JButton(btnName);
    try {
      Image img = ImageIO.read(new File(imgPath + "/system/add.png"));
      btnNew.setIcon(new ImageIcon(img));
    } catch (Exception ignored) {
    }
    btnNew.setAlignmentX(Component.LEFT_ALIGNMENT);
    btnNew.setHorizontalAlignment(SwingConstants.LEFT);
    btnNew.setFont(new Font("TH Sarabun New", Font.BOLD, 28));
    btnNew.addActionListener(e -> {
      CardLayout cl = (CardLayout) (panelRight.getLayout());
      cl.show(panelRight, e.getActionCommand());
    });
    btnNew.setOpaque(false);
    btnNew.setContentAreaFilled(false);
    btnNew.setBorderPainted(false);
    panelLoopInfo.add(btnNew);
    panelLoopInfo.add(Box.createHorizontalGlue());

    return panelLoopInfo;
  }

  private void backTo(String backTo) {
    /* Navigates user back to some page */
    if (backTo.equals("ยังไม่ได้เข้าสู่ระบบ")) {
      CardLayout cl = (CardLayout) (panelWelcome.getLayout());
      cl.show(panelWelcome, "ยังไม่ได้เข้าสู่ระบบ");
    } else {
      CardLayout cl = (CardLayout) (panelRight.getLayout());
      cl.show(panelRight, backTo);
    }
  }

  private JPanel makeMedCard(Medicine medicine) {
    /* Creates a card that will be used on the All medicines panel only. */
    Date medEXP = medicine.getMedEXP();
    String medTitle = medicine.getMedName() + " (" + medicine.getMedDescription() + ")";
    String medShortInfo =
        "เหลืออยู่ " + medicine.getMedRemaining() + " " + medicine.getMedUnit() + " หมดอายุ "
            + GUIHelper.formatDMY
            .format(medEXP);
    JLabel labelTitle = makeBoldLabel(medTitle);
    JLabel labelShortInfo = makeLabel(medShortInfo);
    JLabel labelPic = medUtil.getMedIcon(medicine);
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

  private JPanel makeDoctorCard(Doctor doctor) {
    /* Creates a card that will be used on the All doctors panel only. */
    String doctorName = DoctorUtil.getDoctorFullName(doctor);
    JLabel labelTitle = makeBoldLabel(doctorName);
    String doctorShortInfo = "แผนก " + doctor.getWard() + " โรงพยาบาล" + doctor.getHospital();
    JLabel labelShortInfo = makeLabel(doctorShortInfo);
    JLabel labelPic = new JLabel();
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, 0);

    JPanel panelPic = new JPanel();
    panelPic.setLayout(new BoxLayout(panelPic, BoxLayout.X_AXIS));

    JPanel panelInfo = new JPanel();
    panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.PAGE_AXIS));

    try {
      Image img = ImageIO.read(new File(imgPath + "/system/doctor.png"));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ignored) {
    }

    panelPic.add(labelPic);
    panelInfo.add(labelTitle);
    panelInfo.add(labelShortInfo);

    setPadding(labelTitle, 5, 0, -10, 0);
    setPadding(labelPic, 5, 10, 0, 0);

    panelLoopInfo.add(panelPic);
    panelLoopInfo.add(panelInfo);
    panelLoopInfo.add(Box.createHorizontalGlue());

    panelLoopInfo.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        panelRight.add(panelViewDoctor(doctor), doctorName);
        CardLayout cl = (CardLayout) (panelRight.getLayout());
        cl.show(panelRight, doctorName);
      }
    });

    return panelLoopInfo;
  }

  private JPanel makeAppointmentCard(Appointment appointment) {
    /* Creates a card that will be used on the All appointments panel only. */

    // JPanels
    JPanel panelLoopInfo = new JPanel();
    JPanel panelPic = new JPanel();
    JPanel panelInfo = new JPanel();

    Doctor appDr = appointment.getDoctor();

    // Strings
    String title = AppointmentUtil.getTitle(appointment);
    String shortInfo =
        appDr.getPrefix() + " " + appDr.getName() + " " + appointment.getHospitalName();

    // JLabels
    JLabel labelTitle = makeBoldLabel(title);
    JLabel labelShortInfo = makeLabel(shortInfo);
    JLabel labelPic = new JLabel();

    // Styling
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    panelPic.setLayout(new BoxLayout(panelPic, BoxLayout.X_AXIS));
    panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.PAGE_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, 0);

    try {
      Image img = ImageIO.read(new File(imgPath + "/system/calendar.png"));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ignored) {
    }

    panelPic.add(labelPic);
    panelInfo.add(labelTitle);
    panelInfo.add(labelShortInfo);

    setPadding(labelTitle, 5, 0, -10, 0);
    setPadding(labelPic, 5, 10, 0, 0);

    panelLoopInfo.add(panelPic);
    panelLoopInfo.add(panelInfo);
    panelLoopInfo.add(Box.createHorizontalGlue());

    panelLoopInfo.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        panelRight.add(panelViewAppointment(appointment), title);
        CardLayout cl = (CardLayout) (panelRight.getLayout());
        cl.show(panelRight, title);
      }
    });

    return panelLoopInfo;
  }

  private JScrollPane addMedGUI() {
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
    JButton btnSave = makeButton("บันทึกยา");

    // Arrays
    String[] medType = medUtil.getMedType();
    String[] tabletColor = medUtil.getTabletColor();
    String[] liquidColor = medUtil.getLiquidColor();
    String[] medTime = medUtil.getMedTime();
    String[] medDoseStr = medUtil.getMedDoseStr();
    ArrayList<ImageIcon> tabletColorIcons = new ArrayList<>();
    ArrayList<ImageIcon> liquidColorIcons = new ArrayList<>();

    for (String color : tabletColor) {
      tabletColorIcons.add(new ImageIcon(GUIHelper.imgPath + "/colors/" + color + ".png"));
    }
    for (String color : liquidColor) {
      liquidColorIcons.add(new ImageIcon(GUIHelper.imgPath + "/colors/" + color + ".png"));
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
      String selectedMedType = medUtil.getMedType()[cbMedType.getSelectedIndex()];
      String selectedColor = "";
      if (selectedMedType.equals("ยาแคปซูล")) {
        selectedColor = medUtil.getTabletColor()[cbCapsuleColor01.getSelectedIndex()];
        selectedColor += "-";
        selectedColor += medUtil.getTabletColor()[cbCapsuleColor02.getSelectedIndex()];
      } else if (selectedMedType.equals("ยาเม็ด")) {
        selectedColor = medUtil.getTabletColor()[cbTabletColor.getSelectedIndex()];
      } else if (selectedMedType.equals("ยาน้ำ")) {
        selectedColor = medUtil.getLiquidColor()[cbLiquidColor.getSelectedIndex()];
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

      Medicine med = new Medicine(tfMedName.getText(), selectedMedType, selectedColor,
          tfMedDescription.getText(), selectedMedTime, selectedDoseStr, dose,
          Integer.valueOf(tfTotalMeds.getText()), exp);
      try {
        MedicineDB.addMedicine(med, user.getUserId());
        fireSuccessDialog("ยา " + med.getMedName() + " ได้ถูกเพิ่มเรียบร้อยแล้ว");
        saveSwitcher(panelRight, panelAddMedicine(), panelAllMedicines(), "ยาทั้้งหมด");
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

  private void executeSignIn() {
    if (tfUserName.getText().equals("") || tfPassword.getPassword().equals("")) {
      panelErrorSignIn.setVisible(true);
    } else {
      panelErrorSignIn.setVisible(false);
      panelLoadingSignIn.setVisible(true);
      SwingWorker<Integer, String> swingWorker = new SwingWorker<Integer, String>() {
        @Override
        protected Integer doInBackground() throws Exception {
          String username = tfUserName.getText();
          char[] password = tfPassword.getPassword();
          try {
            user = doSignIn(username, password);
          } catch (LoginException ignored) {
            panelLoadingSignIn.setVisible(false);
            panelErrorSignIn.setVisible(true);
          } catch (NoSuchAlgorithmException | SQLException ex) {
            ex.printStackTrace();
          }
          return null;
        }

        @Override
        protected void done() {
          if (user != null) {
            initSampleMedicine01();
            initSampleMedicine02();
            initSampleMedicine03();
            initSampleAppointment();
            main();
            if (user.getUserMedicines().size() > 0) {
              frameWelcome.setVisible(false);
              frameMain.setVisible(true);
              frameWelcome = null;
              CardLayout cl = (CardLayout) (panelRight.getLayout());
              cl.show(panelRight, "ภาพรวม");
            } else {
              CardLayout cl = (CardLayout) (panelWelcome.getLayout());
              cl.show(panelWelcome, "เพิ่มยาตัวแรก");
            }
          }
        }
      };
      swingWorker.execute();
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String btnCommand = e.getActionCommand();

    switch (btnCommand) {
      case "ข้ามขั้นตอนนี้":
        if (frameWelcome == null) {
          CardLayout cl = (CardLayout) (panelRight.getLayout());
          cl.show(panelRight, "ยาทั้งหมด");
        } else {
          frameWelcome.setVisible(false);
          frameMain.setVisible(true);
          frameWelcome = null;
        }
        break;

      case "เข้าสู่ระบบ":
        executeSignIn();
        break;
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getSource() == tfUserName || e.getSource() == tfPassword) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        executeSignIn();
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }

  private void initSampleMedicine01() {
    ArrayList<String> sampleMedTime = new ArrayList<>();
    sampleMedTime.add("เช้า");
    sampleMedTime.add("กลางวัน");
    sampleMedTime.add("เย็น");
    ArrayList<String> sampleMedDoseStr = new ArrayList<>();
    sampleMedDoseStr.add("หลังอาหาร");
    sampleMedDoseStr.add("หลังอาหาร");
    sampleMedDoseStr.add("หลังอาหาร");
    Date dateEXP = new Date();
    try {
      dateEXP = GUIHelper.formatDMY.parse("28/02/2019");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Medicine prednisolone = new Medicine("Prednisolone", "tablet", "white", "ยาแก้อักเสบ SAMPLE",
        sampleMedTime, sampleMedDoseStr, 1, 20, dateEXP);
    user.addUserMedicine(prednisolone);
  }

  private void initSampleMedicine02() {
    ArrayList<String> sampleMedTime = new ArrayList<>();
    sampleMedTime.add("ก่อนนอน");
    ArrayList<String> sampleMedDoseStr = new ArrayList<>();
    sampleMedDoseStr.add("");
    Date dateEXP = new Date();
    try {
      dateEXP = GUIHelper.formatDMY.parse("28/02/2019");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Medicine chlopheniramine = new Medicine("Chlopheniramine", "tablet", "yellow",
        "ยาแก้แพ้ SAMPLE",
        sampleMedTime, sampleMedDoseStr, 1, 50, dateEXP);
    user.addUserMedicine(chlopheniramine);
  }

  private void initSampleMedicine03() {
    ArrayList<String> sampleMedTime = new ArrayList<>();
    sampleMedTime.add("เช้า");
    ArrayList<String> sampleMedDoseStr = new ArrayList<>();
    sampleMedDoseStr.add("หลังอาหาร");
    Date dateEXP = new Date();
    try {
      dateEXP = GUIHelper.formatDMY.parse("28/02/2019");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Medicine amoxicillin = new Medicine("Amoxicillin", "capsule", "green-orange",
        "ยาแก้อักเสบ SAMPLE",
        sampleMedTime,
        sampleMedDoseStr, 1, 7, dateEXP);
    user.addUserMedicine(amoxicillin);
  }

  private void initSampleAppointment() {
    Date dateStart = new Date();
    Date dateEnd = new Date();
    try {
      dateStart = GUIHelper.formatDMYHM.parse("1/12/2018 09.30");
      dateEnd = GUIHelper.formatDMYHM.parse("1/02/2018 16.30");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Doctor doctor = user.getUserDoctors().get(0);
    Appointment appointment = new Appointment(dateStart, dateEnd, doctor, "บำรุงราษฎร์");
    user.addUserAppointment(appointment);
  }

  static JButton[] getButtons() {
    return buttons;
  }

  static Color getMainBlue() {
    return mainBlue;
  }
}
