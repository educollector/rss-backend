package com.ola;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 11/30/15.
 */
public class ViewModelMappingManager {

  private DatabaseManager databaseManager;

  public ViewModelMappingManager(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public Map<String, Object> getSharedModel() throws SQLException {
    Map<String, Object> model = new HashMap<>();
    model.put("itemsCount", databaseManager.getCurrentCartCount());
    return model;
  }
}
