package GUI.components;

import static GUI.GUIHelper.*;
import static GUI.GUI.*;
import static core.Core.getUser;
import static core.DoctorUtil.getPrefixes;

import api.DoctorDB;
import com.github.lgooddatepicker.components.TimePicker;
import core.Doctor;
import core.DoctorUtil;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * All UIs and handler methods about a doctor will be written here.
 *
 * @author jMedicine
 * @version 0.7.1
 * @since 0.7.0
 */

public class DoctorUI {

  private static JPanel panelDoctors;

  public static void panelAllDoctors() {
    /*
      Creates GUI displaying all doctors that user has had input.
      All doctors will be displayed in a card with a default icon,
      a name and a short summary.
     */

    // Init title panel displaying title label
    panelDoctors = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());
    JPanel panelLoop = newPanelLoop();

    JLabel labelTitle = makeTitleLabel("แพทย์");
    panelTitle.add(labelTitle);

    // Fetch all doctors
    ArrayList<Doctor> userDoctors = getUser().getUserDoctors();

    // Init panel loop

    panelLoop.add(makeNewButton("เพิ่มแพทย์ใหม่"));

    if (userDoctors.isEmpty()) {
      labelTitle.setText("คุณยังไม่มีแพทย์ที่บันทึกไว้");
    } else {
      labelTitle.setText("แพทย์");
      for (Doctor doctorCurrent : userDoctors) {
        JPanel cardLoop = makeDoctorCard(doctorCurrent);
        panelLoop.add(cardLoop);
      }
    }

    // Add all panels into the main panel
    panelDoctors.add(panelLoop);
    panelDoctors.add(panelTitle, BorderLayout.NORTH);
    panelDoctors.repaint();
    panelDoctors.revalidate();

