package GUI;

import static GUI.GUIHelper.*;
import static api.Login.*;

import api.LoginException;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PermissionHandler;
import com.teamdev.jxbrowser.chromium.PermissionRequest;
import com.teamdev.jxbrowser.chromium.PermissionStatus;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import core.Appointment;
import core.Doctor;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import core.LocationHelper;
import mdlaf.shadows.DropShadowBorder;


/**
 * GUI class creates all graphic user interface, all in javax.swing.
 *
 * @param windowSize a Dimension class consists of width and height.
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

    panelSub01.setLayout(new BoxLayout(panelSub01, BoxLayout.PAGE_AXIS));
    // Init title panel displaying title label
    panelTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

    panelLoop.add(Box.createVerticalGlue());
    // Add all panels into the main panel
    panelSub01.add(panelTitle);
    panelSub01.add(panelLoop);
    panelSub01.add(Box.createVerticalGlue());

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
    browser.setPermissionHandler(new PermissionHandler() {
      @Override
      public PermissionStatus onRequestPermission(PermissionRequest request) {
        return PermissionStatus.GRANTED;
      }
    });
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

    JPanel panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeBoldLabel("ผู้ใช้งานปัจจุบัน"));
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(labelUserName);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeBoldLabel("ตั้งค่าผู้ใช้งาน"));
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(labelEdit);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(labelSignOut);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeBoldLabel("ตั้งค่าโปรแกรม"));
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(toggleNoti);
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

  private JPanel panelViewMedicine(Medicine medicine) {
    /* Creates GUI displaying all information of a single medicine */

    // JPanels
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelSub = new JPanel();
    panelTitle = new JPanel(new BorderLayout());

    // JLabels
    JLabel labelPic = medUtil.getMedIcon(medicine);
    String medName = medicine.getMedName();

    // JButtons
    JButton btnRemove = makeButton("ลบยาตัวนี้");
    JButton labelTitle = makeBackButton(medName, "ยาทั้งหมด");

    // Styling
    panelSub.setLayout(new BoxLayout(panelSub, BoxLayout.PAGE_AXIS));
    setPadding(labelPic, 0, 0, 10);
    setPadding(panelTitle, -6, 0, 20, 8);
    setPadding(panelSub, 0, 0, 0, 65);
    setPadding(panelView, 0, 0, 0, -20);

    // Listeners
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
        JLabel labelMessage;
        if (user.removeUserMedicine(medicine)) {
          labelMessage = getRemoveSuccessfulMessage("ยา");
        } else {
          labelMessage = getRemoveFailedMessage("ยา");
        }
        setPadding(labelMessage, 0, 10, 0, 0);
        panelRight.remove(panelAllMedicines());
        panelSub02 = null;
        panelSub02 = new JPanel(new BorderLayout());
        panelRight.add(panelAllMedicines(), "ยาทั้งหมด");
        backTo("ยาทั้งหมด");
        try {
          beep("success");
          Image img = ImageIO.read(new File(GUIHelper.imgSuccessSrc));
          Icon icon = new ImageIcon(img);
          JOptionPane
              .showMessageDialog(null, labelMessage, "ผลการลบยา", JOptionPane.INFORMATION_MESSAGE,
                  icon);
        } catch (Exception ignored) {
          JOptionPane
              .showMessageDialog(null, labelMessage, "ผลการลบยา", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });

    panelTitle.add(labelTitle);
    panelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

    // MedTime is an ArrayList, convert it into a printable format.
    StringBuilder sbMedTime = new StringBuilder();
    for (String medTime : medicine.getMedTime()) {
      sbMedTime.append(medTime).append(" ");
    }

    // DoseStr is an ArrayList, convert it into a printable format.
    StringBuilder sbDoseStr = new StringBuilder();
    for (String dose : medicine.getMedDoseStr()) {
      sbDoseStr.append(dose).append(" ");
    }

    panelSub.add(labelPic);
    panelSub.add(makeLabel("ชื่อยา: " + medName));
    panelSub.add(makeLabel("คำอธิบาย: " + medicine.getMedDescription()));
    panelSub.add(makeLabel("เวลาที่ต้องทาน: " + sbMedTime + " " + sbDoseStr));
    panelSub
        .add(makeLabel("ขนาดรับประทาน: " + medicine.getMedDose() + " " + medicine.getMedUnit()));
    panelSub
        .add(makeLabel("วันที่เพิ่มยา: " + GUIHelper.formatDMY.format(medicine.getDateAdded())));
    panelSub.add(makeLabel("จำนวนยาเริ่มต้น: " + medicine.getMedRemaining()));
    panelSub.add(makeLabel("จำนวนยาที่เหลือ: " + medicine.getMedRemaining()));
    panelSub.add(makeLabel("วันหมดอายุ: " + GUIHelper.formatDMY.format(medicine.getMedEXP())));

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(panelSub, BorderLayout.CENTER);
    panelView.add(btnRemove, BorderLayout.SOUTH);

    return panelView;
  }

  private JPanel panelAddDoctor() {
    /* Creates GUI of the form for adding a new doctor. */

    // JPanels
    JPanel panelAddDoctor = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    panelTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));

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
    cbSunday.addActionListener(e -> {
      if (cbSunday.isSelected()) {
        sundayStartLabel.setVisible(true);
        sundayEndLabel.setVisible(true);
        sundayStartPicker.setVisible(true);
        sundayEndPicker.setVisible(true);
      } else {
        sundayStartLabel.setVisible(false);
        sundayEndLabel.setVisible(false);
        sundayStartPicker.setVisible(false);
        sundayEndPicker.setVisible(false);
      }
    });

    cbMonday.addActionListener(e -> {
      if (cbMonday.isSelected()) {
        mondayStartLabel.setVisible(true);
        mondayEndLabel.setVisible(true);
        mondayStartPicker.setVisible(true);
        mondayEndPicker.setVisible(true);
      } else {
        mondayStartLabel.setVisible(false);
        mondayEndLabel.setVisible(false);
        mondayStartPicker.setVisible(false);
        mondayEndPicker.setVisible(false);
      }
    });

    cbTuesday.addActionListener(e -> {
      if (cbTuesday.isSelected()) {
        tuesStartLabel.setVisible(true);
        tuesEndLabel.setVisible(true);
        tuesStartPicker.setVisible(true);
        tuesEndPicker.setVisible(true);
      } else {
        tuesStartLabel.setVisible(false);
        tuesEndLabel.setVisible(false);
        tuesStartPicker.setVisible(false);
        tuesEndPicker.setVisible(false);
      }
    });

    cbWednesday.addActionListener(e -> {
      if (cbWednesday.isSelected()) {
        wedStartLabel.setVisible(true);
        wedEndLabel.setVisible(true);
        wedStartPicker.setVisible(true);
        wedEndPicker.setVisible(true);
      } else {
        wedStartLabel.setVisible(false);
        wedEndLabel.setVisible(false);
        wedStartPicker.setVisible(false);
        wedEndPicker.setVisible(false);
      }
    });

    cbThursday.addActionListener(e -> {
      if (cbThursday.isSelected()) {
        thurStartLabel.setVisible(true);
        thurEndLabel.setVisible(true);
        thurStartPicker.setVisible(true);
        thurEndPicker.setVisible(true);
      } else {
        thurStartLabel.setVisible(false);
        thurEndLabel.setVisible(false);
        thurStartPicker.setVisible(false);
        thurEndPicker.setVisible(false);
      }
    });

    cbFriday.addActionListener(e -> {
      if (cbFriday.isSelected()) {
        fridayStartLabel.setVisible(true);
        fridayEndLabel.setVisible(true);
        fridayStartPicker.setVisible(true);
        fridayEndPicker.setVisible(true);
      } else {
        fridayStartLabel.setVisible(false);
        fridayEndLabel.setVisible(false);
        fridayStartPicker.setVisible(false);
        fridayEndPicker.setVisible(false);
      }
    });

    cbSaturday.addActionListener(e -> {
      if (cbSaturday.isSelected()) {
        satStartLabel.setVisible(true);
        satEndLabel.setVisible(true);
        satStartPicker.setVisible(true);
        satEndPicker.setVisible(true);
      } else {
        satStartLabel.setVisible(false);
        satEndLabel.setVisible(false);
        satStartPicker.setVisible(false);
        satEndPicker.setVisible(false);
      }
    });

    JTextField tfDoctorName = makeTextField(20);
    JTextField tfDoctorSurName = makeTextField(20);
    JTextField tfDoctorWard = makeTextField(16);
    JTextField tfDoctorHospital = makeTextField(18);
    String[] prefixes = {"นพ.", "พญ.", "ศ.นพ", "ผศ.นพ"};
    JComboBox cbPrefix = makeComboBox(prefixes);

    panelTitle.add(btnBack);

    JPanel panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeLabel("คำนำหน้า"));
    panelSub.add(cbPrefix);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeLabel("ชื่อ"));
    panelSub.add(tfDoctorName);
    panelSub.add(makeLabel("นามสกุล"));
    panelSub.add(tfDoctorSurName);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeLabel("แผนก"));
    panelSub.add(tfDoctorWard);
    panelSub.add(makeLabel("ชื่อสถานพยาบาล"));
    panelSub.add(tfDoctorHospital);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeBoldLabel("วันและเวลาที่เข้าตรวจ"));
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbSunday);
    panelSub.add(sundayStartLabel);
    panelSub.add(sundayStartPicker);
    panelSub.add(sundayEndLabel);
    panelSub.add(sundayEndPicker);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbMonday);
    panelSub.add(mondayStartLabel);
    panelSub.add(mondayStartPicker);
    panelSub.add(mondayEndLabel);
    panelSub.add(mondayEndPicker);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbTuesday);
    panelSub.add(tuesStartLabel);
    panelSub.add(tuesStartPicker);
    panelSub.add(tuesEndLabel);
    panelSub.add(tuesEndPicker);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbWednesday);
    panelSub.add(wedStartLabel);
    panelSub.add(wedStartPicker);
    panelSub.add(wedEndLabel);
    panelSub.add(wedEndPicker);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbThursday);
    panelSub.add(thurStartLabel);
    panelSub.add(thurStartPicker);
    panelSub.add(thurEndLabel);
    panelSub.add(thurEndPicker);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbFriday);
    panelSub.add(fridayStartLabel);
    panelSub.add(fridayStartPicker);
    panelSub.add(fridayEndLabel);
    panelSub.add(fridayEndPicker);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbSaturday);
    panelSub.add(satStartLabel);
    panelSub.add(satStartPicker);
    panelSub.add(satEndLabel);
    panelSub.add(satEndPicker);
    panelBody.add(panelSub);

    //TODO:JScrollPane bug
    JScrollPane scrollPane = new JScrollPane(panelBody);
    setPadding(scrollPane, 0, 0, 20, 0);

    panelAddDoctor.add(panelTitle, BorderLayout.NORTH);
    panelAddDoctor.add(scrollPane, BorderLayout.CENTER);
    panelAddDoctor.add(btnAdd, BorderLayout.SOUTH);

    return panelAddDoctor;
  }

  private JPanel panelViewDoctor(Doctor doctor) {
    /* Creates GUI displaying all information of a single doctor. */

    // JPanels
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelSub = new JPanel();
    panelTitle = new JPanel(new BorderLayout());

    String doctorName = doctor.getPrefix() + " " + doctor.getName();

    // JButtons
    JButton btnRemove = makeButton("ลบแพทย์");
    JButton btnBack = makeBackButton(doctorName, "แพทย์");

    // Styling
    panelSub.setLayout(new BoxLayout(panelSub, BoxLayout.PAGE_AXIS));
    setPadding(panelTitle, -6, 0, 20, 8);
    setPadding(panelSub, 0, 0, 0, 65);
    setPadding(panelView, 0, 0, 0, -20);

    // Listeners
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
        JLabel labelMessage;
        if (user.removeUserDoctor(doctor)) {
          labelMessage = getRemoveSuccessfulMessage("แพทย์");
        } else {
          labelMessage = getRemoveFailedMessage("แพทย์");
        }
        setPadding(labelMessage, 0, 10, 0, 0);
        panelRight.remove(panelAllDoctors());
        panelSub04 = null;
        panelSub04 = new JPanel(new BorderLayout());
        panelRight.add(panelAllDoctors(), "แพทย์");
        backTo("แพทย์");
        try {
          Image img = ImageIO.read(new File(GUIHelper.imgSuccessSrc));
          Icon icon = new ImageIcon(img);
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบแพทย์",
              JOptionPane.INFORMATION_MESSAGE, icon);
        } catch (Exception ignored) {
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบแพทย์",
              JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });

    panelTitle.add(btnBack);
    panelSub.add(makeLabel("ชื่อแพทย์: " + doctorName));
    if (doctor.getWard() != null) {
      panelSub.add(makeLabel("แผนก: " + doctor.getWard()));
    }
    panelSub.add(makeLabel("โรงพยาบาล: " + doctor.getHospital()));
    if (doctor.getWorkTime() != null) {
      panelSub.add(makeLabel("เวลาเข้าตรวจ:"));
      // WorkTime is an ArrayList, convert it to a printable format
      for (ArrayList<String> workTime : doctor.getWorkTime()) {
        JLabel labelWorkTime = makeLabel(
            workTime.get(0) + " เวลา " + workTime.get(1) + " น. - " + workTime.get(2) + " น.");
        setPadding(labelWorkTime, 0, 20);
        panelSub.add(labelWorkTime);
      }
    }

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(panelSub, BorderLayout.CENTER);
    panelView.add(btnRemove, BorderLayout.SOUTH);

    return panelView;
  }

  private JPanel panelViewAppointment(Appointment appointment) {
    /* Creates GUI displaying information of a single appointment. */

    // JPanels
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelSub = new JPanel();
    panelTitle = new JPanel(new BorderLayout());

    Doctor appointmentDr = appointment.getDoctor();
    String doctorName = appointmentDr.getPrefix() + " " + appointmentDr.getName();

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
    String date = dateFormat.format(appointment.getTimeStart());
    dateFormat = new SimpleDateFormat("HH.mm");
    String timeStart = dateFormat.format(appointment.getTimeStart());
    String timeEnd = dateFormat.format(appointment.getTimeStop());

    String title = date + " เวลา " + timeStart + " น. - " + timeEnd + " น.";

    // JButtons
    JButton btnRemove = makeButton("ลบนัดแพทย์");
    JButton btnBack = makeBackButton(title, "นัดแพทย์");

    // Styling
    panelSub.setLayout(new BoxLayout(panelSub, BoxLayout.PAGE_AXIS));
    setPadding(panelTitle, 0, 0, 20);
    setPadding(panelSub, 20, 0, 0, 45);

    // Listeners
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
        JLabel labelMessage;
        if (user.removeUserAppointment(appointment)) {
          labelMessage = getRemoveSuccessfulMessage("นัดแพทย์");
        } else {
          labelMessage = getRemoveFailedMessage("นัดแพทย์");
        }
        setPadding(labelMessage, 0, 10, 0, 0);
        panelRight.remove(panelAllAppointments());
        panelSub03 = null;
        panelSub03 = new JPanel(new BorderLayout());
        panelRight.add(panelAllAppointments(), "นัดแพทย์");
        backTo("นัดแพทย์");
        try {
          Image img = ImageIO.read(new File(GUIHelper.imgSuccessSrc));
          Icon icon = new ImageIcon(img);
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบนัดแพทย์",
              JOptionPane.INFORMATION_MESSAGE, icon);
        } catch (Exception ignored) {
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบนัดแพทย์",
              JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });

    // Init web browser
    Browser browser = new Browser();
    BrowserView view = new BrowserView(browser);
    // Load URL that query the hospital around the current position
    browser.loadURL("https://www.google.co.th/maps/search/" + appointment.getHospitalName());

    panelTitle.add(btnBack);
    panelSub.add(makeLabel("แพทย์ผู้นัด: " + doctorName));
    panelSub.add(makeLabel("โรงพยาบาล: " + appointment.getHospitalName()));
    panelSub.add(makeLabel("กำหนดนัด: " + title));
    panelSub.add(btnRemove);

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(view, BorderLayout.CENTER);
    panelView.add(panelSub, BorderLayout.SOUTH);

    return panelView;
  }

  private JPanel panelAddAppointment() {

    // JPanels
    JPanel panelAddAppointment = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

    JPanel panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(labelDoctor);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(tfDoctor);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(labelHospital);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(tfHospital);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(labelNote);
    panelBody.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
    JPanel panelLoopInfo = new JPanel();
    JPanel panelLine = new JPanel();
    JPanel panelTime = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelMed = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelMedInfo = new JPanel();

    // JLabels
    JLabel labelPic = medUtil.getMedIcon(user.getUserMedicines().get(0));
    JLabel labelTime = makeBoldLabel(time);
    JLabel labelMedName = makeLabel(medName);
    JLabel labelAmount = makeLabel(dose);

    // Styling
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.PAGE_AXIS));
    panelMedInfo.setLayout(new BoxLayout(panelMedInfo, BoxLayout.PAGE_AXIS));
    panelLoopInfo.setBorder(new CompoundBorder(
        BorderFactory.createEmptyBorder(5, 0, 20, 0),
        new DropShadowBorder(UIManager.getColor("Control"), 1, 5, .3f, 16, true, true, true, true)
    ));
    panelLine.setBackground(getMainBlue());
    setPadding(panelLine, 0, 0, -40, 0);
    setPadding(panelTime, 0, 0, -100, 0);
    setPadding(labelTime, 10, 0, 5, 0);
    setPadding(labelMedName, 10, 0, -10, 0);

    panelMedInfo.add(labelMedName);
    panelMedInfo.add(labelAmount);

    panelMed.add(labelPic);
    panelMed.add(panelMedInfo);

    panelTime.add(labelTime);

    panelLoopInfo.add(panelLine);
    panelLoopInfo.add(panelTime);
    panelLoopInfo.add(panelMed);
    panelTime.setSize(panelLoop.getWidth(), 10);
    panelTime.setPreferredSize(new Dimension(panelLoop.getWidth(), 10));
    panelLoopInfo.setSize(panelLoop.getWidth(), 20);
    panelLoopInfo.setPreferredSize(new Dimension(panelLoop.getWidth(), 20));
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
    String doctorName = doctor.getPrefix() + " " + doctor.getName();
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
        panelRight.add(panelViewDoctor(doctor), doctor.getName());
        CardLayout cl = (CardLayout) (panelRight.getLayout());
        cl.show(panelRight, doctor.getName());
      }
    });

    return panelLoopInfo;
  }

  private JPanel makeAppointmentCard(Appointment appointment) {
    /* Creates a card that will be used on the All appointments panel only. */

    String date = GUIHelper.formatDMY.format(appointment.getTimeStart());
    String timeStart = GUIHelper.formatHM.format(appointment.getTimeStart());
    String timeEnd = GUIHelper.formatHM.format(appointment.getTimeStop());

    Doctor appDr = appointment.getDoctor();

    String title = date + " เวลา " + timeStart + " น. - " + timeEnd + " น.";
    String shortInfo =
        appDr.getPrefix() + " " + appDr.getName() + " " + appointment.getHospitalName();

    JLabel labelTitle = makeBoldLabel(title);
    JLabel labelShortInfo = makeLabel(shortInfo);
    JLabel labelPic = new JLabel();
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, 0);

    JPanel panelPic = new JPanel();
    panelPic.setLayout(new BoxLayout(panelPic, BoxLayout.X_AXIS));

    JPanel panelInfo = new JPanel();
    panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.PAGE_AXIS));

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
    JPanel panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelSubMorning = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelSubAfternoon = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelSubEvening = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelSubBed = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelColor = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelTabletColor = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelCapsuleColor = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel panelLiquidColor = new JPanel(new FlowLayout(FlowLayout.LEFT));

    // JTextFields
    JTextField tfMedName = makeTextField(20);
    JTextField tfMedDescription = makeTextField(20);
    JTextField tfAmountMorning = makeTextField(2);
    JTextField tfAmountAfternoon = makeTextField(2);
    JTextField tfAmountEvening = makeTextField(2);
    JTextField tfAmountBed = makeTextField(2);
    JTextField tfTotalMeds = makeTextField(2);
    JTextField tfMedEXP = makeTextField(10);

    JLabel labelUnit = makeLabel(medUnit);

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
    btnSave.addActionListener(this);
    cbMedType.addActionListener(e -> {
      switch (cbMedType.getSelectedIndex()) {
        case 0:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(true);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelUnit.setText("เม็ด");
          break;
        case 1:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(true);
          panelLiquidColor.setVisible(false);
          labelUnit.setText("แคปซูล");
          break;
        case 2:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(true);
          labelUnit.setText("มิลลิลิตร");
          break;
        default:
          panelColor.setVisible(false);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelUnit.setText("มิลลิลิตร");
          break;
      }
    });
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

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeLabel("คำอธิบายยา (เช่น ยาแก้ปวด)"));
    panelSub.add(tfMedDescription);
    panelAddMedGUI.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeBoldLabel("เวลาที่ต้องรับประทาน"));
    panelAddMedGUI.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbMorning);
    panelSubMorning.add(rbMorningBefore);
    panelSubMorning.add(rbMorningAfter);
    panelSubMorning.add(rbMorningImme);
    panelSubMorning.add(makeLabel("จำนวน"));
    panelSubMorning.add(tfAmountMorning);
    panelSubMorning.add(labelUnit);
    panelSub.add(panelSubMorning);
    panelAddMedGUI.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbAfternoon);
    panelSubAfternoon.add(rbAfternoonBefore);
    panelSubAfternoon.add(rbAfternoonAfter);
    panelSubAfternoon.add(rbAfternoonImme);
    panelSubAfternoon.add(makeLabel("จำนวน"));
    panelSubAfternoon.add(tfAmountAfternoon);
    panelSubAfternoon.add(labelUnit);
    panelSub.add(panelSubAfternoon);
    panelAddMedGUI.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbEvening);
    panelSubEvening.add(rbEveningBefore);
    panelSubEvening.add(rbEveningAfter);
    panelSubEvening.add(rbEveningImme);
    panelSubEvening.add(makeLabel("จำนวน"));
    panelSubEvening.add(tfAmountEvening);
    panelSubEvening.add(labelUnit);
    panelSub.add(panelSubEvening);
    panelAddMedGUI.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(cbBed);
    panelSubBed.add(makeLabel("จำนวน"));
    panelSubBed.add(tfAmountBed);
    panelSubBed.add(labelUnit);
    panelSub.add(panelSubBed);
    panelAddMedGUI.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeLabel("จำนวนยาทั้งหมด"));
    panelSub.add(tfTotalMeds);
    panelSub.add(labelUnit);
    panelAddMedGUI.add(panelSub);

    panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelSub.add(makeLabel("วันหมดอายุ"));
    panelSub.add(tfMedEXP);
    panelAddMedGUI.add(panelSub);

    panelSub = new JPanel();
    panelSub.add(btnSave);
    panelAddMedGUI.add(panelSub);

    JScrollPane scrollPane = new JScrollPane(panelAddMedGUI);

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
      case "บันทึกยา":
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
    Date dateEXP = new Date();
    try {
      dateEXP = GUIHelper.formatDMY.parse("28/02/2019");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Medicine prednisolone = new Medicine("Prednisolone", "tablet", "white", "ยาแก้อักเสบ",
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
    Medicine chlopheniramine = new Medicine("Chlopheniramine", "tablet", "yellow", "ยาแก้แพ้",
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
    Medicine amoxicillin = new Medicine("Amoxicillin", "capsule", "", "ยาแก้อักเสบ", sampleMedTime,
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
