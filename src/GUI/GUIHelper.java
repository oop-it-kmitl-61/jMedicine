package GUI;

import core.Medicine;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.print.attribute.standard.Media;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import mdlaf.MaterialLookAndFeel;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

public class GUIHelper {

  public static DateFormat formatHM = new SimpleDateFormat("HH.mm");
  public static DateFormat formatDMY = new SimpleDateFormat("dd/MM/yyyy");
  public static DateFormat formatDMYFull = new SimpleDateFormat("dd MMMM yyyy",
      new Locale("th", "TH"));
  public static DateFormat formatDMYHM = new SimpleDateFormat("dd/MM/yyyy HH.mm");
  public static DateFormat formatDMYFullHM = new SimpleDateFormat("dd MMMM yyyy HH.mm",
      new Locale("th", "TH"));

  public static String imgPath = "src/GUI/img";
  static String imgSuccessSrc = imgPath + "/system/success.png";
  static String imgWarningSrc = imgPath + "/system/warning.png";

  static void setup() {
    try {
      GraphicsEnvironment ge =
          GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(
          Font.createFont(
              Font.TRUETYPE_FONT, new File("src/GUI/font/THSarabunNew.ttf")
          )
      );
      UIManager.setLookAndFeel(new MaterialLookAndFeel());
    } catch (UnsupportedLookAndFeelException | IOException | FontFormatException ex) {
      ex.printStackTrace();
    }
  }

  static void medTimeRadioHandler(Medicine medicine, JRadioButton rbMorningBefore,
      JRadioButton rbMorningAfter, int i) {
    if (medicine.getMedDoseStr().get(i).equals("ก่อนอาหาร")) {
      rbMorningBefore.setSelected(true);
    } else if (medicine.getMedDoseStr().get(i).equals("หลังอาหาร")) {
      rbMorningAfter.setSelected(true);
    } else {
      rbMorningBefore.setSelected(true);
    }
  }

  static void medTypeUIHandler(JPanel panelColor, JPanel panelTabletColor,
      JPanel panelCapsuleColor, JPanel panelLiquidColor, JLabel labelUnit, JLabel labelUnitMorning,
      JLabel labelUnitAfternoon, JLabel labelUnitEvening, JLabel labelUnitBed,
      JComboBox cbMedType) {
    cbMedType.addActionListener(e -> {
      switch (cbMedType.getSelectedIndex()) {
        case 0:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(true);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelUnit.setText("เม็ด");
          labelUnitMorning.setText("เม็ด");
          labelUnitAfternoon.setText("เม็ด");
          labelUnitEvening.setText("เม็ด");
          labelUnitBed.setText("เม็ด");
          break;
        case 1:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(true);
          panelLiquidColor.setVisible(false);
          labelUnit.setText("แคปซูล");
          labelUnitMorning.setText("แคปซูล");
          labelUnitAfternoon.setText("แคปซูล");
          labelUnitEvening.setText("แคปซูล");
          labelUnitBed.setText("แคปซูล");
          break;
        case 2:
          panelColor.setVisible(true);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(true);
          labelUnit.setText("มิลลิลิตร");
          labelUnitMorning.setText("มิลลิลิตร");
          labelUnitAfternoon.setText("มิลลิลิตร");
          labelUnitEvening.setText("มิลลิลิตร");
          labelUnitBed.setText("มิลลิลิตร");
          break;
        default:
          panelColor.setVisible(false);
          panelTabletColor.setVisible(false);
          panelCapsuleColor.setVisible(false);
          panelLiquidColor.setVisible(false);
          labelUnit.setText("cc");
          labelUnitMorning.setText("cc");
          labelUnitAfternoon.setText("cc");
          labelUnitEvening.setText("cc");
          labelUnitBed.setText("cc");
          break;
      }
    });
  }

