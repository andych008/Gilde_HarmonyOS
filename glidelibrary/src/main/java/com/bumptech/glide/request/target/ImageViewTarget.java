package com.bumptech.glide.request.target;

import android.support.annotation.Nullable;
import com.bumptech.glide.request.transition.Transition;
import ohos.agp.components.Image;
import ohos.media.image.PixelMap;

/**
 * A base {@link com.bumptech.glide.request.target.Target} for displaying resources in {@link
 * ohos.agp.components.Image}s.
 *
 * @param <Z> The type of resource that this target will display in the wrapped {@link
 *            ohos.agp.components.Image}.
 */
// Public API.
@SuppressWarnings("WeakerAccess")
public abstract class ImageViewTarget<Z> extends ViewTarget<Image, Z>
    implements Transition.ViewAdapter {

  public ImageViewTarget(Image view) {
    super(view);
  }

  @Override
  public PixelMap getCurrentDrawable() {
    return view.getPixelMap();
  }

  /**
   * Sets the given @link android.graphics.drawable.Drawable} on the view using @link
   * ohos.agp.components.Image#setImageDrawable(android.graphics.drawable.Drawable)}.
   *
   * @param drawable {@inheritDoc}
   */
  @Override
  public void setDrawable(PixelMap drawable) {
    view.setPixelMap(drawable);
  }

  public void setDrawable(int drawable) {
    view.setPixelMap(drawable);
  }




  public void onLoadStarted(@Nullable int placeholder) {
    super.onLoadStarted(placeholder);
    setResourceInternal(null);
    setDrawable(placeholder);
  }


  @Override
  public void onLoadFailed(@Nullable int errorDrawable) {
    super.onLoadFailed(errorDrawable);
    setResourceInternal(null);
    setDrawable(errorDrawable);
  }

  @Override
  public void onLoadCleared(@Nullable int placeholder) {
    super.onLoadCleared(placeholder);

    setResourceInternal(null);
    setDrawable(placeholder);
  }

  @Override
  public void onResourceReady(Z resource, @Nullable Transition<? super Z> transition) {
    if (transition == null || !transition.transition(resource, this)) {
      setResourceInternal(resource);
    }
  }

  @Override
  public void onStart() {
  }

  @Override
  public void onStop() {
  }

  private void setResourceInternal(Z resource) {
    // Order matters here. Set the resource first to make sure that the Drawable has a valid and
    // non-null Callback before starting it.
    setResource(resource);
  }



  protected abstract void setResource(Z resource);
}

