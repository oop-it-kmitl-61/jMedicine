package main;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PermissionHandler;
import com.teamdev.jxbrowser.chromium.PermissionRequest;
import com.teamdev.jxbrowser.chromium.PermissionStatus;
import com.teamdev.jxbrowser.chromium.PermissionType;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;

public class GUI implements ActionListener {
  private JFrame frameWelcome, frameMain;
  private JPanel panelMain, panelLeft, panelRight;
  private JPanel panelWelcome, panelYourName, panelFirstMed;
  private JPanel panelSub01, panelSub02, panelSub03, panelSub04, panelSub05, panelSub06;
  private JPanel panelTitle, panelLoop, cardLoop;
  private JTextField tfUserName, tfPassword, tfConfirmPassword;
  private JButton buttons[];
  private Dimension windowSize, minSize;
  private Color mainBlue;
  private User user;
  private ArrayList<Medicine> userMedicines;
  private ArrayList<Doctor> userDoctors;
  private MedicineUtil medUtil;

  public GUI(Dimension windowSize) {
    this.medUtil = new MedicineUtil();
    this.windowSize = windowSize;
    this.minSize = new Dimension(800, 600);
    this.mainBlue = new Color(20, 101, 155);
    //Load font
    try {
      GraphicsEnvironment ge =
          GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/main/font/THSarabunNew.ttf")));
    } catch (IOException | FontFormatException ex) {
      ex.printStackTrace();
    }
  }

  public void init() {

    userMedicines = user.getUserMedicines();
    userDoctors = user.getUserDoctors();

    // Main Panels
    panelMain = new JPanel(new BorderLayout());
    panelLeft = new JPanel(new GridBagLayout());
    panelRight = new JPanel(new CardLayout());
    setPadding(panelLeft, 20, 0, 5, 0);
    setPadding(panelRight, 25, 20, 10, 20);

    // Panels that will be switched inside the right panel
    panelSub01 = new JPanel(new BorderLayout());
    panelSub02 = new JPanel(new BorderLayout());
    panelSub03 = new JPanel(new BorderLayout());
    panelSub04 = new JPanel(new BorderLayout());
    panelSub05 = new JPanel(new BorderLayout());
    panelSub06 = new JPanel(new BorderLayout());

    // Left navigation
    makeLeftNavigation();

    // Add all subs to the right panel
    panelRight.add(overview(), "ภาพรวม");
    panelRight.add(medicines(), "ยาทั้งหมด");
    panelRight.add(appointments(), "นัดแพทย์");
    panelRight.add(doctors(), "แพทย์");
    panelRight.add(nearbyHospitals(), "โรงพยาบาลใกล้เคียง");
    panelRight.add(settings(), "การตั้งค่า");
    panelRight.add(addMedicine(), "เพิ่มยาใหม่");
    panelRight.add(addDoctor(), "เพิ่มแพทย์ใหม่");

    // Add to main panel
    panelMain.add(panelLeft, BorderLayout.WEST);
    panelMain.add(panelRight, BorderLayout.CENTER);

    // Frame
    frameMain = new JFrame("jMedicine");
    frameMain.add(panelMain);
    frameMain.setMinimumSize(this.minSize);
    frameMain.setSize(this.windowSize);
    frameMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public JPanel overview() {
    panelTitle = new JPanel(new BorderLayout());
    DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    Date date = new Date();
    String today = dateFormat.format(date);
    panelTitle.add(makeTitleLabel(today));

    panelLoop = newPanelLoop();
    // Make Loop
    cardLoop = makeOverviewCard("12.30 น. (อีก 1 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)", "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    cardLoop = makeOverviewCard("18.30 น. (อีก 7 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)", "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    // End Make Loop
    panelSub01.add(panelTitle, BorderLayout.NORTH);
    panelSub01.add(panelLoop);
    panelSub01.setBackground(Color.WHITE);

    return panelSub01;
  }

  public JPanel medicines() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeTitleLabel("ยาทั้งหมด"));

    ArrayList<Medicine> userMedicines = user.getUserMedicines();

    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มยาใหม่"));

    if (userMedicines.isEmpty()) {

    } else {
      // Make Loop
      Iterator<Medicine> iterator = userMedicines.iterator();
      while (iterator.hasNext()) {
        Medicine medCurrent = iterator.next();
        cardLoop = makeMedCard(medCurrent);
        panelLoop.add(cardLoop);
      }
      // End Make Loop
    }

    panelSub02.add(panelTitle, BorderLayout.NORTH);
    panelSub02.add(panelLoop);
    panelSub02.setBackground(Color.WHITE);

    return  panelSub02;
  }

