package com.bumptech.glide;

import ohos.agp.components.Image;
import ohos.app.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.ViewTarget;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Global context for all loads in Glide containing and exposing the various registries and classes
 * required to load resources.
 */
public class GlideContext {
  @VisibleForTesting
  static final TransitionOptions<?, ?> DEFAULT_TRANSITION_OPTIONS =
      new GenericTransitionOptions<>();
  private final EventHandler mainHandler;
  private final ArrayPool arrayPool;
  private final Registry registry;
  private final ImageViewTargetFactory imageViewTargetFactory;
  private final RequestOptions defaultRequestOptions;
  private final Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions;
  private final Engine engine;
  private final int logLevel;
  private final Context context;

  public GlideContext(
      Context context,
      ArrayPool arrayPool,
      Registry registry,
      ImageViewTargetFactory imageViewTargetFactory,
      RequestOptions defaultRequestOptions,
      Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions,
      Engine engine,
      int logLevel) {
    this.context = context;
    this.arrayPool = arrayPool;
    this.registry = registry;
    this.imageViewTargetFactory = imageViewTargetFactory;
    this.defaultRequestOptions = defaultRequestOptions;
    this.defaultTransitionOptions = defaultTransitionOptions;
    this.engine = engine;
    this.logLevel = logLevel;

    mainHandler = new EventHandler(EventRunner.getMainEventRunner());

  }

  public RequestOptions getDefaultRequestOptions() {
    return defaultRequestOptions;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public <T> TransitionOptions<?, T> getDefaultTransitionOptions(Class<T> transcodeClass) {
    TransitionOptions<?, ?> result = defaultTransitionOptions.get(transcodeClass);
    if (result == null) {
      for (Entry<Class<?>, TransitionOptions<?, ?>> value : defaultTransitionOptions.entrySet()) {
        if (value.getKey().isAssignableFrom(transcodeClass)) {
          result = value.getValue();
        }
      }
    }
    if (result == null) {
      result = DEFAULT_TRANSITION_OPTIONS;
    }
    return (TransitionOptions<?, T>) result;
  }

  public <X> ViewTarget<Image, X> buildImageViewTarget(
          Image imageView, Class<X> transcodeClass) {
    return imageViewTargetFactory.buildTarget(imageView, transcodeClass);
  }

  public Context getContext() {
    return context;
  }

  public EventHandler getMainHandler() {
    return mainHandler;
  }

  public Engine getEngine() {
    return engine;
  }

  public Registry getRegistry() {
    return registry;
  }

  public int getLogLevel() {
    return logLevel;
  }

  public ArrayPool getArrayPool() {
    return arrayPool;
  }
}