  static void beep(String type) {
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

  static JLabel getRemoveSuccessfulMessage(String type) {
    return makeLabel("ลบ" + type + "เรียบร้อยแล้ว");
  }

  static JLabel getRemoveFailedMessage(String type) {
    return makeLabel("ไมีข้อผิดพลาดเกิดขึ้น ไม่สามารถลบ" + type + "ได้");
  }

  static JPanel getLoadingPanel(boolean withBG) {
    String src = "src/GUI/img/system/loading";
    if (withBG) {
      src += "-bg";
    } else {
      src += "-no-bg";
    }
    src += ".gif";

    JPanel panelLoading = new JPanel();
    JLabel labelPic = new JLabel();
    JLabel labelLoading = makeLabel("กำลังโหลด");
    setPadding(labelLoading, 4, 0, 0, 0);
    try {
      ImageIcon img = new ImageIcon(src);
      labelPic.setIcon(img);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    panelLoading.add(labelPic);
    panelLoading.add(labelLoading);
    return panelLoading;
  }

  static JPanel getLoadingPanel(String loadingMessage, boolean withBG) {
    String src = "src/GUI/img/system/loading";
    if (withBG) {
      src += "-bg";
    } else {
      src += "-no-bg";
    }
    src += ".gif";

    JPanel panelLoading = new JPanel();
    JLabel labelPic = new JLabel();
    JLabel labelLoading = makeLabel(loadingMessage);
    setPadding(labelLoading, 4, 0, 0, 0);
    try {
      ImageIcon img = new ImageIcon(src);
      labelPic.setIcon(img);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    panelLoading.add(labelPic);
    panelLoading.add(labelLoading);
    return panelLoading;
  }

  static JPanel getErrorPanel(String errorMessage) {
    String src = "src/GUI/img/system/error.png";
    JPanel panelError = new JPanel();
    JLabel labelPic = new JLabel();
    JLabel labelError = makeLabel(errorMessage);
    setPadding(labelError, 8, 0, 0, 0);
    try {
      ImageIcon img = new ImageIcon(src);
      labelPic.setIcon(img);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    panelError.add(labelPic);
    panelError.add(labelError);
    return panelError;
  }

  static void saveSwitcher(JPanel origin, JPanel current, JPanel switchTo, String switchToName) {
    origin.remove(switchTo);
    origin.add(switchTo, switchToName);
    CardLayout cl = (CardLayout) (origin.getLayout());
    cl.show(origin, switchToName);
    origin.remove(current);
  }

  static void editSwitcher(JPanel origin, JPanel switchTo) {
    origin.add(switchTo, "แก้ไข");
    CardLayout cl = (CardLayout) (origin.getLayout());
    cl.show(origin, "แก้ไข");
  }

  static JPanel newFlowLayout() {
    return new JPanel(new FlowLayout(FlowLayout.LEFT));
  }

  static JScrollPane makeScrollPane(JPanel panel) {
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    setPadding(scrollPane, 0, 0, 10);
    return scrollPane;
  }

  static JTextField makeTextField(int columns) {
    JTextField textField = new JTextField(columns);
    textField.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return textField;
  }

  static JTextField makeTextField() {
    JTextField textField = new JTextField();
    textField.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return textField;
  }

  static JPasswordField makePasswordField(int columns) {
    JPasswordField passwordField = new JPasswordField(columns);
    passwordField.setFont(new Font("TH Sarabun New", Font.PLAIN, 20));
    return passwordField;
  }

  static JComboBox makeComboBox(ArrayList<ImageIcon> cbIcons) {
    Object[] icons = cbIcons.toArray();
    JComboBox comboBox = new JComboBox(icons);
    comboBox.setBackground(Color.WHITE);
    return comboBox;
  }

  static JComboBox makeComboBox(String[] comboBoxItems) {
    JComboBox comboBox = new JComboBox(comboBoxItems);
    comboBox.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    comboBox.setBackground(Color.WHITE);
    return comboBox;
  }

  static JRadioButton makeRadioButton(String radioButtonText) {
    JRadioButton radioButton = new JRadioButton(radioButtonText);
    radioButton.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return radioButton;
  }

  static JCheckBox makeCheckBox(String checkBoxText, boolean isChecked) {
    JCheckBox checkBox = new JCheckBox(checkBoxText);
    checkBox.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    checkBox.setSelected(isChecked);
    return checkBox;
  }

  static JCheckBox makeCheckBox(String checkBoxText) {
    JCheckBox checkBox = new JCheckBox(checkBoxText);
    checkBox.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return checkBox;
  }

  static JButton makeLeftNavigationButton(String buttonText) {
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.PLAIN, 26));
    return button;
  }

  static JButton makeRemoveButton() {
    JButton button = new JButton();
    try {
      Image img = ImageIO.read(new File(imgPath + "/system/remove.png"));
      button.setIcon(new ImageIcon(img));
    } catch (Exception ex) {
      System.out.println(ex);
    }
    button.setBackground(MaterialColors.RED_900);
    button.setMaximumSize(new Dimension(400, 60));
    MaterialUIMovement.add(button, MaterialColors.RED_700);
    return button;
  }

  static JButton makeButton(String buttonText) {
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.PLAIN, 26));
    button.setBackground(GUI.getMainBlue());
    button.setForeground(Color.WHITE);
    button.setMaximumSize(new Dimension(400, 60));
    MaterialUIMovement.add(button, MaterialColors.LIGHT_BLUE_600);
    return button;
  }

