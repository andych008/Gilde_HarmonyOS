package com.bumptech.glide.util;


import ohos.miscservices.timeutility.Time;

/**
 * A class for logging elapsed real time in millis.
 */
public final class LogTime {

  private LogTime() {
    // Utility class.
  }

  /**
   * Returns the current time in either millis or nanos depending on the api level to be used with
   * {@link #getElapsedMillis(long)}.
   */
  public static long getLogTime() {

    return Time.getCurrentTime();
  }

  /**
   * Returns the time elapsed since the given logTime in millis.
   *
   * @param logTime The start time of the event.
   */
  public static double getElapsedMillis(long logTime) {
    return (getLogTime() - logTime);
  }
}
