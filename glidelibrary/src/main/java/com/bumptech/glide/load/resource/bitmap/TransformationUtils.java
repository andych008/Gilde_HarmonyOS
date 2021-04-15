package com.bumptech.glide.load.resource.bitmap;

import ohos.media.image.PixelMap;
import ohos.media.image.common.AlphaType;
import ohos.media.image.common.Rect;
import ohos.media.image.common.ScaleMode;
import ohos.media.image.common.Size;

/**
 * A class with methods to efficiently resize Bitmaps.
 */
// Legacy Public APIs.
@SuppressWarnings("WeakerAccess")
public final class TransformationUtils {
  private static final String TAG = "TransformationUtils";


  /**
   * https://github.com/bumptech/glide/issues/738 On some devices (Moto X with android 5.1) bitmap
   * drawing is not thread safe.
   * This lock only locks for these specific devices. For other types of devices the lock is always
   * available and therefore does not impact performance
   */


  private TransformationUtils() {
    // Utility class.
  }


  public static PixelMap circleCrop(PixelMap toCrop) {
    if (toCrop == null) {
      return null;
    } else {
      Size size = toCrop.getImageInfo().size;
      PixelMap.InitializationOptions options = new PixelMap.InitializationOptions();
      options.alphaType = AlphaType.PREMUL;
      options.scaleMode = ScaleMode.CENTER_CROP;

      Rect srcRegion = new Rect();
      srcRegion.width = size.width;
      srcRegion.height = size.height;
      final PixelMap result = PixelMap.create(toCrop, srcRegion, options);

      return result;
    }

  }

}
