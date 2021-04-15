package com.bumptech.glide.load.resource.bitmap;


import ohos.app.Context;
import ohos.media.image.PixelMap;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import ohos.media.image.common.Size;

/**
 * A simple {@link Transformation} for transforming
 * {@link ohos.media.image.PixelMap}s that abstracts away dealing with
 * {@link Resource} objects for subclasses.
 *
 * Use cases will look something like this:
 * <pre>
 * <code>
 * public class FillSpace extends BaseBitmapTransformation {
 *     private static final String ID = "com.bumptech.glide.transformations.FillSpace";
 *     private static final String ID_BYTES = ID.getBytes(STRING_CHARSET_NAME);
 *
 *     {@literal @Override}
 *     public PixelMap transform(BitmapPool pool, PixelMap toTransform, int outWidth, int outHeight) {
 *         if (toTransform.getWidth() == outWidth && toTransform.getHeight() == outHeight) {
 *             return toTransform;
 *         }
 *
 *         return PixelMap.createScaledBitmap(toTransform, outWidth, outHeight, true);
 *     }
 *
 *     {@literal @Override}
 *     public void equals(Object o) {
 *       return o instanceof FillSpace;
 *     }
 *
 *     {@literal @Override}
 *     public int hashCode() {
 *       return ID.hashCode();
 *     }
 *
 *     {@literal @Override}
 *     public void updateDiskCacheKey(MessageDigest messageDigest)
 *         throws UnsupportedEncodingException {
 *       messageDigest.update(ID_BYTES);
 *     }
 * }
 * </code>
 * </pre>
 */
public abstract class BitmapTransformation implements Transformation<PixelMap> {

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public BitmapTransformation() {
    // Intentionally empty.
  }

  /**
   * @deprecated Use {@link #BitmapTransformation()}.
   * @param context Ignored.
   */
  @Deprecated
  public BitmapTransformation(@SuppressWarnings("unused") Context context) {
    this();
  }

  /**
   * @deprecated Use {@link #BitmapTransformation()}.
   * @param bitmapPool Ignored.
   */
  @Deprecated
  public BitmapTransformation(@SuppressWarnings("unused") BitmapPool bitmapPool) {
    this();
  }

  @Override
  public final Resource<PixelMap> transform(
      Context context, Resource<PixelMap> resource, int outWidth, int outHeight) {
    if (!Util.isValidDimensions(outWidth, outHeight)) {
      throw new IllegalArgumentException(
          "Cannot apply transformation on width: " + outWidth + " or height: " + outHeight
              + " less than or equal to zero and not Target.SIZE_ORIGINAL");
    }
    BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
    PixelMap toTransform = resource.get();
    Size toSize = toTransform.getImageInfo().size;
    int targetWidth = outWidth == Target.SIZE_ORIGINAL ? toSize.width : outWidth;
    int targetHeight = outHeight == Target.SIZE_ORIGINAL ? toSize.height : outHeight;
    PixelMap transformed = transform(bitmapPool, toTransform, targetWidth, targetHeight);

    final Resource<PixelMap> result;
    if (toTransform.equals(transformed)) {
      result = resource;
    } else {
      result = BitmapResource.obtain(transformed, bitmapPool);
    }
    return result;
  }

  /**
   * Transforms the given {@link ohos.media.image.PixelMap} based on the given dimensions and returns
   * the transformed result.
   *
   * <p>The provided PixelMap, toTransform, should not be recycled or returned to the pool. Glide will
   * automatically recycle and/or reuse toTransform if the transformation returns a different
   * PixelMap. Similarly implementations should never recycle or return Bitmaps that are returned as
   * the result of this method. Recycling or returning the provided and/or the returned PixelMap to
   * the pool will lead to a variety of runtime exceptions and drawing errors. See #408 for an
   * example. If the implementation obtains and discards intermediate Bitmaps, they may safely be
   * returned to the BitmapPool and/or recycled.
   *
   * <p>outWidth and outHeight will never be
   * {@link Target#SIZE_ORIGINAL},
   * this class converts them to be the size of the PixelMap we're going to transform before calling
   * this method.
   *
   * @param pool        A {@link BitmapPool} that can
   *                    be used to obtain and return intermediate {@link PixelMap}s used in this
   *                    transformation. For every {@link ohos.media.image.PixelMap} obtained from the
   *                    pool during this transformation, a {@link ohos.media.image.PixelMap} must also
   *                    be returned.
   * @param toTransform The {@link ohos.media.image.PixelMap} to transform.
   * @param outWidth    The ideal width of the transformed bitmap (the transformed width does not
   *                    need to match exactly).
   * @param outHeight   The ideal height of the transformed bitmap (the transformed height does not
   *                    need to match exactly).
   */
  protected abstract PixelMap transform(
      @NonNull BitmapPool pool, @NonNull PixelMap toTransform, int outWidth, int outHeight);
}
