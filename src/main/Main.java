package main;

import GUI.GUI;

import java.awt.*;

public class Main {

  public static void main(String[] args) {
    macSetup();

    GUI jMedicine = new GUI(new Dimension(1280, 768));
    jMedicine.initWelcome();
  }

  public static void macSetup() {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.application.name", "jMedicine");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "jMedicine");
  }
}
