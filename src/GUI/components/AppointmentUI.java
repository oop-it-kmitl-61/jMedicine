package GUI.components;

import static GUI.GUIHelper.*;
import static GUI.GUI.*;
import static GUI.components.DoctorUI.reloadDoctors;
import static core.Core.getUser;

import GUI.GUIHelper;
import api.AppointmentDB;
import api.DoctorDB;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import core.Appointment;
import core.AppointmentUtil;
import core.Doctor;
import core.DoctorUtil;
import core.DoctorItem;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * All UIs and handler methods about an appointment will be written here.
 *
 * @author jMedicine
 * @version 0.8.0
 * @since 0.7.0
 */

public class AppointmentUI {

  private static JPanel panelAppointment;
  private static JPanel panelAddAppointment;
  private static boolean newDoctor;

  public static void panelAllAppointments() {
    /*
      Creates GUI displaying all appointments that user has had input.
      All appointments will be displayed in a card with a default icon,
      a date and a short summary.
     */

    // Init title panel displaying title label
    panelAppointment = new JPanel(new BorderLayout());
    JPanel panelLoop = newPanelLoop();
    JPanel panelTitle = new JPanel(new BorderLayout());
    JLabel labelTitle = makeTitleLabel("นัดแพทย์");

    // Init panel loop
    panelLoop.add(makeNewButton("เพิ่มนัดใหม่"));

    // Fetch all appointments from the records
    ArrayList<Appointment> userAppointments = null;
    try {
      userAppointments = AppointmentDB.getAllAppointment(getUser().getUserId());
    } catch (SQLException | ParseException e) {
      e.printStackTrace();
      fireDBErrorDialog();
    }

    if (userAppointments.isEmpty()) {
      labelTitle.setText("คุณยังไม่มีนัดแพทย์ที่บันทึกไว้");
    } else {
      labelTitle.setText("นัดแพทย์");
      for (Appointment appCurrent : userAppointments) {
        JPanel cardLoop = makeAppointmentCard(appCurrent);
        panelLoop.add(cardLoop);
      }
    }

    panelTitle.add(labelTitle);

    // Add all panels into the main panel
    panelAppointment.add(panelLoop);
    panelAppointment.add(panelTitle, BorderLayout.NORTH);

    panelRight.add(panelAppointment, "นัดแพทย์");
    panelRight.add(panelAddAppointment(), "เพิ่มนัดใหม่");
  }

  private static JPanel panelViewAppointment(Appointment appointment) {
    /* Creates GUI displaying information of a single appointment. */

    // JPanels
    JPanel panelView = new JPanel(new BorderLayout());
    JPanel panelBody = new JPanel();
    JPanel panelButtons = new JPanel(new BorderLayout());
    JPanel panelTitle = new JPanel(new BorderLayout());

    String doctorName = appointment.getDoctor().toString();
    String doctorHpt = appointment.getDoctor().getHospital();

    String title = GUIHelper.formatDMYFull.format(appointment.getDate())
        + " เวลา " + appointment.getTimeStart() + " น. - "
        + appointment.getTimeStop() + " น.";

    // JButtons
    JButton btnEdit = makeBlueButton("แก้ไขนัดแพทย์");
    JButton btnRemove = makeRemoveButton();
    JButton btnBack = makeBackButton(title, "นัดแพทย์");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelTitle, -10, 0, 0, -14);
    setPadding(panelBody, 20, 0, 0, 45);

    // Listeners
    btnEdit.addActionListener(e -> {
      editSwitcher(panelRight, panelEditAppointment(appointment));
    });

    btnRemove.addActionListener(e -> {
      int dialogResult = fireConfirmDialog(
          "ต้องการลบนัดแพทย์นี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");

      if (dialogResult == JOptionPane.YES_OPTION) {
        try {
          AppointmentDB.removeAppointment(appointment);
          fireSuccessDialog("ลบนัดแพทย์เรียบร้อยแล้ว");
        } catch (SQLException e1) {
          e1.printStackTrace();
          fireDBErrorDialog();
        }
        panelRight.remove(panelAppointment);
        panelAllAppointments();
        backTo("นัดแพทย์");
      }
    });

