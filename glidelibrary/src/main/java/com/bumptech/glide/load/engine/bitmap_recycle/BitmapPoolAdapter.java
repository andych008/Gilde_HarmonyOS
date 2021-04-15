package com.bumptech.glide.load.engine.bitmap_recycle;

import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Size;

/**
 * An {@link BitmapPool BitmapPool} implementation
 * that rejects all {@link ohos.media.image.PixelMap Bitmap}s added to it and always returns {@code
 * null} from get.
 */
public class BitmapPoolAdapter implements BitmapPool {
  @Override
  public long getMaxSize() {
    return 0;
  }

  @Override
  public void setSizeMultiplier(float sizeMultiplier) {
    // Do nothing.
  }

  @Override
  public void put(PixelMap bitmap) {
    bitmap.release();
  }


  @Override
  public PixelMap get(int width, int height, PixelFormat config) {
    PixelMap.InitializationOptions initializationOptions = new PixelMap.InitializationOptions();
    initializationOptions.size = new Size(width, height);
    initializationOptions.pixelFormat = PixelFormat.ARGB_8888;
    initializationOptions.editable = true;
    return PixelMap.create(initializationOptions);
  }

  @Override
  public PixelMap getDirty(int width, int height, PixelFormat config) {
    return get(width, height, config);
  }

  @Override
  public void clearMemory() {
    // Do nothing.
  }

}
