package com.v1ct04.spreadsheets.persistence;

import com.v1ct04.spreadsheets.model.CellLocation;
import com.v1ct04.spreadsheets.model.Spreadsheet;
import com.v1ct04.spreadsheets.model.SpreadsheetUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Contains helper method for converting a JSONObject into a Spreadsheet instance and the opposite way as well.
 */
public class SpreadsheetJSONAdapter {

  public static JSONObject jsonFromSpreadsheet(Spreadsheet spreadsheet) {
    JSONObject json = new JSONObject();
    for (CellLocation cellLocation : spreadsheet.getOccupiedLocations()) {
      String locationString = SpreadsheetUtils.cellNameForLocation(cellLocation);
      try {
        json.put(locationString, spreadsheet.getCellContentAt(cellLocation));
      } catch (JSONException e) {
        // this will never happen, locationString nor the cell content will be null
      }
    }
    return json;
  }

  public static Spreadsheet spreadsheetFromJSON(JSONObject json) {
    Spreadsheet spreadsheet = new Spreadsheet();
    Iterator iterator = json.keys();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      String value = json.optString(key);
      CellLocation cellLocation = SpreadsheetUtils.cellLocationForName(key);
      spreadsheet.setCellContentAt(cellLocation, value);
    }
    return spreadsheet;
  }
}
