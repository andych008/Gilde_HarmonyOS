package com.bumptech.glide;

import android.support.annotation.Nullable;
import android.util.Log;
import com.bumptech.glide.load.data.InputStreamRewinder;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.model.*;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader;
import com.bumptech.glide.load.model.stream.HttpUriLoader;
import com.bumptech.glide.load.model.stream.UrlLoader;
import com.bumptech.glide.load.resource.bitmap.*;
import com.bumptech.glide.load.resource.bytes.ByteBufferRewinder;
import com.bumptech.glide.load.resource.file.FileDecoder;
import com.bumptech.glide.load.resource.transcode.BitmapBytesTranscoder;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import ohos.app.Context;
import ohos.media.image.PixelMap;
import ohos.utils.net.Uri;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A singleton to present a simple static interface for building requests with
 * {@link RequestBuilder} and maintaining an {@link Engine}, {@link BitmapPool},
 * {@link com.bumptech.glide.load.engine.cache.DiskCache} and {@link MemoryCache}.
 */
public class Glide {
  private static final String DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache";
  private static final String TAG = "Glide";
  private static volatile com.bumptech.glide.Glide glide;
  private static volatile boolean isInitializing;

  private final Engine engine;
  private final BitmapPool bitmapPool;
  private final MemoryCache memoryCache;
  private final GlideContext glideContext;
  private final Registry registry;
  private final ArrayPool arrayPool;
  private final RequestManagerRetriever requestManagerRetriever;
  private final List<RequestManager> managers = new ArrayList<>();
  private MemoryCategory memoryCategory = MemoryCategory.NORMAL;


  public static File getPhotoCacheDir(Context context) {
    return getPhotoCacheDir(context, DEFAULT_DISK_CACHE_DIR);
  }

