package main;

import GUI.GUI;
import java.awt.Dimension;
import notification.NotificationFactory;

public class Main {

  public static void main(String[] args) {
    macSetup();
    NotificationFactory.showNotification("lelelelelel");

//    GUI jMedicine = new GUI(new Dimension(1280, 768));
//    jMedicine.initWelcome();
  }

  public static void macSetup() {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.name", "jMedicine");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "jMedicine");
  }
}
