package com.v1ct04.spreadsheets.ui.views;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Helper for a cell renderer that renders cells with a custom background color.
 */
public class ColorCellRenderer extends DefaultTableCellRenderer {

  private Color mBackgroundColor;

  public ColorCellRenderer(Color mBackgroundColor) {
    this.mBackgroundColor = mBackgroundColor;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    component.setBackground(mBackgroundColor);
    return component;
  }
}
