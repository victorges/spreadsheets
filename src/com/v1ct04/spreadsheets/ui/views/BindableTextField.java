package com.v1ct04.spreadsheets.ui.views;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Text field that can "bind" to another one, meaning that it will start listening to changes on the text of that other
 * text field and copy its value to itself.
 */
public class BindableTextField extends JTextField {

  private JTextField mBoundedTextField;

  private DocumentListener mDocumentListener = new CopyDocumentListener();

  public void bindTo(JTextField textField) {
    unbind();
    mBoundedTextField = textField;
    mBoundedTextField.getDocument().addDocumentListener(mDocumentListener);
    setText(mBoundedTextField.getText());
  }

  public void unbind() {
    if (mBoundedTextField != null) {
      mBoundedTextField.getDocument().removeDocumentListener(mDocumentListener);
      mBoundedTextField = null;
    }
  }

  private void onBoundedTextChanged() {
    if (mBoundedTextField != null) {
      setText(mBoundedTextField.getText());
    }
  }

  private class CopyDocumentListener implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent e) {
      onBoundedTextChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      onBoundedTextChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      onBoundedTextChanged();
    }
  }
}
