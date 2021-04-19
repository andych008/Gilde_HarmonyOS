package com.bumptech.glide.load.model;

import android.content.ContentResolver;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.StreamLocalUriFetcher;
import com.bumptech.glide.signature.ObjectKey;
import ohos.app.Context;
import ohos.utils.net.Uri;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A ModelLoader for {@link ohos.utils.net.Uri}s that handles local {@link ohos.utils.net.Uri}s
 * directly and routes remote {@link ohos.utils.net.Uri}s to a wrapped
 * {@link com.bumptech.glide.load.model.ModelLoader} that handles
 * {@link com.bumptech.glide.load.model.GlideUrl}s.
 *
 * @param <Data> The type of data that will be retrieved for {@link ohos.utils.net.Uri}s.
 */
public class UriLoader<Data> implements ModelLoader<Uri, Data> {
  private static final Set<String> SCHEMES = Collections.unmodifiableSet(
      new HashSet<>(
          Arrays.asList(
              ContentResolver.SCHEME_FILE,
              ContentResolver.SCHEME_CONTENT
          )
      )
  );

  private final LocalUriFetcherFactory<Data> factory;

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public UriLoader(LocalUriFetcherFactory<Data> factory) {
    this.factory = factory;
  }

  @Override
  public LoadData<Data> buildLoadData(Uri model, int width, int height,
                                      Options options) {
    return new LoadData<>(new ObjectKey(model), factory.build(model));
  }

  @Override
  public boolean handles(Uri model) {
    return SCHEMES.contains(model.getScheme());
  }

  /**
   * Factory for obtaining a {@link DataFetcher} for a data type for a particular {@link Uri}.
   *
   * @param <Data> The type of data the returned {@link DataFetcher} will obtain.
   */
  public interface LocalUriFetcherFactory<Data> {
    DataFetcher<Data> build(Uri uri);
  }

  /**
   * Loads {@link InputStream}s from {@link Uri}s.
   */
  public static class StreamFactory implements ModelLoaderFactory<Uri, InputStream>,
      LocalUriFetcherFactory<InputStream> {

    private final Context context;

    public StreamFactory(Context context) {
      this.context = context;
    }

    @Override
    public DataFetcher<InputStream> build(Uri uri) {
      return new StreamLocalUriFetcher(context, uri);
    }

    @Override
    public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new UriLoader<>(this);
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }

}
