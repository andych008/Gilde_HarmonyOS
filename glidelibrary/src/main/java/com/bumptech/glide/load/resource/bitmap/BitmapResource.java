package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.engine.Initializable;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import ohos.media.image.PixelMap;

/**
 * A resource wrapping a {@link ohos.media.image.PixelMap} object.
 */
public class BitmapResource implements Resource<PixelMap>,
    Initializable {
  private final PixelMap bitmap;
  private final BitmapPool bitmapPool;

  /**
   * Returns a new {@link BitmapResource} wrapping the given {@link PixelMap} if the PixelMap is
   * non-null or null if the given PixelMap is null.
   *
   * @param bitmap     A PixelMap.
   * @param bitmapPool A non-null {@link BitmapPool}.
   */
  public static BitmapResource obtain(PixelMap bitmap, BitmapPool bitmapPool) {
    if (bitmap == null) {
      return null;
    } else {
      return new BitmapResource(bitmap, bitmapPool);
    }
  }

  public BitmapResource(PixelMap bitmap, BitmapPool bitmapPool) {
    this.bitmap = Preconditions.checkNotNull(bitmap, "PixelMap must not be null");
    this.bitmapPool = Preconditions.checkNotNull(bitmapPool, "BitmapPool must not be null");
  }

  @Override
  public Class<PixelMap> getResourceClass() {
    return PixelMap.class;
  }

  @Override
  public PixelMap get() {
    return bitmap;
  }

  @Override
  public int getSize() {
    return Util.getBitmapByteSize(bitmap);
  }

  @Override
  public void recycle() {
    bitmapPool.put(bitmap);
  }

  @Override
  public void initialize() {
//    bitmap.prepareToDraw();
  }
}
