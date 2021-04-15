package com.bumptech.glide.load.engine.bitmap_recycle;

import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;

/**
 * An interface for a pool that allows users to reuse {@link ohos.media.image.PixelMap} objects.
 */
public interface BitmapPool {

  /**
   * Returns the current maximum size of the pool in bytes.
   */
  long getMaxSize();

  /**
   * Multiplies the initial size of the pool by the given multiplier to dynamically and
   * synchronously allow users to adjust the size of the pool.
   *
   * <p> If the current total size of the pool is larger than the max size after the given
   * multiplier is applied, {@link PixelMap}s should be evicted until the pool is smaller than the new
   * max size. </p>
   *
   * @param sizeMultiplier The size multiplier to apply between 0 and 1.
   */
  void setSizeMultiplier(float sizeMultiplier);

  /**
   * Adds the given {@link ohos.media.image.PixelMap} if it is eligible to be re-used and the pool
   * can fit it, or calls {@link PixelMap recycle()} on the Bitmap and discards it.
   *
   * <p> Callers must <em>not</em> continue to use the Bitmap after calling this method. </p>
   *
   * @param bitmap The {@link ohos.media.image.PixelMap} to attempt to add.
   * @see ohos.media.image.PixelMap isMutable()
   * @see ohos.media.image.PixelMap recycle()
   */
  void put(PixelMap bitmap);

  /**
   * Returns a {@link ohos.media.image.PixelMap} of exactly the given width, height, and
   * configuration, and containing only transparent pixels.
   *
   * <p> If no Bitmap with the requested attributes is present in the pool, a new one will be
   * allocated. </p>
   *
   * <p> Because this method erases all pixels in the @link Bitmap, this method is slightly slower
   * than {@link #getDirty(int, int, ohos.media.image.common.PixelFormat)}. If the {@link
   * ohos.media.image.PixelMap} is being obtained to be used in BitmapFactory
   * or in any other case where every pixel in the {@link ohos.media.image.PixelMap} will always be
   * overwritten or cleared, {@link #getDirty(int, int, ohos.media.image.common.PixelFormat)} will be
   * faster. When in doubt, use this method to ensure correctness. </p>
   *
   * <pre>
   *     Implementations can should clear out every returned Bitmap using the following:
   *
   * {@code
   * bitmap.eraseColor(Color.TRANSPARENT);
   * }
   * </pre>
   *
   * @param width  The width in pixels of the desired {@link ohos.media.image.PixelMap}.
   * @param height The height in pixels of the desired {@link ohos.media.image.PixelMap}.
   * @param config The {@link PixelFormat} of the desired {@link
   *               ohos.media.image.PixelMap}.
   * @see #getDirty(int, int, PixelFormat)
   */
  PixelMap get(int width, int height, PixelFormat config);

  /**
   * Identical to {@link #get(int, int, ohos.media.image.common.PixelFormat)} except that any returned
   * {@link ohos.media.image.PixelMap} may <em>not</em> have been erased and may contain random data.
   *
   * <p>If no Bitmap with the requested attributes is present in the pool, a new one will be
   * allocated. </p>
   *
   * <p> Although this method is slightly more efficient than {@link #get(int, int,
   * PixelFormat)} it should be used with caution and only when the caller is
   * sure that they are going to erase the {@link ohos.media.image.PixelMap} entirely before writing
   * new data to it. </p>
   *
   * @param width  The width in pixels of the desired {@link ohos.media.image.PixelMap}.
   * @param height The height in pixels of the desired {@link ohos.media.image.PixelMap}.
   * @param config
   * @return A {@link ohos.media.image.PixelMap} with exactly the given width, height, and config
   * potentially containing random image data or null if no such {@link ohos.media.image.PixelMap}
   * could be obtained from the pool.
   */
  PixelMap getDirty(int width, int height, PixelFormat config);

  /**
   * Removes all {@link ohos.media.image.PixelMap}s from the pool.
   */
  void clearMemory();
}
