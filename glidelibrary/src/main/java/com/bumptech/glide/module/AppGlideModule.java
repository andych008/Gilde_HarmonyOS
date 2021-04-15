package com.bumptech.glide.module;

import ohos.app.Context;
import com.bumptech.glide.GlideBuilder;

/**
 * Defines a set of dependencies and options to use when initializing Glide within an application.
 *
 * <p>There can be at most one {@link com.bumptech.glide.module.AppGlideModule} in an application. Only Applications
 * can include a {@link com.bumptech.glide.module.AppGlideModule}. Libraries must use {@link LibraryGlideModule}.
 *
 * <p>Classes that extend {@link com.bumptech.glide.module.AppGlideModule} must be annotated with
 * {@link com.bumptech.glide.annotation.GlideModule} to be processed correctly.
 *
 * <p>Classes that extend {@link com.bumptech.glide.module.AppGlideModule} can optionally be annotated with
 * {@link com.bumptech.glide.annotation.Excludes} to optionally exclude one or more
 * {@link LibraryGlideModule} and/or {@link GlideModule} classes.
 *
 * <p>Once an application has migrated itself and all libraries it depends on to use Glide's
 * annotation processor, {@link com.bumptech.glide.module.AppGlideModule} implementations should override
 * {@link #isManifestParsingEnabled()} and return {@code false}.
 */
// Used only in javadoc.
@SuppressWarnings("deprecation")
public abstract class AppGlideModule extends LibraryGlideModule implements AppliesOptions {
  /**
   * Returns {@code true} if Glide should check the AndroidManifest for {@link GlideModule}s.
   *
   * <p>Implementations should return {@code false} after they and their dependencies have migrated
   * to Glide's annotation processor.
   *
   * <p>Returns {@code true} by default.
   */
  public boolean isManifestParsingEnabled() {
    return true;
  }

  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    // Default empty impl.
  }
}
