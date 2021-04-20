package com.istone.myapplication.slice;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.istone.myapplication.ResourceTable;
import com.istone.myapplication.utils.FileUtils;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.bundle.IBundleManager;
import ohos.data.resultset.ResultSet;
import ohos.global.resource.RawFileEntry;
import ohos.global.resource.Resource;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;
import timber.log.Timber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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


        findComponentById(ResourceTable.Id_btn3_1).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                new Thread(() -> {
                    RawFileEntry rawFileEntry = getResourceManager().getRawFileEntry("resources/rawfile/B.jpg");

                    try {
                        Resource resource = rawFileEntry.openRawFile();
//                        {
//                            ImageSource.SourceOptions sourceOptions = new ImageSource.SourceOptions();
//                            sourceOptions.formatHint = CompressFormat.JPEG;
//                            ImageSource imageSource = ImageSource.create(resource, sourceOptions);
//
//                            PixelMap pixelMap = imageSource.createPixelmap(null);
//
//                            getUITaskDispatcher().asyncDispatch(() -> {
//                                image.setPixelMap(pixelMap);
//                            });
//                        }


//                            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "A.png");
//                            File file = new File(getFilesDir(), "B.jpg");
                        File file = new File(getExternalCacheDir(), "B.jpg");

                        System.out.println(file.getAbsolutePath());
                        file.setWritable(true);
                        getFilesDir().mkdirs();
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream out = new FileOutputStream(file);

                        FileUtils.copyFile(resource, out);

                        out.close();
                        resource.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        });
        findComponentById(ResourceTable.Id_btn3_2).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
//                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "A.png");
//                File file = new File(getFilesDir(), "B.jpg");
                File file = new File(getExternalCacheDir(), "B.jpg");
                System.out.println(file.getAbsolutePath());
                System.out.println("file.exists() = "+file.exists());
//                new Thread(() -> {
//
//                        {
//                            ImageSource imageSource = ImageSource.create(file, null);
//                            PixelMap pixelMap = imageSource.createPixelmap(null);
//                            System.out.println("width = "+imageSource.getImageInfo().size.width);
//
//                            getUITaskDispatcher().asyncDispatch(() -> {
//                                image.setPixelMap(pixelMap);
//                            });
//                        }
//                }).start();

                Glide.with(getContext()).load(file).into(image);

            }
        });


        if (verifySelfPermission("ohos.permission.WRITE_USER_STORAGE") != IBundleManager.PERMISSION_GRANTED) {
            // 应用未被授予权限
            if (canRequestPermission("ohos.permission.WRITE_USER_STORAGE")) {
                String[] permission = {"ohos.permission.WRITE_USER_STORAGE"};
                requestPermissionsFromUser(permission,0);
            } else {
                // 显示应用需要权限的理由，提示用户进入设置授权
            }
        }
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
