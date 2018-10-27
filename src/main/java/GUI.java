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
  private JPanel panelTitle, panelLoop, panelLoopInfo1, panelLoopInfo2;
  private JLabel space, labelToday, labelTitle01, labelTitle02, labelTitle03, labelTitle04, labelTitle05, labelTitle06;
  private GridBagConstraints gbc;
  private Dimension windowSize, minSize;

  public GUI(Dimension windowSize) {
    this.windowSize = windowSize;
    this.minSize = new Dimension(640, 480);
  }

  public void init() {

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
    JButton buttons[] = {
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
    panelLoop = new JPanel();
    panelLoopInfo1 = new JPanel();
    panelLoopInfo2 = new JPanel();

    // Set Layout
    panelLeft.setBorder(BorderFactory.createEmptyBorder(20, 5, 5, 5));
    panelTitle.setLayout(new BorderLayout());
    panelRight.setBorder(BorderFactory.createEmptyBorder(25, 5, 10, 5));
    panelLoop.setLayout(new BoxLayout(panelLoop, BoxLayout.PAGE_AXIS));
    panelLoop.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 5));
    panelLoopInfo1.setLayout(new BoxLayout(panelLoopInfo1, BoxLayout.PAGE_AXIS));
    panelLoopInfo1.setBorder(new CompoundBorder(
        BorderFactory.createEmptyBorder(5, 0, 20, 0),
        new RoundedBorder(10)
    ));
    panelLoopInfo2.setLayout(new BoxLayout(panelLoopInfo2, BoxLayout.PAGE_AXIS));
    panelLoopInfo2.setBorder(new CompoundBorder(
        BorderFactory.createEmptyBorder(5, 0, 20, 0),
        new RoundedBorder(10)
        ));

    // Left navigation
    int buttonY = 0;
    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.ipady = 20;
    gbc.ipadx = 10;
    gbc.weightx = 0.0;
    gbc.weighty = 1;
    gbc.gridwidth = 8;
    gbc.gridx = 0;

    for(JButton button: buttons){
      button.addActionListener(new ActionListener() {
        // Switch between sub panels
        @Override
        public void actionPerformed(ActionEvent e) {
          CardLayout cl = (CardLayout)(panelRight.getLayout());
          cl.show(panelRight, e.getActionCommand());
        }
      });
      gbc.gridy = buttonY;
      buttonY++;
      panelLeft.add(button, gbc);
    }

    gbc.weighty = 1000;
    panelLeft.add(space, gbc);

    // TODO: panelSub01
    panelTitle.add(labelToday);

    JLabel labelTime01 = new JLabel("12.30 น. (อีก 1 ชั่วโมง)");
    JLabel labelMed01 = new JLabel("Prednisolone (ยาแก้อักเสบ)");
    JLabel labelWhen01 = new JLabel("หลังอาหาร 1 เม็ด");
    labelTime01.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelMed01.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelWhen01.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelTime01.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    labelTime01.setFont(new Font(labelTime01.getFont().getName(), Font.BOLD, 15));
    panelLoopInfo1.add(labelTime01);
    panelLoopInfo1.add(labelMed01);
    panelLoopInfo1.add(labelWhen01);
    panelLoopInfo1.add(Box.createHorizontalGlue());

    JLabel labelTime02 = new JLabel("18.30 น. (อีก 7 ชั่วโมง)");
    JLabel labelMed02 = new JLabel("Prednisolone (ยาแก้อักเสบ)");
    JLabel labelWhen02 = new JLabel("หลังอาหาร 1 เม็ด");
    labelTime02.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelMed02.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelWhen02.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelTime02.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    labelTime02.setFont(new Font(labelTime02.getFont().getName(), Font.BOLD, 15));
    panelLoopInfo2.add(labelTime02);
    panelLoopInfo2.add(labelMed02);
    panelLoopInfo2.add(labelWhen02);
    panelLoopInfo2.add(Box.createHorizontalGlue());

    panelLoop.add(panelLoopInfo1);
    panelLoop.add(panelLoopInfo2);

    panelSub01.add(panelTitle, BorderLayout.NORTH);
    panelSub01.add(panelLoop);

    // TODO: panelSub02
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle02);
    panelSub02.add(panelTitle, BorderLayout.NORTH);

    // TODO: panelSub03
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle03);
    panelSub03.add(panelTitle, BorderLayout.NORTH);

    // TODO: panelSub04
    panelTitle = new JPanel(new BorderLayout());
    panelTitle.add(labelTitle04);
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

}
