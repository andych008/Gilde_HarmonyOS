package com.bumptech.glide.load.resource.transcode;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.CompressFormat;
import com.bumptech.glide.load.resource.bytes.BytesResource;
import ohos.media.image.ImagePacker;
import ohos.media.image.PixelMap;

import java.io.ByteArrayOutputStream;

/**
 * An {@link ResourceTranscoder} that converts {@link
 * ohos.media.image.PixelMap}s into byte arrays using @link ohos.media.image.PixelMap#compress
 * (ohos.media.image.PixelMap.CompressFormat,
 * int, java.io.OutputStream)}.
 */
public class BitmapBytesTranscoder implements ResourceTranscoder<PixelMap, byte[]> {
  private final String compressFormat;
  private final int quality;

  public BitmapBytesTranscoder() {
    this(CompressFormat.JPEG, 100);
  }

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public BitmapBytesTranscoder(String compressFormat, int quality) {
    this.compressFormat = compressFormat;
    this.quality = quality;
  }

  @Override
  public Resource<byte[]> transcode(Resource<PixelMap> toTranscode, Options options) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImagePacker imagePacker = ImagePacker.create();
    ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
    packingOptions.format = compressFormat;
    packingOptions.quality = quality;
    imagePacker.initializePacking(os, packingOptions);
    toTranscode.recycle();
    return new BytesResource(os.toByteArray());
  }
}
