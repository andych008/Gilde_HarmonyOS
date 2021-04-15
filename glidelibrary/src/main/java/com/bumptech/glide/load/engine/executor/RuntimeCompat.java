package com.bumptech.glide.load.engine.executor;

/**
 * Compatibility methods for {@link Runtime}.
 */
final class RuntimeCompat {

  private RuntimeCompat() {
    // Utility class.
  }

  /**
   * Determines the number of cores available on the device.
   */
  static int availableProcessors() {
    return Runtime.getRuntime().availableProcessors();
  }

}