  static JToggleButton makeToggle(String toggleText, boolean selected) {
    JToggleButton toggleButton = new JToggleButton(toggleText, selected);
    toggleButton.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return toggleButton;
  }

  static JLabel makeTitleLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.BOLD, 42));
    return label;
  }

  static JLabel makeBoldLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.BOLD, 26));
    return label;
  }

  static JLabel makeLabel(String labelText) {
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return label;
  }

  static void makeLabelLeft(JLabel label) {
    label.setAlignmentX(Component.LEFT_ALIGNMENT);
  }

  static void makeLabelCenter(JLabel label) {
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
  }

  static JPanel newPanelLoop() {
    JPanel panelLoop = new JPanel();
    panelLoop.setLayout(new BoxLayout(panelLoop, BoxLayout.PAGE_AXIS));
    setPadding(panelLoop, 20, 0, 5, 5);
    return panelLoop;
  }

  static void paintButton() {
    /* Handles color painting on the left navigation. */
    for (JButton button : GUI.getButtons()) {
      button.setFont(new Font("TH Sarabun New", Font.PLAIN, 26));
      button.setBorderPainted(false);
      button.setFocusPainted(false);
      button.setBackground(GUI.getMainBlue());
      button.setOpaque(false);
      button.setForeground(Color.WHITE);
    }
  }

  static void paintCurrentTabButton(JButton button) {
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

  static void setPadding(JLabel object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JLabel object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  static void setPadding(JLabel object, int topAndBottom, int leftAndRight) {
    object.setBorder(
        BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  static void setPadding(JLabel object, int allSidesAmount) {
    object.setBorder(BorderFactory
        .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  static void setPadding(JTextField object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JTextField object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  static void setPadding(JTextField object, int topAndBottom, int leftAndRight) {
    object.setBorder(
        BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  static void setPadding(JTextField object, int allSidesAmount) {
    object.setBorder(BorderFactory
        .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  static void setPadding(JPasswordField object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JPasswordField object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  static void setPadding(JPasswordField object, int topAndBottom, int leftAndRight) {
    object.setBorder(
        BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  static void setPadding(JPasswordField object, int allSidesAmount) {
    object.setBorder(BorderFactory
        .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  static void setPadding(JButton object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JButton object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  static void setPadding(JButton object, int topAndBottom, int leftAndRight) {
    object.setBorder(
        BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  static void setPadding(JButton object, int allSidesAmount) {
    object.setBorder(BorderFactory
        .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  static void setPadding(JPanel object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JPanel object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  static void setPadding(JPanel object, int topAndBottom, int leftAndRight) {
    object.setBorder(
        BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  static void setPadding(JPanel object, int allSidesAmount) {
    object.setBorder(BorderFactory
        .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }

  static void setPadding(JScrollPane object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JScrollPane object, int top, int leftAndRight, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, leftAndRight, bottom, leftAndRight));
  }

  static void setPadding(JScrollPane object, int topAndBottom, int leftAndRight) {
    object.setBorder(
        BorderFactory.createEmptyBorder(topAndBottom, leftAndRight, topAndBottom, leftAndRight));
  }

  static void setPadding(JScrollPane object, int allSidesAmount) {
    object.setBorder(BorderFactory
        .createEmptyBorder(allSidesAmount, allSidesAmount, allSidesAmount, allSidesAmount));
  }
}
