package com.bumptech.glide.load.data;

import ohos.global.resource.ResourceManager;
import android.support.annotation.NonNull;
import java.io.IOException;
import java.io.InputStream;

/**
 * Fetches an {@link java.io.InputStream} for an asset path.
 */
public class StreamAssetPathFetcher extends AssetPathFetcher<InputStream> {
  public StreamAssetPathFetcher(ResourceManager assetManager, String assetPath) {
    super(assetManager, assetPath);
  }

  @Override
  protected InputStream loadResource(ResourceManager assetManager, String path) throws IOException {
    return assetManager.getRawFileEntry(path).openRawFile();
  }

  @Override
  protected void close(InputStream data) throws IOException {
    data.close();
  }

  @NonNull
  @Override
  public Class<InputStream> getDataClass() {
    return InputStream.class;
  }
}
