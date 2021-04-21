/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.util;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public final class Log {

    public static final int GLIDE_DOMAIN = 0x99;

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    private Log() {
    }

    /**
     * Send a {@link #VERBOSE} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int v(String tag, String msg) {
        return printlns(VERBOSE, tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int v(String tag, String msg, Throwable tr) {
        return printlns(VERBOSE, tag, msg+"\n"+HiLog.getStackTrace(tr));
    }

    /**
     * Send a {@link #DEBUG} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int d(String tag, String msg) {
        return printlns(DEBUG, tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int d(String tag, String msg, Throwable tr) {
        return printlns(DEBUG, tag, msg+"\n"+HiLog.getStackTrace(tr));
    }

    /**
     * Send an {@link #INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int i(String tag, String msg) {
        return printlns( INFO, tag, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int i(String tag, String msg, Throwable tr) {
        return printlns( INFO, tag, msg+"\n"+HiLog.getStackTrace(tr));
    }

    /**
     * Send a {@link #WARN} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int w(String tag, String msg) {
        return printlns( WARN, tag, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int w(String tag, String msg, Throwable tr) {
        return printlns( WARN, tag, msg+"\n"+HiLog.getStackTrace(tr));
    }

    public static boolean isLoggable(String tag, int level){
        return true;
    }


    public static int w(String tag, Throwable tr) {
        return printlns(WARN, tag, HiLog.getStackTrace(tr));
    }

    /**
     * Send an {@link #ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int e(String tag, String msg) {
        return printlns(ERROR, tag, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int e(String tag, String msg, Throwable tr) {
        return printlns(ERROR, tag, msg+"\n"+HiLog.getStackTrace(tr));
    }

    public static int wtf(String tag, String msg) {
        return wtf(tag, msg);
    }


    public static int wtfStack(String tag, String msg) {
        return wtf(tag, msg);
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     * Similar to {@link #wtf(String, String)}, with an exception to log.
     * @param tag Used to identify the source of a log message.
     * @param tr An exception to log.
     */
    public static int wtf(String tag, Throwable tr) {
        return wtf(tag, tr.getMessage()+"\n"+HiLog.getStackTrace(tr));
    }

    /**
     * What a Terrible Failure: Report an exception that should never happen.
     * Similar to {@link #wtf(String, Throwable)}, with a message as well.
     * @param tag Used to identify the source of a log message.
     * @param msg The message you would like logged.
     * @param tr An exception to log.  May be null.
     */
    public static int wtf(String tag, String msg, Throwable tr) {
        return printlns(WARN, tag, msg+"\n"+HiLog.getStackTrace(tr));
    }


    static HiLogLabel lable = new HiLogLabel(HiLog.DEBUG, GLIDE_DOMAIN, "Glide");
    /**
     * @param logType LogUtils.INFO || LogUtils.ERROR || LogUtils.DEBUG|| LogUtils.WARN
     * @param tag     日志标识  根据喜好，自定义
     * @param message 需要打印的日志信息
     */
    public static int printlns(int logType, String tag, String message) {

//        switch (logType) {
//            case INFO:
//                HiLog.info(lable, "%s : %s", tag, message);
//                break;
//            case ERROR:
//                HiLog.error(lable, "%s : %s", tag, message);
//                break;
//            case DEBUG:
//                HiLog.debug(lable, "%s : %s", tag, message);
//                break;
//            case WARN:
//                HiLog.warn(lable, "%s : %s", tag, message);
//                break;
//            default:
//                HiLog.debug(lable, "%s : %s", tag, message);
//                break;
//        }
        return 0;
    }


}
