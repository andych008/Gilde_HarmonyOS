package com.istone.myapplication;

import ohos.aafwk.ability.AbilityPackage;
import timber.log.Timber;

/**
 * description harmonyos
 * @author baihe
 * created 2021/2/8 14:51
 */
public class HarmonyOS extends AbilityPackage {
    @Override
    public void onInitialize() {

        super.onInitialize();
        Timber.plant(new Timber.DebugTree());
    }
}
