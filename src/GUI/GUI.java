package GUI;

import static GUI.GUIHelper.*;
import static GUI.components.AppointmentUI.*;
import static GUI.components.DoctorUI.*;
import static GUI.components.MedicineUI.*;

import GUI.components.MedicineUI;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PermissionStatus;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import core.MedicineUtil;
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
import mdlaf.shadows.DropShadowBorder;


/**
 * All GUIs will be centralized here. GUI that needed too much methods has been moved to
 * /components
 *
 * @author jMedicine
 * @version 0.7.1
 * @since 0.1.0
 */

public class GUI {

  static JFrame frameWelcome, frameMain;
  static JPanel panelLeft;
  public static JPanel panelRight;
  static JPanel panelWelcome;
  static JPanel panelSignIn, panelLoadingSignIn, panelErrorSignIn;
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

    // JPanels
    JPanel panelOverview = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelLoop = newPanelLoop();
    String today = GUIHelper.formatDMYFull.format(new Date());
    panelTitle.add(makeTitleLabel(today));
    setPadding(panelTitle, 0, 0, 0, 2);

    // Init card loop

    // TODO: Fetch these upcoming events
    // Sample loop
    JPanel cardLoop = makeOverviewCard("12.30 น. (อีก 1 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)",
        "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    cardLoop = makeOverviewCard("18.30 น. (อีก 7 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)",
        "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    // End sample loop
    setPadding(panelLoop, 0, 0, 600, 0);

    // Add all panels into the main panel
    panelOverview.add(panelTitle, BorderLayout.NORTH);
    panelOverview.add(panelLoop);

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
        "https://www.google.co.th/maps/search/hospitals/@" + location[0] + "," + location[1]
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
    JLabel labelEdit = makeLabel("แก้ไขข้อมูลส่วนตัว");
    JLabel labelSignOut = makeLabel("ออกจากระบบ");
    JLabel labelUserName = makeTitleLabel(util.getSignedInUser().getUserName());

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
    panelSub.add(makeLabel("เวอร์ชั่น 0.7.1"));
    panelBody.add(panelSub);

    // Add all sub panels into the main panel
    panelSettings.add(panelTitle, BorderLayout.NORTH);
    panelSettings.add(panelBody);

    panelRight.add(panelSettings, "การตั้งค่า");
  }

  public static void initWelcome() {
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
    panelLoadingSignIn.setVisible(false);
    panelErrorSignIn.setVisible(false);
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
    frameWelcome.setMinimumSize(new Dimension(400, 600));
    frameWelcome.setSize(new Dimension(400, 720));
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

  private static JPanel makeOverviewCard(String time, String medName, String dose) {
    /* Creates a card that will be used on the Overview panel only. */

    // JPanels
    JPanel panelLoopInfo = new JPanel(new BorderLayout());
    JPanel panelCard = new JPanel();
    JPanel panelTime = newFlowLayout();
    JPanel panelMed = newFlowLayout();
    JPanel panelMedInfo = new JPanel();
    JPanel panelBtn = newFlowLayout();

    // JLabels
    JLabel labelPic = MedicineUtil.getMedIcon(util.getSignedInUser().getUserMedicines().get(4));
    JLabel labelTime = makeBoldLabel(time);
    JLabel labelMedName = makeBoldLabel(medName);
    JLabel labelAmount = makeSmallerLabel(dose);

    // JButtons
    JButton btnAte = makeGreyToBlueButton("ทานแล้ว");
    JButton btnSkip = makeGreyToRedButton("ข้ามเวลานี้");

    // Styling
    panelMedInfo.setLayout(new BoxLayout(panelMedInfo, BoxLayout.PAGE_AXIS));
    panelCard.setLayout(new BoxLayout(panelCard, BoxLayout.PAGE_AXIS));
    panelCard.setBorder(
        new DropShadowBorder(UIManager.getColor("Control"), 1, 5, .3f, 16, true, true, true, true));
    setPadding(labelPic, 6, 0, 0, 8);
    setPadding(labelMedName, 7, 0, -12, 0);
    setPadding(labelAmount, 0, 0, 2, 0);
    setPadding(panelLoopInfo, 0, 0, 20);
    setPadding(panelMed, 0, 0, 6, 0);
    setPadding(panelBtn, 6, 0, 0, 0);

    panelTime.add(labelTime);

    panelMedInfo.add(labelMedName);
    panelMedInfo.add(labelAmount);

    panelMed.add(labelPic);
    panelMed.add(panelMedInfo);
    panelCard.add(panelMed);

    panelBtn.add(btnAte);
    panelBtn.add(btnSkip);
    panelCard.add(new JSeparator(SwingConstants.HORIZONTAL));
    panelCard.add(panelBtn);

    panelLoopInfo.add(panelTime, BorderLayout.NORTH);
    panelLoopInfo.add(panelCard);
    panelLoopInfo.add(panelCard);
    return panelLoopInfo;
  }

  static JButton[] getButtons() {
    return buttons;
  }

}