    panelRight.add(panelDoctors, "แพทย์");
    panelRight.add(panelAddDoctor(), "เพิ่มแพทย์ใหม่");
  }

  private static JPanel panelAddDoctor() {
    /* Creates GUI of the form for adding a new doctor. */

    // JPanels
    JPanel panelAddDoctor = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelTitle = newFlowLayout();

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มแพทย์ใหม่", "แพทย์");
    JButton btnAdd = makeBlueButton("บันทึกแพทย์");

    // JCheckBoxes
    JCheckBox cbSunday = makeCheckBox("วันอาทิตย์");
    JCheckBox cbMonday = makeCheckBox("วันจันทร์");
    JCheckBox cbTuesday = makeCheckBox("วันอังคาร");
    JCheckBox cbWednesday = makeCheckBox("วันพุธ");
    JCheckBox cbThursday = makeCheckBox("วันพฤหัสบดี");
    JCheckBox cbFriday = makeCheckBox("วันศุกร์");
    JCheckBox cbSaturday = makeCheckBox("วันเสาร์");

    // TimePickers
    TimePicker sundayStartPicker = makeTimePicker();
    TimePicker sundayEndPicker = makeTimePicker();
    TimePicker mondayStartPicker = makeTimePicker();
    TimePicker mondayEndPicker = makeTimePicker();
    TimePicker tuesStartPicker = makeTimePicker();
    TimePicker tuesEndPicker = makeTimePicker();
    TimePicker wedStartPicker = makeTimePicker();
    TimePicker wedEndPicker = makeTimePicker();
    TimePicker thurStartPicker = makeTimePicker();
    TimePicker thurEndPicker = makeTimePicker();
    TimePicker fridayStartPicker = makeTimePicker();
    TimePicker fridayEndPicker = makeTimePicker();
    TimePicker satStartPicker = makeTimePicker();
    TimePicker satEndPicker = makeTimePicker();
    TimePicker[] timePickers = {sundayStartPicker, sundayEndPicker, mondayStartPicker,
        mondayEndPicker, tuesStartPicker, tuesEndPicker, wedStartPicker, wedEndPicker,
        thurStartPicker, thurEndPicker, fridayStartPicker, fridayEndPicker, satStartPicker,
        satEndPicker};

    // JLabels
    JLabel sundayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel sundayEndLabel = makeLabel("จนถึงเวลา");
    JLabel mondayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel mondayEndLabel = makeLabel("จนถึงเวลา");
    JLabel tuesStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel tuesEndLabel = makeLabel("จนถึงเวลา");
    JLabel wedStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel wedEndLabel = makeLabel("จนถึงเวลา");
    JLabel thurStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel thurEndLabel = makeLabel("จนถึงเวลา");
    JLabel fridayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel fridayEndLabel = makeLabel("จนถึงเวลา");
    JLabel satStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel satEndLabel = makeLabel("จนถึงเวลา");
    JLabel[] labels = {sundayStartLabel, sundayEndLabel, mondayStartLabel, mondayEndLabel,
        tuesStartLabel, tuesEndLabel, wedStartLabel, wedEndLabel, thurStartLabel, thurEndLabel,
        fridayStartLabel, fridayEndLabel, satStartLabel, satEndLabel};

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelTitle, 0, 0, 20);
    setPadding(panelAddDoctor, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 10, 28);
    for (JLabel label : labels) {
      label.setVisible(false);
    }
    for (TimePicker tp : timePickers) {
      tp.setVisible(false);
    }

    workTimeCheckBoxUIHandler(cbSunday, sundayStartPicker, sundayEndPicker, sundayStartLabel,
        sundayEndLabel);
    workTimeCheckBoxUIHandler(cbMonday, mondayStartPicker, mondayEndPicker, mondayStartLabel,
        mondayEndLabel);
    workTimeCheckBoxUIHandler(cbTuesday, tuesStartPicker, tuesEndPicker, tuesStartLabel,
        tuesEndLabel);
    workTimeCheckBoxUIHandler(cbWednesday, wedStartPicker, wedEndPicker, wedStartLabel,
        wedEndLabel);
    workTimeCheckBoxUIHandler(cbThursday, thurStartPicker, thurEndPicker, thurStartLabel,
        thurEndLabel);
    workTimeCheckBoxUIHandler(cbFriday, fridayStartPicker, fridayEndPicker, fridayStartLabel,
        fridayEndLabel);
    workTimeCheckBoxUIHandler(cbSaturday, satStartPicker, satEndPicker, satStartLabel, satEndLabel);

    JTextField tfDoctorName = makeTextField(20);
    JTextField tfDoctorSurName = makeTextField(20);
    JTextField tfDoctorWard = makeTextField(16);
    JTextField tfDoctorHospital = makeTextField(18);
    String[] prefixes = getPrefixes();
    JComboBox cbPrefix = makeComboBox(prefixes);

    // Listeners
    fetchUserInput("add", panelAddDoctor, btnAdd, cbSunday, cbMonday, cbTuesday, cbWednesday,
        cbThursday,
        cbFriday, cbSaturday, sundayStartPicker, sundayEndPicker, mondayStartPicker,
        mondayEndPicker, tuesStartPicker, tuesEndPicker, wedStartPicker, wedEndPicker,
        thurStartPicker, thurEndPicker, fridayStartPicker, fridayEndPicker, tfDoctorName,
        tfDoctorSurName, tfDoctorWard, tfDoctorHospital, cbPrefix);

    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(makeLabel("คำนำหน้า"));
    panelSub.add(cbPrefix);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("ชื่อ"));
    panelSub.add(tfDoctorName);
    panelSub.add(makeLabel("นามสกุล"));
    panelSub.add(tfDoctorSurName);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("แผนก"));
    panelSub.add(tfDoctorWard);
    panelSub.add(makeLabel("ชื่อสถานพยาบาล"));
    panelSub.add(tfDoctorHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("วันและเวลาที่เข้าตรวจ"));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbSunday);
    panelSub.add(sundayStartLabel);
    panelSub.add(sundayStartPicker);
    panelSub.add(sundayEndLabel);
    panelSub.add(sundayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbMonday);
    panelSub.add(mondayStartLabel);
    panelSub.add(mondayStartPicker);
    panelSub.add(mondayEndLabel);
    panelSub.add(mondayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbTuesday);
    panelSub.add(tuesStartLabel);
    panelSub.add(tuesStartPicker);
    panelSub.add(tuesEndLabel);
    panelSub.add(tuesEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbWednesday);
    panelSub.add(wedStartLabel);
    panelSub.add(wedStartPicker);
    panelSub.add(wedEndLabel);
    panelSub.add(wedEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbThursday);
    panelSub.add(thurStartLabel);
    panelSub.add(thurStartPicker);
    panelSub.add(thurEndLabel);
    panelSub.add(thurEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbFriday);
    panelSub.add(fridayStartLabel);
    panelSub.add(fridayStartPicker);
    panelSub.add(fridayEndLabel);
    panelSub.add(fridayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbSaturday);
    panelSub.add(satStartLabel);
    panelSub.add(satStartPicker);
    panelSub.add(satEndLabel);
    panelSub.add(satEndPicker);
    panelBody.add(panelSub);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    panelAddDoctor.add(panelTitle, BorderLayout.NORTH);
    panelAddDoctor.add(scrollPane, BorderLayout.CENTER);
    panelAddDoctor.add(btnAdd, BorderLayout.SOUTH);

    return panelAddDoctor;
  }

  private static JPanel panelEditDoctor(Doctor doctor) {
    /* Creates GUI of the form for editing doctor. */

    // JPanels
    JPanel panelAddDoctor = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelTitle = newFlowLayout();

    // JButtons
    JButton btnBack = makeBackButton("แก้ไขแพทย์", DoctorUtil.getDoctorFullName(doctor));
    JButton btnSave = makeBlueButton("บันทึกแพทย์");

    // JCheckBoxes
    JCheckBox cbSunday = makeCheckBox("วันอาทิตย์");
    JCheckBox cbMonday = makeCheckBox("วันจันทร์");
    JCheckBox cbTuesday = makeCheckBox("วันอังคาร");
    JCheckBox cbWednesday = makeCheckBox("วันพุธ");
    JCheckBox cbThursday = makeCheckBox("วันพฤหัสบดี");
    JCheckBox cbFriday = makeCheckBox("วันศุกร์");
    JCheckBox cbSaturday = makeCheckBox("วันเสาร์");

    // TimePickers
    TimePicker sundayStartPicker = makeTimePicker();
    TimePicker sundayEndPicker = makeTimePicker();
    TimePicker mondayStartPicker = makeTimePicker();
    TimePicker mondayEndPicker = makeTimePicker();
    TimePicker tuesStartPicker = makeTimePicker();
    TimePicker tuesEndPicker = makeTimePicker();
    TimePicker wedStartPicker = makeTimePicker();
    TimePicker wedEndPicker = makeTimePicker();
    TimePicker thurStartPicker = makeTimePicker();
    TimePicker thurEndPicker = makeTimePicker();
    TimePicker fridayStartPicker = makeTimePicker();
    TimePicker fridayEndPicker = makeTimePicker();
    TimePicker satStartPicker = makeTimePicker();
    TimePicker satEndPicker = makeTimePicker();
    TimePicker[] timePickers = {sundayStartPicker, sundayEndPicker, mondayStartPicker,
        mondayEndPicker, tuesStartPicker, tuesEndPicker, wedStartPicker, wedEndPicker,
        thurStartPicker, thurEndPicker, fridayStartPicker, fridayEndPicker, satStartPicker,
        satEndPicker};

    // JLabels
    JLabel sundayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel sundayEndLabel = makeLabel("จนถึงเวลา");
    JLabel mondayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel mondayEndLabel = makeLabel("จนถึงเวลา");
    JLabel tuesStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel tuesEndLabel = makeLabel("จนถึงเวลา");
    JLabel wedStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel wedEndLabel = makeLabel("จนถึงเวลา");
    JLabel thurStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel thurEndLabel = makeLabel("จนถึงเวลา");
    JLabel fridayStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel fridayEndLabel = makeLabel("จนถึงเวลา");
    JLabel satStartLabel = makeLabel("ตั้งแต่เวลา");
    JLabel satEndLabel = makeLabel("จนถึงเวลา");
    JLabel[] labels = {sundayStartLabel, sundayEndLabel, mondayStartLabel, mondayEndLabel,
        tuesStartLabel, tuesEndLabel, wedStartLabel, wedEndLabel, thurStartLabel, thurEndLabel,
        fridayStartLabel, fridayEndLabel, satStartLabel, satEndLabel};

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelTitle, 0, 0, 20);
    setPadding(panelAddDoctor, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 10, 28);
    for (JLabel label : labels) {
      label.setVisible(false);
    }
    for (TimePicker tp : timePickers) {
      tp.setVisible(false);
    }

    for (ArrayList<String> timeArray : doctor.getWorkTime()) {
      switch (timeArray.get(0)) {
        case "วันอาทิตย์":
          cbSunday.setSelected(true);
          sundayStartLabel.setVisible(true);
          sundayEndLabel.setVisible(true);
          sundayStartPicker.setVisible(true);
          sundayEndPicker.setVisible(true);
          sundayStartPicker.setText(timeArray.get(1));
          sundayEndPicker.setText(timeArray.get(2));
          break;

        case "วันจันทร์":
          cbMonday.setSelected(true);
          mondayStartLabel.setVisible(true);
          mondayEndLabel.setVisible(true);
          mondayStartPicker.setVisible(true);
          mondayEndPicker.setVisible(true);
          mondayStartPicker.setText(timeArray.get(1));
          mondayEndPicker.setText(timeArray.get(2));
          break;

        case "วันอังคาร":
          cbTuesday.setSelected(true);
          tuesStartLabel.setVisible(true);
          tuesEndLabel.setVisible(true);
          tuesStartPicker.setVisible(true);
          tuesEndPicker.setVisible(true);
          tuesStartPicker.setText(timeArray.get(1));
          tuesEndPicker.setText(timeArray.get(2));
          break;

        case "วันพุธ":
          cbWednesday.setSelected(true);
          wedStartLabel.setVisible(true);
          wedEndLabel.setVisible(true);
          wedStartPicker.setVisible(true);
          wedEndPicker.setVisible(true);
          wedStartPicker.setText(timeArray.get(1));
          wedEndPicker.setText(timeArray.get(2));
          break;

        case "วันพฤหัสบดี":
          cbThursday.setSelected(true);
          thurStartLabel.setVisible(true);
          thurEndLabel.setVisible(true);
          thurStartPicker.setVisible(true);
          thurEndPicker.setVisible(true);
          thurStartPicker.setText(timeArray.get(1));
          thurEndPicker.setText(timeArray.get(2));
          break;

        case "วันศุกร์":
          cbFriday.setSelected(true);
          fridayStartLabel.setVisible(true);
          fridayEndLabel.setVisible(true);
          fridayStartPicker.setVisible(true);
          fridayEndPicker.setVisible(true);
          fridayStartPicker.setText(timeArray.get(1));
          fridayEndPicker.setText(timeArray.get(2));
          break;

        case "วันเสาร์":
          cbSaturday.setSelected(true);
          satStartLabel.setVisible(true);
          satEndLabel.setVisible(true);
          satStartPicker.setVisible(true);
          satEndPicker.setVisible(true);
          satStartPicker.setText(timeArray.get(1));
          satEndPicker.setText(timeArray.get(2));
          break;
      }
    }

    workTimeCheckBoxUIHandler(cbSunday, sundayStartPicker, sundayEndPicker, sundayStartLabel,
        sundayEndLabel);
    workTimeCheckBoxUIHandler(cbMonday, mondayStartPicker, mondayEndPicker, mondayStartLabel,
        mondayEndLabel);
    workTimeCheckBoxUIHandler(cbTuesday, tuesStartPicker, tuesEndPicker, tuesStartLabel,
        tuesEndLabel);
    workTimeCheckBoxUIHandler(cbWednesday, wedStartPicker, wedEndPicker, wedStartLabel,
        wedEndLabel);
    workTimeCheckBoxUIHandler(cbThursday, thurStartPicker, thurEndPicker, thurStartLabel,
        thurEndLabel);
    workTimeCheckBoxUIHandler(cbFriday, fridayStartPicker, fridayEndPicker, fridayStartLabel,
        fridayEndLabel);
    workTimeCheckBoxUIHandler(cbSaturday, satStartPicker, satEndPicker, satStartLabel, satEndLabel);

    JTextField tfDoctorName = makeTextField(20);
    JTextField tfDoctorSurName = makeTextField(20);
    JTextField tfDoctorWard = makeTextField(16);
    JTextField tfDoctorHospital = makeTextField(18);
    String[] prefixes = getPrefixes();
    JComboBox cbPrefix = makeComboBox(prefixes);

    tfDoctorName.setText(doctor.getFirstName());
    tfDoctorSurName.setText(doctor.getLastName());
    tfDoctorWard.setText(doctor.getWard());
    tfDoctorHospital.setText(doctor.getHospital());
    cbPrefix.setSelectedIndex(DoctorUtil.getPrefixIndex(doctor.getPrefix()));

    // Listeners
    fetchUserInput("update", panelAddDoctor, btnSave, cbSunday, cbMonday, cbTuesday, cbWednesday,
        cbThursday,
        cbFriday, cbSaturday, sundayStartPicker, sundayEndPicker, mondayStartPicker,
        mondayEndPicker,
        tuesStartPicker, tuesEndPicker, wedStartPicker, wedEndPicker, thurStartPicker,
        thurEndPicker,
        fridayStartPicker, fridayEndPicker, tfDoctorName, tfDoctorSurName, tfDoctorWard,
        tfDoctorHospital, cbPrefix);

    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(makeLabel("คำนำหน้า"));
    panelSub.add(cbPrefix);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("ชื่อ"));
    panelSub.add(tfDoctorName);
    panelSub.add(makeLabel("นามสกุล"));
    panelSub.add(tfDoctorSurName);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("แผนก"));
    panelSub.add(tfDoctorWard);
    panelSub.add(makeLabel("ชื่อสถานพยาบาล"));
    panelSub.add(tfDoctorHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeBoldLabel("วันและเวลาที่เข้าตรวจ"));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbSunday);
    panelSub.add(sundayStartLabel);
    panelSub.add(sundayStartPicker);
    panelSub.add(sundayEndLabel);
    panelSub.add(sundayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbMonday);
    panelSub.add(mondayStartLabel);
    panelSub.add(mondayStartPicker);
    panelSub.add(mondayEndLabel);
    panelSub.add(mondayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbTuesday);
    panelSub.add(tuesStartLabel);
    panelSub.add(tuesStartPicker);
    panelSub.add(tuesEndLabel);
    panelSub.add(tuesEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbWednesday);
    panelSub.add(wedStartLabel);
    panelSub.add(wedStartPicker);
    panelSub.add(wedEndLabel);
    panelSub.add(wedEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbThursday);
    panelSub.add(thurStartLabel);
    panelSub.add(thurStartPicker);
    panelSub.add(thurEndLabel);
    panelSub.add(thurEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbFriday);
    panelSub.add(fridayStartLabel);
    panelSub.add(fridayStartPicker);
    panelSub.add(fridayEndLabel);
    panelSub.add(fridayEndPicker);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbSaturday);
    panelSub.add(satStartLabel);
    panelSub.add(satStartPicker);
    panelSub.add(satEndLabel);
    panelSub.add(satEndPicker);
    panelBody.add(panelSub);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    panelAddDoctor.add(panelTitle, BorderLayout.NORTH);
    panelAddDoctor.add(scrollPane, BorderLayout.CENTER);
    panelAddDoctor.add(btnSave, BorderLayout.SOUTH);

    return panelAddDoctor;
  }

  private static void fetchUserInput(String type, JPanel panelAddDoctor, JButton btnSave,
      JCheckBox cbSunday,
      JCheckBox cbMonday, JCheckBox cbTuesday, JCheckBox cbWednesday, JCheckBox cbThursday,
      JCheckBox cbFriday, JCheckBox cbSaturday, TimePicker sundayStartPicker,
      TimePicker sundayEndPicker, TimePicker mondayStartPicker, TimePicker mondayEndPicker,
      TimePicker tuesStartPicker, TimePicker tuesEndPicker, TimePicker wedStartPicker,
      TimePicker wedEndPicker, TimePicker thurStartPicker, TimePicker thurEndPicker,
      TimePicker fridayStartPicker, TimePicker fridayEndPicker, JTextField tfDoctorName,
      JTextField tfDoctorSurName, JTextField tfDoctorWard, JTextField tfDoctorHospital,
      JComboBox cbPrefix) {
    btnSave.addActionListener(e -> {
      String prefix = getPrefixes()[cbPrefix.getSelectedIndex()];
      String fName = tfDoctorName.getText();
      String sName = tfDoctorSurName.getText();
      String ward = tfDoctorWard.getText();
      String hospital = tfDoctorHospital.getText();
      ArrayList<ArrayList> workTime = new ArrayList<>();
      if (cbSunday.isSelected()) {
        ArrayList<String> sunday = new ArrayList<>();
        sunday.add("วันอาทิตย์");
        sunday.add(sundayStartPicker.getText());
        sunday.add(sundayEndPicker.getText());
        workTime.add(sunday);
      }
      if (cbMonday.isSelected()) {
        ArrayList<String> monday = new ArrayList<>();
        monday.add("วันจันทร์");
        monday.add(mondayStartPicker.getText());
        monday.add(mondayEndPicker.getText());
        workTime.add(monday);
      }
      if (cbTuesday.isSelected()) {
        ArrayList<String> tues = new ArrayList<>();
        tues.add("วันอังคาร");
        tues.add(tuesStartPicker.getText());
        tues.add(tuesEndPicker.getText());
        workTime.add(tues);
      }
      if (cbWednesday.isSelected()) {
        ArrayList<String> wed = new ArrayList<>();
        wed.add("วันพุธ");
        wed.add(wedStartPicker.getText());
        wed.add(wedEndPicker.getText());
        workTime.add(wed);
      }
      if (cbThursday.isSelected()) {
        ArrayList<String> thur = new ArrayList<>();
        thur.add("วันพฤหัสบดี");
        thur.add(thurStartPicker.getText());
        thur.add(thurEndPicker.getText());
        workTime.add(thur);
      }
      if (cbFriday.isSelected()) {
        ArrayList<String> friday = new ArrayList<>();
        friday.add("วันศุกร์");
        friday.add(fridayStartPicker.getText());
        friday.add(fridayEndPicker.getText());
        workTime.add(friday);
      }
      if (cbSaturday.isSelected()) {
        ArrayList<String> sat = new ArrayList<>();
        sat.add("วันเสาร์");
        sat.add(fridayStartPicker.getText());
        sat.add(fridayEndPicker.getText());
        workTime.add(sat);
      }
      System.out.println(workTime);
      Doctor doctor = new Doctor(prefix, fName, sName, ward, hospital, workTime);

      switch (type) {
        case "add":
          try {
            DoctorDB.addDoctor(doctor, getUser().getUserId());
            fireSuccessDialog("เพิ่ม " + prefix + " " + fName + " เรียบร้อยแล้ว");
            panelRight.remove(panelDoctors);
            panelAllDoctors();
            panelRight.validate();
            panelRight.repaint();
            backTo("แพทย์");
            panelRight.remove(panelAddDoctor);
            panelRight.add(panelAddDoctor);
          } catch (SQLException e1) {
            e1.printStackTrace();
            fireDBErrorDialog();
          }
          break;
        case "update":
          try {
            DoctorDB.updateDoctor(doctor);
            fireSuccessDialog("แก้ไข " + prefix + " " + fName + " เรียบร้อยแล้ว");
            panelRight.remove(panelDoctors);
            panelAllDoctors();
            panelRight.validate();
            panelRight.repaint();
            backTo("แพทย์");
            panelRight.remove(panelEditDoctor(doctor));
            panelRight.add(panelAddDoctor);
          } catch (SQLException e1) {
            e1.printStackTrace();
            fireDBErrorDialog();
          }
          break;
      }

    });
  }

  private static void workTimeCheckBoxUIHandler(JCheckBox checkBox, TimePicker startPicker,
      TimePicker endPicker, JLabel startLabel, JLabel endLabel) {
    checkBox.addActionListener(e -> {
      if (checkBox.isSelected()) {
        startLabel.setVisible(true);
        endLabel.setVisible(true);
        startPicker.setVisible(true);
        endPicker.setVisible(true);
      } else {
        startLabel.setVisible(false);
        endLabel.setVisible(false);
        startPicker.setVisible(false);
        endPicker.setVisible(false);
      }
    });
  }

  private static JPanel panelViewDoctor(Doctor doctor) {
    /* Creates GUI displaying all information of a single doctor. */

    // JPanels
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelButtons = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());

    String doctorName = DoctorUtil.getDoctorFullName(doctor);

    // JButtons
    JButton btnEdit = makeBlueButton("แก้ไขข้อมูลแพทย์");
    JButton btnRemove = makeRemoveButton();
    JButton btnBack = makeBackButton(doctorName, "แพทย์");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelTitle, -6, 0, 20, 8);
    setPadding(panelBody, 0, 0, 0, 38);
    setPadding(panelView, 0, 0, 0, -20);

    // Listeners
    btnEdit.addActionListener(e -> {
      editSwitcher(panelRight, panelEditDoctor(doctor));
    });

    btnRemove.addActionListener(e -> {
      int dialogResult = fireConfirmDialog(
          "ต้องการลบแพทย์คนนี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");

      if (dialogResult == JOptionPane.YES_OPTION) {
        String labelMessage;
        try {
          DoctorDB.removeDoctor(doctor);
          labelMessage = getRemoveSuccessfulMessage("แพทย์");
          fireSuccessDialog(labelMessage);
          panelRight.remove(panelDoctors);
          panelAllDoctors();
          backTo("แพทย์");
          panelRight.remove(panelViewDoctor(doctor));
        } catch (SQLException e1) {
          e1.printStackTrace();
          labelMessage = getRemoveFailedMessage("แพทย์");
        }
      }
    });

    panelTitle.add(btnBack);
    panelBody.add(makeLabel("ชื่อแพทย์: " + doctorName));
    if (doctor.getWard() != null) {
      panelBody.add(makeLabel("แผนก: " + doctor.getWard()));
    }
    panelBody.add(makeLabel("โรงพยาบาล: " + doctor.getHospital()));
    if (doctor.getWorkTime() != null) {
      panelBody.add(makeLabel("เวลาเข้าตรวจ:"));
      // WorkTime is an ArrayList, convert it to a printable format
      for (ArrayList<String> workTime : doctor.getWorkTime()) {
        JLabel labelWorkTime = makeLabel(
            workTime.get(0) + " เวลา " + workTime.get(1) + " น. - " + workTime.get(2) + " น.");
        setPadding(labelWorkTime, 0, 20);
        panelBody.add(labelWorkTime);
      }
    }

    panelButtons.add(btnEdit, BorderLayout.CENTER);
    panelButtons.add(btnRemove, BorderLayout.EAST);

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(panelBody, BorderLayout.CENTER);
    panelView.add(panelButtons, BorderLayout.SOUTH);

    return panelView;
  }

  private static JPanel makeDoctorCard(Doctor doctor) {
    /* Creates a card that will be used on the All doctors panel only. */
    String doctorName = DoctorUtil.getDoctorFullName(doctor);
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
      Image img = ImageIO.read(new File(imgPath + "/system/doctor.png"));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ignored) {
    }

    panelPic.add(labelPic);
    panelInfo.add(labelTitle);
    panelInfo.add(labelShortInfo);

    setPadding(labelTitle, 5, 0, -10, 0);
    setPadding(labelPic, 5, 10, 0, 0);

    panelLoopInfo.add(panelPic);
    panelLoopInfo.add(panelInfo);
    panelLoopInfo.add(Box.createHorizontalGlue());

    panelLoopInfo.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        panelRight.add(panelViewDoctor(doctor), doctorName);
        CardLayout cl = (CardLayout) (panelRight.getLayout());
        cl.show(panelRight, doctorName);
      }
    });

    return panelLoopInfo;
  }

}
