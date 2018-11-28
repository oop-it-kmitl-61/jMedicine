package GUI.components;

import static GUI.GUIHelper.*;
import static GUI.GUI.*;

import GUI.GUIUtil;
import com.github.lgooddatepicker.components.TimePicker;
import core.Doctor;
import core.DoctorUtil;
import core.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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

public class doctorUI {

  private static JPanel panelDoctors;
  private static GUIUtil util = new GUIUtil();
  private static User user = util.getSignedInUser();

  public static void panelAllDoctors() {
    /*
      Creates GUI displaying all doctors that user has had input.
      All doctors will be displayed in a card with a default icon,
      a name and a short summary.
     */

    // Init title panel displaying title label
    JPanel panelTitle = new JPanel(new BorderLayout());
    JPanel panelLoop = newPanelLoop();

    JLabel labelTitle = makeTitleLabel("แพทย์");
    panelTitle.add(labelTitle);

    // Fetch all doctors
    ArrayList<Doctor> userDoctors = user.getUserDoctors();

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
    panelDoctors.setBackground(Color.WHITE);
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
    JButton btnAdd = makeButton("บันทึกแพทย์");

    // JCheckBoxes
    JCheckBox cbSunday = makeCheckBox("วันอาทิตย์");
    JCheckBox cbMonday = makeCheckBox("วันจันทร์");
    JCheckBox cbTuesday = makeCheckBox("วันอังคาร");
    JCheckBox cbWednesday = makeCheckBox("วันพุธ");
    JCheckBox cbThursday = makeCheckBox("วันพฤหัสบดี");
    JCheckBox cbFriday = makeCheckBox("วันศุกร์");
    JCheckBox cbSaturday = makeCheckBox("วันเสาร์");

    // TimePickers
    TimePicker sundayStartPicker = new TimePicker();
    TimePicker sundayEndPicker = new TimePicker();
    TimePicker mondayStartPicker = new TimePicker();
    TimePicker mondayEndPicker = new TimePicker();
    TimePicker tuesStartPicker = new TimePicker();
    TimePicker tuesEndPicker = new TimePicker();
    TimePicker wedStartPicker = new TimePicker();
    TimePicker wedEndPicker = new TimePicker();
    TimePicker thurStartPicker = new TimePicker();
    TimePicker thurEndPicker = new TimePicker();
    TimePicker fridayStartPicker = new TimePicker();
    TimePicker fridayEndPicker = new TimePicker();
    TimePicker satStartPicker = new TimePicker();
    TimePicker satEndPicker = new TimePicker();
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

    // Listeners
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
    String[] prefixes = {"นพ.", "พญ.", "ศ.นพ", "ผศ.นพ"};
    JComboBox cbPrefix = makeComboBox(prefixes);

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
    JButton btnSave = makeButton("บันทึกแพทย์");

    // JCheckBoxes
    JCheckBox cbSunday = makeCheckBox("วันอาทิตย์");
    JCheckBox cbMonday = makeCheckBox("วันจันทร์");
    JCheckBox cbTuesday = makeCheckBox("วันอังคาร");
    JCheckBox cbWednesday = makeCheckBox("วันพุธ");
    JCheckBox cbThursday = makeCheckBox("วันพฤหัสบดี");
    JCheckBox cbFriday = makeCheckBox("วันศุกร์");
    JCheckBox cbSaturday = makeCheckBox("วันเสาร์");

    // TimePickers
    TimePicker sundayStartPicker = new TimePicker();
    TimePicker sundayEndPicker = new TimePicker();
    TimePicker mondayStartPicker = new TimePicker();
    TimePicker mondayEndPicker = new TimePicker();
    TimePicker tuesStartPicker = new TimePicker();
    TimePicker tuesEndPicker = new TimePicker();
    TimePicker wedStartPicker = new TimePicker();
    TimePicker wedEndPicker = new TimePicker();
    TimePicker thurStartPicker = new TimePicker();
    TimePicker thurEndPicker = new TimePicker();
    TimePicker fridayStartPicker = new TimePicker();
    TimePicker fridayEndPicker = new TimePicker();
    TimePicker satStartPicker = new TimePicker();
    TimePicker satEndPicker = new TimePicker();
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

    // Listeners
    btnSave.addActionListener(e -> {
      saveSwitcher(panelRight, panelEditDoctor(doctor), panelViewDoctor(doctor),
          DoctorUtil.getDoctorFullName(doctor));
    });

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
    String[] prefixes = DoctorUtil.getPrefixes();
    JComboBox cbPrefix = makeComboBox(prefixes);

    tfDoctorName.setText(doctor.getFirstName());
    tfDoctorSurName.setText(doctor.getLastName());
    tfDoctorWard.setText(doctor.getWard());
    tfDoctorHospital.setText(doctor.getHospital());
    cbPrefix.setSelectedIndex(DoctorUtil.getPrefixIndex(doctor.getPrefix()));

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
    JButton btnEdit = makeButton("แก้ไขข้อมูลแพทย์");
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
        if (user.removeUserDoctor(doctor)) {
          labelMessage = getRemoveSuccessfulMessage("แพทย์");
        } else {
          labelMessage = getRemoveFailedMessage("แพทย์");
        }
        panelRight.remove(panelDoctors);
        panelDoctors = null;
        panelDoctors = new JPanel(new BorderLayout());
        panelRight.add(panelDoctors, "แพทย์");
        backTo("แพทย์");
        fireSuccessDialog(labelMessage);
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
