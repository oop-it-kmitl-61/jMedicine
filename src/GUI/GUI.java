package GUI;

import static GUI.GUIHelper.*;
import static api.Login.*;

import api.LoginException;
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
import java.awt.Toolkit;
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


/**
 * GUI class creates all graphic user interface, all in javax.swing.
 * Use a constructor to new a GUI.
 *
 * @param windowSize a Dimension class consists of width and height.
 *
 * ===============================================
 * Main Structure
 * ===============================================
 *
 * GUI.java
 * \_ GUI()         -> Init and setup
 * \_ initWelcome() -> Makes GUI when the user is not signed in.
 * \_ main()        -> Makes GUI after the user has signed in.
 *    \_ panelOverview()         -> Returns a panel that displays all overview cards.
 *    \  \_ makeOverviewCard()   -> Returns an overview card.
 *    \
 *    \_ panelAllMedicines()     -> Returns a panel that displays all user's medicines cards.
 *    \  \_ makeMedCard()        -> Returns a medicine card.
 *    \  \_ viewMedicine()       -> Returns a panel that displays a single medicine information.
 *    \  \_ addMedicine()        -> Returns a panel to add new medicine.
 *    \  \_ addMedGUI()          -> Makes UI to add new medicine, being used by initWelcome() and addMedicine().
 *    \  \_ COMING SOON          -> Returns a panel to edit an existed medicine.
 *    \
 *    \_ panelAllAppointments()  -> Returns a panel that displays all user's appointments cards.
 *    \  \_ makeAppointmentCard()-> Returns an appointment card.
 *    \  \_ viewAppointmentCard()-> Returns a panel that displays a single appointment information.
 *    \  \_ COMING SOON          -> Returns a panel to add new appointment.
 *    \  \_ COMING SOON          -> Returns a panel to edit an existed appointment.
 *    \
 *    \_ panelAllDoctors()       -> Returns a panel that displays all user's doctors cards.
 *    \  \_ makeDoctorCard()     -> Returns a doctor card.
 *    \  \_ viewDoctors()        -> Returns a panel that displays a single doctors information.
 *    \  \_ addDoctors()         -> Returns a panel to add new doctor.
 *    \  \_ COMING SOON          -> Returns a panel to edit an existed doctor.
 *    \
 *    \_ panelNearbyHospitals()  -> Returns a panel that displays Google Maps showing nearby hospitals.
 *    \_ panelSettings()         -> Returns a panel that displays user's settings.
 *    \  \_ COMING SOON          -> Returns a panel to edit user information.
 *    \_ makeLeftNavigation()    -> Returns a left navigation panel.
 *
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
  Locale locale;

  public GUI(Dimension windowSize) {
    this.medUtil = new MedicineUtil();
    this.windowSize = windowSize;
    this.minSize = new Dimension(800, 600);
    this.locale = new Locale("th","TH");
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
    panelTitle = new JPanel(new BorderLayout());
    String today = GUIHelper.formatDMYFull.format(new Date());
    panelTitle.add(makeTitleLabel(today));

    // Init card loop
    panelLoop = newPanelLoop();
    // TODO: Fetch these upcoming events from the database
    // Sample loop
    cardLoop = makeOverviewCard("12.30 น. (อีก 1 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)", "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    cardLoop = makeOverviewCard("18.30 น. (อีก 7 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)", "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    // End sample loop

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
    browser.setPermissionHandler(new PermissionHandler() {
      @Override
      public PermissionStatus onRequestPermission(PermissionRequest request) {
        return PermissionStatus.GRANTED;
      }
    });
    // Load URL that query the hospital around the current position
    browser.loadURL("https://www.google.co.th/maps/search/hospitals/@"+location[0]+","+location[1]+",12z");

    // Add all sub panels into the main panel
    panelSub05.add(panelTitle, BorderLayout.NORTH);
    panelSub05.add(view);

    return panelSub05;
  }

  private JPanel panelSettings() {
    /* Creates GUI displaying user's settings */

    // Init title panel displaying title label
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeTitleLabel("การตั้งค่า"));

    JLabel labelSignOut = makeLabel("ออกจากระบบ");
    labelSignOut.setForeground(Color.RED);
    setPadding(labelSignOut, 0, 0, 20);
    makeLabelClickable(labelSignOut, "ยังไม่ได้เข้าสู่ระบบ");

    JPanel panelBox = new JPanel();
    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.PAGE_AXIS));
    setPadding(panelBox, 20, 0);
    JLabel labelUserName = makeTitleLabel(user.getUserName());
    panelBox.add(makeLabel("ผู้ใช้งานปัจจุบัน"));
    panelBox.add(labelUserName);
    panelBox.add(labelSignOut);
    panelBox.add(makeCheckBox("เปิดการแจ้งเตือน (macOS เท่านั้น)", true));

    // Add all sub panels into the main panel
    panelSub06.add(panelTitle, BorderLayout.NORTH);
    panelSub06.add(panelBox);

    return panelSub06;
  }

  private JPanel panelAddMedicine() {
    /* Creates outer GUI when user add a new medicine from all medicines page. */

    // JPanels
    JPanel panelAddMedicine = new JPanel(new BorderLayout());
    JPanel panelBox = new JPanel();
    panelTitle = new JPanel(new BorderLayout());

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มยาใหม่", "ยาทั้งหมด");

    // Styling
    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.X_AXIS));
    setPadding(panelAddMedicine, 0, 0, 40, -16);
    setPadding(panelBox, 0, 0, 20);
    setPadding(panelTitle, 0, 0, 20);

    panelTitle.add(btnBack);
    panelAddMedicine.add(panelTitle, BorderLayout.NORTH);
    panelAddMedicine.add(addMedGUI());

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
      JLabel labelConfirm = makeLabel("ต้องการลบยานี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");
      setPadding(labelConfirm, 0, 16, 0, 0);

      Toolkit.getDefaultToolkit().beep();
      try {
        Image img = ImageIO.read(new File(GUIHelper.imgWarningSrc));
        Icon icon = new ImageIcon(img);
        dialogResult = JOptionPane.showConfirmDialog (null, labelConfirm, "คุณกำลังทำการลบยา", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,icon);
      } catch (Exception ignored) {
        dialogResult = JOptionPane.showConfirmDialog (null, labelConfirm, "คุณกำลังทำการลบยา", JOptionPane.YES_NO_OPTION);
      }

      if(dialogResult == JOptionPane.YES_OPTION){
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
          Image img = ImageIO.read(new File(GUIHelper.imgSuccessSrc));
          Icon icon = new ImageIcon(img);
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบยา", JOptionPane.INFORMATION_MESSAGE, icon);
        } catch (Exception ignored) {
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบยา", JOptionPane.INFORMATION_MESSAGE);
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
    panelSub.add(makeLabel("ขนาดรับประทาน: " + medicine.getMedDose() + " " + medicine.getMedUnit()));
    panelSub.add(makeLabel("วันที่เพิ่มยา: " + GUIHelper.formatDMY.format(medicine.getDateAdded())));
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
    JPanel panelAddDoctor = new JPanel();
    JPanel panelSub = new JPanel();
    panelTitle = new JPanel(new BorderLayout());

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มแพทย์ใหม่", "แพทย์");

    // Styling
    panelSub.setLayout(new BoxLayout(panelSub, BoxLayout.PAGE_AXIS));
    panelTitle.add(btnBack);
    setPadding(panelTitle, 0, 0, 20);

    JTextField tfDoctorName = makeTextField();
    JTextField tfDoctorWard = makeTextField();
    JTextField tfDoctorHospital = makeTextField();
    String[] prefixes = {"นพ.", "พญ.", "ศ.นพ", "ผศ.นพ"};
    JComboBox cbPrefix = makeComboBox(prefixes);
    //TODO: ทำให้เพิ่มวันและเวลาที่แพทย์เข้าตรวจได้
    //แนวคิด: ทำ JCheckBox ให้ครบทุกวัน (จันทร์-เสาร์) ให้ติ๊กเลือก ถ้าติ๊กช่องไหนก็ใส่เวลาลง textfield ของวันนั้นเข้าไปด้วย

    panelSub.add(makeLabel("คำนำหน้า"));
    panelSub.add(cbPrefix);
    panelSub.add(makeLabel("ชื่อ-สกุล"));
    panelSub.add(tfDoctorName);
    panelSub.add(makeLabel("แผนก"));
    panelSub.add(tfDoctorWard);
    panelSub.add(makeLabel("ชื่อสถานพยาบาล"));
    panelSub.add(tfDoctorHospital);

    panelAddDoctor.add(panelTitle, BorderLayout.NORTH);
    panelAddDoctor.add(panelSub);

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
      JLabel labelConfirm = makeLabel("ต้องการลบแพทย์คนนี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");
      setPadding(labelConfirm, 0, 16, 0 ,0);
      int dialogResult = 0;

      Toolkit.getDefaultToolkit().beep();
      try {
        Image img = ImageIO.read(new File(GUIHelper.imgWarningSrc));
        Icon icon = new ImageIcon(img);
        dialogResult = JOptionPane.showConfirmDialog (null, labelConfirm, "คุณกำลังทำการลบแพทย์", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icon);
      } catch (Exception ignored) {
        dialogResult = JOptionPane.showConfirmDialog (null, labelConfirm, "คุณกำลังทำการลบแพทย์", JOptionPane.YES_NO_OPTION);
      }

      if(dialogResult == JOptionPane.YES_OPTION){
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
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบแพทย์", JOptionPane.INFORMATION_MESSAGE, icon);
        } catch (Exception ignored) {
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบแพทย์", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });

    panelTitle.add(btnBack);
    panelSub.add(makeLabel("ชื่อแพทย์: " + doctorName));
    panelSub.add(makeLabel("แผนก: " + doctor.getWard()));
    panelSub.add(makeLabel("โรงพยาบาล: " + doctor.getHospital()));
    panelSub.add(makeLabel("เวลาเข้าตรวจ:"));

    // WorkTime is an ArrayList, convert it to a printable format
    for (String workTime:doctor.getWorkTime()) {
      JLabel labelWorkTime = makeLabel(workTime);
      setPadding(labelWorkTime, 0, 20);
      panelSub.add(labelWorkTime);
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
      JLabel labelConfirm = makeLabel("ต้องการลบนัดแพทย์นี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");
      setPadding(labelConfirm, 0, 16, 0 ,0);
      int dialogResult = 0;

      Toolkit.getDefaultToolkit().beep();
      try {
        Image img = ImageIO.read(new File(GUIHelper.imgWarningSrc));
        Icon icon = new ImageIcon(img);
        dialogResult = JOptionPane.showConfirmDialog (null, labelConfirm, "คุณกำลังทำการลบนัดแพทย์", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icon);
      } catch (Exception ignored) {
        dialogResult = JOptionPane.showConfirmDialog (null, labelConfirm, "คุณกำลังทำการลบนัดแพทย์", JOptionPane.YES_NO_OPTION);
      }

      if(dialogResult == JOptionPane.YES_OPTION){
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
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบนัดแพทย์", JOptionPane.INFORMATION_MESSAGE, icon);
        } catch (Exception ignored) {
          JOptionPane.showMessageDialog(null, labelMessage, "ผลการลบนัดแพทย์", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });

    // Init web browser
    Browser browser = new Browser();
    BrowserView view = new BrowserView(browser);
    // Load URL that query the hospital around the current position
    browser.loadURL("https://www.google.co.th/maps/search/"+ appointment.getHospitalName());

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
    buttons = new JButton[] {
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
    for(JButton button: buttons){
      button.setHorizontalAlignment(SwingConstants.LEFT);
      paintButton();
      if (tempCount == 0) {
        paintCurrentTabButton(button);
      }
      // Switch between sub panels
      button.addActionListener(e -> {
        CardLayout cl = (CardLayout)(panelRight.getLayout());
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
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.PAGE_AXIS));
    panelLoopInfo.setBorder(new CompoundBorder(
        BorderFactory.createEmptyBorder(5, 0, 20, 0),
        new RoundedBorder(10)
    ));
    JLabel labelTime = makeBoldLabel(time);
    JLabel labelMed = makeLabel(medName);
    JLabel labelAmount = makeLabel(dose);
    makeLabelCenter(labelTime);
    makeLabelCenter(labelMed);
    makeLabelCenter(labelAmount);
    setPadding(labelTime, 5, 0, 5, 0);
    panelLoopInfo.add(labelTime);
    panelLoopInfo.add(labelMed);
    panelLoopInfo.add(labelAmount);
    panelLoopInfo.add(Box.createHorizontalGlue());
    return panelLoopInfo;
  }

  private void makeLabelClickable(JLabel label, String href) {
    /* Works like <a> */
    label.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        if (href.equals("ลงทะเบียน") || href.equals("ยังไม่ได้เข้าสู่ระบบ") ) {
          if (frameWelcome == null) {
            initWelcome();
          }
          frameWelcome.setVisible(true);
          CardLayout cl = (CardLayout)(panelWelcome.getLayout());
          cl.show(panelWelcome, href);
          try {
            frameMain.setVisible(false);
          } catch (NullPointerException ignored) {
          }
        } else {
          CardLayout cl = (CardLayout)(panelRight.getLayout());
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
      Image img = ImageIO.read(new File("src/GUI/img/back.png"));
      button.setIcon(new ImageIcon(img));
    } catch (Exception ignored) { }
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
      Image img = ImageIO.read(new File("src/GUI/img/add.png"));
      btnNew.setIcon(new ImageIcon(img));
    } catch (Exception ignored) { }
    btnNew.setAlignmentX(Component.LEFT_ALIGNMENT);
    btnNew.setHorizontalAlignment(SwingConstants.LEFT);
    btnNew.setFont(new Font("TH Sarabun New", Font.BOLD, 28));
    btnNew.addActionListener(e -> {
      CardLayout cl = (CardLayout)(panelRight.getLayout());
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
      CardLayout cl = (CardLayout)(panelWelcome.getLayout());
      cl.show(panelWelcome, "ยังไม่ได้เข้าสู่ระบบ");
    } else {
      CardLayout cl = (CardLayout)(panelRight.getLayout());
      cl.show(panelRight, backTo);
    }
  }

  private JPanel makeMedCard(Medicine medicine) {
    /* Creates a card that will be used on the All medicines panel only. */
    Date medEXP = medicine.getMedEXP();
    String medTitle = medicine.getMedName()+" ("+medicine.getMedDescription()+")";
    String medShortInfo = "เหลืออยู่ "+medicine.getMedRemaining()+" "+medicine.getMedUnit()+" หมดอายุ "+GUIHelper.formatDMY
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

    panelLoopInfo.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        panelRight.add(panelViewMedicine(medicine), medicine.getMedName());
        CardLayout cl = (CardLayout)(panelRight.getLayout());
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
      Image img = ImageIO.read(new File("src/GUI/img/doctor.png"));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ignored) { }

    panelPic.add(labelPic);
    panelInfo.add(labelTitle);
    panelInfo.add(labelShortInfo);

    setPadding(labelTitle, 5, 0, -10, 0);
    setPadding(labelPic, 5, 10, 0, 0);

    panelLoopInfo.add(panelPic);
    panelLoopInfo.add(panelInfo);
    panelLoopInfo.add(Box.createHorizontalGlue());

    panelLoopInfo.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        panelRight.add(panelViewDoctor(doctor), doctor.getName());
        CardLayout cl = (CardLayout)(panelRight.getLayout());
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
    String shortInfo = appDr.getPrefix() + " " + appDr.getName() + " " + appointment.getHospitalName();

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
      Image img = ImageIO.read(new File("src/GUI/img/calendar.png"));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ignored) { }

    panelPic.add(labelPic);
    panelInfo.add(labelTitle);
    panelInfo.add(labelShortInfo);

    setPadding(labelTitle, 5, 0, -10, 0);
    setPadding(labelPic, 5, 10, 0, 0);

    panelLoopInfo.add(panelPic);
    panelLoopInfo.add(panelInfo);
    panelLoopInfo.add(Box.createHorizontalGlue());

    panelLoopInfo.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        panelRight.add(panelViewAppointment(appointment), title);
        CardLayout cl = (CardLayout)(panelRight.getLayout());
        cl.show(panelRight, title);
      }
    });

    return panelLoopInfo;
  }

  private JPanel addMedGUI() {
    /* Creates GUI of the form for adding a new medicine. */
    String medUnit = "เม็ด";
    JPanel panelAddMed = new JPanel();
    JTextField tfMedName = makeTextField(10);
    JTextField tfMedDescription = makeTextField(20);
    JTextField tfAmount = makeTextField(2);
    JTextField tfTotalMeds = makeTextField(2);
    JTextField tfMedEXP = makeTextField(10);
    JButton btnSave = makeButton("บันทึกยา");
    ButtonGroup rdGroup = new ButtonGroup();

    String[] medType = medUtil.getMedType();
    String[] medColor = medUtil.getMedColor();
    String[] medTime = medUtil.getMedTime();
    String[] medDoseStr = medUtil.getMedDoseStr();
    ArrayList<JCheckBox> chTime = new ArrayList<>();
    ArrayList<JRadioButton> rdAmountStr = new ArrayList<>();

    JComboBox cbMedType = makeComboBox(medType);
    JComboBox cbMedColor = makeComboBox(medColor);

    for (String time : medTime) {
      chTime.add(makeCheckBox(time));
    }

    for (String amountStr : medDoseStr) {
      JRadioButton rdItem = makeRadioButton(amountStr);
      rdAmountStr.add(rdItem);
      rdGroup.add(rdItem);
    }

    btnSave.addActionListener(this);
    panelAddMed.setLayout(new BoxLayout(panelAddMed, BoxLayout.PAGE_AXIS));
    setPadding(panelAddMed, 0, 0, 40);

    cbMedType.addActionListener(this);
    cbMedColor.addActionListener(this);

    JPanel panelInline = new JPanel(new FlowLayout());
    panelInline.add(makeLabel("ชื่อยา"));
    panelInline.add(tfMedName);
    panelInline.add(makeLabel("ประเภท"));
    panelInline.add(cbMedType);
    panelInline.add(makeLabel("สีของยา"));
    panelInline.add(cbMedColor);
    panelAddMed.add(panelInline);

    JPanel panelBorder = new JPanel(new BorderLayout());
    setPadding(panelBorder, 10, 20);
    panelBorder.add(makeLabel("คำอธิบายยา (เช่น ยาแก้ปวด)"), BorderLayout.NORTH);
    panelBorder.add(tfMedDescription);
    panelAddMed.add(panelBorder);

    panelBorder = new JPanel(new BorderLayout());
    setPadding(panelBorder, 10, 20);
    //NORTH
    panelBorder.add(makeLabel("เวลาที่ต้องรับประทาน"), BorderLayout.NORTH);

    // WEST
    panelInline = new JPanel(new FlowLayout());
    for (JCheckBox aChTime : chTime) {
      panelInline.add(aChTime);
    }
    panelBorder.add(panelInline, BorderLayout.WEST);

    // EAST
    panelInline = new JPanel(new FlowLayout());
    for (JRadioButton aRdAmountStr : rdAmountStr) {
      panelInline.add(aRdAmountStr);
    }
    panelInline.setPreferredSize(new Dimension(100, panelInline.getHeight()));
    panelBorder.add(panelInline, BorderLayout.CENTER);
    panelInline = new JPanel(new FlowLayout());
    panelInline.add(makeLabel("จำนวน"));
    panelInline.add(tfAmount);
    panelInline.add(makeLabel(medUnit));
    panelBorder.add(panelInline, BorderLayout.EAST);

    // SOUTH
    JPanel panelBox = new JPanel();
    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.PAGE_AXIS));
    panelInline = new JPanel(new FlowLayout());
    panelInline.add(makeLabel("จำนวนยาทั้งหมด"));
    panelInline.add(tfTotalMeds);
    panelInline.add(makeLabel(medUnit));
    panelBox.add(panelInline);
    panelInline = new JPanel(new FlowLayout());
    panelInline.add(makeLabel("วันหมดอายุ"));
    panelInline.add(tfMedEXP);
    panelBox.add(panelInline);
    panelBorder.add(panelBox, BorderLayout.SOUTH);

    btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
    panelAddMed.add(panelBorder);
    panelAddMed.add(btnSave);

    return panelAddMed;
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
            initSampleDoctor();
            initSampleMedicine01();
            initSampleMedicine02();
            initSampleMedicine03();
            initSampleAppointment();
            main();
            if (user.getUserMedicines().size() > 0) {
              frameWelcome.setVisible(false);
              frameMain.setVisible(true);
              frameWelcome = null;
              CardLayout cl = (CardLayout)(panelRight.getLayout());
              cl.show(panelRight, "ภาพรวม");
            } else {
              CardLayout cl = (CardLayout)(panelWelcome.getLayout());
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
          CardLayout cl = (CardLayout)(panelRight.getLayout());
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
    Medicine prednisolone = new Medicine("Prednisolone", "tablet", "white", "ยาแก้อักเสบ", sampleMedTime, sampleMedDoseStr, 1, 20, dateEXP);
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
    Medicine chlopheniramine = new Medicine("Chlopheniramine", "tablet", "yellow", "ยาแก้แพ้", sampleMedTime, sampleMedDoseStr, 1, 50, dateEXP);
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
    Medicine amoxicillin = new Medicine("Amoxicillin", "capsule", "", "ยาแก้อักเสบ", sampleMedTime, sampleMedDoseStr, 1, 7, dateEXP);
    user.addUserMedicine(amoxicillin);
  }

  private void initSampleDoctor() {
    ArrayList<String> workTime = new ArrayList<String>();
    workTime.add("วันพุธ เวลา 09.00 น. - 16.00 น.");
    workTime.add("วันพฤหัสบดี เวลา 09.00 น. - 16.00 น.");
    Doctor doctor = new Doctor("นพ.", "เก่ง จัง", "หู คอ จมูก", "บำรุงราษฎร์", workTime);
    user.addUserDoctor(doctor);
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
