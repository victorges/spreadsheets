package com.v1ct04.spreadsheets;

import com.v1ct04.spreadsheets.ui.SpreadsheetFrame;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

public class SpreadsheetApplication {

  public static final String APP_NAME = "Spreadsheets";

  private static final float DEFAULT_WINDOW_SIZE_RATIO = 2.0f / 3;

  public static void main(String[] args) {
    final String filename = args.length == 1 ? args[0] : null;
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          createAndShowGUI(filename);
        } catch (Exception e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null,
                  "Error opening Spreadsheets application window.",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
        }
      }
    });
  }

  private static void createAndShowGUI(String filename) throws Exception {
    // for a better experience on OS X
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_NAME);
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    SpreadsheetFrame spreadsheetFrame = new SpreadsheetFrame();

    Dimension scrDim = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frmDim = new Dimension((int) (scrDim.width * DEFAULT_WINDOW_SIZE_RATIO),
            (int) (scrDim.height * DEFAULT_WINDOW_SIZE_RATIO));
    spreadsheetFrame.setSize(frmDim);
    spreadsheetFrame.setLocation((scrDim.width - frmDim.width) / 2, (scrDim.height - frmDim.height) / 2);

    spreadsheetFrame.setVisible(true);

    if (filename != null) {
      spreadsheetFrame.openSpreadsheet(Paths.get(filename));
    }
  }
}
