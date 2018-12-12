package GUI;

import static GUI.GUI.*;
import static api.Login.doSignIn;
import static api.Login.doSignUp;

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
import java.util.Arrays;
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

  void executeSignIn() {
    panelErrorSignUpUsername.setVisible(false);
    panelErrorSignUpPassword.setVisible(false);
    if (tfUserName.getText().equals("") || tfPassword.getPassword().equals("")) {
      panelNoInput.setVisible(true);
      panelErrorSignIn.setVisible(false);
    } else {
      panelNoInput.setVisible(false);
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
    panelErrorSignIn.setVisible(false);
    if (tfUserName.getText().equals("")) {
      panelNoInput.setVisible(true);
      panelErrorSignUpPassword.setVisible(false);
    } else if (!Arrays.equals(tfPassword.getPassword(), tfPasswordConfirm.getPassword())) {
      panelNoInput.setVisible(false);
      panelErrorSignUpPassword.setVisible(true);
    } else {
      panelLoading.setVisible(true);
      SwingWorker<Integer, String> swingWorker = new SwingWorker<Integer, String>() {
        @Override
        protected Integer doInBackground() throws Exception {
          // TODO: ASSUME THAT doSignUp() HAD ALREADY THROWN SOMETHING IF DUPLICATED USER
          doSignUp(new User(tfUserName.getText()), tfPassword.getPassword());
          return null;
        }

        @Override
        protected void done() {
          executeSignIn();
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
    if (isSignInPage && (e.getSource() == tfUserName || e.getSource() == tfPassword)) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        executeSignIn();
      }
    }
    if (isSignUpPage && (e.getSource() == tfUserName || e.getSource() == tfPassword
        || e.getSource() == tfPasswordConfirm)) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        executeSignUp();
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }
}
