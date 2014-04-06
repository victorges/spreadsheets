package com.v1ct04.spreadsheets.ui.views;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Mod to JFileChooser for a file chooser that allows only one kind of file extension. Will add the extension to the
 * file name if trying to save with a file without the single allowed extension. Will also display a confirmation dialog
 * in case the current file already exists. Do not call setFileFilter method or the inner logic for the extension safety
 * may get broken.
 */
public class SingleExtensionFileChooser extends JFileChooser {

  private String mFileExtension;

  public void setFileExtension(String fileExtension, String description) {
    mFileExtension = fileExtension;
    FileFilter filter = new FileNameExtensionFilter(description, fileExtension);
    super.setFileFilter(filter);
  }

  @Override
  public void approveSelection() {
    if (getDialogType() != SAVE_DIALOG) {
      super.approveSelection();
    } else {
      File file = getSelectedFile();
      File fixedFile = fixFileExtension(file);
      if (fixedFile != file) {
        setSelectedFile(fixedFile);
        return;
      }
      if (file.exists()) {
        int result = JOptionPane.showConfirmDialog(this,
                "“" + file.getName() + "” already exists in " + file.getParentFile().getName() + ". Do you want to replace it?",
                "Save",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (result != JOptionPane.YES_OPTION) return;
      }
      super.approveSelection();
    }
  }

  private File fixFileExtension(File file) {
    if (mFileExtension != null && !getFileFilter().accept(file)) {
      String filename = file.toString();
      filename += '.' + mFileExtension;
      file = new File(filename);
    }
    return file;
  }
}
