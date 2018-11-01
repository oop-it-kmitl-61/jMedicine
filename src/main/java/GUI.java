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
  private JLabel labelToday, labelTitle01, labelTitle02, labelTitle03, labelTitle04, labelTitle05, labelTitle06;
  private JTextField tfUserName;
  private JButton buttons[];
  private Dimension windowSize, minSize;
  private Color mainBlue;
  private User user;

  public GUI(Dimension windowSize) {
    this.windowSize = windowSize;
    this.minSize = new Dimension(640, 480);
  }

  public void init() {

    //Colors
    mainBlue = new Color(20, 101, 155);

    // Labels
    DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    Date date = new Date();
    String today = dateFormat.format(date);
    labelToday = new JLabel(today);
    labelTitle01 = new JLabel("ภาพรวม");
    labelTitle02 = new JLabel("ยาทั้งหมด");
    labelTitle03 = new JLabel("นัดแพทย์");
    labelTitle04 = new JLabel("แพทย์");
    labelTitle05 = new JLabel("โรงพยาบาลใกล้เคียง");
    labelTitle06 = new JLabel("การตั้งค่า");
    JLabel titles[] = {labelToday, labelTitle01, labelTitle02, labelTitle03, labelTitle04, labelTitle05, labelTitle06};

    // Set font size for title
    for (JLabel title: titles) {
      makeLabelTitle(title);
    }

    // Buttons
    buttons = new JButton[] {
        new JButton("ภาพรวม"),
        new JButton("ยาทั้งหมด"),
        new JButton("นัดแพทย์"),
        new JButton("แพทย์"),
        new JButton("โรงพยาบาลใกล้เคียง"),
        new JButton("การตั้งค่า"),
    };

    // Main Panels
    panelMain = new JPanel(new BorderLayout());
    panelLeft = new JPanel(new GridBagLayout());
    panelRight = new JPanel(new CardLayout());
    // Panels that will be switched inside the right panel
    panelSub01 = new JPanel(new BorderLayout());
    panelSub02 = new JPanel(new BorderLayout());
    panelSub03 = new JPanel(new BorderLayout());
    panelSub04 = new JPanel(new BorderLayout());
    panelSub05 = new JPanel(new BorderLayout());
    panelSub06 = new JPanel(new BorderLayout());
    // Panels for panelSub01
    panelTitle = new JPanel(new BorderLayout());

    // Set Layout
    setPadding(panelLeft, 20, 0, 5, 0);
    setPadding(panelRight, 25, 20, 10, 20);
    panelLeft.setBackground(mainBlue);
    panelTitle.setLayout(new BorderLayout());

    // Left navigation
    makeLeftNavigation();

    // TODO: panelSub01
    panelTitle.add(labelToday);
    panelLoop = newPanelLoop();
    // Make Loop
    cardLoop = makeOverviewCard("12.30 น. (อีก 1 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)", "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    cardLoop = makeOverviewCard("18.30 น. (อีก 7 ชั่วโมง)", "Prednisolone (ยาแก้อักเสบ)", "หลังอาหาร 1 เม็ด");
    panelLoop.add(cardLoop);
    // End Make Loop
    panelSub01.add(panelTitle, BorderLayout.NORTH);
    panelSub01.add(panelLoop);

    // TODO: panelSub02
    panelTitle = new JPanel(new BorderLayout());
    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มยาใหม่"));
    // Make Loop
    cardLoop = makeDetailCard("medicine", "Prednisolone (ยาแก้อักเสบ)", "เหลืออยู่ 2 เม็ด หมดอายุ 31/12/2560");
    panelLoop.add(cardLoop);
    cardLoop = makeDetailCard("medicine", "CPM (ยาแก้แพ้)", "เหลืออยู่ 20 เม็ด หมดอายุ 20/11/2561");
    panelLoop.add(cardLoop);
    // End Make Loop
    panelTitle.add(labelTitle02);
    panelSub02.add(panelTitle, BorderLayout.NORTH);
    panelSub02.add(panelLoop);


    // TODO: panelSub03
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle03);
    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มนัดใหม่"));
    // Make Loop
    cardLoop = makeDetailCard("appointment", "1/12/2561 เวลา 09.00 น. - 16.00 น.", "นพ.เก่ง จัง โรงพยาบาลบำรุงราษฎร์");
    panelLoop.add(cardLoop);
    // End Make Loop
    panelSub03.add(panelLoop);
    panelSub03.add(panelTitle, BorderLayout.NORTH);

    // TODO: panelSub04
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle04);
    panelLoop = newPanelLoop();
    panelLoop.add(makeNewButton("เพิ่มแพทย์ใหม่"));
    // Make Loop
    cardLoop = makeDetailCard("doctor", "นพ.เก่ง จัง", "แผนกหู คอ จมูก โรงพยาบาลบำรุงราษฎร์");
    panelLoop.add(cardLoop);
    // End Make Loop
    panelSub04.add(panelLoop);
    panelSub04.add(panelTitle, BorderLayout.NORTH);

    // TODO: panelSub05
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle05);

