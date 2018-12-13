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
 * @version 0.7.14
 * @since 0.7.0
 */

public class GUIUtil implements ActionListener, KeyListener {

  private User user;

  public void listeners() {
    btnSignIn.addActionListener(this);
    btnSignUp.addActionListener(this);
    btnSkipAddingInfo.addActionListener(this);
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
            if (user.getUserFirstName().equals("")) {
              CardLayout cl = (CardLayout) (panelWelcome.getLayout());
              cl.show(panelWelcome, "เพิ่มข้อมูลส่วนตัว");
            } else {
              promptFirstMedicine();
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
      panelErrorSignUpUsername.setVisible(false);
      panelErrorSignUpPassword.setVisible(false);
    } else if (!Arrays.equals(tfPassword.getPassword(), tfPasswordConfirm.getPassword())) {
      panelNoInput.setVisible(false);
      panelErrorSignUpUsername.setVisible(false);
      panelErrorSignUpPassword.setVisible(true);
    } else {
      panelLoading.setVisible(true);
      SwingWorker<Integer, String> swingWorker = new SwingWorker<Integer, String>() {
        @Override
        protected Integer doInBackground() throws Exception {
          try {
            doSignUp(new User(tfUserName.getText()), tfPassword.getPassword());
          } catch (LoginException ex) {
            panelErrorSignUpUsername.setVisible(true);
          }
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
    Object btn = e.getSource();

    if (btn == btnSignIn) {
      executeSignIn();
    }
    if (btn == btnSignUp) {
      executeSignUp();
    }
    if (btn == btnSkipAddingInfo) {
      promptFirstMedicine();
    }

  }

  private void promptFirstMedicine() {
    if (user.getUserMedicines().size() == 0) {
      CardLayout cl = (CardLayout) (panelWelcome.getLayout());
      cl.show(panelWelcome, "เพิ่มยาตัวแรก");
    } else {
      frameWelcome.setVisible(false);
      frameMain.setVisible(true);
      frameWelcome = null;
      CardLayout cl = (CardLayout) (panelRight.getLayout());
      cl.show(panelRight, "ภาพรวม");
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
