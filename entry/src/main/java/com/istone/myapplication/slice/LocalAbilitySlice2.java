package com.istone.myapplication.slice;

import com.bumptech.glide.Glide;
import com.isotne.glidelibrary.utils.OhosGlide;
import com.istone.myapplication.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.app.Environment;
import ohos.bundle.IBundleManager;
import ohos.data.resultset.ResultSet;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;
import ohos.media.photokit.metadata.AVMetadataHelper;
import ohos.media.photokit.metadata.AVStorage;
import ohos.utils.net.Uri;

import java.io.*;
import java.util.ArrayList;


/**
 * description localslice
 * @author baihe
 * created 2021/2/8 14:52
 */
public class LocalAbilitySlice2 extends AbilitySlice {
    private Image image;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_local);
        image = (Image) findComponentById(ResourceTable.Id_local_image);
        if (verifySelfPermission("ohos.permission.READ_USER_STORAGE") != IBundleManager.PERMISSION_GRANTED) {
            // 应用未被授予权限
            if (canRequestPermission("ohos.permission.READ_USER_STORAGE")) {
                String[] permission = {"ohos.permission.READ_USER_STORAGE"};
                requestPermissionsFromUser(permission,0);
            } else {
                // 显示应用需要权限的理由，提示用户进入设置授权
            }
        }
    }

    @Override
    public void onActive() {
        super.onActive();
        loadPicture();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }


    public void loadPicture(){

        DataAbilityHelper dataAbilityHelper = DataAbilityHelper.creator(getContext());
        try {
            ResultSet result = dataAbilityHelper.query(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, null, null);
            if(result != null && result.getRowCount()>0 && result.goToFirstRow()){
                System.out.println("照片数量："+result.getRowCount());

                int mediaId = result.getInt(result.getColumnIndexForName(AVStorage.Images.Media.ID));
                Uri uri = Uri.appendEncodedPathToUri(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI,""+mediaId);
                System.out.println("uri："+uri.getDecodedPath());

                FileDescriptor fd = dataAbilityHelper.openFile(uri,"r");

                InputStream inputStream = new FileInputStream(fd);

                Glide.with(getContext()).load(inputStream).into(image);
            }

        }catch (DataAbilityRemoteException | FileNotFoundException e){
            e.printStackTrace();
        }

    }

}
