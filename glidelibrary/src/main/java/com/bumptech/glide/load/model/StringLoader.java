package com.bumptech.glide.load.model;


import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import ohos.agp.utils.TextTool;
import ohos.utils.net.Uri;
import timber.log.Timber;

import java.io.InputStream;

/**
 * A model loader for handling certain string models. Handles paths, urls, and any uri string with a
 * scheme handled by @link android.content.ContentResolver#openInputStream(Uri)}.
 *
 * @param <Data> The type of data that will be loaded from the given {@link java.lang.String}.
 */
public class StringLoader<Data> implements ModelLoader<String, Data> {
  private final ModelLoader<Uri, Data> uriLoader;

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public StringLoader(ModelLoader<Uri, Data> uriLoader) {
    this.uriLoader = uriLoader;
  }

  @Override
  public LoadData<Data> buildLoadData(String model, int width, int height,
      Options options) {
    Timber.d("buildLoadData() called with: model = [ %s ], width = [ %s ], height = [ %s ], options = [ %s ]", model, width, height, options);
    Uri uri = parseUri(model);
    return uri == null ? null : uriLoader.buildLoadData(uri, width, height, options);
  }

  @Override
  public boolean handles(String model) {
    Timber.d("handles() called");
    return true;
  }

  @Nullable
  private static Uri parseUri(String model) {
    Uri uri;
    if (TextTool.isNullOrEmpty(model)) {
      return null;
    } else {
      uri = Uri.parse(model);
    }
    return uri;
  }


  /**
   * Factory for loading {@link InputStream}s from Strings.
   */
  public static class StreamFactory implements ModelLoaderFactory<String, InputStream> {

    @Override
    public ModelLoader<String, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new StringLoader<>(multiFactory.build(Uri.class, InputStream.class));
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }
}
