package com.v1ct04.spreadsheets.ui;

import com.v1ct04.spreadsheets.SpreadsheetApplication;
import com.v1ct04.spreadsheets.model.CellLocation;
import com.v1ct04.spreadsheets.model.Spreadsheet;
import com.v1ct04.spreadsheets.model.SpreadsheetUtils;
import com.v1ct04.spreadsheets.persistence.SpreadsheetPersistenceManager;
import com.v1ct04.spreadsheets.ui.views.BindableTextField;
import com.v1ct04.spreadsheets.ui.views.ColorCellRenderer;
import com.v1ct04.spreadsheets.ui.views.SingleExtensionFileChooser;
import com.v1ct04.spreadsheets.ui.views.TableCellEditorWrapper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Path;

public class SpreadsheetFrame extends JFrame {

  private static final int MENU_SHORTCUT_KEY = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

  private static final Color ROW_HEADER_COLOR = new Color(230, 230, 230);
  private static final Color TABLE_CELL_COLOR = new Color(250, 250, 250);


  private static final String NEW_MENU_ITEM_NAME = "New";
  private static final String OPEN_MENU_ITEM_NAME = "Open...";
  private static final String SAVE_MENU_ITEM_NAME = "Save";
  private static final String SAVE_AS_MENU_ITEM_NAME = "Save As...";

  private static final String COPY_MENU_ITEM_NAME = "Copy";
  private static final String COPY_FORMULA_MENU_ITEM_NAME = "Copy Formula";
  private static final String PASTE_MENU_ITEM_NAME = "Paste";

  private int mDisplayedColumnsCount = 26;
  private int mDisplayedRowsCount = 50;

  private AbstractTableModel mTableModel;
  private JTable mTableView;
  private BindableTextField mFormulaTextField;
  private SingleExtensionFileChooser mFileChooser;

  private JMenu mEditMenu;

  private Spreadsheet mSpreadsheet;
  private CellDisplayValueManager mCellDisplayValueManager;
  private boolean mHasEdits;

  private SpreadsheetPersistenceManager mPersistenceManager = new SpreadsheetPersistenceManager();
  private Path mCurrentFile;

