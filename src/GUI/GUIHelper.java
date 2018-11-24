package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

class GUIHelper {

  static JLabel getRemoveSuccessfulMessage(String type) {
    return makeLabel("ลบ"+type+"เรียบร้อยแล้ว");
  }

  static JLabel getRemoveFailedMessage(String type) {
    return makeLabel("ไมีข้อผิดพลาดเกิดขึ้น ไม่สามารถลบ"+type+"ได้");
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
    passwordField.setFont(new Font("TH Sarabun New", Font.PLAIN, 24));
    return passwordField;
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

  static JButton makeButton(String buttonText) {
    JButton button = new JButton(buttonText);
    button.setFont(new Font("TH Sarabun New", Font.PLAIN, 26));
    return button;
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
    for(JButton button: GUI.getButtons()) {
      button.setBorderPainted(false);
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
    button.setBackground(Color.WHITE);
    button.setOpaque(true);
    button.setForeground(Color.BLACK);
  }

  static void setPadding(JLabel object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JLabel object, int top, int right, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, bottom, right));
  }

  static void setPadding(JLabel object, int top, int right) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, top, right));
  }

  static void setPadding(JLabel object, int top) {
    object.setBorder(BorderFactory.createEmptyBorder(top, top, top, top));
  }

  static void setPadding(JTextField object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JTextField object, int top, int right, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, bottom, right));
  }

  static void setPadding(JTextField object, int top, int right) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, top, right));
  }

  static void setPadding(JTextField object, int top) {
    object.setBorder(BorderFactory.createEmptyBorder(top, top, top, top));
  }

  static void setPadding(JButton object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JButton object, int top, int right, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, bottom, right));
  }

  static void setPadding(JButton object, int top, int right) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, top, right));
  }

  static void setPadding(JButton object, int top) {
    object.setBorder(BorderFactory.createEmptyBorder(top, top, top, top));
  }

  static void setPadding(JPanel object, int top, int right, int bottom, int left) {
    object.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
  }

  static void setPadding(JPanel object, int top, int right, int bottom) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, bottom, right));
  }

  static void setPadding(JPanel object, int top, int right) {
    object.setBorder(BorderFactory.createEmptyBorder(top, right, top, right));
  }

  static void setPadding(JPanel object, int top) {
    object.setBorder(BorderFactory.createEmptyBorder(top, top, top, top));
  }
}
