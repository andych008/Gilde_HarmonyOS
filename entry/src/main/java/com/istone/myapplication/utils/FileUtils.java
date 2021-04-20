package com.istone.myapplication.utils;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileUtils {
  public static void copyFile(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }
  }

  public static void copyFileUsingChannel(File source, File dest) throws IOException {
    FileChannel sourceChannel = null;
    FileChannel destChannel = null;
    try {
      sourceChannel = new FileInputStream(source).getChannel();
      destChannel = new FileOutputStream(dest).getChannel();
      destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
    } finally {
      if(sourceChannel != null) {
        sourceChannel.close();
      }
      if(destChannel != null) {
        destChannel.close();
      }
    }
  }

  public static void copyFileUsingChannel(FileInputStream source, FileOutputStream dest) throws IOException {
    FileChannel sourceChannel = null;
    FileChannel destChannel = null;
    try {
      sourceChannel = source.getChannel();
      destChannel = dest.getChannel();
      destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
    } finally {
      if(sourceChannel != null) {
        sourceChannel.close();
      }
      if(destChannel != null) {
        destChannel.close();
      }
    }
  }


  public static boolean copyFile(String srcPath, String dstPath) {
    try {
      File dstFile = new File(dstPath);
      if (!dstFile.exists()) {
        dstFile.createNewFile();
      }
      File srcFile = new File(srcPath);
      if (srcFile.exists()) {
        copyFileUsingChannel(srcFile, dstFile);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

}
