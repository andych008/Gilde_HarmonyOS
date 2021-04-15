package com.bumptech.glide.request.target;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Synthetic;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.utils.Point;
import ohos.agp.window.service.Display;
import ohos.agp.window.service.DisplayManager;
import ohos.app.Context;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A base {@link Target} for loading {@link ohos.media.image.PixelMap}s into @link View}s that
 * provides default implementations for most most methods and can determine the size of views using
 * a @link android.view.ViewTreeObserver.OnDrawListener}.
 *
 * <p> To detect @link View} reuse in @link android.widget.ListView} or any @link
 * android.view.ViewGroup} that reuses views, this class uses the @link View#setTag(Object)} method
 * to store some metadata so that if a view is reused, any previous loads or resources from previous
 * loads can be cancelled or reused. </p>
 *
 * <p> Any calls to @link View#setTag(Object)}} on a View given to this class will result in
 * excessive allocations and and/or {@link IllegalArgumentException}s. If you must call @link
 * View#setTag(Object)} on a view, consider using {@link BaseTarget} or {@link SimpleTarget}
 * instead. </p>
 *
 * <p> Subclasses must call super in @link #onLoadCleared(Drawable)} </p>
 *
 * @param <T> The specific subclass of view wrapped by this target.
 * @param <Z> The resource type this target will receive.
 */
public abstract class ViewTarget<T extends Component, Z> extends BaseTarget<Z> {
  private static final String TAG = "ViewTarget";

  protected final T view;
  private final SizeDeterminer sizeDeterminer;
  @Nullable
  private Component.BindStateChangedListener attachStateListener;
  private boolean isClearedByUs;
  private boolean isAttachStateListenerAdded;


  /**
   * Constructor that defaults {@code waitForLayout} to {@code false}.
   */
  public ViewTarget(T view) {
    this.view = Preconditions.checkNotNull(view);
    sizeDeterminer = new SizeDeterminer(view);
  }

  /**
   * Clears the @link View}'s {@link Request} when the @link View} is detached from its
   * @link android.view.Window} and restarts the {@link Request} when the @link View} is
   * re-attached from its @link android.view.Window}.
   *
   * <p>This is an experimental API that may be removed in a future version.
   *
   * <p>Using this method can save memory by allowing Glide to more eagerly clear resources when
   * transitioning screens or swapping adapters in scrolling views. However it also substantially
   * increases the odds that images will not be in memory if users subsequently return to a screen
   * where images were previously loaded. Whether or not this happens will depend on the number
   * of images loaded in the new screen and the size of the memory cache. Increasing the size of
   * the memory cache can improve this behavior but it largely negates the memory benefits of using
   * this method.
   *
   * <p>Use this method with caution and measure your memory usage to ensure that it's actually
   * improving your memory usage in the cases you care about.
   */
  // Public API.
  @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
  public final ViewTarget<T, Z> clearOnDetach() {
    if (attachStateListener != null) {
      return this;
    }
    attachStateListener = new Component.BindStateChangedListener() {
      @Override
      public void onComponentBoundToWindow(Component v) {
        Request request = getRequest();
        if (request != null && request.isPaused()) {
          request.begin();
        }
      }

      @Override
      public void onComponentUnboundFromWindow(Component v) {
        Request request = getRequest();
        if (request != null && !request.isCancelled() && !request.isPaused()) {
          isClearedByUs = true;
          request.pause();
          isClearedByUs = false;
        }
      }

    };
    maybeAddAttachStateListener();
    return this;
  }

  @CallSuper
  @Override
  public void onLoadStarted(@Nullable int placeholder) {
    super.onLoadStarted(placeholder);
    maybeAddAttachStateListener();
  }

  private void maybeAddAttachStateListener() {
    if (attachStateListener == null || isAttachStateListenerAdded) {
      return;
    }

    view.setBindStateChangedListener(attachStateListener);
    isAttachStateListenerAdded = true;
  }

  private void maybeRemoveAttachStateListener() {
    if (attachStateListener == null || !isAttachStateListenerAdded) {
      return;
    }

    view.setBindStateChangedListener(null);
    isAttachStateListenerAdded = false;
  }

  /**
   * Returns the wrapped {@link ohos.agp.components.Component}.
   */
  public T getView() {
    return view;
  }

  /**
   * Determines the size of the view by first checking {@link ohos.agp.components.Component#getWidth()} and
   * {@link ohos.agp.components.Component#getHeight()}.
   */
  @CallSuper
  @Override
  public void getSize(SizeReadyCallback cb) {
    sizeDeterminer.getSize(cb);
  }

  @CallSuper
  @Override
  public void removeCallback(SizeReadyCallback cb) {
    sizeDeterminer.removeCallback(cb);
  }

