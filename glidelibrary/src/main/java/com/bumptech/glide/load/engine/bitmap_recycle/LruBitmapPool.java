package com.bumptech.glide.load.engine.bitmap_recycle;


import ohos.agp.utils.Color;
import ohos.media.image.PixelMap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.bumptech.glide.util.Synthetic;
import ohos.media.image.common.PixelFormat;
import ohos.media.image.common.Size;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An {@link BitmapPool} implementation that uses an
 * {@link LruPoolStrategy} to bucket {@link PixelMap}s
 * and then uses an LRU eviction policy to evict {@link ohos.media.image.PixelMap}s from the least
 * recently used bucket in order to keep the pool below a given maximum size limit.
 */
public class LruBitmapPool implements BitmapPool {
  private static final String TAG = "LruBitmapPool";
  private static final PixelFormat DEFAULT_CONFIG = PixelFormat.ARGB_8888;

  private final LruPoolStrategy strategy;
  private final Set<PixelFormat> allowedConfigs;
  private final long initialMaxSize;
  private final BitmapTracker tracker;

  private long maxSize;
  private long currentSize;
  private int hits;
  private int misses;
  private int puts;
  private int evictions;

  // Exposed for testing only.
  LruBitmapPool(long maxSize, LruPoolStrategy strategy, Set<PixelFormat> allowedConfigs) {
    this.initialMaxSize = maxSize;
    this.maxSize = maxSize;
    this.strategy = strategy;
    this.allowedConfigs = allowedConfigs;
    this.tracker = new NullBitmapTracker();
  }

  /**
   * Constructor for LruBitmapPool.
   *
   * @param maxSize The initial maximum size of the pool in bytes.
   */
  public LruBitmapPool(long maxSize) {
    this(maxSize, getDefaultStrategy(), getDefaultAllowedConfigs());
  }

  /**
   * Constructor for LruBitmapPool.
   *
   * @param maxSize        The initial maximum size of the pool in bytes.
   * @param allowedConfigs A white listed put of {@link ohos.media.image.common.PixelFormat} that are
   *                       allowed to be put into the pool. Configs not in the allowed put will be
   *                       rejected.
   */
  // Public API.
  @SuppressWarnings("unused")
  public LruBitmapPool(long maxSize, Set<PixelFormat> allowedConfigs) {
    this(maxSize, getDefaultStrategy(), allowedConfigs);
  }

  @Override
  public long getMaxSize() {
    return maxSize;
  }

  @Override
  public synchronized void setSizeMultiplier(float sizeMultiplier) {
    maxSize = Math.round(initialMaxSize * sizeMultiplier);
    evict();
  }

  @Override
  public synchronized void put(PixelMap bitmap) {
    if (bitmap == null) {
      throw new NullPointerException("PixelMap must not be null");
    }
    if (bitmap.isReleased()) {
      throw new IllegalStateException("Cannot pool recycled bitmap");
    }
    if (!bitmap.isEditable() || strategy.getSize(bitmap) > maxSize
        || !allowedConfigs.contains(bitmap.getImageInfo().pixelFormat)) {
      if (Log.isLoggable(TAG, Log.VERBOSE)) {
        Log.v(TAG, "Reject bitmap from pool"
                + ", bitmap: " + strategy.logBitmap(bitmap)
                + ", is mutable: " + bitmap.isEditable()
                + ", is allowed config: " + allowedConfigs.contains(bitmap.getImageInfo().pixelFormat));
      }
      bitmap.release();
      return;
    }

    final int size = strategy.getSize(bitmap);
    strategy.put(bitmap);
    tracker.add(bitmap);

    puts++;
    currentSize += size;

    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      Log.v(TAG, "Put bitmap in pool=" + strategy.logBitmap(bitmap));
    }
    dump();

