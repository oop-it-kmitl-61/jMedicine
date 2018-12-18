package GUI;

import static GUI.GUI.*;
import static GUI.GUIHelper.fireDBErrorDialog;
import static api.Login.doSignIn;
import static api.Login.doSignUp;
import static core.Core.getUser;
import static core.Core.setUser;

import api.LoginException;
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
import org.postgresql.util.PSQLException;


/**
 * An utility class for GUI.java
 *
 * @author jMedicine
 * @version 0.9.3
 * @since 0.7.0
 */

public class GUIUtil implements ActionListener, KeyListener {


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
            setUser(doSignIn(username, password));
          } catch (LoginException ignored) {
            panelLoading.setVisible(false);
            panelErrorSignIn.setVisible(true);
          } catch (NoSuchAlgorithmException | SQLException ex) {
            ex.printStackTrace();
            fireDBErrorDialog();
          }
          return null;
        }

        @Override
        protected void done() {
          if (getUser() != null) {
            main();
            if (getUser().getUserFirstName().equals("")) {
              panelWelcome.add(panelFirstInfo(), "เพิ่มข้อมูลส่วนตัว");
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
      showErrorPanel(0);
    } else if (tfUserName.getText().length() < 4 || tfUserName.getText().length() > 32 || !isValidUsername(tfUserName.getText())) {
      showErrorPanel(2);
    } else if (!Arrays.equals(tfPassword.getPassword(), tfPasswordConfirm.getPassword())) {
      showErrorPanel(3);
    } else if (tfPassword.getPassword().length < 6) {
      showErrorPanel(4);
    } else {
      panelLoading.setVisible(true);
      showErrorPanel(-1);
      SwingWorker<Integer, String> swingWorker = new SwingWorker<Integer, String>() {
        @Override
        protected Integer doInBackground() throws Exception {
          try {
            doSignUp(new User(tfUserName.getText()), tfPassword.getPassword());
            executeSignIn();
          } catch (LoginException ex) {
            showErrorPanel(1);
            panelLoading.setVisible(false);
          } catch (NoSuchAlgorithmException | SQLException ex) {
            ex.printStackTrace();
            fireDBErrorDialog();
          }
          return null;
        }
      };
      swingWorker.execute();
    }
  }

  public static void showErrorPanel(int panelNumber) {
    panelNoInput.setVisible(panelNumber == 0);
    panelErrorSignUpUsername.setVisible(panelNumber == 1);
    panelErrorSignUpUsernameValid.setVisible(panelNumber == 2);
    panelErrorSignUpPassword.setVisible(panelNumber == 3);
    panelErrorSignUpPasswordLength.setVisible(panelNumber == 4);
  }

  private boolean isValidUsername(String username) {
    String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    boolean isFound;

    for (int i = 0; i < username.length(); i++) {
      isFound = false;
      for (int j = 0; j < validCharacters.length(); j++) {
        if (username.charAt(i) == validCharacters.charAt(j)) {
          isFound = true;
          break;
        }
      }
      if (!isFound) {
        return false;
      }
    }
    return true;
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

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (isSignInPage && (e.getSource().equals(tfUserName) || e.getSource().equals(tfPassword))) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        executeSignIn();
      }
    }
    if (isSignUpPage && (e.getSource().equals(tfUserName) || e.getSource().equals(tfPassword)
        || e.getSource().equals(tfPasswordConfirm))) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        executeSignUp();
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }
}
