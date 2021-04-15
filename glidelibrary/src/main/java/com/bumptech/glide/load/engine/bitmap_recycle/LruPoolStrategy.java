package com.bumptech.glide.load.engine.bitmap_recycle;

import ohos.media.image.PixelMap;
import android.support.annotation.Nullable;
import ohos.media.image.common.PixelFormat;

interface LruPoolStrategy {
  void put(PixelMap bitmap);

  @Nullable
  PixelMap get(int width, int height, PixelFormat config);

  @Nullable
  PixelMap removeLast();

  String logBitmap(PixelMap bitmap);

  String logBitmap(int width, int height, PixelFormat config);

  int getSize(PixelMap bitmap);
}
