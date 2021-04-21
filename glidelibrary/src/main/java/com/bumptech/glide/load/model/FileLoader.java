package com.bumptech.glide.load.model;

import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.signature.ObjectKey;
import timber.log.Timber;

import java.io.*;

/**
 * A simple model loader for loading data from {@link File}s.
 *
 * @param <Data> The type of data loaded from the given {@link File} ({@link
 *               InputStream} or {@link java.io.FileDescriptor} etc).
 */
public class FileLoader<Data> implements ModelLoader<File, Data> {
  private static final String TAG = "FileLoader";

  private final FileOpener<Data> fileOpener;

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public FileLoader(FileOpener<Data> fileOpener) {
    this.fileOpener = fileOpener;
  }

  @Override
  public LoadData<Data> buildLoadData(File model, int width, int height,
      Options options) {
    Timber.d("buildLoadData() called with: model = [ %s ], width = [ %s ], height = [ %s ], options = [ %s ]", model, width, height, options);
    return new LoadData<>(new ObjectKey(model), new FileFetcher<>(model, fileOpener));
  }

  @Override
  public boolean handles(File model) {
    Timber.d("handles() called");
    return true;
  }

  /**
   * Allows opening a specific type of data from a {@link File}.
   * @param <Data> The type of data that can be opened.
   */
  public interface FileOpener<Data> {
    Data open(File file) throws FileNotFoundException;
    void close(Data data) throws IOException;
    Class<Data> getDataClass();
  }

  private static class FileFetcher<Data> implements DataFetcher<Data> {
    private final File file;
    private final FileOpener<Data> opener;
    private Data data;

    FileFetcher(File file, FileOpener<Data> opener) {
      this.file = file;
      this.opener = opener;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super Data> callback) {
      Timber.d("loadData() called with: priority = [ %s ], callback = [ %s ]", priority, callback);
      try {
        data = opener.open(file);
      } catch (FileNotFoundException e) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
          Log.d(TAG, "Failed to open file", e);
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
          opener.close(data);
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
    public Class<Data> getDataClass() {
      return opener.getDataClass();
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
      return DataSource.LOCAL;
    }
  }

  /**
   * Base factory for loading data from {@link File files}.
   * @param <Data> The type of data that will be loaded for a given {@link File}.
   */
  public static class Factory<Data> implements ModelLoaderFactory<File, Data> {
    private final FileOpener<Data> opener;

    public Factory(FileOpener<Data> opener) {
      this.opener = opener;
    }

    @Override
    public final ModelLoader<File, Data> build(MultiModelLoaderFactory multiFactory) {
      return new FileLoader<>(opener);
    }

    @Override
    public final void teardown() {
      // Do nothing.
    }
  }

  /**
   * Factory for loading {@link InputStream}s from {@link File}s.
   */
  public static class StreamFactory extends Factory<InputStream> {
    public StreamFactory() {
      super(new FileOpener<InputStream>() {
        @Override
        public InputStream open(File file) throws FileNotFoundException {
          return new FileInputStream(file);
        }

        @Override
        public void close(InputStream inputStream) throws IOException {
          inputStream.close();
        }

        @Override
        public Class<InputStream> getDataClass() {
          return InputStream.class;
        }
      });
    }
  }

}
