package GUI;

import static GUI.GUIHelper.*;
import static GUI.GUI.*;
import static api.Login.doSignIn;

import api.LoginException;
import core.User;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.swing.SwingWorker;

public class GUIUtil implements ActionListener, KeyListener {

  private User user;

  public void listeners() {
    btnSignIn.addActionListener(this);
    tfUserName.addKeyListener(this);
    tfPassword.addKeyListener(this);
    btnSkip.addActionListener(this);
  }

  public User getSignedInUser() {
    return this.user;
  }

  void executeSignIn() {
    if (tfUserName.getText().equals("") || tfPassword.getPassword().equals("")) {
      panelErrorSignIn.setVisible(true);
    } else {
      panelErrorSignIn.setVisible(false);
      panelLoadingSignIn.setVisible(true);
      SwingWorker<Integer, String> swingWorker = new SwingWorker<Integer, String>() {
        @Override
        protected Integer doInBackground() throws Exception {
          String username = tfUserName.getText();
          char[] password = tfPassword.getPassword();
          try {
            user = doSignIn(username, password);
          } catch (LoginException ignored) {
            panelLoadingSignIn.setVisible(false);
            panelErrorSignIn.setVisible(true);
          } catch (NoSuchAlgorithmException | SQLException ex) {
            ex.printStackTrace();
          }
          return null;
        }

        @Override
        protected void done() {
          if (user != null) {
            main();
            if (user.getUserMedicines().size() > 0) {
              frameWelcome.setVisible(false);
              frameMain.setVisible(true);
              frameWelcome = null;
              CardLayout cl = (CardLayout) (panelRight.getLayout());
              cl.show(panelRight, "ภาพรวม");
            } else {
              CardLayout cl = (CardLayout) (panelWelcome.getLayout());
              cl.show(panelWelcome, "เพิ่มยาตัวแรก");
            }
          } else {
            fireErrorDialog("User ไม่ได้ถูกสร้าง");
          }
        }
      };
      swingWorker.execute();
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String btnCommand = e.getActionCommand();

    switch (btnCommand) {
      case "ข้ามขั้นตอนนี้":
        if (frameWelcome == null) {
          CardLayout cl = (CardLayout) (panelRight.getLayout());
          cl.show(panelRight, "ยาทั้งหมด");
        } else {
          frameWelcome.setVisible(false);
          frameMain.setVisible(true);
          frameWelcome = null;
        }
        break;

      case "เข้าสู่ระบบ":
        executeSignIn();
        break;
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getSource() == tfUserName || e.getSource() == tfPassword) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        executeSignIn();
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }
}
