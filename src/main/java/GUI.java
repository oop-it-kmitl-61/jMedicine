// import com.teamdev.jxmaps.MapViewOptions;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
  private JTextField tfUserName;
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
    this.minSize = new Dimension(640, 480);
    this.mainBlue = new Color(20, 101, 155);
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
    panelTitle.add(makeLabelTitle(today));

    panelLoop = newPanelLoop();
    // Make Loop
    cardLoop = makeOverviewCard("12.30 น. (อีก 1 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)", "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    cardLoop = makeOverviewCard("18.30 น. (อีก 7 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)", "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    // End Make Loop
    panelSub01.add(panelTitle, BorderLayout.NORTH);
    panelSub01.add(panelLoop);

    return panelSub01;
  }

  public JPanel medicines() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeLabelTitle("ยาทั้งหมด"));

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

    return  panelSub02;
  }

  public JPanel appointments() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeLabelTitle("นัดแพทย์"));

    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มนัดใหม่"));
    // Make Loop
    cardLoop = makeAppointmentCard("1/12/2561 เวลา 09.00 น. - 16.00 น.", "นพ.เก่ง จัง โรงพยาบาลบำรุงราษฎร์");
    panelLoop.add(cardLoop);
    // End Make Loop
    panelSub03.add(panelLoop);
    panelSub03.add(panelTitle, BorderLayout.NORTH);

    return panelSub03;
  }

  public JPanel doctors() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeLabelTitle("แพทย์"));

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

    return panelSub04;
  }

  public JPanel nearbyHospitals() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeLabelTitle("โรงพยาบาลใกล้เคียง"));

//    MapViewOptions options = new MapViewOptions();
//    options.importPlaces();
//    final Maps mapView = new Maps(options);

    panelSub05.add(panelTitle, BorderLayout.NORTH);
