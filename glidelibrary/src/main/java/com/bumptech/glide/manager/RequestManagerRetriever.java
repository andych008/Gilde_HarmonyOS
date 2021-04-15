package com.bumptech.glide.manager;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import ohos.app.Context;

/**
 * A collection of static methods for creating new {@link RequestManager}s or
 * retrieving existing ones from activities and fragment.
 */
public class RequestManagerRetriever {
  @VisibleForTesting
  static final String FRAGMENT_TAG = "com.bumptech.glide.manager";
  private static final String TAG = "RMRetriever";

  private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;
  private static final int ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2;

  // Hacks based on the implementation of FragmentManagerImpl in the non-support libraries that
  // allow us to iterate over and retrieve all active Fragments in a FragmentManager.
  private static final String FRAGMENT_INDEX_KEY = "key";

  /**
   * The top application level RequestManager.
   */
  private volatile RequestManager applicationManager;



  private final RequestManagerFactory factory;


  public RequestManagerRetriever(@Nullable RequestManagerFactory factory) {
    this.factory = factory != null ? factory : DEFAULT_FACTORY;
  }

  private RequestManager getApplicationManager(Context context) {
    // Either an application context or we're on a background thread.
    if (applicationManager == null) {
      synchronized (this) {
        if (applicationManager == null) {
          // Normally pause/resume is taken care of by the fragment we add to the fragment or
          // activity. However, in this case since the manager attached to the application will not
          // receive lifecycle events, we must force the manager to start resumed using
          // ApplicationLifecycle.

          // TODO(b/27524013): Factor out this Glide.get() call.
          Glide glide = Glide.get(context.getApplicationContext());
          applicationManager =
              factory.build(
                  glide,
                  new com.bumptech.glide.manager.ApplicationLifecycle(),
                  new EmptyRequestManagerTreeNode(),
                  context.getApplicationContext());
        }
      }
    }

    return applicationManager;
  }

  public RequestManager get(Context context) {
    if (context == null) {
      throw new IllegalArgumentException("You cannot start a load on a null Context");
    }

    return getApplicationManager(context);
  }


  /**
   * Used internally to create {@link RequestManager}s.
   */
  public interface RequestManagerFactory {
    RequestManager build(
        Glide glide,
        Lifecycle lifecycle,
        RequestManagerTreeNode requestManagerTreeNode,
        Context context);
  }

  private static final RequestManagerFactory DEFAULT_FACTORY = new RequestManagerFactory() {
    @Override
    public RequestManager build(Glide glide, Lifecycle lifecycle,
        RequestManagerTreeNode requestManagerTreeNode, Context context) {
      return new RequestManager(glide, lifecycle, requestManagerTreeNode, context);
    }
  };
}
