package com.istone.myapplication.slice;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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

                RequestOptions options = new RequestOptions();
                options.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
                RequestBuilder<PixelMap> request = requestManager.applyDefaultRequestOptions(options).load("https://developer.harmonyos.com/resource/image/DevEco-Studio/DevEco-Studio-0.png");

                System.out.println("00000/"+ request);
                request.into(image);
//
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
