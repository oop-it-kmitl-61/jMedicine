package main;

import GUI.GUI;
import java.awt.Dimension;

public class Main {

  public static void main(String[] args) {
    GUI jMedicine = new GUI(new Dimension(1024, 768));
    jMedicine.initWelcome();
  }
}