  @CallSuper
  @Override
  public void onLoadCleared(int placeholder) {
    super.onLoadCleared(placeholder);
    sizeDeterminer.clearCallbacksAndListener();

    if (!isClearedByUs) {
      maybeRemoveAttachStateListener();
    }
  }

  /**
   * Stores the request using @link View#setTag(Object)}.
   *
   * @param request {@inheritDoc}
   */
  @Override
  public void setRequest(@Nullable Request request) {
    setTag(request);
  }

  /**
   * Returns any stored request using {@link ohos.agp.components.Component#getTag()}.
   *
   * <p> For Glide to function correctly, Glide must be the only thing that calls @link
   * View#setTag(Object)}. If the tag is cleared or put to another object type, Glide will not be
   * able to retrieve and cancel previous loads which will not only prevent Glide from reusing
   * resource, but will also result in incorrect images being loaded and lots of flashing of images
   * in lists. As a result, this will throw an {@link IllegalArgumentException} if {@link
   * ohos.agp.components.Component#getTag()}} returns a non null object that is not an {@link
   * Request}. </p>
   */
  @Override
  @Nullable
  public Request getRequest() {
    Object tag = getTag();
    Request request = null;
    if (tag != null) {
      if (tag instanceof Request) {
        request = (Request) tag;
      } else {
        throw new IllegalArgumentException(
            "You must not call setTag() on a view Glide is targeting");
      }
    }
    return request;
  }

  @Override
  public String toString() {
    return "Target for: " + view;
  }

  private void setTag(@Nullable Object tag) {
    view.setTag(tag);
  }

  @Nullable
  private Object getTag() {
    return view.getTag();
  }



  @VisibleForTesting
  static final class SizeDeterminer {
    // Some negative sizes (Target.SIZE_ORIGINAL) are valid, 0 is never valid.
    private static final int PENDING_SIZE = 0;
    @VisibleForTesting
    @Nullable
    static Integer maxDisplayLength;
    private final Component view;
    private final List<SizeReadyCallback> cbs = new ArrayList<>();
    private boolean waitForLayout;

    @Nullable private SizeDeterminerLayoutListener layoutListener;

    SizeDeterminer(Component view) {
      this.view = view;
    }

    // Use the maximum to avoid depending on the device's current orientation.
    private static int getMaxDisplayLength(Context context) {
      if (maxDisplayLength == null) {
        Optional<Display>
            display = DisplayManager.getInstance().getDefaultDisplay(context);
        Point displayDimensions = new Point();
        display.get().getSize(displayDimensions);

        maxDisplayLength = Math.max(displayDimensions.getPointXToInt(), displayDimensions.getPointYToInt());
      }
      return maxDisplayLength;
    }

    private void notifyCbs(int width, int height) {
      // One or more callbacks may trigger the removal of one or more additional callbacks, so we
      // need a copy of the list to avoid a concurrent modification exception. One place this
      // happens is when a full request completes from the in memory cache while its thumbnail is
      // still being loaded asynchronously. See #2237.
      for (SizeReadyCallback cb : new ArrayList<>(cbs)) {
        cb.onSizeReady(width, height);
      }
    }

    @Synthetic
    void checkCurrentDimens() {
      if (cbs.isEmpty()) {
        return;
      }

      int currentWidth = getTargetWidth();
      int currentHeight = getTargetHeight();
      if (!isViewStateAndSizeValid(currentWidth, currentHeight)) {
        return;
      }

      notifyCbs(currentWidth, currentHeight);
      clearCallbacksAndListener();
    }

    void getSize(SizeReadyCallback cb) {
      int currentWidth = getTargetWidth();
      int currentHeight = getTargetHeight();
      if (isViewStateAndSizeValid(currentWidth, currentHeight)) {
        cb.onSizeReady(currentWidth, currentHeight);
        return;
      }

      // We want to notify callbacks in the order they were added and we only expect one or two
      // callbacks to be added a time, so a List is a reasonable choice.
      if (!cbs.contains(cb)) {
        cbs.add(cb);
      }
      if (layoutListener == null) {
        layoutListener = new SizeDeterminerLayoutListener(this);
      }
    }

    /**
     * The callback may be called anyway if it is removed by another {@link SizeReadyCallback} or
     * otherwise removed while we're notifying the list of callbacks.
     *
     * <p>See #2237.
     */
    void removeCallback(SizeReadyCallback cb) {
      cbs.remove(cb);
    }

    void clearCallbacksAndListener() {
      // Keep a reference to the layout attachStateListener and remove it here
      // rather than having the observer remove itself because the observer
      // we add the attachStateListener to will be almost immediately merged into
      // another observer and will therefore never be alive. If we instead
      // keep a reference to the attachStateListener and remove it here, we get the
      // current view tree observer and should succeed.
      layoutListener = null;
      cbs.clear();
    }

