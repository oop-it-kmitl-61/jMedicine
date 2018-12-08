package GUI;

import static GUI.GUIHelper.*;
import static GUI.components.AppointmentUI.*;
import static GUI.components.DoctorUI.*;
import static GUI.components.MedicineUI.*;
import static core.Core.getUser;
import static core.UserUtil.getGenderIndex;
import static core.UserUtil.getGenders;
import static core.UserUtil.getPrefixIndex;
import static core.UserUtil.getPrefixes;

import GUI.components.MedicineUI;
import com.github.lgooddatepicker.components.TimePicker;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PermissionStatus;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import core.Overview;
import core.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import javax.swing.*;
import core.LocationHelper;


/**
 * All GUIs will be centralized here. GUI that needed too much methods has been moved to
 * /components
 *
 * @author jMedicine
 * @version 0.7.10
 * @since 0.1.0
 */

public class GUI {

  static JFrame frameWelcome, frameMain;
  static JPanel panelLeft;
  public static JPanel panelRight;
  static JPanel panelWelcome;
  static JPanel panelSignIn, panelLoading, panelErrorSignIn, panelErrorSignUpUsername, panelErrorSignUpPassword;
  static JTextField tfUserName;
  static JPasswordField tfPassword, tfPasswordConfirm;
  static JButton buttons[], btnSignIn, btnSignUp, btnSkip;
  private static Dimension windowSize, minSize;
  private static GUIUtil util;
  private static MedicineUI medUI;

  public GUI(Dimension windowSize) {
    GUI.util = new GUIUtil();
    GUI.windowSize = windowSize;
    GUI.minSize = new Dimension(800, 600);
    JOptionPane.setDefaultLocale(locale);
    GUIHelper.setup();
  }

  static void main() {
    /* Creates the main frame including left navigation and 6 sub panels on the right */

    // Init main panels
    JPanel panelMain = new JPanel(new BorderLayout());
    panelLeft = new JPanel(new GridBagLayout());
    panelRight = new JPanel(new CardLayout());
    setPadding(panelLeft, 20, 0, 5, 0);
    setPadding(panelRight, 25, 20, 10, 20);

    makeLeftNavigation();
    panelOverview();
    panelAllMedicines();
    panelAllAppointments();
    panelAllDoctors();
    panelNearbyHospitals();
    panelSettings();

    // Add left navigation and right panel into the main panel
    panelMain.add(panelLeft, BorderLayout.WEST);
    panelMain.add(panelRight, BorderLayout.CENTER);

    // Init main frame
    frameMain = new JFrame("jMedicine");
    frameMain.add(panelMain);
    frameMain.setMinimumSize(minSize);
    frameMain.setSize(windowSize);
    frameMain.setLocationRelativeTo(null);
    frameMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  private static void panelOverview() {
    /*
      Creates GUI of overview panel, displaying a summary of upcoming events,
      including medication reminders and doctor appointments.
     */

    Overview overview = new Overview();

    // JPanels
    JPanel panelOverview = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelLoop = overview.renderOverview();

    String today = GUIHelper.formatDMYFull.format(new Date());
    panelTitle.add(makeTitleLabel(today));

    // Styling
    setPadding(panelTitle, 0, 0, -12, 2);
    setPadding(panelLoop, 0, 0, 20, 0);

    JScrollPane scrollPane = makeScrollPane(panelLoop);

    // Add all panels into the main panel
    panelOverview.add(panelTitle, BorderLayout.NORTH);
    panelOverview.add(scrollPane);

    panelRight.add(panelOverview, "ภาพรวม");
  }

  private static void panelNearbyHospitals() {
    /*
      Creates GUI displaying Google LocationHelper that is showing the current position
      of the user, fetched from a public IP address, queried nearby hospitals.
     */

    // Init title panel displaying title label
    JPanel panelNearBy = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());
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
        "https://www.google.co.th/maps/search/โรงพยาบาล/@" + location[0] + "," + location[1]
            + ",12z");

