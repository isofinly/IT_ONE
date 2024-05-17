
package com.github.kxrxh.javalin.rest;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DB;

import com.github.kxrxh.javalin.rest.api.RestServer;

public class App {
  public static void main(String[] args) {
    RestServer server = new RestServer();
    server.setupRoutes();
    server.setupJWTAuthentication("v1/api", "");

    try (DB db = Base.open("org.postgresql.Driver",
        "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:5432/postgres",
        "postgres.junuanmjrmkmwtwlmgvd", "09c8uPeIrzXi3nZf")) {
      server.listen(3030);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
