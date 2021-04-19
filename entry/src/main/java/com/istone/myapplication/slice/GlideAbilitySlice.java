package com.istone.myapplication.slice;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.istone.myapplication.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.data.resultset.ResultSet;
import ohos.media.image.PixelMap;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;
import timber.log.Timber;

import java.util.ArrayList;

public class GlideAbilitySlice extends AbilitySlice {

    private Image image;
    private ArrayList<Integer> img_ids = new ArrayList<>();;
    int img_id;
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
                options.skipMemoryCache(true);
//                options.diskCacheStrategy(DiskCacheStrategy.NONE);
                RequestBuilder<PixelMap> request = requestManager.load("https://developer.harmonyos.com/resource/image/DevEco-Studio/DevEco-Studio-0.png");

                System.out.println("00000/"+ request);
                request.into(image);
//
            }
        });
        findComponentById(ResourceTable.Id_btn2).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                Timber.i("加载图片....");
                DataAbilityHelper dataAbilityHelper = DataAbilityHelper.creator(getContext());

                if (img_ids.size()==0) {
                    ResultSet result = null;
                    try {
                        result = dataAbilityHelper.query(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, null, null);

                        System.out.println("照片数量："+result.getRowCount());
                    } catch (DataAbilityRemoteException e) {
                        e.printStackTrace();
                    }

                    while(result != null && result.goToNextRow()){
                        int mediaId = result.getInt(result.getColumnIndexForName(AVStorage.Images.Media.ID));
                        img_ids.add(mediaId);
                    }
                }

                if (img_ids.size()>0) {
                    if (img_id==img_ids.size()) {
                        img_id=0;
                    }
                    Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI,""+img_ids.get(img_id++));
                    System.out.println("uri："+uri.getDecodedPath());

                    Glide.with(getContext()).load(uri).into(image);
                }


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
