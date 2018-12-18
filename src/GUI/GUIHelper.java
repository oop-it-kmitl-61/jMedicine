package GUI;

import static GUI.GUI.frameMain;
import static GUI.GUI.frameWelcome;
import static GUI.GUI.initWelcome;
import static GUI.GUI.panelRight;
import static GUI.GUI.panelWelcome;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import mdlaf.MaterialLookAndFeel;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.shadows.DropShadowBorder;
import mdlaf.utils.MaterialColors;

/**
 * Provides static methods and variables to GUI. Since the limitation of javax.swing, we have to
 * make this class ourselves to help customizing the application design.
 *
 * @author jMedicine
 * @version 1.0.0
 * @since 0.3.0
 */

public class GUIHelper {

  public static Locale locale = new Locale("th", "TH");
  public static Color mainBlue = new Color(20, 101, 155);
  public static Color secondaryBlue = new Color(68, 157, 209);

  private final static LocalDate today = LocalDate.now();
  public static DateFormat formatHM = new SimpleDateFormat("HH:mm");
  public static DateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");
  public static DateFormat formatDMY = new SimpleDateFormat("dd/MM/yyyy");
  public static DateFormat formatDatePicker = new SimpleDateFormat("MMMM d, yyyy");
  public static DateFormat formatDMYFull = new SimpleDateFormat("dd MMMM yyyy", locale);
  public static DateFormat formatDMYHM = new SimpleDateFormat("dd/MM/yyyy HH.mm");
  public static DateFormat formatTimestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");

  public static String imgPath = "src/GUI/img";
  private static String imgSuccessSrc = imgPath + "/system/success.png";
  private static String imgWarningSrc = imgPath + "/system/warning.png";

  static void setup() {
    try {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/GUI/font/THSarabunNew.ttf")));
      UIManager.setLookAndFeel(new MaterialLookAndFeel());
    } catch (UnsupportedLookAndFeelException | IOException | FontFormatException ex) {
      ex.printStackTrace();
    }
  }

