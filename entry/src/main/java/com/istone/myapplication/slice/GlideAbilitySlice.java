package com.istone.myapplication.slice;


import com.bumptech.glide.Glide;
import com.istone.myapplication.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
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

                Glide.with(getContext()).
                    load("https://developer.harmonyos.com/resource/image/DevEco-Studio/DevEco-Studio-0.png")
                    .into(image);
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