  public SpreadsheetFrame() {
    loadMenu();
    loadView();

    setSpreadsheet(new Spreadsheet());
    setCurrentFile(null);

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new SpreadsheetWindowListener());
  }

  public void openSpreadsheet(Path file) {
    try {
      setSpreadsheet(mPersistenceManager.readSpreadsheetFromFile(file));
      setCurrentFile(file);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this,
              e.toString(),
              "Failed to open document",
              JOptionPane.ERROR_MESSAGE);
    }
  }

  private void setSpreadsheet(Spreadsheet spreadsheet) {
    mSpreadsheet = spreadsheet;
    mCellDisplayValueManager = new CellDisplayValueManager(spreadsheet);
    mHasEdits = false;
    mDisplayedColumnsCount = mSpreadsheet.columnCount() + 25;
    mDisplayedRowsCount = mSpreadsheet.rowCount() + 50;
    mTableModel.fireTableStructureChanged();
  }

  private void loadView() {
    // main views
    mTableModel = new SpreadsheetTableModel();

    mTableView = new JTable(mTableModel);
    mTableView.setRowSelectionAllowed(false);
    mTableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    mTableView.setFillsViewportHeight(true);
    mTableView.setDefaultRenderer(String.class, new ColorCellRenderer(TABLE_CELL_COLOR));
    mTableView.setDefaultEditor(String.class, new SpreadsheetCellEditor(mTableView.getDefaultEditor(String.class)));
    mTableView.getSelectionModel().addListSelectionListener(new SpreadsheetSelectionListener());
    mTableView.getColumnModel().getSelectionModel().addListSelectionListener(new SpreadsheetSelectionListener());

    JTable rowHeaderTable = new JTable(new RowHeaderTableModel());
    rowHeaderTable.setCellSelectionEnabled(false);
    rowHeaderTable.setPreferredScrollableViewportSize(new Dimension(50, Integer.MAX_VALUE));
    rowHeaderTable.setDefaultRenderer(Object.class, new ColorCellRenderer(ROW_HEADER_COLOR));

    mFormulaTextField = new BindableTextField();
    mFormulaTextField.setEditable(false);
    mFormulaTextField.setFocusable(false);

    JScrollPane scrollView = new JScrollPane(mTableView);
    scrollView.setRowHeaderView(rowHeaderTable);

    add(mFormulaTextField, BorderLayout.BEFORE_FIRST_LINE);
    add(scrollView, BorderLayout.CENTER);

    // helpers

    mFileChooser = new SingleExtensionFileChooser();
    String extension = SpreadsheetPersistenceManager.SPREADSHEET_FILE_EXTENSION;
    mFileChooser.setFileExtension(extension, "Spreadsheets file (." + extension + ")");
  }

  private void loadMenu() {
    ActionListener actionListener = new MenuItemsActionHandler();
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    fileMenu.add(createMenuItem(NEW_MENU_ITEM_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_N, MENU_SHORTCUT_KEY), actionListener));
    fileMenu.add(createMenuItem(OPEN_MENU_ITEM_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU_SHORTCUT_KEY), actionListener));

    fileMenu.addSeparator();

    fileMenu.add(createMenuItem(SAVE_MENU_ITEM_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_SHORTCUT_KEY), actionListener));
    fileMenu.add(createMenuItem(SAVE_AS_MENU_ITEM_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_SHORTCUT_KEY | InputEvent.SHIFT_MASK), actionListener));

    // need a field for this to enable/disable with selection status
    mEditMenu = new JMenu("Edit");

    mEditMenu.add(createMenuItem(COPY_MENU_ITEM_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_C, MENU_SHORTCUT_KEY), actionListener));
    mEditMenu.add(createMenuItem(COPY_FORMULA_MENU_ITEM_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_C, MENU_SHORTCUT_KEY | InputEvent.SHIFT_MASK), actionListener));
    mEditMenu.add(createMenuItem(PASTE_MENU_ITEM_NAME, KeyStroke.getKeyStroke(KeyEvent.VK_V, MENU_SHORTCUT_KEY), actionListener));

    menuBar.add(fileMenu);
    menuBar.add(mEditMenu);
    setJMenuBar(menuBar);
  }

  private JMenuItem createMenuItem(String name, KeyStroke accelerator, ActionListener actionListener) {
    JMenuItem item = new JMenuItem(name);
    item.setName(name);
    item.setAccelerator(accelerator);
    item.addActionListener(actionListener);
    return item;
  }

  private void setCurrentFile(Path currentFile) {
    mCurrentFile = currentFile;
    if (currentFile != null) {
      setTitle(SpreadsheetApplication.APP_NAME + " - " + currentFile.getFileName().toString());
    } else {
      setTitle(SpreadsheetApplication.APP_NAME);
    }
  }

  private boolean saveToDisk(boolean mustChooseFile) {
    Path fileToSave = mustChooseFile ? null : mCurrentFile;
    if (fileToSave == null) {
      fileToSave = chooseFile(JFileChooser.SAVE_DIALOG);
    }
    if (fileToSave != null) {
      try {
        mPersistenceManager.writeSpreadsheetToFile(mSpreadsheet, fileToSave);
        setCurrentFile(fileToSave);
        mHasEdits = false;
        return true;
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this,
                ex.toString(),
                "Failed to save document",
                JOptionPane.ERROR_MESSAGE);
      }
    }
    return false;
  }

  private Path chooseFile(int dialogType) {
    if (mCurrentFile != null) mFileChooser.setSelectedFile(mCurrentFile.toFile());
    else mFileChooser.setCurrentDirectory(null);

    int result = JFileChooser.CANCEL_OPTION;
    if (dialogType == JFileChooser.OPEN_DIALOG) {
      result = mFileChooser.showOpenDialog(this);
    } else if (dialogType == JFileChooser.SAVE_DIALOG) {
      result = mFileChooser.showSaveDialog(this);
    }

    if(result == JFileChooser.APPROVE_OPTION) {
      return mFileChooser.getSelectedFile().toPath();
    } else {
      return null;
    }
  }

  private void showSaveConfirmationIfNecessaryAndRun(String message, String title, Runnable runnable) {
    if (!mHasEdits) {
      runnable.run();
    } else {
      int result = JOptionPane.showConfirmDialog(SpreadsheetFrame.this, message, title,
              JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
      switch (result) {
        case JOptionPane.YES_OPTION:
          if (saveToDisk(false)) {
            runnable.run();
          }
          break;
        case JOptionPane.NO_OPTION:
          runnable.run();
          break;
      }
    }
  }

  private CellLocation getSelectedCellLocation() {
    if (mTableView.getSelectionModel().isSelectionEmpty()) {
      return null;
    }
    return new CellLocation(mTableView.getSelectedColumn(), mTableView.getSelectedRow());
  }

  private class SpreadsheetTableModel extends AbstractTableModel {

    @Override
    public String getColumnName(int column) {
      return SpreadsheetUtils.columnNameForIndex(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      return String.class;
    }

    @Override
    public int getRowCount() {
      return mDisplayedRowsCount;
    }

    @Override
    public int getColumnCount() {
      return mDisplayedColumnsCount;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return true;
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
      if (mDisplayedColumnsCount - columnIndex < 5) {
        mDisplayedColumnsCount += 10;
        fireTableStructureChanged();
      }
      return mCellDisplayValueManager.getDisplayValueOfCellAt(columnIndex, rowIndex).toString();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
      mHasEdits |= mSpreadsheet.setCellContentAt(columnIndex, rowIndex, value.toString());
      // we may have interdependent cells, fire a whole table data changed event
      fireTableDataChanged();
      mCellDisplayValueManager.invalidateCache();
    }
  }

  private class SpreadsheetCellEditor extends TableCellEditorWrapper {

    public SpreadsheetCellEditor(TableCellEditor wrappedTableCellEditor) {
      super(wrappedTableCellEditor);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      Component component = mWrappedTableCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
      if (component instanceof JTextField) {
        // when editing, we should start from the actual cell value, not the rendered value
        final JTextField textField = (JTextField) component;
        textField.setText(mSpreadsheet.getCellContentAt(column, row));
        mFormulaTextField.bindTo(textField);
      }
      return component;
    }

    @Override
    public boolean stopCellEditing() {
      mFormulaTextField.unbind();
      return super.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
      mFormulaTextField.unbind();
      super.cancelCellEditing();
    }
  }

  private class RowHeaderTableModel extends AbstractTableModel {

    @Override
    public int getRowCount() {
      return mDisplayedRowsCount;
    }

    @Override
    public int getColumnCount() {
      return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (mDisplayedRowsCount - rowIndex < 10) {
        mDisplayedRowsCount += 20;
        fireTableDataChanged();
        mTableModel.fireTableDataChanged();
      }
      return rowIndex + 1;
    }
  }

  private class SpreadsheetSelectionListener implements ListSelectionListener {

    @Override
    public void valueChanged(ListSelectionEvent e) {
      CellLocation selectedLocation = getSelectedCellLocation();
      boolean hasSelection = (selectedLocation != null);
      if (hasSelection) {
        String cellContent = mSpreadsheet.getCellContentAt(selectedLocation);
        mFormulaTextField.setText(cellContent);
      }
      mEditMenu.setEnabled(hasSelection);
    }
  }

  private class MenuItemsActionHandler implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ev) {
      Component component = (Component) ev.getSource();
      final String componentName = component.getName();
      switch (componentName) {
        case SAVE_MENU_ITEM_NAME:
        case SAVE_AS_MENU_ITEM_NAME:
          boolean mustChooseFile = SAVE_AS_MENU_ITEM_NAME.equals(componentName);
          saveToDisk(mustChooseFile);
          break;
        case NEW_MENU_ITEM_NAME:
        case OPEN_MENU_ITEM_NAME:
          showSaveConfirmationIfNecessaryAndRun(
                  "You have changes on your current document. Save before continuing?",
                  componentName,
                  new Runnable() {
                    @Override
                    public void run() {
                      if (NEW_MENU_ITEM_NAME.equals(componentName)) {
                        setSpreadsheet(new Spreadsheet());
                        setCurrentFile(null);
                      } else { // open
                        Path chosenFile = chooseFile(JFileChooser.OPEN_DIALOG);
                        openSpreadsheet(chosenFile);
                      }
                    }
                  });
          break;
        case COPY_MENU_ITEM_NAME:
          Object displayValue = mCellDisplayValueManager.getDisplayValueOfCellAt(getSelectedCellLocation());
          if (!"".equals(displayValue)) {
            Transferable contents = new StringSelection(displayValue.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
          }
          break;
        case COPY_FORMULA_MENU_ITEM_NAME:
          String cellContent = mSpreadsheet.getCellContentAt(getSelectedCellLocation());
          if (cellContent != null) {
            Transferable contents = new StringSelection(cellContent);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
          }
          break;
        case PASTE_MENU_ITEM_NAME:
          Transferable clipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
          CellLocation selectedLocation = getSelectedCellLocation();
          try {
            Object transferData = clipboard.getTransferData(DataFlavor.stringFlavor);
            mTableModel.setValueAt(transferData.toString(), selectedLocation.getRow(), selectedLocation.getColumn());
          } catch (UnsupportedFlavorException | IOException ex) {
            // we can only use the string flavor anyway, nothing we can do here
          }
          break;
      }
    }
  }

  private class SpreadsheetWindowListener extends WindowAdapter {

    @Override
    public void windowClosing(WindowEvent e) {
      showSaveConfirmationIfNecessaryAndRun(
              "You have changes on your current document. Save before exiting?",
              "Exit",
              new Runnable() {
                @Override
                public void run() {
                  dispose();
                }
              });
    }
  }
}