  /**
   * Returns a directory with the given name in the private cache directory of the application to
   * use to store retrieved media and thumbnails.
   *
   * @param context   A context.
   * @param cacheName The name of the subdirectory in which to store the cache.
   */
  public static File getPhotoCacheDir(Context context, String cacheName) {
    File cacheDir = context.getCacheDir();
    if (cacheDir != null) {
      File result = new File(cacheDir, cacheName);
      if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
        // File wasn't able to create a directory, or the result exists but not a directory
        return null;
      }
      return result;
    }
    if (Log.isLoggable(TAG, Log.ERROR)) {
      Log.e(TAG, "default disk cache dir is null");
    }
    return null;
  }

  /**
   * Get the singleton.
   *
   * @return the singleton
   */
  public static com.bumptech.glide.Glide get(Context context) {
    if (glide == null) {
      synchronized (com.bumptech.glide.Glide.class) {
        if (glide == null) {
          checkAndInitializeGlide(context);
        }
      }
    }

    return glide;
  }

  private static void checkAndInitializeGlide(Context context) {
    // In the thread running initGlide(), one or more classes may call Glide.get(context).
    // Without this check, those calls could trigger infinite recursion.
    if (isInitializing) {
      throw new IllegalStateException("You cannot call Glide.get() in registerComponents(),"
          + " use the provided Glide instance instead");
    }
    isInitializing = true;
    initializeGlide(context);
    isInitializing = false;
  }


  private static void initializeGlide(Context context) {
    initializeGlide(context, new GlideBuilder());
  }

  @SuppressWarnings("deprecation")
  private static void initializeGlide(Context context, GlideBuilder builder) {
    Context applicationContext = context.getApplicationContext();
    GeneratedAppGlideModule annotationGeneratedModule = getAnnotationGeneratedGlideModules();


    RequestManagerRetriever.RequestManagerFactory factory =
        annotationGeneratedModule != null
            ? annotationGeneratedModule.getRequestManagerFactory() : null;
    builder.setRequestManagerFactory(factory);


    Glide glide = builder.build(applicationContext);

    Glide.glide = glide;
  }

  @Nullable
  @SuppressWarnings({"unchecked", "deprecation", "TryWithIdenticalCatches"})
  private static GeneratedAppGlideModule getAnnotationGeneratedGlideModules() {
    GeneratedAppGlideModule result = null;
    try {
      Class<GeneratedAppGlideModule> clazz =
          (Class<GeneratedAppGlideModule>)
              Class.forName("com.bumptech.glide.GeneratedAppGlideModuleImpl");
      result = clazz.newInstance();
    } catch (ClassNotFoundException e) {
      if (Log.isLoggable(TAG, Log.WARN)) {
        Log.w(TAG, "Failed to find GeneratedAppGlideModule. You should include an"
            + " annotationProcessor compile dependency on com.github.bumptech.glide:compiler"
            + " in your application and a @GlideModule annotated AppGlideModule implementation or"
            + " LibraryGlideModules will be silently ignored");
      }
    } catch (InstantiationException e) {
      throw new IllegalStateException("GeneratedAppGlideModuleImpl is implemented incorrectly."
          + " If you've manually implemented this class, remove your implementation. The Annotation"
          + " processor will generate a correct implementation.", e);
      // These exceptions can't be squashed across all versions of Android.
    } catch (IllegalAccessException e) {
      throw new IllegalStateException("GeneratedAppGlideModuleImpl is implemented incorrectly."
          + " If you've manually implemented this class, remove your implementation. The Annotation"
          + " processor will generate a correct implementation.", e);
    }
    return result;
  }

  Glide(
      Context context,
      Engine engine,
      MemoryCache memoryCache,
      BitmapPool bitmapPool,
      ArrayPool arrayPool,
      RequestManagerRetriever requestManagerRetriever,
      int logLevel,
      RequestOptions defaultRequestOptions,
      Map<Class<?>, TransitionOptions<?, ?>> defaultTransitionOptions) {
    this.engine = engine;
    this.bitmapPool = bitmapPool;
    this.arrayPool = arrayPool;
    this.memoryCache = memoryCache;
    this.requestManagerRetriever = requestManagerRetriever;


    registry = new Registry();
    registry.register(new DefaultImageHeaderParser());


    Downsampler downsampler = new Downsampler(bitmapPool);
    ByteBufferBitmapDecoder byteBufferBitmapDecoder = new ByteBufferBitmapDecoder(downsampler);
    StreamBitmapDecoder streamBitmapDecoder = new StreamBitmapDecoder(downsampler, arrayPool);

    BitmapEncoder bitmapEncoder = new BitmapEncoder();

    registry
        .append(ByteBuffer.class, new ByteBufferEncoder())
        .append(InputStream.class, new StreamEncoder(arrayPool))
        /* Bitmaps */
        .append(Registry.BUCKET_BITMAP, ByteBuffer.class, PixelMap.class, byteBufferBitmapDecoder)
        .append(Registry.BUCKET_BITMAP, InputStream.class, PixelMap.class, streamBitmapDecoder)
        .append(
            Registry.BUCKET_BITMAP, PixelMap.class, PixelMap.class, new UnitBitmapDecoder())
        .append(PixelMap.class, PixelMap.class, UnitModelLoader.Factory.<PixelMap>getInstance())
        .append(PixelMap.class, bitmapEncoder)

        /* Files */
        .register(new ByteBufferRewinder.Factory())
        .append(File.class, ByteBuffer.class, new ByteBufferFileLoader.Factory())
        .append(File.class, InputStream.class, new FileLoader.StreamFactory())
        .append(File.class, File.class, new FileDecoder())
        // Compilation with Gradle requires the type to be specified for UnitModelLoader here.
        .append(File.class, File.class, UnitModelLoader.Factory.<File>getInstance())

        /* Models */
        .register(new InputStreamRewinder.Factory(arrayPool))
        .append(String.class, InputStream.class, new DataUrlLoader.StreamFactory())
        .append(String.class, InputStream.class, new StringLoader.StreamFactory())

        .append(Uri.class, InputStream.class, new HttpUriLoader.Factory())

        .append(
            Uri.class,
            InputStream.class,
            new UriLoader.StreamFactory(context))
        .append(Uri.class, InputStream.class, new UrlUriLoader.StreamFactory())
        .append(Uri.class, InputStream.class, new MediaStoreInputStreamLoader.Factory(context))
        .append(URL.class, InputStream.class, new UrlLoader.StreamFactory())
        .append(GlideUrl.class, InputStream.class, new HttpGlideUrlLoader.Factory())
        .append(byte[].class, ByteBuffer.class, new ByteArrayLoader.ByteBufferFactory())
        .append(byte[].class, InputStream.class, new ByteArrayLoader.StreamFactory())
        .register(PixelMap.class, byte[].class, new BitmapBytesTranscoder());

    ImageViewTargetFactory imageViewTargetFactory = new ImageViewTargetFactory();
    glideContext =
        new GlideContext(
            context,
            arrayPool,
            registry,
            imageViewTargetFactory,
            defaultRequestOptions,
            defaultTransitionOptions,
            engine,
            logLevel);
  }

  /**
   * Returns the {@link BitmapPool} used to
   * temporarily store {@link ohos.media.image.PixelMap}s so they can be reused to avoid garbage
   * collections.
   *
   * <p> Note - Using this pool directly can lead to undefined behavior and strange drawing errors.
   * Any {@link ohos.media.image.PixelMap} added to the pool must not be currently in use in any other
   * part of the application. Any {@link ohos.media.image.PixelMap} added to the pool must be removed
   * from the pool before it is added a second time. </p>
   *
   * <p> Note - To make effective use of the pool, any {@link ohos.media.image.PixelMap} removed from
   * the pool must eventually be re-added. Otherwise the pool will eventually empty and will not
   * serve any useful purpose. </p>
   *
   * <p> The primary reason this object is exposed is for use in custom
   * {@link com.bumptech.glide.load.ResourceDecoder}s and
   * {@link com.bumptech.glide.load.Transformation}s. Use outside of these classes is not generally
   * recommended. </p>
   */
  public BitmapPool getBitmapPool() {
    return bitmapPool;
  }

  public ArrayPool getArrayPool() {
    return arrayPool;
  }

  /**
   * @return The context associated with this instance.
   */
  public Context getContext() {
    return glideContext.getContext();
  }

  GlideContext getGlideContext() {
    return glideContext;
  }



  /**
   * Clears as much memory as possible.
   *
   */
  public void clearMemory() {
    // Engine asserts this anyway when removing resources, fail faster and consistently
    Util.assertMainThread();
    // memory cache needs to be cleared before bitmap pool to clear re-pooled Bitmaps too. See #687.
    memoryCache.clearMemory();
    bitmapPool.clearMemory();
    arrayPool.clearMemory();
  }

  /**
   * Clears disk cache.
   *
   * <p>
   *     This method should always be called on a background thread, since it is a blocking call.
   * </p>
   */
  // Public API.
  @SuppressWarnings({"unused", "WeakerAccess"})
  public void clearDiskCache() {
    Util.assertBackgroundThread();
    engine.clearDiskCache();
  }

  /**
   * Internal method.
   */
  public RequestManagerRetriever getRequestManagerRetriever() {
    return requestManagerRetriever;
  }

  /**
   * Adjusts Glide's current and maximum memory usage based on the given {@link MemoryCategory}.
   *
   * <p> The default {@link MemoryCategory} is {@link MemoryCategory#NORMAL}.
   * {@link MemoryCategory#HIGH} increases Glide's maximum memory usage by up to 50% and
   * {@link MemoryCategory#LOW} decreases Glide's maximum memory usage by 50%. This method should be
   * used to temporarily increase or decrease memory usage for a single Activity or part of the app.
   * Use {@link GlideBuilder#setMemoryCache(MemoryCache)} to put a permanent memory size if you want
   * to change the default. </p>
   *
   * @return the previous MemoryCategory used by Glide.
   */
  public MemoryCategory setMemoryCategory(MemoryCategory memoryCategory) {
    // Engine asserts this anyway when removing resources, fail faster and consistently
    Util.assertMainThread();
    // memory cache needs to be trimmed before bitmap pool to trim re-pooled Bitmaps too. See #687.
    memoryCache.setSizeMultiplier(memoryCategory.getMultiplier());
    bitmapPool.setSizeMultiplier(memoryCategory.getMultiplier());
    MemoryCategory oldCategory = this.memoryCategory;
    this.memoryCategory = memoryCategory;
    return oldCategory;
  }

  private static RequestManagerRetriever getRetriever(@Nullable Context context) {
    // Context could be null for other reasons (ie the user passes in null), but in practice it will
    // only occur due to errors with the Fragment lifecycle.
    Preconditions.checkNotNull(
        context,
        "You cannot start a load on a not yet attached View or a  Fragment where getActivity() "
            + "returns null (which usually occurs when getActivity() is called before the Fragment "
            + "is attached or after the Fragment is destroyed).");
    return get(context).getRequestManagerRetriever();
  }


  public static RequestManager with(Context context) {
    return getRetriever(context).get(context);
  }

  public Registry getRegistry() {
    return registry;
  }

  boolean removeFromManagers(Target<?> target) {
    synchronized (managers) {
      for (RequestManager requestManager : managers) {
        if (requestManager.untrack(target)) {
          return true;
        }
      }
    }

    return false;
  }

  void registerRequestManager(RequestManager requestManager) {
    synchronized (managers) {
      if (managers.contains(requestManager)) {
        throw new IllegalStateException("Cannot register already registered manager");
      }
      managers.add(requestManager);
    }
  }

  void unregisterRequestManager(RequestManager requestManager) {
    synchronized (managers) {
      if (!managers.contains(requestManager)) {
        throw new IllegalStateException("Cannot unregister not yet registered manager");
      }
      managers.remove(requestManager);
    }
  }
}
