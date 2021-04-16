package com.istone.myapplication.slice;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.istone.myapplication.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.media.image.PixelMap;
import timber.log.Timber;

public class GlideAbilitySlice extends AbilitySlice {

    private Image image;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_glide);

        image = (Image)findComponentById(ResourceTable.Id_image);
//        image.setForeground(new ShapeElement(this, ShapeElement.OVAL));
        findComponentById(ResourceTable.Id_btn1).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                Timber.i("加载图片....");

                RequestManager requestManager = Glide.with(getContext());

                RequestBuilder<PixelMap> request = requestManager.load("https://developer.harmonyos.com/resource/image/DevEco-Studio/DevEco-Studio-0.png");
                request.listener(new RequestListener<PixelMap>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<PixelMap> target, boolean isFirstResource) {
                        Timber.e("onLoadFailed() called with: e = [ %s ], model = [ %s ], target = [ %s ], isFirstResource = [ %s ]", e, model, target, isFirstResource);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(PixelMap resource, Object model, Target<PixelMap> target, DataSource dataSource, boolean isFirstResource) {
                        Timber.e("onResourceReady() called with: resource = [ %s ], model = [ %s ], target = [ %s ], dataSource = [ %s ], isFirstResource = [ %s ]", resource, model, target, dataSource, isFirstResource);
                        return true;
                    }
//                }).submit();
                }).into(image);
//                request.into(image);
            }
        });
        findComponentById(ResourceTable.Id_btn2).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                Timber.i("加载图片....");


            }
        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
