package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import ohos.app.Context;
import ohos.media.image.PixelMap;

import java.security.MessageDigest;

/**
 * Returns the image with its original size if its dimensions match or are smaller
 * than the target's, couple with @link ohos.agp.components.Image.ScaleType#CENTER_INSIDE}
 * in order to center it in Target. If not, then it is scaled so that one of the dimensions of
 * the image will be equal to the given dimension and the other will be less than the given
 * dimension (maintaining the image's aspect ratio).
 */
public class CenterInside extends BitmapTransformation {
  private static final String ID = "com.bumptech.glide.load.resource.bitmap.CenterInside";
  private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

  public CenterInside() {
    // Intentionally empty.
  }

  /**
   * @deprecated Use {@link #CenterInside()}.
   * @param context Ignored.
   */
  @Deprecated
  public CenterInside(@SuppressWarnings("unused") Context context) {
    this();
  }

  /**
   * @deprecated Use {@link #CenterInside()}.
   * @param bitmapPool Ignored.
   */
  @Deprecated
  public CenterInside(@SuppressWarnings("unused") BitmapPool bitmapPool) {
    this();
  }

  @Override
  protected PixelMap transform(BitmapPool pool, PixelMap toTransform, int outWidth, int outHeight) {
    return TransformationUtils.circleCrop(toTransform);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof com.bumptech.glide.load.resource.bitmap.CenterInside;
  }

  @Override
  public int hashCode() {
    return ID.hashCode();
  }

  @Override
  public void updateDiskCacheKey(MessageDigest messageDigest) {
    messageDigest.update(ID_BYTES);
  }
}


