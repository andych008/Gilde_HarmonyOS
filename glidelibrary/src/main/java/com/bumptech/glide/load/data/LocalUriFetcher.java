package com.bumptech.glide.load.data;

import android.content.ContentResolver;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.app.Context;
import ohos.utils.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import timber.log.Timber;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A DataFetcher that uses an {@link android.content.ContentResolver} to load data from a {@link
 * ohos.utils.net.Uri} pointing to a local resource.
 *
 * @param <T> The type of data that will obtained for the given uri (For example, {@link
 *            java.io.InputStream} .
 */
public abstract class LocalUriFetcher<T> implements DataFetcher<T> {
  private static final String TAG = "LocalUriFetcher";
  private final Uri uri;
  private final DataAbilityHelper dataAbilityHelper;

  private T data;

  /**
   * Opens an input stream for a uri pointing to a local asset. Only certain uris are supported
   *
   * @param uri     A Uri pointing to a local asset. This load will fail if the uri isn't openable
   *                by @link ContentResolver#openInputStream(ohos.utils.net.Uri)}
   * @see ContentResolver# openInputStream(ohos.utils.net.Uri)
   */
  // Public API.
  @SuppressWarnings("WeakerAccess")
  public LocalUriFetcher(Context context, Uri uri) {
    this.dataAbilityHelper = DataAbilityHelper.creator(context);
    this.uri = uri;
  }

  @Override
  public final void loadData(Priority priority, DataCallback<? super T> callback) {
    Timber.d("loadData() called with: priority = [ %s ], callback = [ %s ]", priority, callback);
    try {
      data = loadResource(uri, dataAbilityHelper);
    } catch (FileNotFoundException e) {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Failed to open Uri", e);
      }
      callback.onLoadFailed(e);
      return;
    }
    callback.onDataReady(data);
  }

  @Override
  public void cleanup() {
    if (data != null) {
      try {
        close(data);
      } catch (IOException e) {
        // Ignored.
      }
    }
  }

  @Override
  public void cancel() {
    // Do nothing.
  }

  @NonNull
  @Override
  public DataSource getDataSource() {
    return DataSource.LOCAL;
  }

  /**
   * Returns a concrete data type from the given {@link ohos.utils.net.Uri} using the given {@link
   * android.content.ContentResolver}.
   */
  protected abstract T loadResource(Uri uri, DataAbilityHelper dataAbilityHelper)
      throws FileNotFoundException;

  /**
   * Closes the concrete data type if necessary.
   *
   * <p> Note - We can't rely on the closeable interface because it was added after our min API
   * level. See issue #157. </p>
   *
   * @param data The data to close.
   */
  protected abstract void close(T data) throws IOException;
}

