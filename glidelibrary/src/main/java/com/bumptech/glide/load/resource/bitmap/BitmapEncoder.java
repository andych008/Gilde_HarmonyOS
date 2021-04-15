package com.bumptech.glide.load.resource.bitmap;

import android.util.Log;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.CompressFormat;
import ohos.media.image.ImagePacker;
import ohos.media.image.PixelMap;
import ohos.media.image.common.PixelFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An {@link ResourceEncoder} that writes {@link ohos.media.image.PixelMap}s
 * to {@link OutputStream}s.
 */
public class BitmapEncoder implements ResourceEncoder<PixelMap> {
  /**
   * An integer option between 0 and 100 that is used as the compression quality.
   *
   * <p> Defaults to 90. </p>
   */
  public static final Option<Integer> COMPRESSION_QUALITY = Option.memory(
      "com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionQuality", 90);

  /**
   * An {@link PixelFormat} option used as the format to encode
   * the {@link ohos.media.image.PixelMap}.
   *
   * <p> Defaults to JPEG} for images without alpha
   * and PNG} for images with alpha. </p>
   */
  public static final Option<CompressFormat> COMPRESSION_FORMAT = Option.memory(
      "com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionFormat");

  private static final String TAG = "BitmapEncoder";

  @Override
  public boolean encode(Resource<PixelMap> resource, File file, Options options) {
    final PixelMap bitmap = resource.get();
    String format = getFormat(bitmap, options);

    int quality = options.get(COMPRESSION_QUALITY);

    boolean success = false;
    OutputStream os = null;
    try {
      os = new FileOutputStream(file);

      ImagePacker imagePacker = ImagePacker.create();
      ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
      packingOptions.format = format;
      packingOptions.quality = 90;
      success = imagePacker.initializePacking(os, packingOptions);

      os.close();
    } catch (IOException e) {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Failed to encode PixelMap", e);
      }
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          // Do nothing.
        }
      }
    }

    return success;
  }

  private String getFormat(PixelMap bitmap, Options options) {
    return CompressFormat.PNG;
  }

  @Override
  public EncodeStrategy getEncodeStrategy(Options options) {
    return EncodeStrategy.TRANSFORMED;
  }
}
