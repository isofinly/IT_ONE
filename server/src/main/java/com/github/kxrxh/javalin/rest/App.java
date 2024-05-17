
package com.github.kxrxh.javalin.rest;

import com.github.kxrxh.javalin.rest.api.RestServer;
import com.github.kxrxh.javalin.rest.database.DatabaseManager;

public class App {
  public static void main(String[] args) {
    RestServer server = new RestServer();
    server.setupRoutes();
    server.setupJWTAuthentication("v1/api", "");

    DatabaseManager.initialize("org.sqlite.JDBC",
        "jdbc:sqlite:/home/kxrxh/Dev/Java/it_one_hackathon/server/database.db", "", "");

    server.listen(3030);
  }
}
