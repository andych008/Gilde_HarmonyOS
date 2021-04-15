package com.bumptech.glide;

import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.request.transition.ViewPropertyTransition;

/**
 * Implementation of {@link TransitionOptions} that exposes only generic methods that can be applied
 * to any resource type.
 *
 * @param <TranscodeType> The type of the resource that will be displayed.
 */
// Public API.
@SuppressWarnings({"PMD.UseUtilityClass", "unused"})
public final class GenericTransitionOptions<TranscodeType> extends
    TransitionOptions<com.bumptech.glide.GenericTransitionOptions<TranscodeType>, TranscodeType> {
  /**
   * Removes any existing animation put on the builder.
   *
   * @see com.bumptech.glide.GenericTransitionOptions#dontTransition()
   */
  public static <TranscodeType> com.bumptech.glide.GenericTransitionOptions<TranscodeType> withNoTransition() {
    return new com.bumptech.glide.GenericTransitionOptions<TranscodeType>().dontTransition();
  }

  /**
   * Returns a typed {@link com.bumptech.glide.GenericTransitionOptions} object that uses the given view animation.
   *
   * @see com.bumptech.glide.GenericTransitionOptions#transition(int)
   */
  public static <TranscodeType> com.bumptech.glide.GenericTransitionOptions<TranscodeType> with(
      int viewAnimationId) {
    return new com.bumptech.glide.GenericTransitionOptions<TranscodeType>().transition(viewAnimationId);
  }

  /**
   * Returns a typed {@link com.bumptech.glide.GenericTransitionOptions} object that uses the given animator.
   *
   * @see com.bumptech.glide.GenericTransitionOptions#transition(ViewPropertyTransition.Animator)
   */
  public static <TranscodeType> com.bumptech.glide.GenericTransitionOptions<TranscodeType> with(
      ViewPropertyTransition.Animator animator) {
    return new com.bumptech.glide.GenericTransitionOptions<TranscodeType>().transition(animator);
  }

  /**
   * Returns a typed {@link com.bumptech.glide.GenericTransitionOptions} object that uses the given transition factory.
   *
   * @see com.bumptech.glide.GenericTransitionOptions#transition(TransitionFactory)
   */
  public static <TranscodeType> com.bumptech.glide.GenericTransitionOptions<TranscodeType> with(
      TransitionFactory<? super TranscodeType> transitionFactory) {
    return new com.bumptech.glide.GenericTransitionOptions<TranscodeType>().transition(transitionFactory);
  }
}
