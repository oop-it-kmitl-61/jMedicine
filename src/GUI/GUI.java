package GUI;

import static GUI.GUIHelper.backTo;
import static GUI.GUIHelper.fireConfirmDialog;
import static GUI.GUIHelper.fireDBErrorDialog;
import static GUI.GUIHelper.fireErrorDialog;
import static GUI.GUIHelper.fireSuccessDialog;
import static GUI.GUIHelper.getErrorPanel;
import static GUI.GUIHelper.getLoadingPanel;
import static GUI.GUIHelper.locale;
import static GUI.GUIHelper.mainBlue;
import static GUI.GUIHelper.makeBackButton;
import static GUI.GUIHelper.makeBlueButton;
import static GUI.GUIHelper.makeBoldLabel;
import static GUI.GUIHelper.makeComboBox;
import static GUI.GUIHelper.makeLabel;
import static GUI.GUIHelper.makeLabelCenter;
import static GUI.GUIHelper.makeLabelClickable;
import static GUI.GUIHelper.makeLeftNavigationButton;
import static GUI.GUIHelper.makeNumberField;
import static GUI.GUIHelper.makePasswordField;
import static GUI.GUIHelper.makeRedButton;
import static GUI.GUIHelper.makeScrollPane;
import static GUI.GUIHelper.makeSmallerLabel;
import static GUI.GUIHelper.makeTextField;
import static GUI.GUIHelper.makeTimePicker;
import static GUI.GUIHelper.makeTitleLabel;
import static GUI.GUIHelper.makeToggle;
import static GUI.GUIHelper.newFlowLayout;
import static GUI.GUIHelper.paintButton;
import static GUI.GUIHelper.paintCurrentTabButton;
import static GUI.GUIHelper.setPadding;
import static GUI.components.AppointmentUI.panelAllAppointments;
import static GUI.components.DoctorUI.panelAllDoctors;
import static GUI.components.MedicineUI.panelAllMedicines;
import static GUI.components.MedicineUI.panelFirstMedicine;
import static api.UserDB.deleteUser;
import static api.UserDB.updateUserData;
import static api.UserDB.updateUserPassword;
import static core.Core.getUser;
import static core.Core.setUser;
import static core.UserUtil.getGenderIndex;
import static core.UserUtil.getGenders;
import static core.UserUtil.getPrefixIndex;
import static core.UserUtil.getPrefixes;

import api.Login;
import api.LoginException;
import api.UserDB;
import com.github.lgooddatepicker.components.TimePicker;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PermissionStatus;
import com.teamdev.jxbrowser.chromium.bb;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import core.LocationHelper;
import core.Overview;
import core.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * All GUIs will be centralized here. GUI that needed too much methods has been moved to
 * /components
 *
 * @author jMedicine
 * @version 0.8.2
 * @since 0.1.0
 */

public class GUI {

  static {
    try {
      Field e = bb.class.getDeclaredField("e");
      e.setAccessible(true);
      Field f = bb.class.getDeclaredField("f");
      f.setAccessible(true);
      Field modifersField = Field.class.getDeclaredField("modifiers");
      modifersField.setAccessible(true);
      modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
      modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
      e.set(null, new BigInteger("1"));
      f.set(null, new BigInteger("1"));
      modifersField.setAccessible(false);
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  public static JFrame frameWelcome, frameMain;
  public static JPanel panelRight, panelOverview, panelWelcome;
  static JPanel panelLeft, panelSignIn, panelLoading, panelNoInput, panelErrorSignIn, panelErrorSignUpUsername, panelErrorSignUpPassword, panelSettings;
  static JTextField tfUserName;
  static JPasswordField tfPassword, tfPasswordConfirm;
  static JButton buttons[], btnSignIn, btnSignUp, btnSkipAddingInfo;
  static boolean isSignInPage, isSignUpPage;
  private static Dimension windowSize, minSize;
  private static GUIUtil util;


  public GUI(Dimension windowSize) {
    GUI.util = new GUIUtil();
    GUI.windowSize = windowSize;
    GUI.minSize = new Dimension(800, 680);
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
    panelOverview = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();

    String today = GUIHelper.formatDMYFull.format(new Date());
    LocalTime now = LocalTime.now();
    JLabel title = makeTitleLabel(today + " " + now.getHour() + ":" + now.getMinute());
    panelTitle.add(title);

    // Styling
    setPadding(panelTitle, 0, 0, 0, 2);

    panelOverview.add(panelTitle, BorderLayout.NORTH);

    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        LocalTime lNow = LocalTime.now();
        title.setText(GUIHelper.formatDMYFull.format(new Date()) + " " + lNow.getHour() + ":" + lNow
            .getMinute());
        JPanel panelLoop = overview.renderOverview();
        if (overview.getOverviewCount() < 3) {
          setPadding(panelLoop, 0, 0, 1000, 10);
          panelOverview.add(panelLoop);
        } else {
          setPadding(panelLoop, 0, 0, 20, 10);
          panelOverview.add(makeScrollPane(panelLoop));
        }
      }
    }, 0, 30 * 1000);

