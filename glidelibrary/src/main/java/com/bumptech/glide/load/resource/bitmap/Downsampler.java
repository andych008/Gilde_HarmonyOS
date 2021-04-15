package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Downsamples, decodes, and rotates images according to their exif orientation.
 */
public final class Downsampler {



  private final BitmapPool bitmapPool;

  public Downsampler(BitmapPool bitmapPool) {
  	this.bitmapPool = Preconditions.checkNotNull(bitmapPool);
  }

  public boolean handles(@SuppressWarnings("unused") InputStream is) {
    // We expect Downsampler to handle any available type Android supports.
    return true;
  }

  public boolean handles(@SuppressWarnings("unused") ByteBuffer byteBuffer) {
    // We expect downsampler to handle any available type Android supports.
    return true;
  }

  /**
   * Returns a Bitmap decoded from the given {@link InputStream} that is rotated to match any EXIF
   * data present in the stream and that is downsampled according to the given dimensions and any
   * provided  {@link com.bumptech.glide.load.resource.bitmap.DownsampleStrategy} option.
   *
   */
  public Resource<PixelMap> decode(InputStream is, int outWidth, int outHeight,
      Options options) throws IOException {
    return decode(is, outWidth, outHeight, options, EMPTY_CALLBACKS);
  }

  @SuppressWarnings({"resource", "deprecation"})
  public Resource<PixelMap> decode(InputStream is, int requestedWidth, int requestedHeight,
      Options options, DecodeCallbacks callbacks) throws IOException {
	  
      ImageSource imageSource = ImageSource.create(is, new ImageSource.SourceOptions());
      PixelMap result = imageSource.createPixelmap(new ImageSource.DecodingOptions());

      callbacks.onDecodeComplete(bitmapPool, result);
      return BitmapResource.obtain(result, bitmapPool);
  }

  private static final DecodeCallbacks EMPTY_CALLBACKS = new DecodeCallbacks() {
      @Override
      public void onObtainBounds() {
          // Do nothing.
      }

      @Override
      public void onDecodeComplete(BitmapPool bitmapPool, PixelMap downsampled) throws IOException {
          // Do nothing.
      }
  };

  /**
   * Callbacks for key points during decodes.
   */
  public interface DecodeCallbacks {
    void onObtainBounds();
    void onDecodeComplete(BitmapPool bitmapPool, PixelMap downsampled) throws IOException;
  }
}