    // Add all sub panels into the main panel
    panelNearBy.add(panelTitle, BorderLayout.NORTH);
    panelNearBy.add(view);
    panelRight.add(panelNearBy, "โรงพยาบาลใกล้เคียง");
  }

  private static void panelSettings() {
    /* Creates GUI displaying user's settings */

    // JPanels
    JPanel panelSettings = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();

    panelTitle.add(makeTitleLabel("การตั้งค่า"));

    // JToggle
    JToggleButton toggleNoti = makeToggle("เปิดการแจ้งเตือน (macOS เท่านั้น)", true);

    // JLabels
    JLabel labelEditProfile = makeLabel("แก้ไขข้อมูลส่วนตัว");
    JLabel labelEditTime = makeLabel("ตั้งค่าเวลา");
    JLabel labelSignOut = makeLabel("ออกจากระบบ");
    JLabel labelUserName = makeTitleLabel(getUser().getUserName());

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(labelSignOut, 0, 0, 20);
    setPadding(panelBody, 20, 0, 180);
    setPadding(labelUserName, 0, 0, 20, 0);

    makeLabelClickable(labelEditProfile, "แก้ไขข้อมูลส่วนตัว");
    makeLabelClickable(labelEditTime, "ตั้งค่าเวลา");
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
    panelSub.add(labelEditProfile);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelEditTime);
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
    panelSub.add(makeLabel("เวอร์ชั่น 0.7.10"));
    panelBody.add(panelSub);

    // Add all sub panels into the main panel
    panelSettings.add(panelTitle, BorderLayout.NORTH);
    panelSettings.add(panelBody);

    panelRight.add(panelSettings, "การตั้งค่า");

    panelEditProfile();
    panelEditTime();
  }

  public static void initWelcome() {
    /*
      Creates a very first GUI. This GUI will be displayed if the program is being
      run for the first time or the user is not logged in.
     */

    // Frame
    frameWelcome = new JFrame("jMedicine: เข้าสู่ระบบ");

    // Panels
    panelLoading = getLoadingPanel(false);
    panelErrorSignIn = getErrorPanel("ชื่อผู้ใช้งานหรือรหัสผ่านไม่ถูกต้อง");
    panelErrorSignUpUsername = getErrorPanel("ชื่อผู้ใช้งานนี้เคยสมัครไปแล้ว");
    panelErrorSignUpPassword = getErrorPanel("รหัสผ่านทั้งสองช่องไม่ตรงกัน");
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
    tfUserName = makeTextField(10);
    tfPassword = makePasswordField(10);
    tfPasswordConfirm = makePasswordField(10);

    // JButtons
    btnSignIn = makeBlueButton("เข้าสู่ระบบ");
    btnSignUp = makeBlueButton("ลงทะเบียน");

    // Styling
    setPadding(labelUsername, 0, 0, -16, 0);
    setPadding(labelPassword, 0, 0, -10, 0);
    setPadding(labelPasswordConfirm, 0, 0, -10, 0);
    setPadding(labelRegister, 20, 60);
    setPadding(labelSignIn, 20, 60);
    panelLoading.setVisible(false);
    panelErrorSignIn.setVisible(false);
    panelErrorSignUpUsername.setVisible(false);
    panelErrorSignUpPassword.setVisible(false);
    labelPasswordConfirm.setVisible(false);
    tfPasswordConfirm.setVisible(false);
    btnSignUp.setVisible(false);
    labelSignIn.setVisible(false);
    makeLabelCenter(labelWelcome);
    makeLabelCenter(labelWelcomeSub);

    // Listeners
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
    panelSignIn.add(panelLoading, gbc);
    gbc.gridy++;
    panelSignIn.add(panelErrorSignIn, gbc);
    gbc.gridy++;
    panelSignIn.add(panelErrorSignUpUsername, gbc);
    gbc.gridy++;
    panelSignIn.add(panelErrorSignUpPassword, gbc);
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
    btnSkip = makeBlueButton("ข้ามขั้นตอนนี้");
    setPadding(labelTitle, 0, 0, 30);
    btnSkip.setAlignmentX(Component.CENTER_ALIGNMENT);

    panelFirstMed.setLayout(new BoxLayout(panelFirstMed, BoxLayout.PAGE_AXIS));
    setPadding(panelFirstMed, 80, 0, 40, 0);

    JPanel panelInline = new JPanel(new FlowLayout());
    panelInline.add(labelTitle);
    panelFirstMed.add(panelInline);
    panelFirstMed.add(medUI.addMedGUI());
    panelFirstMed.add(btnSkip);

    util.listeners();

    panelWelcome.add(panelSignIn, "ยังไม่ได้เข้าสู่ระบบ");
    panelWelcome.add(panelFirstMed, "เพิ่มยาตัวแรก");

    frameWelcome.add(panelWelcome);
    frameWelcome.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frameWelcome.setMinimumSize(minSize);
    frameWelcome.setSize(windowSize);
    frameWelcome.setLocationRelativeTo(null);
    frameWelcome.setVisible(true);
  }

  private static void makeLeftNavigation() {
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

  private static void panelEditProfile() {

    // JPanels
    JPanel panelMain = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelBody = new JPanel();

    User user = getUser();

    // JButtons
    JButton btnBack = makeBackButton("แก้ไขข้อมูลส่วนตัว", "การตั้งค่า");
    JButton btnEditPwd = makeBlueButton("เปลี่ยนรหัสผ่าน");
    JButton btnSave = makeBlueButton("บันทึก");

    // JTextFields
    // JTextField tfUsername = makeTextField(20);
    JTextField tfEmail = makeTextField(20);
    JTextField tfFName = makeTextField(20);
    JTextField tfLName = makeTextField(20);
    JTextField tfWeight = makeNumberField(4);
    JTextField tfHeight = makeNumberField(4);
    JTextField tfAge = makeNumberField(2);

    // tfUsername.setText(user.getUserName());
    tfFName.setText(user.getUserFirstName());
    tfLName.setText(user.getUserLastName());
    tfWeight.setText(user.getUserWeight());
    tfHeight.setText(user.getUserHeight());
    tfAge.setText(user.getUserAge());

    // JPasswordFields
    JPasswordField tfPassword = makePasswordField(20);
    JPasswordField tfPasswordConfirm = makePasswordField(20);

    // JComboBoxes
    JComboBox cbPrefix = makeComboBox(getPrefixes());
    JComboBox cbGender = makeComboBox(getGenders());

    cbPrefix.setSelectedIndex(getPrefixIndex(user.getUserTitle()));
    cbGender.setSelectedIndex(getGenderIndex(user.getUserGender()));

    // JLabels
    JLabel labelHeading1 = makeBoldLabel("ข้อมูลการเข้าใช้งาน");
    JLabel labelHeading2 = makeBoldLabel("ข้อมูลส่วนตัว");
    JLabel labelUserName = makeLabel("Username");
    JLabel labelFName = makeLabel("ชื่อ");
    JLabel labelLName = makeLabel("นามสกุล");
    JLabel labelEmail = makeLabel("อีเมล");
    JLabel labelAge = makeLabel("อายุ");
    JLabel labelAgeUnit = makeLabel("ปี");
    JLabel labelGender = makeLabel("เพศ");
    JLabel labelWeight = makeLabel("น้ำหนัก");
    JLabel labelWeightUnit = makeLabel("กิโลกรัม");
    JLabel labelHeight = makeLabel("ส่วนสูง");
    JLabel labelHeightUnit = makeLabel("เซนติเมตร");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelMain, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 260, 28);
    setPadding(panelTitle, 0, 0, 20);

    // Panel Title
    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(labelHeading1);
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = newFlowLayout();
    // panelSub.add(labelUserName);
    // panelSub.add(tfUsername);
    panelSub.add(btnEditPwd);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelHeading2);
    setPadding(panelSub, 10, 0, 0, 0);
    panelBody.add(panelSub);

    panelBody.add(new JSeparator(SwingConstants.HORIZONTAL));

    panelSub = newFlowLayout();
    panelSub.add(cbPrefix);
    panelSub.add(labelFName);
    panelSub.add(tfFName);
    panelSub.add(labelLName);
    panelSub.add(tfLName);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelGender);
    panelSub.add(cbGender);
    panelSub.add(labelAge);
    panelSub.add(tfAge);
    panelSub.add(labelAgeUnit);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelWeight);
    panelSub.add(tfWeight);
    panelSub.add(labelWeightUnit);
    panelSub.add(labelHeight);
    panelSub.add(tfHeight);
    panelSub.add(labelHeightUnit);
    panelBody.add(panelSub);

    panelMain.add(panelTitle, BorderLayout.NORTH);
    panelMain.add(panelBody, BorderLayout.CENTER);
    panelMain.add(btnSave, BorderLayout.SOUTH);

    panelRight.add(panelMain, "แก้ไขข้อมูลส่วนตัว");
  }

  private static void panelEditTime() {

    // JPanels
    JPanel panelMain = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelBody = new JPanel();

    // JButtons
    JButton btnBack = makeBackButton("ตั้งค่าเวลา", "การตั้งค่า");
    JButton btnSave = makeBlueButton("บันทึก");

    // Pickers
    TimePicker tpMorning = makeTimePicker();
    TimePicker tpAfternoon = makeTimePicker();
    TimePicker tpEvening = makeTimePicker();
    TimePicker tpBed = makeTimePicker();

    // TODO: getUser()->time
    tpMorning.setText("08:30");
    tpAfternoon.setText("12:30");
    tpEvening.setText("18:30");
    tpBed.setText("22:30");

    // JLabels
    JLabel labelDescription = makeLabel(
        "ตั้งค่าเวลาทานยาของคุณ ระบบจะทำการแจ้งเตือนการทานยาตามเวลาที่ท่่านได้กำหนดไว้");
    JLabel labelMorning = makeBoldLabel("เช้า");
    JLabel labelAfternoon = makeBoldLabel("กลางวัน");
    JLabel labelEvening = makeBoldLabel("เย็น");
    JLabel labelBed = makeBoldLabel("ก่อนนอน");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelMain, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 260, 28);
    setPadding(panelTitle, 0, 0, 20);

    // Panel Title
    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(labelDescription);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelMorning);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tpMorning);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelAfternoon);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tpAfternoon);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelEvening);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tpEvening);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelBed);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tpBed);
    panelBody.add(panelSub);

    panelMain.add(panelTitle, BorderLayout.NORTH);
    panelMain.add(panelBody, BorderLayout.CENTER);
    panelMain.add(btnSave, BorderLayout.SOUTH);

    panelRight.add(panelMain, "ตั้งค่าเวลา");
  }

  static JButton[] getButtons() {
    return buttons;
  }

}
