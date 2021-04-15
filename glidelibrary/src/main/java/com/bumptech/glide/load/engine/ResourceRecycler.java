package com.bumptech.glide.load.engine;

import com.bumptech.glide.util.Util;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;

/**
 * A class that can safely recycle recursive resources.
 */
class ResourceRecycler {
  private boolean isRecycling;
  private final EventHandler handler =
      new ResourceRecyclerEventHandler(EventRunner.getMainEventRunner());

  void recycle(Resource<?> resource) {
    Util.assertMainThread();

    if (isRecycling) {
      // If a resource has sub-resources, releasing a sub resource can cause it's parent to be
      // synchronously
      // evicted which leads to a recycle loop when the parent releases it's children. Posting
      // breaks this loop.
      handler.sendEvent(InnerEvent.get(ResourceRecyclerEventHandler.RECYCLE_RESOURCE, resource));
    } else {
      isRecycling = true;
      resource.recycle();
      isRecycling = false;
    }
  }

  private static final class ResourceRecyclerEventHandler extends EventHandler {
    static final int RECYCLE_RESOURCE = 1;

    public ResourceRecyclerEventHandler(EventRunner runner) throws IllegalArgumentException {
      super(runner);
    }

    @Override
    protected void processEvent(InnerEvent event) {

      if (event.eventId == RECYCLE_RESOURCE) {
        Resource<?> resource = (Resource<?>) event.object;
        resource.recycle();
      }
    }
  }
}
