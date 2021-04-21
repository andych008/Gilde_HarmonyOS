package com.bumptech.glide.load.model;

import android.content.ContentResolver;
import ohos.app.Context;
import ohos.global.resource.ResourceManager;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.StreamAssetPathFetcher;
import com.bumptech.glide.signature.ObjectKey;
import ohos.utils.net.Uri;
import timber.log.Timber;

import java.io.InputStream;

/**
 * Loads a specific data type from an Asset Manager Uri.
 *
 *  file:///resources/rawfile/B.jpg
 *
 * @param <Data> The type of data this loader will obtain.
 */
public class AssetUriLoader<Data> implements ModelLoader<Uri, Data> {
  private static final String ASSET_PATH_SEGMENT = "resources";
  private static final String ASSET_PREFIX =
      ContentResolver.SCHEME_FILE + ":///" + ASSET_PATH_SEGMENT + "/";
  private static final int ASSET_PREFIX_LENGTH = ASSET_PREFIX.length();

  private final ResourceManager assetManager;
  private final AssetFetcherFactory<Data> factory;

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public AssetUriLoader(ResourceManager assetManager, AssetFetcherFactory<Data> factory) {
    this.assetManager = assetManager;
    this.factory = factory;
  }

  @Override
  public LoadData<Data> buildLoadData(Uri model, int width, int height,
      Options options) {
    Timber.d("buildLoadData() called with: model = [ %s ], width = [ %s ], height = [ %s ], options = [ %s ]", model, width, height, options);
    String assetPath = model.getDecodedPath().substring(1);
    return new LoadData<>(new ObjectKey(model), factory.buildFetcher(assetManager, assetPath));
  }

  @Override
  public boolean handles(Uri model) {
    boolean ret = ContentResolver.SCHEME_FILE.equals(model.getScheme()) && !model.getDecodedPathList()
            .isEmpty() && ASSET_PATH_SEGMENT.equals(model.getDecodedPathList().get(0));
    Timber.d("handles() : ret = [ %s ]", ret);
    return ret;
  }

  /**
   * A factory to build a {@link DataFetcher} for a specific asset path.
   *
   * @param <Data> The type of data that will be obtained by the fetcher.
   */
  public interface AssetFetcherFactory<Data> {
    DataFetcher<Data> buildFetcher(ResourceManager assetManager, String assetPath);
  }

  /**
   * Factory for loading {@link InputStream}s from asset manager Uris.
   */
  public static class StreamFactory implements ModelLoaderFactory<Uri, InputStream>,
      AssetFetcherFactory<InputStream> {

    private final ResourceManager assetManager;

    public StreamFactory(Context context) {
      this.assetManager = context.getResourceManager();
    }

    @Override
    public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new AssetUriLoader<>(assetManager, this);
    }

    @Override
    public void teardown() {
      // Do nothing.
    }

    @Override
    public DataFetcher<InputStream> buildFetcher(ResourceManager assetManager, String assetPath) {
      return new StreamAssetPathFetcher(assetManager, assetPath);
    }
  }
}