  public static void fireDBErrorDialog() {
    JLabel labelMessage = makeLabel("เกิดความผิดพลาดกับฐานข้อมูล หากข้อความนี้ยังคงปรากฎ โปรดรีสตาร์ทโปรแกรมแล้วลองอีกครั้ง");
    setPadding(labelMessage, 0, 10, 0, 0);
    try {
      beep("warning");
      Icon icon = new ImageIcon(ImageIO.read(new File(imgPath + "/system/error.png")));
      JOptionPane.showMessageDialog(null, labelMessage, "ผิดพลาด", JOptionPane.INFORMATION_MESSAGE, icon);
    } catch (IOException ignored) {
      JOptionPane.showMessageDialog(null, labelMessage, "ผิดพลาด", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  public static void fireErrorDialog(String message) {
    JLabel labelMessage = makeLabel(message);
    setPadding(labelMessage, 0, 10, 0, 0);
    try {
      beep("warning");
      Icon icon = new ImageIcon(ImageIO.read(new File(imgPath + "/system/error.png")));
      JOptionPane.showMessageDialog(null, labelMessage, "ผิดพลาด", JOptionPane.INFORMATION_MESSAGE, icon);
    } catch (IOException ignored) {
      JOptionPane.showMessageDialog(null, labelMessage, "ผิดพลาด", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  public static void fireSuccessDialog(String message) {
    JLabel labelMessage = makeLabel(message);
    setPadding(labelMessage, 0, 10, 0, 0);
    try {
      beep("success");
      Icon icon = new ImageIcon(ImageIO.read(new File(imgSuccessSrc)));
      JOptionPane.showMessageDialog(null, labelMessage, "สำเร็จ", JOptionPane.INFORMATION_MESSAGE, icon);
    } catch (IOException ignored) {
      JOptionPane.showMessageDialog(null, labelMessage, "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  public static void fireInfoDialog(String message) {
    JLabel labelMessage = makeLabel(message);
    setPadding(labelMessage, 0, 10, 0, 0);
    try {
      beep("warning");
      Icon icon = new ImageIcon(ImageIO.read(new File(imgWarningSrc)));
      JOptionPane.showMessageDialog(null, labelMessage, "คำชี้แจง", JOptionPane.INFORMATION_MESSAGE, icon);
    } catch (IOException ignored) {
      JOptionPane.showMessageDialog(null, labelMessage, "คำชี้แจง", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  public static int fireConfirmDialog(String message) {
    JLabel labelMessage = makeLabel(message);
    setPadding(labelMessage, 0, 10, 0, 0);

    int dialogResult;

    try {
      beep("warning");
      Icon icon = new ImageIcon(ImageIO.read(new File(GUIHelper.imgWarningSrc)));
      dialogResult = JOptionPane.showConfirmDialog(null, labelMessage, "ยืนยันการทำรายการ", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icon);
    } catch (IOException ignored) {
      dialogResult = JOptionPane.showConfirmDialog(null, labelMessage, "ยืนยันการทำรายการ", JOptionPane.YES_NO_OPTION);
    }
    return dialogResult;
  }

  public static JLabel getSuccessIcon() {
    JLabel labelPic = makeLabel(" ");
    try {
      labelPic.setIcon(new ImageIcon(ImageIO.read(new File(imgSuccessSrc))));
    } catch (IOException ignored) {
    }
    return labelPic;
  }

  public static void beep(String type) {
    String path = "src/GUI/sounds/" + type + ".wav";
    File f = new File(path);
    try {
      AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
      Clip clip = AudioSystem.getClip();
      clip.open(audioIn);
      clip.start();
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ignored) {
    }
  }

  public static String getRemoveSuccessfulMessage(String type) {
    return "ลบ" + type + "เรียบร้อยแล้ว";
  }

  public static String getRemoveFailedMessage(String type) {
    return "มีข้อผิดพลาดเกิดขึ้น ไม่สามารถลบ" + type + "ได้";
  }

  public static JLabel getInfoPic() {
    String src = imgPath + "/system/info.png";
    JLabel labelPic = new JLabel();
    labelPic.setIcon(new ImageIcon(src));
    setPadding(labelPic, 0, 0, 6);
    return labelPic;
  }

  public static JPanel getLoadingPanel(boolean withBG) {
    String src = imgPath + "/system/loading";
    if (!withBG) {
      src += "-no";
    }
    src += "-bg.gif";

    JPanel panelLoading = new JPanel();
    JLabel labelPic = new JLabel();
    JLabel labelLoading = makeLabel("กำลังโหลด");
    setPadding(labelLoading, 4, 0, 0, 0);
    labelPic.setIcon(new ImageIcon(src));

    panelLoading.add(labelPic);
    panelLoading.add(labelLoading);

    return panelLoading;
  }

  public static JPanel getErrorPanel(String errorMessage) {
    String src = imgPath + "/system/error.png";
    JPanel panelError = new JPanel();
    JLabel labelPic = new JLabel();
    JLabel labelError = makeLabel(errorMessage);
    setPadding(labelError, 8, 0, 0, 0);

    labelPic.setIcon(new ImageIcon(src));

    panelError.add(labelPic);
    panelError.add(labelError);

    return panelError;
  }

  public static void editSwitcher(JPanel origin, JPanel switchTo) {
    origin.add(switchTo, "แก้ไข");
    CardLayout cl = (CardLayout) (origin.getLayout());
    cl.show(origin, "แก้ไข");
  }

  public static Border newCardBorder() {
    return new DropShadowBorder(UIManager.getColor("Control"), 1, 5, .3f, 16, true, true, true, true);
  }

  public static JPanel newFlowLayout() {
    return new JPanel(new FlowLayout(FlowLayout.LEFT));
  }

  public static DatePicker makeTodayPicker() {
    DatePicker datePicker = new DatePicker();
    datePicker.setDateToToday();
    return datePicker;
  }

  public static DatePicker makeDatePicker() {
    DatePickerSettings dateSettings = new DatePickerSettings();
    DatePicker datePicker = new DatePicker(dateSettings);
    dateSettings.setDateRangeLimits(today, today.plusYears(100));
    return datePicker;
  }

  public static TimePicker makeTimeNowPicker() {
    TimePickerSettings timeSettings = new TimePickerSettings(locale);
    timeSettings.initialTime = LocalTime.of(8, 00);
    timeSettings.setFormatForDisplayTime("HH:mm");
    timeSettings.setFormatForMenuTimes("HH:mm");
    timeSettings.setSizeTextFieldMinimumWidth(70);
    TimePicker timePicker = new TimePicker(timeSettings);
    timePicker.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    timePicker.setTimeToNow();

    return timePicker;
  }

  public static TimePicker makeTimePicker() {
    TimePickerSettings timeSettings = new TimePickerSettings(locale);
    timeSettings.setFormatForDisplayTime("HH:mm");
    timeSettings.setFormatForMenuTimes("HH:mm");
    timeSettings.setSizeTextFieldMinimumWidth(70);
    timeSettings.initialTime = LocalTime.of(8, 00);
    TimePicker timePicker = new TimePicker(timeSettings);
    timePicker.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));

    return timePicker;
  }

  public static JScrollPane makeScrollPane(JPanel panel) {
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    setPadding(scrollPane, 0, 0, 10);

    return scrollPane;
  }

  public static JTextField makeNumberField(int columns) {
    JTextField textField = new JTextField(columns);
    textField.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    textField.addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (((c < '0') || (c > '9')) && (c != KeyEvent.VK_BACK_SPACE) && (c != KeyEvent.VK_PERIOD)) {
          fireErrorDialog("คุณไม่สามารถกรอกตัวอักษรลงในช่องที่ต้องการตัวเลขได้");
          e.consume();
        }
      }
    });

    return textField;
  }

  public static JTextField makeTextField(int columns) {
    JTextField textField = new JTextField(columns);
    textField.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return textField;
  }

  public static JTextField makeTextField() {
    JTextField textField = new JTextField();
    textField.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return textField;
  }

  public static JPasswordField makePasswordField(int columns) {
    JPasswordField passwordField = new JPasswordField(columns);
    passwordField.setFont(new Font("TH Sarabun New", Font.PLAIN, 20));
    return passwordField;
  }

  public static JComboBox makeComboBox() {
    JComboBox comboBox = new JComboBox();
    comboBox.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    comboBox.setBackground(Color.WHITE);
    return comboBox;
  }

  public static JComboBox makeComboBox(ArrayList<ImageIcon> cbIcons) {
    Object[] icons = cbIcons.toArray();
    JComboBox comboBox = new JComboBox(icons);
    comboBox.setBackground(Color.WHITE);
    return comboBox;
  }

  public static JComboBox makeComboBox(String[] comboBoxItems) {
    JComboBox comboBox = new JComboBox(comboBoxItems);
    comboBox.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    comboBox.setBackground(Color.WHITE);
    return comboBox;
  }

  public static JRadioButton makeRadioButton(String radioButtonText) {
    JRadioButton radioButton = new JRadioButton(radioButtonText);
    radioButton.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return radioButton;
  }

  public static JCheckBox makeCheckBox(String checkBoxText, boolean isChecked) {
    JCheckBox checkBox = new JCheckBox(checkBoxText);
    checkBox.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    checkBox.setSelected(isChecked);
    return checkBox;
  }

  public static JCheckBox makeCheckBox(String checkBoxText) {
    JCheckBox checkBox = new JCheckBox(checkBoxText);
    checkBox.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return checkBox;
  }

  public static JButton makeLeftNavigationButton(String buttonText) {
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.PLAIN, 26));
    return button;
  }

  public static void backTo(String backTo) {
    /* Navigates user back to some page */
    if (backTo.equals("ลงทะเบียน") || backTo.equals("ยังไม่ได้เข้าสู่ระบบ")) {
      if (frameWelcome == null) {
        initWelcome();
      }
      frameWelcome.setVisible(true);
      CardLayout cl = (CardLayout) (panelWelcome.getLayout());
      cl.show(panelWelcome, backTo);
      try {
        frameMain.setVisible(false);
      } catch (NullPointerException ex) {
        ex.printStackTrace();
      }
    } else {
      CardLayout cl = (CardLayout) (panelRight.getLayout());
      cl.show(panelRight, backTo);
    }
  }

  public static void makeLabelClickable(JLabel label, String href) {
    /* Works like <a> */
    label.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        backTo(href);
      }
    });
  }

  public static JPanel makeNewButton(String btnName) {
    /* Creates a new button. */
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, -16);

    JButton btnNew = new JButton(btnName);
    try {
      btnNew.setIcon(new ImageIcon(ImageIO.read(new File(imgPath + "/system/add.png"))));
    } catch (IOException ignored) {
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

  public static JButton makeBackButton(String buttonText, String backTo) {
    /* Creates a button that will be used on the nested page. */
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.BOLD, 42));
    button.setHorizontalAlignment(SwingConstants.LEFT);

    try {
      button.setIcon(new ImageIcon(ImageIO.read(new File(imgPath + "/system/back.png"))));
    } catch (IOException ignored) {
    }

    button.setOpaque(false);
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.addActionListener(e -> {
      backTo(backTo);
    });

    return button;
  }

