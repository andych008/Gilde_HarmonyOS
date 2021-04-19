package com.bumptech.glide.load.data;

import android.support.annotation.NonNull;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.app.Context;
import ohos.utils.net.Uri;

import java.io.*;

/**
 * Fetches an {@link java.io.InputStream} for a local {@link ohos.utils.net.Uri}.
 */
public class StreamLocalUriFetcher extends LocalUriFetcher<InputStream> {


  public StreamLocalUriFetcher(Context context, Uri uri) {
    super(context, uri);
  }

  @Override
  protected InputStream loadResource(Uri uri, DataAbilityHelper dataAbilityHelper)
      throws FileNotFoundException {
    InputStream inputStream = loadResourceFromUri(uri, dataAbilityHelper);
    if (inputStream == null) {
      throw new FileNotFoundException("InputStream is null for " + uri);
    }
    return inputStream;
  }


  private InputStream loadResourceFromUri(Uri uri, DataAbilityHelper dataAbilityHelper)
      throws FileNotFoundException {
    InputStream inputStream = null;

    try {
      FileDescriptor fd = dataAbilityHelper.openFile(uri,"r");
      inputStream = new FileInputStream(fd);
    } catch (DataAbilityRemoteException e) {
      e.printStackTrace();
    }

    return inputStream;
  }


  @Override
  protected void close(InputStream data) throws IOException {
    data.close();
  }

  @NonNull
  @Override
  public Class<InputStream> getDataClass() {
    return InputStream.class;
  }
}
