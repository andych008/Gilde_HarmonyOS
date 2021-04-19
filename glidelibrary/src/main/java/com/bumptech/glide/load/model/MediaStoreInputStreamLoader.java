package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.mediastore.MediaStoreUtil;
import com.bumptech.glide.signature.ObjectKey;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.app.Context;
import ohos.data.resultset.ResultSet;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;

import java.io.*;

/**
 * Loads the file path for {@link AVStorage} owned {@link Uri uris}.
 */
public final class MediaStoreInputStreamLoader implements ModelLoader<Uri, InputStream>  {

  private final Context context;

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public MediaStoreInputStreamLoader(Context context) {
    this.context = context;
  }

  @Override
  public LoadData<InputStream> buildLoadData(Uri uri, int width, int height, Options options) {
    return new LoadData<>(new ObjectKey(uri), new FilePathFetcher(context, uri));
  }

  @Override
  public boolean handles(Uri uri) {
    return MediaStoreUtil.isMediaStoreUri(uri);
  }

  private static class FilePathFetcher implements DataFetcher<InputStream> {
    private static final String[] PROJECTION = new String[] {
        AVStorage.Images.Media.ID
    };

    private final DataAbilityHelper dataAbilityHelper;
    private final Uri uri;

    FilePathFetcher(Context context, Uri uri) {
      this.dataAbilityHelper = DataAbilityHelper.creator(context);
      this.uri = uri;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
      try {
        ResultSet result = dataAbilityHelper.query(uri, PROJECTION, null);

        if(result != null && result.getRowCount()>0 && result.goToFirstRow()){

          FileDescriptor fd = dataAbilityHelper.openFile(uri,"r");

          InputStream inputStream = new FileInputStream(fd);

          callback.onDataReady(inputStream);
        }

      } catch (DataAbilityRemoteException | FileNotFoundException e) {
        e.printStackTrace();
        callback.onLoadFailed(new FileNotFoundException("Failed to find file path for: " + uri));
      }
    }

    @Override
    public void cleanup() {
      // Do nothing.
    }

    @Override
    public void cancel() {
      // Do nothing.
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
      return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
      return DataSource.LOCAL;
    }
  }

  /**
   * {@link ModelLoaderFactory} for {@link MediaStoreInputStreamLoader}s.
   */
  public static final class Factory implements ModelLoaderFactory<Uri, InputStream> {

    private final Context context;

    public Factory(Context context) {
      this.context = context;
    }

    @Override
    public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new MediaStoreInputStreamLoader(context);
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }
}
