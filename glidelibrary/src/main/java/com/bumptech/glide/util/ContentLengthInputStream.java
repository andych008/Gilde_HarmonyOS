package com.bumptech.glide.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Uses the content length as the basis for the return value of {@link #available()} and verifies
 * that at least content length bytes are returned from the various read methods.
 */
public final class ContentLengthInputStream extends FilterInputStream {
  private static final String TAG = "ContentLengthStream";
  private static final int UNKNOWN = -1;

  private final long contentLength;
  private int readSoFar;

  public static InputStream obtain(InputStream other, String contentLengthHeader) {
    return obtain(other, parseContentLength(contentLengthHeader));
  }

  public static InputStream obtain(InputStream other, long contentLength) {
    return new com.bumptech.glide.util.ContentLengthInputStream(other, contentLength);
  }

  private static int parseContentLength(String contentLengthHeader) {
    int result = UNKNOWN;
    if (!TextTool.isNullOrEmpty(contentLengthHeader)) {
      try {
        result = Integer.parseInt(contentLengthHeader);
      } catch (NumberFormatException e) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
          Log.d(TAG, "failed to parse content length header: " + contentLengthHeader, e);
        }
      }
    }
    return result;
  }

  private ContentLengthInputStream(InputStream in, long contentLength) {
    super(in);
    this.contentLength = contentLength;
  }

  @Override
  public synchronized int available() throws IOException {
    return (int) Math.max(contentLength - readSoFar, in.available());
 }

  @Override
  public synchronized int read() throws IOException {
    int value = super.read();
    checkReadSoFarOrThrow(value >= 0 ? 1 : -1);
    return value;
  }

  @Override
  public int read(@NonNull byte[] buffer) throws IOException {
    return read(buffer, 0 /*byteOffset*/, buffer.length /*byteCount*/);
  }

  @Override
  public synchronized int read(@NonNull byte[] buffer, int byteOffset, int byteCount)
      throws IOException {
    return checkReadSoFarOrThrow(super.read(buffer, byteOffset, byteCount));
  }

  private int checkReadSoFarOrThrow(int read) throws IOException {
    if (read >= 0) {
      readSoFar += read;
    } else if (contentLength - readSoFar > 0) {
      throw new IOException("Failed to read all expected data"
          + ", expected: " + contentLength
          + ", but read: " + readSoFar);
    }
    return read;
  }
}
