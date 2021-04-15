package com.bumptech.glide.load.resource.bitmap;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;
import ohos.app.Context;
import ohos.media.image.PixelMap;

import java.io.IOException;

/**
 * Decodes an PixelMap for a data type.
 *
 * @param <DataType> The type of data that will be decoded.
 */
public class BitmapDrawableDecoder<DataType> implements ResourceDecoder<DataType, PixelMap> {

  private final ResourceDecoder<DataType, PixelMap> decoder;

  // Public API.
  @SuppressWarnings({"unused", "WeakerAccess"})
  public BitmapDrawableDecoder(Context context, ResourceDecoder<DataType, PixelMap> decoder) {
    this(decoder);
  }


  public BitmapDrawableDecoder(@NonNull ResourceDecoder<DataType, PixelMap> decoder) {
    this.decoder = Preconditions.checkNotNull(decoder);
  }

  @Override
  public boolean handles(DataType source, Options options) throws IOException {
    return decoder.handles(source, options);
  }

  @Override
  public Resource<PixelMap> decode(DataType source, int width, int height, Options options)
      throws IOException {
    Resource<PixelMap> bitmapResource = decoder.decode(source, width, height, options);
    return bitmapResource;
  }
}
