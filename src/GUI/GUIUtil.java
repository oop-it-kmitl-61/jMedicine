package GUI;

import static GUI.GUI.*;
import static api.Login.doSignIn;

import api.LoginException;
import core.Core;
import core.User;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.swing.SwingWorker;

/**
 * An utility class for GUI.java
 *
 * @author jMedicine
 * @version 0.7.11
 * @since 0.7.0
 */

public class GUIUtil implements ActionListener, KeyListener {

  private User user;

  public void listeners() {
    btnSignIn.addActionListener(this);
    btnSignUp.addActionListener(this);
    tfUserName.addKeyListener(this);
    tfPassword.addKeyListener(this);
  }

  public User getSignedInUser() {
    return this.user;
  }

  void executeSignIn() {
    if (tfUserName.getText().equals("") || tfPassword.getPassword().equals("")) {
      panelErrorSignIn.setVisible(true);
    } else {
      panelErrorSignIn.setVisible(false);
      panelLoading.setVisible(true);
      SwingWorker<Integer, String> swingWorker = new SwingWorker<Integer, String>() {
        @Override
        protected Integer doInBackground() throws Exception {
          String username = tfUserName.getText();
          char[] password = tfPassword.getPassword();
          try {
            user = doSignIn(username, password);
            Core.setUser(user);
          } catch (LoginException ignored) {
            panelLoading.setVisible(false);
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
          }
        }
      };
      swingWorker.execute();
    }
  }

  void executeSignUp() {
    if (tfUserName.getText().equals("")) {
      // CHANGE THIS IF STATEMENT TO CHECK IF PASSWORD MISMATCH BEFORE STARTING doSignUp()
    } else {
      panelLoading.setVisible(true);
      SwingWorker<Integer, String> swingWorker = new SwingWorker<Integer, String>() {
        @Override
        protected Integer doInBackground() throws Exception {
          // doSignUp() TRY CATCH HERE
          return null;
        }

        @Override
        protected void done() {
          // PLEASE LOGIN THE USER THAT JUST SIGNED UP HERE USING doSignIn()
          if (user != null) {
            main();
            CardLayout cl = (CardLayout) (panelWelcome.getLayout());
            cl.show(panelWelcome, "เพิ่มยาตัวแรก");
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
      case "เข้าสู่ระบบ":
        executeSignIn();
        break;

      case "ลงทะเบียน":
        executeSignUp();
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