  public JPanel appointments() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeTitleLabel("นัดแพทย์"));

    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มนัดใหม่"));
    // Make Loop
    cardLoop = makeAppointmentCard("1/12/2561 เวลา 09.00 น. - 16.00 น.", "นพ.เก่ง จัง โรงพยาบาลบำรุงราษฎร์");
    panelLoop.add(cardLoop);
    // End Make Loop
    panelSub03.add(panelLoop);
    panelSub03.add(panelTitle, BorderLayout.NORTH);
    panelSub03.setBackground(Color.WHITE);

    return panelSub03;
  }

  public JPanel doctors() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeTitleLabel("แพทย์"));

    ArrayList<Doctor> userDoctors = user.getUserDoctors();

    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มแพทย์ใหม่"));

    if (userDoctors.isEmpty()) {

    } else {
      // Make Loop
      Iterator<Doctor> iterator = userDoctors.iterator();
      while (iterator.hasNext()) {
        Doctor doctorCurrent = iterator.next();
        cardLoop = makeDoctorCard(doctorCurrent);
        panelLoop.add(cardLoop);
      }
      // End Make Loop
    }

    panelSub04.add(panelLoop);
    panelSub04.add(panelTitle, BorderLayout.NORTH);
    panelSub04.setBackground(Color.WHITE);

    return panelSub04;
  }

  public JPanel nearbyHospitals() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeTitleLabel("โรงพยาบาลใกล้เคียง"));
    double[] location = Maps.getLocation();

    Browser browser = new Browser();
    BrowserView view = new BrowserView(browser);
    browser.setPermissionHandler(new PermissionHandler() {
      @Override
      public PermissionStatus onRequestPermission(PermissionRequest request) {
        return PermissionStatus.GRANTED;
      }
    });
    browser.loadURL("https://www.google.co.th/maps/search/hospitals/@"+location[0]+","+location[1]+",12z");

    panelSub05.add(panelTitle, BorderLayout.NORTH);
    panelSub05.add(view);

    return panelSub05;
  }

  public JPanel settings() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeTitleLabel("การตั้งค่า"));

    JPanel panelBox = new JPanel();
    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.PAGE_AXIS));
    setPadding(panelBox, 20, 0);
    JLabel labelUserName = makeTitleLabel(user.getUserName());
    panelBox.add(makeLabel("ผู้ใช้งานปัจจุบัน"));
    panelBox.add(labelUserName);
    panelSub06.add(panelTitle, BorderLayout.NORTH);
    panelSub06.add(panelBox);
    panelSub06.setBackground(Color.WHITE);

    return panelSub06;
  }

  public JPanel addMedicine() {
    JPanel panelAddMedicine = new JPanel(new BorderLayout());
    JButton btnBack = makeBackButton("เพิ่มยาใหม่", "ยาทั้งหมด");
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(btnBack);
    setPadding(panelTitle, 0, 0, 20);

    JPanel panelBox = new JPanel();

    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.X_AXIS));
    setPadding(panelAddMedicine, 0, 0, 40, 0);
    setPadding(panelBox, 0, 0, 20);

    panelAddMedicine.add(panelTitle, BorderLayout.NORTH);
    panelAddMedicine.add(addMedGUI());

    return panelAddMedicine;
  }

  public JPanel viewMedicine(Medicine medicine) {
    String medName = medicine.getMedName();
    JLabel labelPic = medUtil.getMedIcon(medicine);
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelSub = new JPanel();
    panelSub.setLayout(new BoxLayout(panelSub, BoxLayout.PAGE_AXIS));
    JButton labelTitle = makeBackButton(medName, "ยาทั้งหมด");
    setPadding(labelPic, 0, 0, 10);

    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle);
    panelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
    setPadding(panelTitle, 0, 0, 20);
    setPadding(panelSub, 0, 0, 0, 45);

    StringBuilder sbMedTime = new StringBuilder();
    for (String medTime : medicine.getMedTime()) {
      sbMedTime.append(medTime + " ");
    }

    StringBuilder sbDoseStr = new StringBuilder();
    for (String dose : medicine.getMedDoseStr()) {
      sbDoseStr.append(dose + " ");
    }

    panelSub.add(labelPic);
    panelSub.add(makeLabel("ชื่อยา: " + medName));
    panelSub.add(makeLabel("คำอธิบาย: " + medicine.getMedDescription()));
    panelSub.add(makeLabel("เวลาที่ต้องทาน: " + sbMedTime + " " + sbDoseStr));
    panelSub.add(makeLabel("ขนาดรับประทาน: " + medicine.getMedDose() + " " + medicine.getMedUnit()));
    panelSub.add(makeLabel("วันที่เพิ่มยา: " + medicine.getDateAdded()));
    panelSub.add(makeLabel("จำนวนยาเริ่มต้น: " + medicine.getMedRemaining()));
    panelSub.add(makeLabel("จำนวนยาที่เหลือ: " + medicine.getMedRemaining()));
    panelSub.add(makeLabel("วันหมดอายุ: " + medicine.getMedEXP()));

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(panelSub);

    return panelView;
  }

  public JPanel addDoctor() {
    JPanel panelAddDoctor = new JPanel();
    JPanel panelSub = new JPanel();
    panelSub.setLayout(new BoxLayout(panelSub, BoxLayout.PAGE_AXIS));
    JButton btnBack = makeBackButton("เพิ่มแพทย์ใหม่", "แพทย์");
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(btnBack);
    setPadding(panelTitle, 0, 0, 20);

    JTextField tfDoctorName = new JTextField();
    JTextField tfDoctorWard = new JTextField();
    JTextField tfDoctorHospital = new JTextField();
    String[] prefixes = {"นพ.", "พญ.", "ศ.นพ", "ผศ.นพ"};
    JComboBox<String> cbPrefix = makeComboBox(prefixes);
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

  public JPanel viewDoctor(Doctor doctor) {
    String doctorName = doctor.getPrefix() + " " + doctor.getName();
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelSub = new JPanel();
    panelSub.setLayout(new BoxLayout(panelSub, BoxLayout.PAGE_AXIS));
    JButton btnBack = makeBackButton(doctorName, "แพทย์");
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(btnBack);
    setPadding(panelTitle, 0, 0, 20);
    setPadding(panelSub, 0, 0, 0, 45);

    panelSub.add(makeLabel("ชื่อแพทย์: " + doctorName));
    panelSub.add(makeLabel("แผนก: " + doctor.getWard()));
    panelSub.add(makeLabel("โรงพยาบาล: " + doctor.getHospital()));
    panelSub.add(makeLabel("เวลาเข้าตรวจ:"));

    for (String workTime:doctor.getWorkTime()) {
      JLabel labelWorkTime = makeLabel(workTime);
      setPadding(labelWorkTime, 0, 20);
      panelSub.add(labelWorkTime);
    }

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(panelSub);

    return panelView;
  }

  public void initWelcome() {
    frameWelcome = new JFrame("jMedicine: ตั้งค่าครั้งแรก");
    JLabel space = new JLabel();
    JLabel labelWelcomeSub = makeLabel("เข้าสู่ระบบเพื่อ Sync ข้อมูลของคุณทุกที่ ทุกเวลา");
    JLabel labelRegister = makeLabel("ยังไม่มีบัญชี? ลงทะเบียนที่นี่");
    JLabel labelUsername = makeLabel("Username");
    JLabel labelPassword = makeLabel("Password");
    makeLabelClickable(labelRegister, "ลงทะเบียน");
    tfUserName = new JTextField(20);
    tfPassword = new JTextField(20);
    JButton btnSignIn = makeButton("เข้าสู่ระบบ");
    panelWelcome = new JPanel(new CardLayout());
    panelYourName = new JPanel(new GridBagLayout());
    panelFirstMed = new JPanel();

    // Welcome Panel
    JLabel labelWelcome = makeTitleLabel("ยินดีต้อนรับ");
    makeLabelCenter(labelWelcome);
    makeLabelCenter(labelWelcomeSub);

    btnSignIn.addActionListener(this);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.weighty = 1000;
    gbc.gridy = 0;
    panelYourName.add(space, gbc);
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
    panelYourName.add(panelBox, gbc);
    gbc.gridy = 2;
    panelYourName.add(labelUsername, gbc);
    gbc.gridy = 3;
    panelYourName.add(tfUserName, gbc);
    gbc.gridy = 4;
    panelYourName.add(labelPassword, gbc);
    gbc.gridy = 5;
    panelYourName.add(tfPassword, gbc);
    gbc.gridy = 6;
    panelYourName.add(btnSignIn, gbc);
    gbc.gridy = 7;
    panelYourName.add(labelRegister, gbc);
    space = new JLabel();
    gbc.weighty = 300;
    panelYourName.add(space, gbc);

    // Sign Up Panel
    JPanel panelSignUp = new JPanel();
    panelSignUp.setLayout(new BoxLayout(panelSignUp, BoxLayout.PAGE_AXIS));
    labelUsername = makeLabel("Username");
    labelPassword = makeLabel("Password");
    tfUserName = new JTextField(20);
    tfPassword = new JTextField(20);
    tfConfirmPassword = new JTextField(20);
    JButton btnSignUp = makeButton("ลงทะเบียน");
    JPanel panelSub = new JPanel();
    panelSub.setLayout(new BoxLayout(panelSub, BoxLayout.PAGE_AXIS));
    JButton btnTitle = makeBackButton("ลงทะเบียน", "ยังไม่ได้เข้าสู่ระบบ");

    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(btnTitle);
    panelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
    setPadding(panelTitle, 0, 0, 20);
    setPadding(panelSub, 0, 45, 200, 25);
    setPadding(btnSignUp, 20, 0, 0, 0);

    panelSub.add(labelUsername);
    panelSub.add(tfUserName);
    panelSub.add(labelPassword);
    panelSub.add(tfPassword);
    panelSub.add(makeLabel("กรอก Password อีกครั้ง"));
    panelSub.add(tfConfirmPassword);
    panelSub.add(btnSignUp);

    panelSignUp.add(panelTitle, BorderLayout.NORTH);
    panelSignUp.add(panelSub);

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

    panelWelcome.add(panelYourName, "ยังไม่ได้เข้าสู่ระบบ");
    panelWelcome.add(panelFirstMed, "เพิ่มยาตัวแรก");
    panelWelcome.add(panelSignUp, "ลงทะเบียน");

    frameWelcome.add(panelWelcome);
    frameWelcome.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frameWelcome.setMinimumSize(this.minSize);
    frameWelcome.setSize(this.windowSize);
    frameWelcome.setVisible(true);
  }

  public void paintButton() {
    for(JButton button: buttons) {
      button.setBorderPainted(false);
      button.setBackground(mainBlue);
      button.setOpaque(false);
      button.setForeground(Color.WHITE);
    }
  }

  public void paintCurrentTabButton(JButton button) {
    button.setBackground(Color.WHITE);
    button.setOpaque(true);
    button.setForeground(Color.BLACK);
  }

  public void makeLeftNavigation() {
    buttons = new JButton[] {
        makeButton("ภาพรวม"),
        makeButton("ยาทั้งหมด"),
        makeButton("นัดแพทย์"),
        makeButton("แพทย์"),
        makeButton("โรงพยาบาลใกล้เคียง"),
        makeButton("การตั้งค่า"),
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
      button.addActionListener(new ActionListener() {
        // Switch between sub panels
        @Override
        public void actionPerformed(ActionEvent e) {
          CardLayout cl = (CardLayout)(panelRight.getLayout());
          cl.show(panelRight, e.getActionCommand());
          paintButton();
          if (e.getActionCommand() == button.getText()) {
            paintCurrentTabButton(button);
          }
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

  public JPanel makeOverviewCard(String time, String medName, String dose) {
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

  public void makeLabelClickable(JLabel label, String href) {
    label.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        if (href.equals("ลงทะเบียน")) {
          CardLayout cl = (CardLayout)(panelWelcome.getLayout());
          cl.show(panelWelcome, "ลงทะเบียน");
        } else {
          CardLayout cl = (CardLayout)(panelRight.getLayout());
          cl.show(panelRight, href);
        }
      }
    });
  }

  public JButton makeBackButton(String buttonText, String backTo) {
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.BOLD, 42));
    button.setHorizontalAlignment(SwingConstants.LEFT);
    try {
      Image img = ImageIO.read(new File("src/main/img/back.png"));
      button.setIcon(new ImageIcon(img));
    } catch (Exception ex) {
      System.out.println(ex);
    }
    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (backTo.equals("ยังไม่ได้เข้าสู่ระบบ")) {
          CardLayout cl = (CardLayout)(panelWelcome.getLayout());
          cl.show(panelWelcome, "ยังไม่ได้เข้าสู่ระบบ");
        } else {
          CardLayout cl = (CardLayout)(panelRight.getLayout());
          cl.show(panelRight, backTo);
        }
      }
    });
    return button;
  }

  public JPanel makeNewButton(String btnName) {
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, -16);

    JButton btnNew = new JButton(btnName);
    try {
      Image img = ImageIO.read(new File("src/main/img/add.png"));
      btnNew.setIcon(new ImageIcon(img));
    } catch (Exception ex) {
      System.out.println(ex);
    }
    btnNew.setAlignmentX(Component.LEFT_ALIGNMENT);
    btnNew.setHorizontalAlignment(SwingConstants.LEFT);
    btnNew.setFont(new Font("TH Sarabun New", Font.BOLD, 28));
    btnNew.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout)(panelRight.getLayout());
        cl.show(panelRight, e.getActionCommand());
      }
    });
    btnNew.setOpaque(false);
    btnNew.setContentAreaFilled(false);
    btnNew.setBorderPainted(false);
    panelLoopInfo.add(btnNew);
    panelLoopInfo.add(Box.createHorizontalGlue());

    return panelLoopInfo;
  }

  public JPanel makeMedCard(Medicine medicine) {
    String medTitle = medicine.getMedName()+" ("+medicine.getMedDescription()+")";
    String medShortInfo = "เหลืออยู่ "+medicine.getMedRemaining()+" "+medicine.getMedUnit()+" หมดอายุ "+medicine.getMedEXP();
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
        panelRight.add(viewMedicine(medicine), medicine.getMedName());
        CardLayout cl = (CardLayout)(panelRight.getLayout());
        cl.show(panelRight, medicine.getMedName());
      }
    });

    return panelLoopInfo;
  }

  public JPanel makeDoctorCard(Doctor doctor) {
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
      Image img = ImageIO.read(new File("src/main/img/doctor.png"));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ex) {
      System.out.println(ex);
    }

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
        panelRight.add(viewDoctor(doctor), doctor.getName());
        CardLayout cl = (CardLayout)(panelRight.getLayout());
        cl.show(panelRight, doctor.getName());
      }
    });

    return panelLoopInfo;
  }

  public JPanel makeAppointmentCard(String title, String shortInfo) {
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
      Image img = ImageIO.read(new File("src/main/img/calendar.png"));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ex) {
      System.out.println(ex);
    }

    panelPic.add(labelPic);
    panelInfo.add(labelTitle);
    panelInfo.add(labelShortInfo);

    setPadding(labelTitle, 5, 0, -10, 0);
    setPadding(labelPic, 5, 10, 0, 0);

    panelLoopInfo.add(panelPic);
    panelLoopInfo.add(panelInfo);
    panelLoopInfo.add(Box.createHorizontalGlue());
    return panelLoopInfo;
  }

  public JPanel addMedGUI() {
    String medUnit = "เม็ด";
    JPanel panelAddMed = new JPanel();
    JTextField tfMedName = new JTextField(10);
    JTextField tfMedDescription = new JTextField(20);
    JTextField tfAmount = new JTextField(2);
    JTextField tfTotalMeds = new JTextField(2);
    JTextField tfMedEXP = new JTextField(10);
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
    Iterator<JCheckBox> chIterator = chTime.iterator();
    while (chIterator.hasNext()) {
      panelInline.add(chIterator.next());
    }
    panelBorder.add(panelInline, BorderLayout.WEST);

    // EAST
    panelInline = new JPanel(new FlowLayout());
    Iterator<JRadioButton> rdIterator = rdAmountStr.iterator();
    while (rdIterator.hasNext()) {
      panelInline.add(rdIterator.next());
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

  public JPanel newPanelLoop() {
    JPanel panelLoop = new JPanel();
    panelLoop.setLayout(new BoxLayout(panelLoop, BoxLayout.PAGE_AXIS));
    setPadding(panelLoop, 20, 0, 5, 5);
    return panelLoop;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String btnCommand = e.getActionCommand();
    if (btnCommand.equals("บันทึกยา") || btnCommand.equals("ข้ามขั้นตอนนี้")) {
      if (frameWelcome == null) {
        CardLayout cl = (CardLayout)(panelRight.getLayout());
        cl.show(panelRight, "ยาทั้งหมด");
      } else {
        frameWelcome.setVisible(false);
        frameMain.setVisible(true);
        frameWelcome = null;
      }
    } else if (btnCommand.equals("เข้าสู่ระบบ")) {
      CardLayout cl = (CardLayout)(panelWelcome.getLayout());
      cl.show(panelWelcome, "เพิ่มยาตัวแรก");
      String username = "";
      if (tfUserName.getText().isEmpty()) {
        username = "(ไม่ได้ตั้งชื่อ)";
      } else {
        username = tfUserName.getText();
      }
      user = new User(username);
      initSampleDoctor();
      initSampleMedicine01();
      initSampleMedicine02();
      initSampleMedicine03();
      init();
    }
  }

  public void initSampleMedicine01() {
    ArrayList<String> sampleMedTime = new ArrayList<>();
    sampleMedTime.add("เช้า");
    sampleMedTime.add("กลางวัน");
    sampleMedTime.add("เย็น");
    ArrayList<String> sampleMedDoseStr = new ArrayList<>();
    sampleMedDoseStr.add("หลังอาหาร");
    Medicine prednisolone = new Medicine("Prednisolone", "tablet", "white", "ยาแก้อักเสบ", sampleMedTime, sampleMedDoseStr, 1, 20, "31/12/2018");
    user.addUserMedicine(prednisolone);
  }

  public void initSampleMedicine02() {
    ArrayList<String> sampleMedTime = new ArrayList<>();
    sampleMedTime.add("ก่อนนอน");
    ArrayList<String> sampleMedDoseStr = new ArrayList<>();
    sampleMedDoseStr.add("");
    Medicine chlopheniramine = new Medicine("Chlopheniramine", "tablet", "yellow", "ยาแก้แพ้", sampleMedTime, sampleMedDoseStr, 1, 50, "21/12/2019");
    user.addUserMedicine(chlopheniramine);
  }

  public void initSampleMedicine03() {
    ArrayList<String> sampleMedTime = new ArrayList<>();
    sampleMedTime.add("เช้า");
    ArrayList<String> sampleMedDoseStr = new ArrayList<>();
    sampleMedDoseStr.add("หลังอาหาร");
    Medicine amoxicillin = new Medicine("Amoxicillin", "capsule", "", "ยาแก้อักเสบ", sampleMedTime, sampleMedDoseStr, 1, 7, "28/02/2019");
    user.addUserMedicine(amoxicillin);
  }

  public void initSampleDoctor() {
    ArrayList<String> workTime = new ArrayList<String>();
    workTime.add("วันพุธ เวลา 09.00 น. - 16.00 น.");
    workTime.add("วันพฤหัสบดี เวลา 09.00 น. - 16.00 น.");
    Doctor doctor = new Doctor("นพ.", "เก่ง จัง", "หู คอ จมูก", "บำรุงราษฎร์", workTime);
    user.addUserDoctor(doctor);
  }

  public JComboBox makeComboBox(String[] comboBoxItems) {
    JComboBox comboBox = new JComboBox(comboBoxItems);
    comboBox.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return comboBox;
  }

  public JRadioButton makeRadioButton(String radioButtonText) {
    JRadioButton radioButton = new JRadioButton(radioButtonText);
    radioButton.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return radioButton;
  }

  public JCheckBox makeCheckBox(String checkBoxText) {
    JCheckBox checkBox = new JCheckBox(checkBoxText);
    checkBox.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return checkBox;
  }

  public JButton makeButton(String buttonText) {
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.PLAIN, 26));
    return button;
  }

  public JLabel makeTitleLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.BOLD, 42));
    return label;
  }

  public JLabel makeBoldLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.BOLD, 26));
    return label;
  }

  public JLabel makeLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return label;
  }

  public void makeLabelLeft(JLabel label) {
    label.setAlignmentX(Component.LEFT_ALIGNMENT);
  }

  public void makeLabelCenter(JLabel label) {
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
  }

  public void setPadding(JLabel object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public void setPadding(JLabel object, int top, int right, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, bottom, right));
  }

  public void setPadding(JLabel object, int top, int right) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, top, right));
  }

  public void setPadding(JLabel object, int top) {
    object.setBorder(BorderFactory.createEmptyBorder(top, top, top, top));
  }

  public void setPadding(JTextField object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public void setPadding(JTextField object, int top, int right, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, bottom, right));
  }

  public void setPadding(JTextField object, int top, int right) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, top, right));
  }

  public void setPadding(JTextField object, int top) {
    object.setBorder(BorderFactory.createEmptyBorder(top, top, top, top));
  }

  public void setPadding(JButton object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public void setPadding(JButton object, int top, int right, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, bottom, right));
  }

  public void setPadding(JButton object, int top, int right) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, top, right));
  }

  public void setPadding(JButton object, int top) {
    object.setBorder(BorderFactory.createEmptyBorder(top, top, top, top));
  }

  public void setPadding(JPanel object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public void setPadding(JPanel object, int top, int right, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, bottom, right));
  }

  public void setPadding(JPanel object, int top, int right) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, top, right));
  }

  public void setPadding(JPanel object, int top) {
    object.setBorder(BorderFactory.createEmptyBorder(top, top, top, top));
  }
}
