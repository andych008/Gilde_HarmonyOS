package com.bumptech.glide.request.target;

import ohos.media.image.PixelMap;
import ohos.agp.components.Image;
import com.bumptech.glide.request.target.ImageViewTarget;

/**
 * A {@link com.bumptech.glide.request.target.Target} that can display an {@link
 * ohos.media.image.PixelMap} in an {@link ohos.agp.components.Image}.
 */
public class BitmapImageViewTarget extends ImageViewTarget<PixelMap> {
  // Public API.
  @SuppressWarnings("WeakerAccess")
  public BitmapImageViewTarget(Image view) {
    super(view);
  }

  /**
   * Sets the {@link ohos.media.image.PixelMap} on the view using @link
   * ohos.agp.components.Image#setImageBitmap(ohos.media.image.PixelMap)}.
   *
   * @param resource The bitmap to display.
   */
  @Override
  protected void setResource(PixelMap resource) {
    view.setPixelMap(resource);
  }
}
