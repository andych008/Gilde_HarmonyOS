package com.bumptech.glide.manager;

import com.bumptech.glide.manager.ConnectivityMonitor;
import ohos.app.Context;
import android.support.annotation.NonNull;

/**
 * A factory class that produces a functional
 * {@link ConnectivityMonitor}.
 */
public interface ConnectivityMonitorFactory {

  @NonNull
  ConnectivityMonitor build(
      @NonNull Context context,
      @NonNull ConnectivityMonitor.ConnectivityListener listener);
}
