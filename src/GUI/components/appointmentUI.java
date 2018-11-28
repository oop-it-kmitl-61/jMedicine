package GUI.components;

import static GUI.GUIHelper.*;
import static GUI.GUI.*;

import GUI.GUIUtil;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import core.Appointment;
import core.AppointmentUtil;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class appointmentUI {
  private static JPanel panelAppointment;
  private static GUIUtil util = new GUIUtil();
  private static User user = util.getSignedInUser();

  public static void panelAllAppointments() {
    /*
      Creates GUI displaying all appointments that user has had input.
      All appointments will be displayed in a card with a default icon,
      a date and a short summary.
     */

    // Init title panel displaying title label
    JPanel panelLoop = newPanelLoop();
    JPanel panelTitle = new JPanel(new BorderLayout());

    // Init panel loop
    panelLoop.add(makeNewButton("เพิ่มนัดใหม่"));

    // Fetch all medicines from the records
    ArrayList<Appointment> userAppointment = user.getUserAppointments();

    JLabel labelTitle = makeTitleLabel("นัดแพทย์");
    
    if (userAppointment.isEmpty()) {
      labelTitle.setText("คุณยังไม่มีนัดแพทย์ที่บันทึกไว้");
    } else {
      labelTitle.setText("นัดแพทย์");
      for (Appointment appCurrent : userAppointment) {
        JPanel cardLoop = makeAppointmentCard(appCurrent);
        panelLoop.add(cardLoop);
      }
    }

    
    panelTitle.add(labelTitle);
    
    // Add all panels into the main panel
    panelAppointment.add(panelLoop);
    panelAppointment.add(panelTitle, BorderLayout.NORTH);
    panelAppointment.setBackground(Color.WHITE);
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

    Doctor appointmentDr = appointment.getDoctor();
    String doctorName = appointmentDr.getPrefix() + " " + appointmentDr.getName();

    String title = formatDMY.format(appointment.getTimeStart())
        + " เวลา " + formatHM.format(appointment.getTimeStart()) + " น. - "
        + formatHM.format(appointment.getTimeStop()) + " น.";

    // JButtons
    JButton btnEdit = makeButton("แก้ไขนัดแพทย์");
    JButton btnRemove = makeRemoveButton();
    JButton btnBack = makeBackButton(title, "นัดแพทย์");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelBody, 20, 0, 0, 45);

    // Listeners
    btnEdit.addActionListener(e -> {
      editSwitcher(panelRight, panelEditAppointment(appointment));
    });

    btnRemove.addActionListener(e -> {
      int dialogResult = fireConfirmDialog("ต้องการลบนัดแพทย์นี้จริง ๆ ใช่หรือไม่ คุณไม่สามารถแก้ไขการกระทำนี้ได้อีกในภายหลัง");

      if (dialogResult == JOptionPane.YES_OPTION) {
        String labelMessage;
        if (user.removeUserAppointment(appointment)) {
          labelMessage = getRemoveSuccessfulMessage("นัดแพทย์");
        } else {
          labelMessage = getRemoveFailedMessage("นัดแพทย์");
        }
        panelRight.remove(panelAppointment);
        panelAppointment = new JPanel(new BorderLayout());
        panelRight.add(panelAppointment, "นัดแพทย์");
        backTo("นัดแพทย์");
        fireSuccessDialog(labelMessage);
      }
    });

    // Init web browser
    Browser browser = new Browser();
    BrowserView view = new BrowserView(browser);
    // Load URL that query the hospital around the current position
    browser.loadURL("https://www.google.co.th/maps/search/" + appointment.getHospitalName());

    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(makeLabel("แพทย์ผู้นัด: " + doctorName));
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(makeLabel("โรงพยาบาล: " + appointment.getHospitalName()));
    panelBody.add(panelSub);

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

  private static JPanel panelEditAppointment(Appointment app) {

    // JPanels
    JPanel panelEditAppointment = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelBody = new JPanel();

    // JButtons
    JButton btnBack = makeBackButton("แก้ไขนัด", AppointmentUtil.getTitle(app));
    JButton btnSave = makeButton("บันทึก");

    // JTextFields
    // TODO: tfDoctor @ editAppointment
    JTextField tfDoctor = makeTextField(30);
    JTextField tfHospital = makeTextField(30);
    JTextField tfNote = makeTextField(40);

    tfDoctor.setText(DoctorUtil.getDoctorFullName(app.getDoctor()));
    tfHospital.setText(app.getHospitalName());

    // JLabels
    JLabel labelDateTitle = makeLabel("วันที่นัด");
    JLabel labelTimeStart = makeLabel("ตั้งแต่เวลา");
    JLabel labelTimeEnd = makeLabel("จนถึงเวลา");
    JLabel labelDoctor = makeLabel("แพทย์ที่นัด");
    JLabel labelHospital = makeLabel("ชื่อสถานพยาบาล");
    JLabel labelNote = makeLabel("หมายเหตุการนัด");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelEditAppointment, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 260, 28);
    setPadding(panelTitle, 0, 0, 20);
    setPadding(labelDoctor, 10, 0, -10, 0);
    setPadding(labelHospital, 10, 0, -10, 0);
    setPadding(labelNote, 10, 0, -10, 0);

    // Listener
    btnSave.addActionListener(e -> {
      saveSwitcher(panelRight, panelEditAppointment(app), panelViewAppointment(app),
          AppointmentUtil.getTitle(app));
    });

    // Panel Title
    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(labelDateTitle);
    DatePicker datePicker1 = new DatePicker();
    panelSub.add(datePicker1);
    panelSub.add(labelTimeStart);
    TimePicker timePicker1 = new TimePicker();
    panelSub.add(timePicker1);
    panelSub.add(labelTimeEnd);
    TimePicker timePicker2 = new TimePicker();
    panelSub.add(timePicker2);
    setPadding(panelSub, 0, 0, 10, 0);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelNote);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfNote);
    panelBody.add(panelSub);

    panelEditAppointment.add(panelTitle, BorderLayout.NORTH);
    panelEditAppointment.add(panelBody, BorderLayout.CENTER);
    panelEditAppointment.add(btnSave, BorderLayout.SOUTH);

    return panelEditAppointment;
  }

  private static JPanel panelAddAppointment() {

    // JPanels
    JPanel panelAddAppointment = new JPanel(new BorderLayout());
    JPanel panelTitle = newFlowLayout();
    JPanel panelBody = new JPanel();

    // JButtons
    JButton btnBack = makeBackButton("เพิ่มนัดใหม่", "นัดแพทย์");
    JButton btnAdd = makeButton("บันทึกนัด");

    // JTextFields
    // TODO: tfDoctor @ addAppointment
    JTextField tfDoctor = makeTextField(30);
    JTextField tfHospital = makeTextField(30);
    JTextField tfNote = makeTextField(40);

    // JLabels
    JLabel labelDateTitle = makeLabel("วันที่นัด");
    JLabel labelTimeStart = makeLabel("ตั้งแต่เวลา");
    JLabel labelTimeEnd = makeLabel("จนถึงเวลา");
    JLabel labelDoctor = makeLabel("แพทย์ที่นัด");
    JLabel labelHospital = makeLabel("ชื่อสถานพยาบาล");
    JLabel labelNote = makeLabel("หมายเหตุการนัด");

    // Styling
    panelBody.setLayout(new BoxLayout(panelBody, BoxLayout.PAGE_AXIS));
    setPadding(panelAddAppointment, -11, 0, 20, -18);
    setPadding(panelBody, 0, 0, 260, 28);
    setPadding(panelTitle, 0, 0, 20);
    setPadding(labelDoctor, 10, 0, -10, 0);
    setPadding(labelHospital, 10, 0, -10, 0);
    setPadding(labelNote, 10, 0, -10, 0);

    // Panel Title
    panelTitle.add(btnBack);

    JPanel panelSub = newFlowLayout();
    panelSub.add(labelDateTitle);
    DatePicker datePicker1 = new DatePicker();
    panelSub.add(datePicker1);
    panelSub.add(labelTimeStart);
    TimePicker timePicker1 = new TimePicker();
    panelSub.add(timePicker1);
    panelSub.add(labelTimeEnd);
    TimePicker timePicker2 = new TimePicker();
    panelSub.add(timePicker2);
    setPadding(panelSub, 0, 0, 10, 0);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfDoctor);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfHospital);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(labelNote);
    panelBody.add(panelSub);

    panelSub = newFlowLayout();
    panelSub.add(tfNote);
    panelBody.add(panelSub);

    panelAddAppointment.add(panelTitle, BorderLayout.NORTH);
    panelAddAppointment.add(panelBody, BorderLayout.CENTER);
    panelAddAppointment.add(btnAdd, BorderLayout.SOUTH);

    return panelAddAppointment;
  }
  
  private static JPanel makeAppointmentCard(Appointment appointment) {
    /* Creates a card that will be used on the All appointments panel only. */

    // JPanels
    JPanel panelLoopInfo = new JPanel();
    JPanel panelPic = new JPanel();
    JPanel panelInfo = new JPanel();

    Doctor appDr = appointment.getDoctor();

    // Strings
    String title = AppointmentUtil.getTitle(appointment);
    String shortInfo =
        appDr.getPrefix() + " " + appDr.getName() + " " + appointment.getHospitalName();

    // JLabels
    JLabel labelTitle = makeBoldLabel(title);
    JLabel labelShortInfo = makeLabel(shortInfo);
    JLabel labelPic = new JLabel();

    // Styling
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.X_AXIS));
    panelPic.setLayout(new BoxLayout(panelPic, BoxLayout.X_AXIS));
    panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.PAGE_AXIS));
    setPadding(panelLoopInfo, 5, 0, 20, 0);

    try {
      Image img = ImageIO.read(new File(imgPath + "/system/calendar.png"));
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
        panelRight.add(panelViewAppointment(appointment), title);
        CardLayout cl = (CardLayout) (panelRight.getLayout());
        cl.show(panelRight, title);
      }
    });

    return panelLoopInfo;
  }
}
