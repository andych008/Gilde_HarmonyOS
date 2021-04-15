package com.bumptech.glide.load.resource.bitmap;

import ohos.media.image.PixelMap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Util;
import java.io.IOException;

/**
 * Passes through a (hopefully) non-owned {@link PixelMap} as a {@link PixelMap} based {@link Resource}
 * so that the given {@link PixelMap} is not recycled.
 */
public final class UnitBitmapDecoder implements ResourceDecoder<PixelMap, PixelMap> {

  @Override
  public boolean handles(PixelMap source, Options options) throws IOException {
    return true;
  }

  @Override
  public Resource<PixelMap> decode(PixelMap source, int width, int height, Options options)
      throws IOException {
    return new NonOwnedBitmapResource(source);
  }

  private static final class NonOwnedBitmapResource implements Resource<PixelMap> {

    private final PixelMap bitmap;

    NonOwnedBitmapResource(PixelMap bitmap) {
      this.bitmap = bitmap;
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
      // Do nothing.
    }
  }
}