//    panelSub05.add(mapView);

    return panelSub05;
  }

  public JPanel settings() {
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(makeLabelTitle("การตั้งค่า"));

    JPanel panelBox = new JPanel();
    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.PAGE_AXIS));
    setPadding(panelBox, 20, 0);
    JLabel labelUserName = makeLabelTitle(user.getUserName());
    panelBox.add(new JLabel("ผู้ใช้งานปัจจุบัน"));
    panelBox.add(labelUserName);
    panelSub06.add(panelTitle, BorderLayout.NORTH);
    panelSub06.add(panelBox);

    return panelSub06;
  }

  public JPanel addMedicine() {
    JPanel panelAddMedicine = new JPanel(new BorderLayout());
    JLabel labelTitle = makeLabelTitle("< เพิ่มยาใหม่");
//    JButton btnBack = makeBackButton("ยาทั้งหมด");

    makeLabelClickable(labelTitle, "ยาทั้งหมด");
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle);
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
    JLabel labelTitle = makeLabelTitle("< "+medName);
    makeLabelClickable(labelTitle, "ยาทั้งหมด");
    setPadding(labelPic, 0, 0, 10);

    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle);
    setPadding(panelTitle, 0, 0, 20);

    panelSub.add(labelPic);
    panelSub.add(new JLabel("ชื่อยา: " + medName));
    panelSub.add(new JLabel("คำอธิบาย: " + medicine.getMedDescription()));
    panelSub.add(new JLabel("เวลาที่ต้องทาน: " + medicine.getMedTime() + " " + medicine.getMedDoseStr()));
    panelSub.add(new JLabel("ขนาดรับประทาน: " + medicine.getMedDose() + " " + medicine.getMedUnit()));
    panelSub.add(new JLabel("วันที่เพิ่มยา: " + medicine.getDateAdded()));
    panelSub.add(new JLabel("จำนวนยาเริ่มต้น: " + medicine.getMedRemaining()));
    panelSub.add(new JLabel("จำนวนยาที่เหลือ: " + medicine.getMedRemaining()));
    panelSub.add(new JLabel("วันหมดอายุ: " + medicine.getMedEXP()));

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(panelSub);

    return panelView;
  }

  public JPanel addDoctor() {
    JPanel panelAddDoctor = new JPanel();
    JPanel panelSub = new JPanel();
    panelSub.setLayout(new BoxLayout(panelSub, BoxLayout.PAGE_AXIS));
    JLabel labelTitle = makeLabelTitle("< เพิ่มแพทย์ใหม่");
    makeLabelClickable(labelTitle, "แพทย์");
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle);
    setPadding(panelTitle, 0, 0, 20);

    JTextField tfDoctorName = new JTextField();
    JTextField tfDoctorWard = new JTextField();
    JTextField tfDoctorHospital = new JTextField();
    String[] prefixes = {"นพ.", "พญ.", "ศ.นพ", "ผศ.นพ"};
    JComboBox<String> cbPrefix = new JComboBox<>(prefixes);
    //TODO: ทำให้เพิ่มวันและเวลาที่แพทย์เข้าตรวจได้
    //แนวคิด: ทำ JCheckBox ให้ครบทุกวัน (จันทร์-เสาร์) ให้ติ๊กเลือก ถ้าติ๊กช่องไหนก็ใส่เวลาลง textfield ของวันนั้นเข้าไปด้วย

    panelSub.add(new JLabel("คำนำหน้า"));
    panelSub.add(cbPrefix);
    panelSub.add(new JLabel("ชื่อ-สกุล"));
    panelSub.add(tfDoctorName);
    panelSub.add(new JLabel("แผนก"));
    panelSub.add(tfDoctorWard);
    panelSub.add(new JLabel("ชื่อสถานพยาบาล"));
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
    JLabel labelTitle = makeLabelTitle("< "+doctorName);
    makeLabelClickable(labelTitle, "แพทย์");

    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle);
    setPadding(panelTitle, 0, 0, 20);


    panelSub.add(new JLabel("ชื่อแพทย์: " + doctorName));
    panelSub.add(new JLabel("แผนก: " + doctor.getWard()));
    panelSub.add(new JLabel("โรงพยาบาล: " + doctor.getHospital()));
    panelSub.add(new JLabel("เวลาเข้าตรวจ: " + doctor.getWorkTime()));

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(panelSub);

    return panelView;
  }

  public void initWelcome() {
    frameWelcome = new JFrame("jMedicine: ตั้งค่าครั้งแรก");
    JLabel space = new JLabel();
    JLabel labelWelcomeSub = new JLabel("กรอกชื่อของคุณเพื่อเริ่ม");
    tfUserName = new JTextField(20);
    JButton btnNext = new JButton("ถัดไป");
    panelWelcome = new JPanel(new CardLayout());
    panelYourName = new JPanel(new GridBagLayout());
    panelFirstMed = new JPanel();

    // Welcome Panel
    JLabel labelWelcome = makeLabelTitle("ยินดีต้อนรับ");
    makeLabelCenter(labelWelcome);
    makeLabelCenter(labelWelcomeSub);

    btnNext.addActionListener(this);

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
    setPadding(btnNext, 10, 0, 0);
    panelBox.add(labelWelcome);
    panelBox.add(labelWelcomeSub);
    panelYourName.add(panelBox, gbc);
    gbc.gridy = 2;
    panelYourName.add(tfUserName, gbc);
    gbc.gridy = 3;
    panelYourName.add(btnNext, gbc);
    space = new JLabel();
    gbc.weighty = 300;
    panelYourName.add(space, gbc);

    // FirstMed Panel
    JLabel labelTitle = makeLabelTitle("เพิ่มยาตัวแรกของคุณ");
    JButton btnSkip = new JButton("ข้ามขั้นตอนนี้");
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

    panelWelcome.add(panelYourName);
    panelWelcome.add(panelFirstMed, "ถัดไป");

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
        new JButton("ภาพรวม"),
        new JButton("ยาทั้งหมด"),
        new JButton("นัดแพทย์"),
        new JButton("แพทย์"),
        new JButton("โรงพยาบาลใกล้เคียง"),
        new JButton("การตั้งค่า"),
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
    JLabel labelTime = new JLabel(time);
    JLabel labelMed = new JLabel(medName);
    JLabel labelAmount = new JLabel(dose);
    makeLabelCenter(labelTime);
    makeLabelCenter(labelMed);
    makeLabelCenter(labelAmount);
    setPadding(labelTime, 5, 0, 5, 0);
    makeLabelBold(labelTime);
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
        CardLayout cl = (CardLayout)(panelRight.getLayout());
        cl.show(panelRight, href);
      }
    });
  }

  public JButton makeBackButton(String backTo) {
    JButton button = new JButton("< กลับ");
    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout)(panelRight.getLayout());
        cl.show(panelRight, backTo);
      }
    });
    return button;
  }

  public JPanel makeNewButton(String btnName) {
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, 0);

    JButton btnNew = new JButton(btnName);
    try {
      Image img = ImageIO.read(new File("src/main/img/add.png"));
      btnNew.setIcon(new ImageIcon(img));
    } catch (Exception ex) {
      System.out.println(ex);
    }
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
    btnNew.setAlignmentX(Component.LEFT_ALIGNMENT);
    panelLoopInfo.add(btnNew);

    return panelLoopInfo;
  }

  public JPanel makeMedCard(Medicine medicine) {
    String medTitle = medicine.getMedName()+" ("+medicine.getMedDescription()+")";
    String medShortInfo = "เหลืออยู่ "+medicine.getMedRemaining()+" "+medicine.getMedUnit()+" หมดอายุ "+medicine.getMedEXP();
    JLabel labelTitle = new JLabel(medTitle);
    JLabel labelShortInfo = new JLabel(medShortInfo);
    JLabel labelPic = medUtil.getMedIcon(medicine);
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, 0);

    JPanel panelPic = new JPanel();
    panelPic.setLayout(new BoxLayout(panelPic, BoxLayout.X_AXIS));

    JPanel panelInfo = new JPanel();
    panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.PAGE_AXIS));

    panelPic.add(labelPic);
    panelInfo.add(labelTitle);
    panelInfo.add(labelShortInfo);

    setPadding(labelTitle, 5, 0, 5, 0);
    makeLabelBold(labelTitle);

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
    JLabel labelTitle = new JLabel(doctorName);
    String doctorShortInfo = "แผนก " + doctor.getWard() + " โรงพยาบาล" + doctor.getHospital();
    JLabel labelShortInfo = new JLabel(doctorShortInfo);
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

    setPadding(labelTitle, 5, 0, 5, 0);
    makeLabelBold(labelTitle);

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
    JLabel labelTitle = new JLabel(title);
    JLabel labelShortInfo = new JLabel(shortInfo);
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

    setPadding(labelTitle, 5, 0, 5, 0);
    makeLabelBold(labelTitle);

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
    JButton btnSave = new JButton("บันทึกยา");
    ButtonGroup rdGroup = new ButtonGroup();

    String[] medType = medUtil.getMedType();
    String[] medColor = medUtil.getMedColor();
    String[] medTime = medUtil.getMedTime();
    String[] medDoseStr = medUtil.getMedDoseStr();
    ArrayList<JCheckBox> chTime = new ArrayList<>();
    ArrayList<JRadioButton> rdAmountStr = new ArrayList<>();

    JComboBox cbMedType = new JComboBox(medType);
    JComboBox cbMedColor = new JComboBox(medColor);

    for (String time : medTime) {
      chTime.add(new JCheckBox(time));
    }

    for (String amountStr : medDoseStr) {
      JRadioButton rdItem = new JRadioButton(amountStr);
      rdAmountStr.add(rdItem);
      rdGroup.add(rdItem);
    }

    btnSave.addActionListener(this);
    panelAddMed.setLayout(new BoxLayout(panelAddMed, BoxLayout.PAGE_AXIS));
    setPadding(panelAddMed, 0, 0, 40);

    cbMedType.addActionListener(this);
    cbMedColor.addActionListener(this);

    JPanel panelInline = new JPanel(new FlowLayout());
    panelInline.add(new JLabel("ชื่อยา"));
    panelInline.add(tfMedName);
    panelInline.add(new JLabel("ประเภท"));
    panelInline.add(cbMedType);
    panelInline.add(new JLabel("สีของยา"));
    panelInline.add(cbMedColor);
    panelAddMed.add(panelInline);

    JPanel panelBorder = new JPanel(new BorderLayout());
    setPadding(panelBorder, 10, 20);
    panelBorder.add(new JLabel("คำอธิบายยา (เช่น ยาแก้ปวด)"), BorderLayout.NORTH);
    panelBorder.add(tfMedDescription);
    panelAddMed.add(panelBorder);

    panelBorder = new JPanel(new BorderLayout());
    setPadding(panelBorder, 10, 20);
    //NORTH
    panelBorder.add(new JLabel("เวลาที่ต้องรับประทาน"), BorderLayout.NORTH);

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
    panelInline.add(new JLabel("จำนวน"));
    panelInline.add(tfAmount);
    panelInline.add(new JLabel(medUnit));
    panelBorder.add(panelInline, BorderLayout.EAST);

    // SOUTH
    JPanel panelBox = new JPanel();
    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.PAGE_AXIS));
    panelInline = new JPanel(new FlowLayout());
    panelInline.add(new JLabel("จำนวนยาทั้งหมด"));
    panelInline.add(tfTotalMeds);
    panelInline.add(new JLabel(medUnit));
    panelBox.add(panelInline);
    panelInline = new JPanel(new FlowLayout());
    panelInline.add(new JLabel("วันหมดอายุ"));
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
    } else if (btnCommand.equals("ถัดไป")) {
      CardLayout cl = (CardLayout)(panelWelcome.getLayout());
      cl.show(panelWelcome, e.getActionCommand());
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

  public JLabel makeLabelTitle(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font(label.getFont().getName(), Font.BOLD, 24));
    return label;
  }

  public void makeLabelBold(JLabel label) {
    label.setFont(new Font(label.getFont().getName(), Font.BOLD, 15));
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
