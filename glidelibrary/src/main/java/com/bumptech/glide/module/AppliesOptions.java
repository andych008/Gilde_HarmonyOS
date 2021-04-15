package com.bumptech.glide.module;

import com.bumptech.glide.module.GlideModule;
import ohos.app.Context;
import com.bumptech.glide.GlideBuilder;

/**
 * An internal interface, to be removed when {@link GlideModule}s are removed.
 */
interface AppliesOptions {
  /**
   * Lazily apply options to a {@link GlideBuilder} immediately before the Glide
   * singleton is created.
   *
   * <p> This method will be called once and only once per implementation. </p>
   *
   * @param context An Application {@link android.content.Context}.
   * @param builder The {@link GlideBuilder} that will be used to create Glide.
   */
  void applyOptions(Context context, GlideBuilder builder);
}
