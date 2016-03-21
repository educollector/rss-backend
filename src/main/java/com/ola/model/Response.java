package com.ola.model;

import com.google.gson.Gson;

/**
 * Created on 3/21/16.
 */
public class Response {

  public static String success() {
    return new Gson().toJson(new Response(true));
  }

  public static String fail() {
    return new Gson().toJson(new Response(false));
  }

  private boolean success;

  public Response(boolean isSuccess) {
    this.success = isSuccess;
  }
}