    panelRight.add(panelOverview, "ภาพรวม");
  }

  public static void reloadOverview() {
    panelRight.remove(panelOverview);
    panelOverview();
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
    panelSettings = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();

    panelTitle.add(makeTitleLabel("การตั้งค่า"));

    // JToggle
    JToggleButton toggleNoti = makeToggle("เปิดการแจ้งเตือน (macOS เท่านั้น)", true);
    toggleNoti.setSelected(getUser().isShowNotification());

    String userFullName;
    if (getUser().getUserFirstName().equals("")) {
      userFullName = "(ยังไม่ได้ตั้งชื่อ)";
    } else {
      userFullName = getUser().getUserPrefix() + getUser().getUserFirstName() + " " + getUser()
          .getUserLastName();
    }

    // JLabels
    JLabel labelEditProfile = makeLabel("แก้ไขข้อมูลส่วนตัว");
    JLabel labelEditTime = makeLabel("ตั้งค่าเวลา");
    JLabel labelSignOut = makeLabel("ออกจากระบบ");
    JLabel labelAbout = makeLabel("เกี่ยวกับ");
    JLabel labelUserName = makeTitleLabel(userFullName);

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(labelSignOut, 0, 0, 20);
    setPadding(panelBody, 20, 0, 180);
    setPadding(labelUserName, 0, 0, 20, 0);

    // Listeners
    makeLabelClickable(labelEditProfile, "แก้ไขข้อมูลส่วนตัว");
    makeLabelClickable(labelEditTime, "ตั้งค่าเวลา");
    makeLabelClickable(labelAbout, "เกี่ยวกับ");
    labelSignOut.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        setUser(null);
        backTo("ยังไม่ได้เข้าสู่ระบบ");
      }
    });
    toggleNoti.addActionListener(e -> {
      String successMessage = "";
      if (toggleNoti.isSelected()) {
        getUser().setShowNotification(true);
        successMessage = "เปิดการแจ้งเตือนเรียบร้อยแล้ว";
      } else {
        getUser().setShowNotification(false);
        successMessage = "ปิดการแจ้งเตือนเรียบร้อยแล้ว";
      }
      try {
        updateUserData();
        fireSuccessDialog(successMessage);
      } catch (SQLException e1) {
        e1.printStackTrace();
        fireDBErrorDialog();
      }
    });

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
    panelSub.add(labelAbout);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeSmallerLabel("เวอร์ชั่น 0.8.2"));
    panelBody.add(panelSub);

    // Add all sub panels into the main panel
    panelSettings.add(panelTitle, BorderLayout.NORTH);
    panelSettings.add(panelBody);

    panelRight.add(panelSettings, "การตั้งค่า");

    panelEditProfile();
    panelEditTime();
    panelAbout();
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
    panelNoInput = getErrorPanel("กรุณากรอกข้อมูลลงในช่องว่าง");
    panelErrorSignIn = getErrorPanel("ชื่อผู้ใช้งานหรือรหัสผ่านไม่ถูกต้อง");
    panelErrorSignUpUsername = getErrorPanel("ชื่อผู้ใช้งานนี้เคยสมัครไปแล้ว");
    panelErrorSignUpPassword = getErrorPanel("รหัสผ่านทั้งสองช่องไม่ตรงกัน");
    panelWelcome = new JPanel(new CardLayout());
    panelSignIn = new JPanel(new GridBagLayout());

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
    panelNoInput.setVisible(false);
    panelErrorSignIn.setVisible(false);
    panelErrorSignUpUsername.setVisible(false);
    panelErrorSignUpPassword.setVisible(false);
    labelPasswordConfirm.setVisible(false);
    tfPasswordConfirm.setVisible(false);
    btnSignUp.setVisible(false);
    labelSignIn.setVisible(false);
    makeLabelCenter(labelWelcome);
    makeLabelCenter(labelWelcomeSub);

    isSignInPage = true;

    // Listeners
    labelRegister.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        isSignUpPage = true;
        isSignInPage = false;
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
        isSignUpPage = false;
        isSignInPage = true;
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
    panelSignIn.add(panelNoInput, gbc);
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

    panelWelcome.add(panelSignIn, "ยังไม่ได้เข้าสู่ระบบ");

    util.listeners();

    frameWelcome.add(panelWelcome);
    frameWelcome.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frameWelcome.setMinimumSize(minSize);
    frameWelcome.setSize(windowSize);
    frameWelcome.setLocationRelativeTo(null);
    frameWelcome.setVisible(true);
  }

  public static JPanel panelFirstInfo() {

    // JPanels
    JPanel panelFirstInfo = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());
    JPanel panelBtn = new JPanel(new BorderLayout());

    // JButton
    JButton btnSave = makeBlueButton("บันทึก");
    btnSkipAddingInfo = makeRedButton("ข้ามขั้นตอนนี้");

    panelTitle.add(makeTitleLabel("เพิ่มข้อมูลส่วนตัวของคุณ"));

    panelBtn.add(btnSave, BorderLayout.CENTER);
    panelBtn.add(btnSkipAddingInfo, BorderLayout.EAST);

    // JTextFields
    JTextField tfEmail = makeTextField(20);
    JTextField tfFName = makeTextField(20);
    JTextField tfLName = makeTextField(20);
    JTextField tfWeight = makeNumberField(4);
    JTextField tfHeight = makeNumberField(4);
    JTextField tfAge = makeNumberField(2);

    // JComboBoxes
    JComboBox cbPrefix = makeComboBox(getPrefixes());
    JComboBox cbGender = makeComboBox(getGenders());

    // JLabels
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
    setPadding(panelFirstInfo, 20, 20, 20);
    setPadding(panelTitle, 20, 0, 20, 20);
    setPadding(panelBody, 0, 0, 1000, 28);

    // Listener
    btnSkipAddingInfo.addActionListener(e -> promptFirstMedicine());

    JPanel panelSub = newFlowLayout();
    panelSub.add(cbPrefix);
    panelSub.add(labelFName);
    panelSub.add(tfFName);
    panelSub.add(labelLName);
    panelSub.add(tfLName);
    setPadding(panelSub, 10, 0, 0);
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

    // Listener
    btnSave.addActionListener(e -> {
      String fName = tfFName.getText();
      String lName = tfLName.getText();
      String age = tfAge.getText();
      String weight = tfWeight.getText();
      String height = tfHeight.getText();
      if (fName.equals("") || lName.equals("") || age.equals("") || weight.equals("") || height
          .equals("")) {
        fireErrorDialog(
            "คุณกรอกข้อมูลไม่ครบถ้วน หากไม่ต้องการเพิ่มข้อมูลส่วนตัว กรุณากดปุ่ม \"ข้ามขั้นตอนนี้\"");
      } else {
        getUser().setUserPrefix(cbPrefix.getSelectedItem().toString());
        getUser().setUserFirstName(tfFName.getText());
        getUser().setUserLastName(tfLName.getText());
        getUser().setUserGender(cbGender.getSelectedItem().toString());
        getUser().setUserAge(Integer.parseInt(tfAge.getText()));
        getUser().setUserWeight(Double.parseDouble(tfWeight.getText()));
        getUser().setUserHeight(Double.parseDouble(tfHeight.getText()));

        try {
          UserDB.updateUserData();
          fireSuccessDialog("บันทึกข้อมูลสำเร็จ");
          panelRight.remove(panelSettings);
          panelSettings();
          promptFirstMedicine();
        } catch (SQLException ex) {
          ex.printStackTrace();
          fireDBErrorDialog();
        }
      }
    });

    panelFirstInfo.add(panelTitle, BorderLayout.NORTH);
    panelFirstInfo.add(panelBody, BorderLayout.CENTER);
    panelFirstInfo.add(panelBtn, BorderLayout.SOUTH);

    return panelFirstInfo;
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
    JButton btnRemoveAccount = makeRedButton("ลบบัญชีนี้");
    JButton btnSave = makeBlueButton("บันทึก");

    // JTextFields
    // JTextField tfUsername = makeTextField(20);
    JTextField tfEmail = makeTextField(20);
    JTextField tfFName = makeTextField(20);
    JTextField tfLName = makeTextField(20);
    JTextField tfWeight = makeNumberField(4);
    JTextField tfHeight = makeNumberField(4);
    JTextField tfAge = makeNumberField(2);

    // tfUsername.setText(user.getUserFullName());
    tfFName.setText(user.getUserFirstName());
    tfLName.setText(user.getUserLastName());
    tfWeight.setText(String.valueOf(user.getUserWeight()));
    tfHeight.setText(String.valueOf(user.getUserHeight()));
    tfAge.setText(String.valueOf(user.getUserAge()));

    // JPasswordFields
    JPasswordField tfPassword = makePasswordField(20);
    JPasswordField tfPasswordConfirm = makePasswordField(20);

    // JComboBoxes
    JComboBox cbPrefix = makeComboBox(getPrefixes());
    JComboBox cbGender = makeComboBox(getGenders());

    cbPrefix.setSelectedIndex(getPrefixIndex(user.getUserPrefix()));
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
    panelSub.add(btnRemoveAccount);
    setPadding(panelSub, 10, 0, 8);
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
    setPadding(panelSub, 10, 0, 0);
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

    // Password Changing Frame
    JFrame passwordEditFrame = new JFrame("เปลี่ยนรหัสผ่าน");
    JPanel panel = new JPanel(new BorderLayout());
    JPanel panelPasswordEdit = new JPanel(new GridLayout(4, 2));

    JPasswordField oldPasswordField = makePasswordField(20);
    JPasswordField newPasswordField = makePasswordField(20);
    JPasswordField confirmNewPasswordField = makePasswordField(20);

    JLabel oldPasswordLabel = makeLabel("รหัสผ่านปัจจุบัน");
    JLabel newPasswordLabel = makeLabel("รหัสผ่านใหม่");
    JLabel confirmNewPasswordLabel = makeLabel("ยืนยันรหัสผ่านใหม่");

    JButton passwordConfirmButton = makeBlueButton("ยืนยัน");

    setPadding(panel, 20);

    panelPasswordEdit.add(oldPasswordLabel);
    panelPasswordEdit.add(oldPasswordField);
    panelPasswordEdit.add(newPasswordLabel);
    panelPasswordEdit.add(newPasswordField);
    panelPasswordEdit.add(confirmNewPasswordLabel);
    panelPasswordEdit.add(confirmNewPasswordField);

    panel.add(panelPasswordEdit, BorderLayout.CENTER);
    panel.add(passwordConfirmButton, BorderLayout.SOUTH);

    passwordEditFrame.add(panel);
    passwordEditFrame.setMinimumSize(new Dimension(480, 240));
    passwordEditFrame.setSize(new Dimension(480, 270));
    passwordEditFrame.setVisible(false);
    passwordEditFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    passwordEditFrame.setLocationRelativeTo(null);

    // Password Changing
    btnEditPwd.addActionListener(e -> {
      passwordEditFrame.setVisible(true);
    });

    passwordConfirmButton.addActionListener(ev -> {
      try {
        Login.doSignIn(user.getUserName(), oldPasswordField.getPassword());
        if (!Arrays.equals(newPasswordField.getPassword(), confirmNewPasswordField.getPassword())) {
          fireErrorDialog("รหัสผ่านใหม่ทั้งสองช่องไม่ตรงกัน");
        } else if (newPasswordField.getPassword().length < 6) {
          fireErrorDialog("รหัสผ่านใหม่ต้องมีความยาวตั้งแต่ 6 ตัวอักษรขึ้นไป");
        } else if (Arrays.equals(oldPasswordField.getPassword(), newPasswordField.getPassword())) {
          fireErrorDialog("รหัสผ่านใหม่ไม่สามารถเป็นรหัสเดิมได้");
        } else {
          updateUserPassword(newPasswordField.getPassword());
          oldPasswordField.setText("");
          newPasswordField.setText("");
          confirmNewPasswordField.setText("");
          passwordEditFrame.setVisible(false);
          fireSuccessDialog("รหัสผ่านถูกเปลี่ยนเรียบร้อย");
        }
      } catch (NoSuchAlgorithmException | SQLException | LoginException | ParseException ex) {
        fireErrorDialog("รหัสผ่านปัจจุบันไม่ถูกต้อง");
      }
    });

    // Remove account
    btnRemoveAccount.addActionListener(e -> {
      int result = fireConfirmDialog(
          "คุณต้องการลบบัญชีนี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถกู้คืนบัญชีนี้กลับมาได้อีก");
      if (result == JOptionPane.YES_OPTION) {
        try {
          deleteUser();
          fireSuccessDialog("บัญชีของคุณถูกลบเรียบร้อยแล้ว");
          backTo("ยังไม่ได้เข้าสู่ระบบ");
        } catch (SQLException e1) {
          e1.printStackTrace();
          fireDBErrorDialog();
        }
      }
    });

    // Update user information
    btnSave.addActionListener(e -> {
      String fName = tfFName.getText();
      String lName = tfLName.getText();
      String age = tfAge.getText();
      String weight = tfWeight.getText();
      String height = tfHeight.getText();
      if (fName.equals("") || lName.equals("") || age.equals("") || weight.equals("") || height
          .equals("")) {
        fireErrorDialog("คุณกรอกข้อมูลไม่ครบถ้วน");
      } else {
        user.setUserPrefix(cbPrefix.getSelectedItem().toString());
        user.setUserFirstName(fName);
        user.setUserLastName(lName);
        user.setUserGender(cbGender.getSelectedItem().toString());
        user.setUserAge(Integer.parseInt(age));
        user.setUserWeight(Double.parseDouble(weight));
        user.setUserHeight(Double.parseDouble(height));

        try {
          UserDB.updateUserData();
          fireSuccessDialog("บันทึกข้อมูลสำเร็จ");
          panelRight.remove(panelSettings);
          panelSettings();
          backTo("การตั้งค่า");
          panelRight.remove(panelMain);
          panelRight.add(panelMain, "แก้ไขข้อมูลส่วนตัว");
        } catch (SQLException ex) {
          ex.printStackTrace();
          fireDBErrorDialog();
        }
      }
    });

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

    // Get Time from user preferences
    tpMorning.setText(getUser().getUserTime()[0]);
    tpAfternoon.setText(getUser().getUserTime()[1]);
    tpEvening.setText(getUser().getUserTime()[2]);
    tpBed.setText(getUser().getUserTime()[3]);

    // JLabels
    JLabel labelDescription = makeLabel(
        "ตั้งค่าเวลาทานยาของคุณ ระบบจะทำการแจ้งเตือนให้ทานยาก่อนเวลาที่ท่่านได้กำหนดไว้ 10 นาที");
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

    // Save Configured Time
    btnSave.addActionListener(e -> {
      getUser().setUserTime(
          new String[]{tpMorning.getText(), tpAfternoon.getText(), tpEvening.getText(),
              tpBed.getText()});
      try {
        UserDB.updateUserTime();
        fireSuccessDialog("บันทึกเวลาสำเร็จ");
        backTo("การตั้งค่า");
        panelRight.remove(panelOverview);
        panelRight.remove(panelMain);
        panelOverview();
        panelRight.add(panelMain, "ตั้งค่าเวลา");
      } catch (SQLException ex) {
        ex.printStackTrace();
        fireDBErrorDialog();
      }
    });

    panelRight.add(panelMain, "ตั้งค่าเวลา");
  }

  private static void panelAbout() {

    // JPanels
    JPanel panelMain = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelBody = new JPanel();

    // JButtons
    JButton btnBack = makeBackButton("เกี่ยวกับ", "การตั้งค่า");

    // JLabels
    JLabel labelHeading1 = makeBoldLabel("คณะผู้จัดทำ");
    JLabel labelHeading2 = makeBoldLabel("ลิขสิทธิ์");
    JLabel labelHeading3 = makeBoldLabel("ขอบคุณ");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelMain, -11, 0, 20, -18);
    setPadding(panelBody, 0, 20, 10, 28);
    setPadding(panelTitle, 0, 0, 20);

    // Panel Title
    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(labelHeading1);
    panelBody.add(panelSub);
    panelBody.add(new JSeparator());

    panelSub = new JPanel(new GridLayout(5, 1));
    panelSub.add(makeLabel("1. นายวิพุธ ภู่ทอง (60070090)"));
    panelSub.add(makeLabel("2. นายสาคร เสาแก้ว (60070102)"));
    panelSub.add(makeLabel("3. นายธีรภัทร ไกรศรีสิริกุล (60070183)"));
    panelSub.add(makeLabel("นักศึกษาชั้นปีที่ 2 คณะเทคโนโลยีสารสนเทศ"));
    panelSub.add(makeLabel("สถาบันเทคโนโลยีพระจอมเกล้าเจ้าคุณทหารลาดกระบัง"));
    setPadding(panelSub, 10, 0, 20, 4);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelHeading2);
    panelBody.add(panelSub);
    panelBody.add(new JSeparator());

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("jMedicine เป็นโปรเจกต์แบบ Open Source บน Apache License 2.0"));
    setPadding(panelSub, 10, 0, 20, 0);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelHeading3);
    panelBody.add(panelSub);
    panelBody.add(new JSeparator());

    panelSub = new JPanel(new GridLayout(4, 1));
    panelSub.add(makeBoldLabel("ไอคอน success, error และ bin"));
    panelSub.add(makeLabel(
        "by Smashicons https://www.flaticon.com/authors/smashicons (is licensed by Creative Commons BY 3.0)"));
    panelSub.add(makeBoldLabel("ไอคอน warning และ spray"));
    panelSub.add(makeLabel(
        "by freepik https://www.flaticon.com/authors/freepik (is licensed by Creative Commons BY 3.0)"));
    setPadding(panelSub, 10, 0, 20, 4);
    panelBody.add(panelSub);

    panelMain.add(panelTitle, BorderLayout.NORTH);
    panelMain.add(makeScrollPane(panelBody), BorderLayout.CENTER);

    panelRight.add(panelMain, "เกี่ยวกับ");
  }

  static void promptFirstMedicine() {
    if (getUser().getUserMedicines().size() == 0) {
      panelWelcome.add(panelFirstMedicine(), "เพิ่มยาตัวแรก");
      CardLayout cl = (CardLayout) (panelWelcome.getLayout());
      cl.show(panelWelcome, "เพิ่มยาตัวแรก");
    } else {
      frameWelcome.setVisible(false);
      frameMain.setVisible(true);
      frameWelcome = null;
      CardLayout cl = (CardLayout) (panelRight.getLayout());
      cl.show(panelRight, "ภาพรวม");
    }
  }

  static JButton[] getButtons() {
    return buttons;
  }

}
