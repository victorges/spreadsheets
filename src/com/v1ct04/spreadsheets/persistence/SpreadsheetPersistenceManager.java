package com.v1ct04.spreadsheets.persistence;

import com.v1ct04.spreadsheets.model.Spreadsheet;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Helper class to manage saving and loading a spreadsheet to/from the filesystem.
 */
public class SpreadsheetPersistenceManager {

  public static final String SPREADSHEET_FILE_EXTENSION = "pss";

  private static final Charset CHARSET = StandardCharsets.UTF_8;

  public void writeSpreadsheetToFile(Spreadsheet spreadsheet, Path file) throws IOException {
    try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file, CHARSET))) {
      JSONObject spreadsheetJSON = SpreadsheetJSONAdapter.jsonFromSpreadsheet(spreadsheet);
      writer.println(customJsonHash(spreadsheetJSON));
      writer.println(spreadsheetJSON.toString());
    }
  }

  public Spreadsheet readSpreadsheetFromFile(Path file) throws IOException {
    try (Scanner scanner = new Scanner(Files.newBufferedReader(file, CHARSET))) {
      long savedHashCode = Long.parseLong(scanner.nextLine());
      JSONObject jsonObject = new JSONObject(scanner.nextLine());
      if (customJsonHash(jsonObject) != savedHashCode) {
        throw new IOException("Safety hash code mismatch!");
      }
      return SpreadsheetJSONAdapter.spreadsheetFromJSON(jsonObject);
    } catch (Exception ex) {
      throw new IOException("Spreadsheet file is corrupted!", ex);
    }
  }

  /**
   * JSONObject does not implement hashCode so we need this helper for calculating a consistent one.
   */
  private long customJsonHash(JSONObject jsonObject) {
    long hash = 0;
    Iterator iterator = jsonObject.sortedKeys();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      Object value = jsonObject.opt(key);
      hash = 31 * hash + key.hashCode();
      hash = 31 * hash + value.hashCode();
    }
    return hash;
  }
}