  public static JButton makeRemoveButton() {
    JButton button = new JButton();

    try {
      button.setIcon(new ImageIcon(ImageIO.read(new File(imgPath + "/system/remove.png"))));
    } catch (IOException ignored) {
    }

    button.setBackground(MaterialColors.RED_900);
    button.setMaximumSize(new Dimension(400, 60));
    MaterialUIMovement.add(button, MaterialColors.RED_700);

    return button;
  }

  public static JButton makeGreyToRedButton(String buttonText) {
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.PLAIN, 21));
    button.setBackground(MaterialColors.GRAY_100);
    button.setForeground(Color.BLACK);
    button.setMaximumSize(new Dimension(400, 60));
    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        button.setBackground(MaterialColors.RED_700);
        button.setForeground(Color.WHITE);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        button.setBackground(MaterialColors.GRAY_100);
        button.setForeground(Color.BLACK);
      }
    });

    return button;
  }

  public static JButton makeGreyToBlueButton(String buttonText) {
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.PLAIN, 21));
    button.setBackground(MaterialColors.GRAY_100);
    button.setForeground(Color.BLACK);
    button.setMaximumSize(new Dimension(400, 60));
    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        button.setBackground(mainBlue);
        button.setForeground(Color.WHITE);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        button.setBackground(MaterialColors.GRAY_100);
        button.setForeground(Color.BLACK);
      }
    });

    return button;
  }

  public static JButton makeRedButton(String buttonText) {
    return makeButton(buttonText, MaterialColors.RED_900, MaterialColors.RED_700);
  }

  public static JButton makeBlueButton(String buttonText) {
    return makeButton(buttonText, mainBlue, MaterialColors.LIGHT_BLUE_600);
  }

  private static JButton makeButton(String buttonText, Color bgColor, Color hoverColor) {
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);
    button.setMaximumSize(new Dimension(400, 60));
    MaterialUIMovement.add(button, hoverColor);

    return button;
  }

  public static JToggleButton makeToggle(String toggleText, boolean selected) {
    JToggleButton toggleButton = new JToggleButton(toggleText, selected);
    toggleButton.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return toggleButton;
  }

  public static JLabel makeTitleLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.BOLD, 42));
    return label;
  }

  public static JLabel makeSubTitleLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.BOLD, 32));
    return label;
  }

  public static JLabel makeBoldLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.BOLD, 26));
    return label;
  }

  public static JLabel makeLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return label;
  }

  public static JLabel makeSmallerLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.PLAIN, 22));
    return label;
  }

  public static void makeLabelCenter(JLabel label) {
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
  }

  public static JPanel newPanelLoop() {
    JPanel panelLoop = new JPanel();
    panelLoop.setLayout(new BoxLayout(panelLoop, BoxLayout.PAGE_AXIS));
    setPadding(panelLoop, 20, 0, 5, 5);
    return panelLoop;
  }

  public static void paintButton() {
    /* Handles color painting on the left navigation. */
    for (JButton button : GUI.getButtons()) {
      button.setFont(new Font("TH Sarabun New", Font.PLAIN, 26));
      button.setBorderPainted(false);
      button.setFocusPainted(false);
      button.setBackground(mainBlue);
      button.setOpaque(false);
      button.setForeground(Color.WHITE);
    }
  }

  public static void paintCurrentTabButton(JButton button) {
    /*
      Handles color painting on the left navigation. The current tab
      will be painted in white.
     */
    button.setFont(new Font("TH Sarabun New", Font.BOLD, 26));
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setBackground(Color.WHITE);
    button.setOpaque(true);
    button.setForeground(Color.BLACK);
  }

  public static void setPadding(JLabel object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public static void setPadding(JLabel object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  public static void setPadding(JLabel object, int topAndBottom, int leftAndRight) {
    object.setBorder(
            BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  public static void setPadding(JLabel object, int allSidesAmount) {
    object.setBorder(BorderFactory
            .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  public static void setPadding(JTextField object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public static void setPadding(JTextField object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  public static void setPadding(JTextField object, int topAndBottom, int leftAndRight) {
    object.setBorder(
            BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  public static void setPadding(JTextField object, int allSidesAmount) {
    object.setBorder(BorderFactory
            .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  public static void setPadding(JPasswordField object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public static void setPadding(JPasswordField object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  public static void setPadding(JPasswordField object, int topAndBottom, int leftAndRight) {
    object.setBorder(
            BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  public static void setPadding(JPasswordField object, int allSidesAmount) {
    object.setBorder(BorderFactory
            .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  public static void setPadding(JButton object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public static void setPadding(JButton object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  public static void setPadding(JButton object, int topAndBottom, int leftAndRight) {
    object.setBorder(
            BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  public static void setPadding(JButton object, int allSidesAmount) {
    object.setBorder(BorderFactory
            .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  public static void setPadding(JPanel object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public static void setPadding(JPanel object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  public static void setPadding(JPanel object, int topAndBottom, int leftAndRight) {
    object.setBorder(
            BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  public static void setPadding(JPanel object, int allSidesAmount) {
    object.setBorder(BorderFactory
            .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  public static void setPadding(JScrollPane object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  public static void setPadding(JScrollPane object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  public static void setPadding(JScrollPane object, int topAndBottom, int leftAndRight) {
    object.setBorder(
            BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  public static void setPadding(JScrollPane object, int allSidesAmount) {
    object.setBorder(BorderFactory
            .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }
}
