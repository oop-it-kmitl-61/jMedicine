// import com.teamdev.jxmaps.MapViewOptions;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import javax.swing.*;
import javax.swing.border.CompoundBorder;

public class GUI {
  private JFrame mainFrame;
  private JPanel panelMain, panelLeft, panelRight;
  private JPanel panelSub01, panelSub02, panelSub03, panelSub04, panelSub05, panelSub06;
  private JPanel panelTitle, panelLoop, cardLoop;
  private JLabel space, labelToday, labelTitle01, labelTitle02, labelTitle03, labelTitle04, labelTitle05, labelTitle06;
  private GridBagConstraints gbc;
  private JButton buttons[];
  private Dimension windowSize, minSize;
  private Color mainBlue;

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
    space = new JLabel();
    JLabel titles[] = {labelToday, labelTitle01, labelTitle02, labelTitle03, labelTitle04, labelTitle05, labelTitle06};

    // Set font size for title
    for (JLabel title: titles) {
      title.setFont(new Font(labelToday.getFont().getName(), Font.BOLD, 24));
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
    panelLeft.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
    panelLeft.setBackground(mainBlue);
    panelTitle.setLayout(new BorderLayout());
    panelRight.setBorder(BorderFactory.createEmptyBorder(25, 20, 10, 15));

    // Left navigation
    int buttonY = 0;
    gbc = new GridBagConstraints();
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
    panelTitle.add(labelTitle02);
    panelLoop = newPanelLoop();
    // Make Loop
    cardLoop = makeDetailCard("Prednisolone (ยาแก้อักเสบ)", "เหลืออยู่ 2 เม็ด หมดอายุ 31/12/2560");
    panelLoop.add(cardLoop);
    cardLoop = makeDetailCard("CPM (ยาแก้แพ้)", "เหลืออยู่ 20 เม็ด หมดอายุ 20/11/2561");
    panelLoop.add(cardLoop);
    // End Make Loop
    panelSub02.add(cardLoop);
    panelSub02.add(panelTitle, BorderLayout.NORTH);

    // TODO: panelSub03
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle03);
    panelLoop = newPanelLoop();
    // Make Loop
    cardLoop = makeDetailCard("1/12/2561 เวลา 09.00 น. - 16.00 น.", "นพ.เก่ง จัง โรงพยาบาลบำรุงราษฎร์");
    panelLoop.add(cardLoop);
    panelSub03.add(cardLoop);
    panelSub03.add(panelTitle, BorderLayout.NORTH);

    // TODO: panelSub04
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle04);
    panelLoop = newPanelLoop();
    // Make Loop
    cardLoop = makeDetailCard("นพ.เก่ง จัง", "แผนกหู คอ จมูก โรงพยาบาลบำรุงราษฎร์");
    panelLoop.add(cardLoop);
    panelSub04.add(cardLoop);
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
    panelSub06.add(panelTitle, BorderLayout.NORTH);

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
    mainFrame = new JFrame("jMedicine");
    mainFrame.add(panelMain);
    mainFrame.setMinimumSize(this.minSize);
    mainFrame.setSize(this.windowSize);
    mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    mainFrame.setVisible(true);
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
    labelTime.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    labelTime.setFont(new Font(labelTime.getFont().getName(), Font.BOLD, 15));
    panelLoopInfo.add(labelTime);
    panelLoopInfo.add(labelMed);
    panelLoopInfo.add(labelAmount);
    panelLoopInfo.add(Box.createHorizontalGlue());
    return panelLoopInfo;
  }

  // TODO: Example parameters will be replaced with data from DB
  public JPanel makeDetailCard(String example01, String example02) {
    JPanel panelLoopInfo = new JPanel();
    panelLoopInfo.setLayout(new BoxLayout(panelLoopInfo, BoxLayout.PAGE_AXIS));
    panelLoopInfo.setBorder(BorderFactory.createEmptyBorder(5, 0, 20, 0));
    JLabel labelMed = new JLabel(example01);
    JLabel labelShortInfo = new JLabel(example02);
    labelMed.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    labelMed.setFont(new Font(labelMed.getFont().getName(), Font.BOLD, 15));
    panelLoopInfo.add(labelMed);
    panelLoopInfo.add(labelShortInfo);
    panelLoopInfo.add(Box.createHorizontalGlue());
    return panelLoopInfo;
  }

  public JPanel newPanelLoop() {
    JPanel panelLoop = new JPanel();
    panelLoop.setLayout(new BoxLayout(panelLoop, BoxLayout.PAGE_AXIS));
    panelLoop.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 5));
    return panelLoop;
  }

}
