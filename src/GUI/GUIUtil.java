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
      panelNoInput.setVisible(true);
      panelErrorSignUpUsername.setVisible(false);
      panelErrorSignUpUsernameLength.setVisible(false);
      panelErrorSignUpPassword.setVisible(false);
      panelErrorSignUpPasswordLength.setVisible(false);
    } else if (tfUserName.getText().length() < 4) {
      panelNoInput.setVisible(false);
      panelErrorSignUpUsername.setVisible(false);
      panelErrorSignUpUsernameLength.setVisible(true);
      panelErrorSignUpPassword.setVisible(false);
      panelErrorSignUpPasswordLength.setVisible(false);
    } else if (!Arrays.equals(tfPassword.getPassword(), tfPasswordConfirm.getPassword())) {
      panelNoInput.setVisible(false);
      panelErrorSignUpUsername.setVisible(false);
      panelErrorSignUpUsernameLength.setVisible(false);
      panelErrorSignUpPassword.setVisible(true);
      panelErrorSignUpPasswordLength.setVisible(false);
    } else if (tfPassword.getPassword().length < 6) {
      panelNoInput.setVisible(false);
      panelErrorSignUpUsername.setVisible(false);
      panelErrorSignUpUsernameLength.setVisible(false);
      panelErrorSignUpPassword.setVisible(false);
      panelErrorSignUpPasswordLength.setVisible(true);
    } else {
      panelLoading.setVisible(true);
      panelNoInput.setVisible(false);
      panelErrorSignUpUsername.setVisible(false);
      panelErrorSignUpUsernameLength.setVisible(false);
      panelErrorSignUpPassword.setVisible(false);
      panelErrorSignUpPasswordLength.setVisible(false);
      SwingWorker<Integer, String> swingWorker = new SwingWorker<Integer, String>() {
        @Override
        protected Integer doInBackground() throws Exception {
          try {
            doSignUp(new User(tfUserName.getText()), tfPassword.getPassword());
            executeSignIn();
          } catch (LoginException ex) {
            panelErrorSignUpUsername.setVisible(true);
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
