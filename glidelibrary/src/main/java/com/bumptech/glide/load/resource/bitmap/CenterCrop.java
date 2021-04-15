package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import ohos.app.Context;
import ohos.media.image.PixelMap;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.security.MessageDigest;

/**
 * Scale the image so that either the width of the image matches the given width and the height of
 * the image is greater than the given height or vice versa, and then crop the larger dimension to
 * match the given dimension.
 *
 * Does not maintain the image's aspect ratio
 */
public class CenterCrop extends BitmapTransformation {
  private static final String ID = "com.bumptech.glide.load.resource.bitmap.CenterCrop";
  private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

  public CenterCrop() {
    // Intentionally empty.
  }

  /**
   * @deprecated Use {@link #CenterCrop()}.
   * @param context Ignored.
   */
  @Deprecated
  public CenterCrop(@SuppressWarnings("unused") Context context) {
    this();
  }

  /**
   * @deprecated Use {@link #CenterCrop()}.
   * @param bitmapPool Ignored.
   */
  @Deprecated
  public CenterCrop(@SuppressWarnings("unused") BitmapPool bitmapPool) {
    this();
  }

  // Bitmap doesn't implement equals, so == and .equals are equivalent here.
  @SuppressWarnings("PMD.CompareObjectsWithEquals")
  @Override
  protected PixelMap transform(
      @NonNull BitmapPool pool, @NonNull PixelMap toTransform, int outWidth, int outHeight) {
    return TransformationUtils.circleCrop(toTransform);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof com.bumptech.glide.load.resource.bitmap.CenterCrop;
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
