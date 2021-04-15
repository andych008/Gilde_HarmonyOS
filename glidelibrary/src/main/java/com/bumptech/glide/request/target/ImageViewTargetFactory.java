package com.bumptech.glide.request.target;

import ohos.agp.components.Image;
import ohos.media.image.PixelMap;

/**
 * A factory responsible for producing the correct type of
 * {@link com.bumptech.glide.request.target.Target} for a given @link android.view.View} subclass.
 */
public class ImageViewTargetFactory {

  @SuppressWarnings("unchecked")
  public <Z> ViewTarget<Image, Z> buildTarget(Image view, Class<Z> clazz) {
    if (PixelMap.class.equals(clazz)) {
      return (ViewTarget<Image, Z>) new BitmapImageViewTarget(view);
    } else {
      throw new IllegalArgumentException(
          "Unhandled class: " + clazz + ", try .as*(Class).transcode(ResourceTranscoder)");
    }
  }
}