    evict();
  }

  private void evict() {
    trimToSize(maxSize);
  }

  @Override
  @NonNull
  public PixelMap get(int width, int height, PixelFormat config) {
    PixelMap result = getDirtyOrNull(width, height, config);
    if (result != null) {
      // Bitmaps in the pool contain random data that in some cases must be cleared for an image
      // to be rendered correctly. we shouldn't force all consumers to independently erase the
      // contents individually, so we do so here. See issue #131.
      result.writePixels(Color.TRANSPARENT.getValue());
    } else {
      result = createBitmap(width, height, config);
    }

    return result;
  }

  private PixelMap createBitmap(int width, int height, PixelFormat config) {
    PixelMap.InitializationOptions initializationOptions = new PixelMap.InitializationOptions();
    initializationOptions.size = new Size(width, height);
    initializationOptions.pixelFormat = config;
    initializationOptions.editable = true;
    return PixelMap.create(initializationOptions);
  }

  @Override
  public PixelMap getDirty(int width, int height, PixelFormat config) {
    PixelMap result = getDirtyOrNull(width, height, config);
    if (result == null) {
      result = createBitmap(width, height, config);
    }
    return result;
  }


  @Nullable
  private synchronized PixelMap getDirtyOrNull(int width, int height, PixelFormat config) {
    // Config will be null for non public config types, which can lead to transformations naively
    // passing in null as the requested config here. See issue #194.
    final PixelMap result = strategy.get(width, height, config != null ? config : DEFAULT_CONFIG);
    if (result == null) {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Missing bitmap=" + strategy.logBitmap(width, height, config));
      }
      misses++;
    } else {
      hits++;
      currentSize -= strategy.getSize(result);
      tracker.remove(result);
    }
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      Log.v(TAG, "Get bitmap=" + strategy.logBitmap(width, height, config));
    }
    dump();

    return result;
  }




  @Override
  public void clearMemory() {
    if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.d(TAG, "clearMemory");
    }
    trimToSize(0);
  }

  private synchronized void trimToSize(long size) {
    while (currentSize > size) {
      final PixelMap removed = strategy.removeLast();
      // TODO: This shouldn't ever happen, see #331.
      if (removed == null) {
        if (Log.isLoggable(TAG, Log.WARN)) {
          Log.w(TAG, "Size mismatch, resetting");
          dumpUnchecked();
        }
        currentSize = 0;
        return;
      }
      tracker.remove(removed);
      currentSize -= strategy.getSize(removed);
      evictions++;
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Evicting bitmap=" + strategy.logBitmap(removed));
      }
      dump();
      removed.release();
    }
  }

  private void dump() {
    if (Log.isLoggable(TAG, Log.VERBOSE)) {
      dumpUnchecked();
    }
  }

  private void dumpUnchecked() {
    Log.v(TAG, "Hits=" + hits + ", misses=" + misses + ", puts=" + puts + ", evictions=" + evictions
        + ", currentSize=" + currentSize + ", maxSize=" + maxSize + "\nStrategy=" + strategy);
  }

  private static LruPoolStrategy getDefaultStrategy() {
    final LruPoolStrategy strategy;
    strategy = new AttributeStrategy();
    return strategy;
  }

  private static Set<PixelFormat> getDefaultAllowedConfigs() {
    Set<PixelFormat> configs = new HashSet<>();
    configs.addAll(Arrays.asList(PixelFormat.values()));
    return Collections.unmodifiableSet(configs);
  }

  private interface BitmapTracker {
    void add(PixelMap bitmap);

    void remove(PixelMap bitmap);
  }

  @SuppressWarnings("unused")
  // Only used for debugging
  private static class ThrowingBitmapTracker implements BitmapTracker {
    private final Set<PixelMap> bitmaps = Collections.synchronizedSet(new HashSet<PixelMap>());

    @Override
    public void add(PixelMap bitmap) {
      if (bitmaps.contains(bitmap)) {
        throw new IllegalStateException(
            "Can't add already added bitmap: " + bitmap );
      }
      bitmaps.add(bitmap);
    }

    @Override
    public void remove(PixelMap bitmap) {
      if (!bitmaps.contains(bitmap)) {
        throw new IllegalStateException("Cannot remove bitmap not in tracker");
      }
      bitmaps.remove(bitmap);
    }
  }

  private static final class NullBitmapTracker implements BitmapTracker {

    @Synthetic
    NullBitmapTracker() { }

    @Override
    public void add(PixelMap bitmap) {
      // Do nothing.
    }

    @Override
    public void remove(PixelMap bitmap) {
      // Do nothing.
    }
  }
}
