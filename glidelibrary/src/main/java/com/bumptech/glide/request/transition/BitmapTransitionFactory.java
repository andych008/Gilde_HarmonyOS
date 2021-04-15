package com.bumptech.glide.request.transition;

import ohos.media.image.PixelMap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import com.bumptech.glide.request.transition.BitmapContainerTransitionFactory;
import com.bumptech.glide.request.transition.TransitionFactory;

/**
 * A {@link TransitionFactory} for {@link ohos.media.image.PixelMap}s that uses a Drawable transition
 * factory to transition from an existing drawable already visible on the target to the new bitmap.
 *
 * @see BitmapContainerTransitionFactory
 */
public class BitmapTransitionFactory extends BitmapContainerTransitionFactory<Bitmap> {
  public BitmapTransitionFactory(@NonNull TransitionFactory<Drawable> realFactory) {
    super(realFactory);
  }

  @Override
  @NonNull
  protected Bitmap getBitmap(@NonNull Bitmap current) {
    return current;
  }
}