    // Init web browser
    Browser browser = new Browser();
    BrowserView view = new BrowserView(browser);
    // Load URL that query the hospital around the current position
    browser.loadURL("https://www.google.co.th/maps/search/" + doctorHpt);

    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(makeLabel("แพทย์ผู้นัด: " + doctorName));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("โรงพยาบาล: " + doctorHpt));
    panelBody.add(panelSub);

    if (!appointment.getNote().equals("")) {
      panelSub = newFlowLayout();
      panelSub.add(makeLabel("หมายเหตุ: " + appointment.getNote()));
      panelBody.add(panelSub);
    }

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("กำหนดนัด: " + title));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelButtons.add(btnEdit, BorderLayout.CENTER);
    panelButtons.add(btnRemove, BorderLayout.EAST);
    panelSub.add(panelButtons);
    panelBody.add(panelSub);

    panelView.add(panelTitle, BorderLayout.NORTH);
    panelView.add(view, BorderLayout.CENTER);
    panelView.add(panelBody, BorderLayout.SOUTH);

    return panelView;
  }

  private static JPanel panelAddAppointment() {

    // JPanels
    panelAddAppointment = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelBody = new JPanel();
    JPanel panelNewDr = new JPanel();

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มนัดใหม่", "นัดแพทย์");
    JButton btnAdd = makeBlueButton("บันทึกนัด");

    // JTextFields
    JTextField tfNote = makeTextField(40);
    JTextField tfDrFName = makeTextField(20);
    JTextField tfDrLName = makeTextField(20);
    JTextField tfDrHpt = makeTextField(20);
    JTextField tfDrWard = makeTextField(20);

    // JLabels
    JLabel labelDateTitle = makeLabel("วันที่นัด*");
    JLabel labelTimeStart = makeLabel("ตั้งแต่เวลา*");
    JLabel labelTimeEnd = makeLabel("จนถึงเวลา*");
    JLabel labelDoctor = makeLabel("แพทย์ที่นัด*");
    JLabel labelNote = makeLabel("หมายเหตุการนัด");
    JLabel labelNewDr = makeBoldLabel("กรอกข้อมูลแพทย์ใหม่");
    JLabel labelNewDrFName = makeLabel("ชื่อ*");
    JLabel labelNewDrLName = makeLabel("นามสกุล*");
    JLabel labelNewDrHpt = makeLabel("ชื่อสถานพยาบาล*");
    JLabel labelNewDrWard = makeLabel("แผนก");

    // JComboBoxes
    JComboBox cbDoctor = makeComboBox();
    JComboBox cbPrefixes = makeComboBox(DoctorUtil.getPrefixes());

    // Fetch all existed doctors
    ArrayList<Doctor> doctors = null;
    try {
      doctors = DoctorDB.getAllDoctor(getUser().getUserId());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if (doctors != null) {
      for (Doctor doctor : doctors) {
        cbDoctor.addItem(new DoctorItem(doctor.toString(), doctor));
      }
    }
    cbDoctor.addItem(new DoctorItem("เพิ่มแพทย์ใหม่", null));

    // Pickers
    DatePicker datePicker = makeDatePicker();
    TimePicker timePickerStart = makeTimePicker();
    TimePicker timePickerEnd = makeTimePicker();

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    panelNewDr.setLayout(new BoxLayout(panelNewDr, BoxLayout.PAGE_AXIS));
    panelNewDr.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 0, 10, 10),
        newCardBorder()
    ));
    setPadding(panelAddAppointment, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 260, 28);
    setPadding(panelTitle, 0, 0, 20);
    setPadding(labelDoctor, 10, 0, -10, 0);
    setPadding(labelNote, 10, 0, -6, 0);
    if (cbDoctor.getItemCount() == 1) {
      panelNewDr.setVisible(true);
    } else {
      panelNewDr.setVisible(false);
    }

    // Listeners
    newDoctorListener(panelNewDr, cbDoctor);
    btnAdd.addActionListener(e -> {
      if (datePicker.getText().equals("") || timePickerStart.getText().equals("") || timePickerEnd
          .getText().equals("")) {
        fireErrorDialog("กรุณากรอกข้อมูลให้ครบตามช่องที่มีเครื่องหมาย *");
      } else if (newDoctor && (tfDrFName.getText().equals("") || tfDrLName.getText().equals("")
          || tfDrHpt.getText().equals(""))) {
        fireErrorDialog("กรุณากรอกข้อมูลให้ครบตามช่องที่มีเครื่องหมาย *");
      } else {
        Date date = Date
            .from(datePicker.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        String timeStart = timePickerStart.getText();
        String timeEnd = timePickerEnd.getText();
        String note = tfNote.getText();
        DoctorItem selectedDoctor = (DoctorItem) cbDoctor.getSelectedItem();
        Doctor doctor = newDoctor(tfDrFName, tfDrLName, tfDrHpt, tfDrWard, cbPrefixes,
            selectedDoctor);

        Appointment app = new Appointment(date, timeStart, timeEnd, doctor, note);
        try {
          AppointmentDB.addAppointment(app, getUser().getUserId());
          fireSuccessDialog("เพิ่มนัดเรียบร้อยแล้ว");
          panelRight.remove(panelAppointment);
          panelAppointment = null;
          panelAllAppointments();
          backTo("นัดแพทย์");
          panelRight.remove(panelAddAppointment);
          panelRight.add(panelAddAppointment, "เพิ่มนัดใหม่");
        } catch (SQLException e1) {
          e1.printStackTrace();
          fireDBErrorDialog();
        }
      }
    });

    // Panel Title
    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(labelDateTitle);
    panelSub.add(datePicker);
    panelSub.add(labelTimeStart);
    panelSub.add(timePickerStart);
    panelSub.add(labelTimeEnd);
    panelSub.add(timePickerEnd);
    setPadding(panelSub, 0, 0, 10, 0);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelNewDr);
    setPadding(panelSub, 10, 0, 4, 12);
    panelNewDr.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbPrefixes);
    panelSub.add(labelNewDrFName);
    panelSub.add(tfDrFName);
    panelSub.add(labelNewDrLName);
    panelSub.add(tfDrLName);
    setPadding(panelSub, 0, 0, 0, 12);
    panelNewDr.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelNewDrWard);
    panelSub.add(tfDrWard);
    panelSub.add(labelNewDrHpt);
    panelSub.add(tfDrHpt);
    setPadding(panelSub, 0, 0, 14, 12);
    panelNewDr.add(panelSub);
    panelBody.add(panelNewDr);

    panelSub = newFlowLayout();
    panelSub.add(labelNote);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfNote);
    panelBody.add(panelSub);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    panelAddAppointment.add(panelTitle, BorderLayout.NORTH);
    panelAddAppointment.add(scrollPane, BorderLayout.CENTER);
    panelAddAppointment.add(btnAdd, BorderLayout.SOUTH);

    return panelAddAppointment;
  }

  private static JPanel panelEditAppointment(Appointment appointment) {

    // JPanels
    panelAddAppointment = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelBody = new JPanel();
    JPanel panelNewDr = new JPanel();

    // JButtons
    JButton btnBack = makeBackButton("แก้ไขนัด", "นัดแพทย์");
    JButton btnAdd = makeBlueButton("บันทึกนัด");

    // JTextFields
    JTextField tfNote = makeTextField(40);
    JTextField tfDrFName = makeTextField(20);
    JTextField tfDrLName = makeTextField(20);
    JTextField tfDrHpt = makeTextField(20);
    JTextField tfDrWard = makeTextField(20);

    tfNote.setText(appointment.getNote());

    // JLabels
    JLabel labelDateTitle = makeLabel("วันที่นัด*");
    JLabel labelTimeStart = makeLabel("ตั้งแต่เวลา*");
    JLabel labelTimeEnd = makeLabel("จนถึงเวลา*");
    JLabel labelDoctor = makeLabel("แพทย์ที่นัด*");
    JLabel labelNote = makeLabel("หมายเหตุการนัด");
    JLabel labelNewDr = makeBoldLabel("กรอกข้อมูลแพทย์ใหม่");
    JLabel labelNewDrFName = makeLabel("ชื่อ");
    JLabel labelNewDrLName = makeLabel("นามสกุล");
    JLabel labelNewDrHpt = makeLabel("ชื่อสถานพยาบาล");
    JLabel labelNewDrWard = makeLabel("แผนก");

    // JComboBoxes
    JComboBox cbDoctor = makeComboBox();
    JComboBox cbPrefixes = makeComboBox(DoctorUtil.getPrefixes());

    // Fetch all existed doctors
    ArrayList<Doctor> doctors = null;
    try {
      doctors = DoctorDB.getAllDoctor(getUser().getUserId());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if (doctors != null) {
      for (Doctor doctor : doctors) {
        cbDoctor.addItem(new DoctorItem(doctor.toString(), doctor));
      }
    }
    cbDoctor.addItem(new DoctorItem("เพิ่มแพทย์ใหม่", null));

    // Pickers
    DatePicker datePicker = makeDatePicker();
    TimePicker timePickerStart = makeTimePicker();
    TimePicker timePickerEnd = makeTimePicker();

    datePicker.setText(formatDatePicker.format(appointment.getDate()));
    timePickerStart.setText(appointment.getTimeStart());
    timePickerEnd.setText(appointment.getTimeStop());

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    panelNewDr.setLayout(new BoxLayout(panelNewDr, BoxLayout.PAGE_AXIS));
    panelNewDr.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 0, 10, 10),
        newCardBorder()
    ));
    setPadding(panelAddAppointment, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 260, 28);
    setPadding(panelTitle, 0, 0, 20);
    setPadding(labelDoctor, 10, 0, -10, 0);
    setPadding(labelNote, 10, 0, -6, 0);
    if (cbDoctor.getItemCount() == 1) {
      panelNewDr.setVisible(true);
    } else {
      panelNewDr.setVisible(false);
    }

    // Listeners
    newDoctorListener(panelNewDr, cbDoctor);
    btnAdd.addActionListener(e -> {
      if (datePicker.getText().equals("") || timePickerStart.getText().equals("") || timePickerEnd
          .getText().equals("")) {
        fireErrorDialog("กรุณากรอกข้อมูลให้ครบตามช่องที่มีเครื่องหมาย *");
      } else if (newDoctor && (tfDrFName.getText().equals("") || tfDrLName.getText().equals("")
          || tfDrHpt.getText().equals(""))) {
        fireErrorDialog("กรุณากรอกข้อมูลให้ครบตามช่องที่มีเครื่องหมาย *");
      } else {
        Instant var = datePicker.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(var);
        String timeStart = timePickerStart.getText();
        String timeEnd = timePickerEnd.getText();
        String note = tfNote.getText();
        DoctorItem selectedDoctor = (DoctorItem) cbDoctor.getSelectedItem();
        Doctor doctor;
        doctor = newDoctor(tfDrFName, tfDrLName, tfDrHpt, tfDrWard, cbPrefixes, selectedDoctor);

        appointment.setDate(date);
        appointment.setTimeStart(timeStart);
        appointment.setTimeStop(timeEnd);
        appointment.setDoctor(doctor);
        appointment.setNote(note);
        try {
          AppointmentDB.updateAppointment(appointment);
          fireSuccessDialog("แก้ไขนัดเรียบร้อยแล้ว");
          panelRight.remove(panelAppointment);
          panelAppointment = null;
          panelAllAppointments();
          backTo("นัดแพทย์");
          panelRight.remove(panelEditAppointment(appointment));
        } catch (SQLException e1) {
          e1.printStackTrace();
          fireDBErrorDialog();
        }
      }
    });

    // Panel Title
    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(labelDateTitle);
    panelSub.add(datePicker);
    panelSub.add(labelTimeStart);
    panelSub.add(timePickerStart);
    panelSub.add(labelTimeEnd);
    panelSub.add(timePickerEnd);
    setPadding(panelSub, 0, 0, 10, 0);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelNewDr);
    setPadding(panelSub, 10, 0, 4, 12);
    panelNewDr.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(cbPrefixes);
    panelSub.add(labelNewDrFName);
    panelSub.add(tfDrFName);
    panelSub.add(labelNewDrLName);
    panelSub.add(tfDrLName);
    setPadding(panelSub, 0, 0, 0, 12);
    panelNewDr.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelNewDrWard);
    panelSub.add(tfDrWard);
    panelSub.add(labelNewDrHpt);
    panelSub.add(tfDrHpt);
    setPadding(panelSub, 0, 0, 14, 12);
    panelNewDr.add(panelSub);
    panelBody.add(panelNewDr);

    panelSub = newFlowLayout();
    panelSub.add(labelNote);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfNote);
    panelBody.add(panelSub);

    JScrollPane scrollPane = makeScrollPane(panelBody);

    panelAddAppointment.add(panelTitle, BorderLayout.NORTH);
    panelAddAppointment.add(scrollPane, BorderLayout.CENTER);
    panelAddAppointment.add(btnAdd, BorderLayout.SOUTH);

    return panelAddAppointment;
  }

  private static Doctor newDoctor(JTextField tfDrFName, JTextField tfDrLName, JTextField tfDrHpt,
      JTextField tfDrWard, JComboBox cbPrefixes, DoctorItem selectedDoctor) {
    Doctor doctor;
    if (selectedDoctor.getDoctor() != null) {
      doctor = selectedDoctor.getDoctor();
    } else {
      String drPrefix = DoctorUtil.getPrefixes()[cbPrefixes.getSelectedIndex()];
      String drFName = tfDrFName.getText();
      String drLName = tfDrLName.getText();
      String drWard = tfDrWard.getText();
      String drHpt = tfDrHpt.getText();
      doctor = new Doctor(drPrefix, drFName, drLName, drWard, drHpt, null, null);
      try {
        DoctorDB.addDoctor(doctor, getUser().getUserId());
        reloadDoctors();
      } catch (SQLException e1) {
        e1.printStackTrace();
        fireDBErrorDialog();
      }
    }
    return doctor;
  }

  private static void newDoctorListener(JPanel panelNewDr, JComboBox cbDoctor) {
    cbDoctor.addActionListener(e -> {
      DoctorItem current = (DoctorItem) cbDoctor.getSelectedItem();
      if (current.getDoctor() == null) {
        panelNewDr.setVisible(true);
        newDoctor = true;
      } else {
        panelNewDr.setVisible(false);
        newDoctor = false;
      }
    });
  }

  private static JPanel makeAppointmentCard(Appointment appointment) {
    /* Creates a card that will be used on the All appointments panel only. */

    // JPanels
    JPanel panelLoopInfo = new JPanel();
    JPanel panelPic = new JPanel();
    JPanel panelInfo = new JPanel();

    // Strings
    Doctor appDr = appointment.getDoctor();
    String title = AppointmentUtil.getTitle(appointment);
    String shortInfo = appDr.toString() + " (โรงพยาบาล" + appDr.getHospital() + ")";

    // JLabels
    JLabel labelTitle = makeBoldLabel(title);
    JLabel labelShortInfo = makeSmallerLabel(shortInfo);
    JLabel labelPic = getAppointmentIcon();

    // Styling
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    panelPic.setLayout(new BoxLayout(panelPic, BoxLayout.X_AXIS));
    panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.PAGE_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, 0);

    panelPic.add(labelPic);
    panelInfo.add(labelTitle);
    panelInfo.add(labelShortInfo);

    setPadding(labelTitle, 5, 0, -10, 0);
    setPadding(labelPic, 5, 0, 0, 0);

    panelLoopInfo.add(panelPic);
    panelLoopInfo.add(panelInfo);
    panelLoopInfo.add(Box.createHorizontalGlue());

    panelLoopInfo.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        panelRight.add(panelViewAppointment(appointment), title);
        CardLayout cl = (CardLayout) (panelRight.getLayout());
        cl.show(panelRight, title);
      }
    });

    return panelLoopInfo;
  }

  public static JLabel getAppointmentIcon() {
    JLabel labelPic = makeLabel(" ");
    try {
      Image img = ImageIO.read(new File(imgPath + "/system/calendar.png"));
      labelPic.setIcon(new ImageIcon(img));
    } catch (Exception ignored) {
    }
    return labelPic;
  }

  static void reloadAppDoctors() {
    panelRight.remove(panelAddAppointment);
    panelRight.add(panelAddAppointment(), "เพิ่มนัดใหม่");
  }
}
