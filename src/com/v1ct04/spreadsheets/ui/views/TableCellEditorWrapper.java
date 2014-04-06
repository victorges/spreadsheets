package com.v1ct04.spreadsheets.ui.views;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

/**
 * Helper class to wrap another TableCellEditor, forwarding all calls to the other one. This is so that one can easily
 * add functionality to another TableCellEditor instance without having to know its class and override it at all. We
 * couldn't just use the default table cell editor delegate, since what we really need is to add some functionality to
 * the getTableCellEditorComponent method, and that is exactly the one which the delegate isn't called.
 */
public class TableCellEditorWrapper implements TableCellEditor {

  protected final TableCellEditor mWrappedTableCellEditor;

  protected TableCellEditorWrapper(TableCellEditor wrappedTableCellEditor) {
    mWrappedTableCellEditor = wrappedTableCellEditor;
  }

  @Override
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    return mWrappedTableCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
  }

  @Override
  public Object getCellEditorValue() {
    return mWrappedTableCellEditor.getCellEditorValue();
  }

  @Override
  public boolean isCellEditable(EventObject anEvent) {
    return mWrappedTableCellEditor.isCellEditable(anEvent);
  }

  @Override
  public boolean shouldSelectCell(EventObject anEvent) {
    return mWrappedTableCellEditor.shouldSelectCell(anEvent);
  }

  @Override
  public boolean stopCellEditing() {
    return mWrappedTableCellEditor.stopCellEditing();
  }

  @Override
  public void cancelCellEditing() {
    mWrappedTableCellEditor.cancelCellEditing();
  }

  @Override
  public void addCellEditorListener(CellEditorListener l) {
    mWrappedTableCellEditor.addCellEditorListener(l);
  }

  @Override
  public void removeCellEditorListener(CellEditorListener l) {
    mWrappedTableCellEditor.removeCellEditorListener(l);
  }
}