    private boolean isViewStateAndSizeValid(int width, int height) {
      return isDimensionValid(width) && isDimensionValid(height);
    }

    private int getTargetHeight() {
      int verticalPadding = view.getPaddingTop() + view.getPaddingBottom();
      ComponentContainer.LayoutConfig layoutParams = view.getLayoutConfig();
      int layoutParamSize = layoutParams != null ? layoutParams.height : PENDING_SIZE;
      return getTargetDimen(view.getHeight(), layoutParamSize, verticalPadding);
    }

    private int getTargetWidth() {
      int horizontalPadding = view.getPaddingLeft() + view.getPaddingRight();
      ComponentContainer.LayoutConfig layoutParams = view.getLayoutConfig();
      int layoutParamSize = layoutParams != null ? layoutParams.width : PENDING_SIZE;
      return getTargetDimen(view.getWidth(), layoutParamSize, horizontalPadding);
    }

    private int getTargetDimen(int viewSize, int paramSize, int paddingSize) {
      // We consider the View state as valid if the View has non-null layout params and a non-zero
      // layout params width and height. This is imperfect. We're making an assumption that View
      // parents will obey their child's layout parameters, which isn't always the case.
      int adjustedParamSize = paramSize - paddingSize;
      if (adjustedParamSize > 0) {
        return adjustedParamSize;
      }


      // We also consider the View state valid if the View has a non-zero width and height. This
      // means that the View has gone through at least one layout pass. It does not mean the Views
      // width and height are from the current layout pass. For example, if a View is re-used in
      // RecyclerView or ListView, this width/height may be from an old position. In some cases
      // the dimensions of the View at the old position may be different than the dimensions of the
      // View in the new position because the LayoutManager/ViewParent can arbitrarily decide to
      // change them. Nevertheless, in most cases this should be a reasonable choice.
      int adjustedViewSize = viewSize - paddingSize;
      if (adjustedViewSize > 0) {
        return adjustedViewSize;
      }

      // Finally we consider the view valid if the layout parameter size is set to wrap_content.
      // It's difficult for Glide to figure out what to do here. Although Target.SIZE_ORIGINAL is a
      // coherent choice, it's extremely dangerous because original images may be much too large to
      // fit in memory or so large that only a couple can fit in memory, causing OOMs. If users want
      // the original image, they can always use .override(Target.SIZE_ORIGINAL). Since wrap_content
      // may never resolve to a real size unless we load something, we aim for a square whose length
      // is the largest screen size. That way we're loading something and that something has some
      // hope of being downsampled to a size that the device can support. We also log a warning that
      // tries to explain what Glide is doing and why some alternatives are preferable.
      // Since WRAP_CONTENT is sometimes used as a default layout parameter, we always wait for
      // layout to complete before using this fallback parameter (ConstraintLayout among others).
      if (paramSize == ComponentContainer.LayoutConfig.MATCH_CONTENT) {
        if (Log.isLoggable(TAG, Log.INFO)) {
          Log.i(TAG, "Glide treats LayoutParams.WRAP_CONTENT as a request for an image the size of"
              + " this device's screen dimensions. If you want to load the original image and are"
              + " ok with the corresponding memory cost and OOMs (depending on the input size), use"
              + " .override(Target.SIZE_ORIGINAL). Otherwise, use LayoutParams.MATCH_PARENT, set"
              + " layout_width and layout_height to fixed dimension, or use .override() with fixed"
              + " dimensions.");
        }
        return getMaxDisplayLength(view.getContext());
      }

      // If the layout parameters are < padding, the view size is < padding, or the layout
      // parameters are set to match_parent or wrap_content and no layout has occurred, we should
      // wait for layout and repeat.
      return PENDING_SIZE;
    }

    private boolean isDimensionValid(int size) {
      return size > 0 || size == SIZE_ORIGINAL;
    }

    private static final class SizeDeterminerLayoutListener {
      private final WeakReference<SizeDeterminer> sizeDeterminerRef;

      SizeDeterminerLayoutListener(SizeDeterminer sizeDeterminer) {
        sizeDeterminerRef = new WeakReference<>(sizeDeterminer);
      }

      public boolean onPreDraw() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
          Log.v(TAG, "OnGlobalLayoutListener called attachStateListener=" + this);
        }
        SizeDeterminer sizeDeterminer = sizeDeterminerRef.get();
        if (sizeDeterminer != null) {
          sizeDeterminer.checkCurrentDimens();
        }
        return true;
      }
    }
  }
}