//    MapViewOptions options = new MapViewOptions();
//    options.importPlaces();
//    final Maps mapView = new Maps(options);

    panelSub05.add(panelTitle, BorderLayout.NORTH);
//    panelSub05.add(mapView);

    // TODO: panelSub06
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle06);
    JPanel panelBox = new JPanel();
    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.PAGE_AXIS));
    setPadding(panelBox, 20, 0);
    JLabel labelUserName = new JLabel(user.getUserName());
    makeLabelTitle(labelUserName);
    panelBox.add(new JLabel("ผู้ใช้งานปัจจุบัน"));
    panelBox.add(labelUserName);
    panelSub06.add(panelTitle, BorderLayout.NORTH);
    panelSub06.add(panelBox);

    // Add all subs to the right panel
    panelRight.add(panelSub01, "ภาพรวม");
    panelRight.add(panelSub02, "ยาทั้งหมด");
    panelRight.add(panelSub03, "นัดแพทย์");
    panelRight.add(panelSub04, "แพทย์");
    panelRight.add(panelSub05, "โรงพยาบาลใกล้เคียง");
    panelRight.add(panelSub06, "การตั้งค่า");

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

  public void makeLabelTitle(JLabel label) {
    label.setFont(new Font(label.getFont().getName(), Font.BOLD, 24));
  }

  public void makeLabelBold(JLabel label) {
    label.setFont(new Font(label.getFont().getName(), Font.BOLD, 15));
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

  public void initWelcome() {
    frameWelcome = new JFrame("jMedicine: ตั้งค่าครั้งแรก");
    JLabel space = new JLabel();
    JLabel labelWelcome = new JLabel("ยินดีต้อนรับ");
    JLabel labelWelcomeSub = new JLabel("กรอกชื่อของคุณเพื่อเริ่ม");
    tfUserName = new JTextField(20);
    JButton btnNext = new JButton("ถัดไป");
    panelWelcome = new JPanel(new CardLayout());
    panelYourName = new JPanel(new GridBagLayout());
    panelFirstMed = new JPanel();
    JPanel panelInline = new JPanel(new FlowLayout());

    // Welcome Panel
    makeLabelTitle(labelWelcome);
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
    JLabel labelTitle = new JLabel("เพิ่มยาตัวแรกของคุณ (กดบันทึกข้ามไปก่อนได้เลย)");
    makeLabelTitle(labelTitle);
    setPadding(labelTitle, 0, 0, 30);

    panelFirstMed.setLayout(new BoxLayout(panelFirstMed, BoxLayout.PAGE_AXIS));
    setPadding(panelFirstMed, 80, 0, 40, 0);

    panelInline = new JPanel(new FlowLayout());
    panelInline.add(labelTitle);
    panelFirstMed.add(panelInline);
    panelFirstMed.add(addMedGUI());

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
    labelTime.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelMed.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
    setPadding(labelTime, 5, 0, 5, 0);
    makeLabelBold(labelTime);
    panelLoopInfo.add(labelTime);
    panelLoopInfo.add(labelMed);
    panelLoopInfo.add(labelAmount);
    panelLoopInfo.add(Box.createHorizontalGlue());
    return panelLoopInfo;
  }

  public JPanel makeNewButton(String btnName) {
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.PAGE_AXIS));

    JButton btnNew = new JButton(btnName);
    try {
      Image img = ImageIO.read(new File("src/main/img/add.png"));
      btnNew.setIcon(new ImageIcon(img));
    } catch (Exception ex) {
      System.out.println(ex);
    }
    btnNew.setOpaque(false);
    btnNew.setContentAreaFilled(false);
    btnNew.setBorderPainted(false);
    setPadding(btnNew, 10, 0);
    panelLoopInfo.add(Box.createHorizontalGlue());
    panelLoopInfo.add(btnNew);

    return panelLoopInfo;
  }

  // TODO: Example parameters will be replaced with data from DB
  public JPanel makeDetailCard(String type, String example01, String example02) {
    String imgURL = "";
    switch (type) {
      case "medicine":
        imgURL = "tablet.png";
        break;
      case "appointment":
        imgURL = "calendar.png";
        break;
      case "doctor":
        imgURL = "doctor.png";
        break;
    }

    JLabel labelTitle = new JLabel(example01);
    JLabel labelShortInfo = new JLabel(example02);
    JLabel labelPic = new JLabel();
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, 0);

    JPanel panelPic = new JPanel();
    panelPic.setLayout(new BoxLayout(panelPic, BoxLayout.X_AXIS));

    JPanel panelInfo = new JPanel();
    panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.PAGE_AXIS));

    try {
      Image img = ImageIO.read(new File("src/main/img/"+imgURL));
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
    MedicineUtil medUtil = new MedicineUtil();

    String medUnit = "เม็ด";
    JPanel panelAddMed = new JPanel();
    JLabel labelMedName = new JLabel("ชื่อยา");
    JLabel labelMedType = new JLabel("ประเภท");
    JLabel labelMedColor = new JLabel("สีของยา");
    JLabel labelDescription = new JLabel("คำอธิบายยา (เช่น ยาแก้ปวด)");
    JLabel labelTime = new JLabel("เวลาที่ต้องรับประทาน");
    JLabel labelAmount = new JLabel("จำนวน");
    JLabel labelTotalMeds = new JLabel("จำนวนยาทั้งหมด");
    JLabel labelEXP = new JLabel("วันหมดอายุ");
    JTextField tfMedName = new JTextField(10);
    JTextField tfMedDescription = new JTextField(20);
    JTextField tfAmount = new JTextField(2);
    JTextField tfTotalMeds = new JTextField(2);
    JTextField tfMedEXP = new JTextField(10);
    JButton btnSave = new JButton("บันทึก");
    ButtonGroup rdGroup = new ButtonGroup();

    String[] medType = medUtil.getMedType();
    String[] medColor = medUtil.getMedColor();
    String[] medTime = medUtil.getMedTime();
    String[] medAmountStr = medUtil.getMedAmountStr();
    ArrayList<JCheckBox> chTime = new ArrayList<>();
    ArrayList<JRadioButton> rdAmountStr = new ArrayList<>();

    JComboBox cbMedType = new JComboBox(medType);
    JComboBox cbMedColor = new JComboBox(medColor);

    for (String time : medTime) {
      chTime.add(new JCheckBox(time));
    }

    for (String amountStr : medAmountStr) {
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
    panelInline.add(labelMedName);
    panelInline.add(tfMedName);
    panelInline.add(labelMedType);
    panelInline.add(cbMedType);
    panelInline.add(labelMedColor);
    panelInline.add(cbMedColor);
    panelAddMed.add(panelInline);

    JPanel panelBorder = new JPanel(new BorderLayout());
    setPadding(panelBorder, 10, 20);
    panelBorder.add(labelDescription, BorderLayout.NORTH);
    panelBorder.add(tfMedDescription);
    panelAddMed.add(panelBorder);

    panelBorder = new JPanel(new BorderLayout());
    setPadding(panelBorder, 10, 20);
    panelBorder.add(labelTime, BorderLayout.NORTH);
    panelInline = new JPanel(new FlowLayout());
    Iterator<JCheckBox> chIterator = chTime.iterator();
    while (chIterator.hasNext()) {
      panelInline.add(chIterator.next());
    }
    panelBorder.add(panelInline, BorderLayout.WEST);
    panelInline = new JPanel(new FlowLayout());
    Iterator<JRadioButton> rdIterator = rdAmountStr.iterator();
    while (rdIterator.hasNext()) {
      panelInline.add(rdIterator.next());
    }
    panelInline.setPreferredSize(new Dimension(100, panelInline.getHeight()));
    panelBorder.add(panelInline, BorderLayout.CENTER);
    panelInline = new JPanel(new FlowLayout());
    panelInline.add(labelAmount);
    panelInline.add(tfAmount);
    panelInline.add(new JLabel(medUnit));
    panelBorder.add(panelInline, BorderLayout.EAST);
    JPanel panelBox = new JPanel();
    panelBox.setLayout(new BoxLayout(panelBox, BoxLayout.PAGE_AXIS));
    panelInline = new JPanel(new FlowLayout());
    panelInline.add(labelTotalMeds);
    panelInline.add(tfTotalMeds);
    panelInline.add(new JLabel(medUnit));
    panelBox.add(panelInline);
    panelInline = new JPanel(new FlowLayout());
    panelInline.add(labelEXP);
    panelInline.add(tfMedEXP);
    panelBox.add(panelInline);
    panelBorder.add(panelBox, BorderLayout.SOUTH);
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
    if (btnCommand.equals("บันทึก")) {
      frameWelcome.setVisible(false);
      frameMain.setVisible(true);
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
      init();
    }
  }
}
