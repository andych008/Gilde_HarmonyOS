package com.istone.myapplication.slice;


import android.content.ContentResolver;
import android.util.Log;
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
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
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
        copyPic2Local();

        image = (Image)findComponentById(ResourceTable.Id_image);
//        image.setForeground(new ShapeElement(this, ShapeElement.OVAL));
        findComponentById(ResourceTable.Id_btn1).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                logBtn(component);

                RequestManager requestManager = Glide.with(getContext());

                RequestOptions options = new RequestOptions();
//                options.skipMemoryCache(true);
//                options.diskCacheStrategy(DiskCacheStrategy.NONE);
                RequestBuilder<PixelMap> request = requestManager.load("https://developer.harmonyos.com/resource/image/DevEco-Studio/DevEco-Studio-0.png");

                request.into(image);
//
            }
        });
        findComponentById(ResourceTable.Id_btn2).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                logBtn(component);

                DataAbilityHelper dataAbilityHelper = DataAbilityHelper.creator(getContext());

                if (img_ids.size()==0) {
                    ResultSet result = null;
                    try {
                        result = dataAbilityHelper.query(AVStorage.Images.Media.EXTERNAL_DATA_ABILITY_URI, null, null);

                        Timber.d("照片数量 = [ %s ]", result.getRowCount());
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

                    Timber.d("uri = [ %s ]", uri.getDecodedPath());
                    Glide.with(getContext()).load(uri).into(image);
                }


            }
        });

//
//        findComponentById(ResourceTable.Id_btn3_1).setClickedListener(new Component.ClickedListener() {
//            @Override
//            public void onClick(Component component) {
//
//
//            }
//        });
        findComponentById(ResourceTable.Id_btn3).setClickedListener(component -> {

            logBtn(component);

            File file = new File(getExternalCacheDir(), "B.jpg");

            Timber.d("file.getAbsolutePath() = [ %s ]", file.getAbsolutePath());
            Timber.d("file.exists() = [ %s ]", file.exists());
            Glide.with(getContext()).load(file).into(image);
        });

        findComponentById(ResourceTable.Id_btn4).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                logBtn(component);

                Glide.with(getContext()).load("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMoAAABCCAYAAAAWhkkdAAAAAXNSR0IArs4c6QAAGZNJREFUeAHtXQ18VMW1n7l3v/JFDBAIIbuBACJZikqCCmhFrRTf06etYtUW+/BVrfVHq7aWVp8F+4lIq/ZZf632WdvaWkX7+myV6qsI1icKBBFJ4FFFkg0JkgAhX/t17533n012s3f33rv3bnYTlDs/wr1zzpkzM+fOmZlz5mMJsYMtAVsCtgRsCdgSsCVgS8CWwAhJgI5QPid1NowxWn8zcdQca4K8a+X166mcjUBifOobHDU1dbSjY5OyadMFUjZ87DTWJWArinWZmU5x5nnt5aH+yA8ooZ9HIy+MJ4TQmwRRuGf3Nu8f4zCj5/z5gYKuiLKKMnITI6QsifaAQOi63Q3eRygF1g55k4CtKHkS7SfmNtfJlLzAGJmol4VAyNrGHdUr9fAc7p8fGMvCbCMj7HRdOkpfLiSOzzQ0VPbr0tiIYUkA38oOuZaAv67NJ1H6kpGS8DwVQr7pnxu40Sh/FlHWGyoJT8zY4iCT/mDEx8YNTwK2ogxPftqpWfT7aLzjtJFqqEKV+/mooYYOxGrnBjBlIxdq4VJhUKbLausDl6TC7XhuJGArSm7kmOAy++zAHAU2SQKQ6YWRUhZhy7XIGFFu14LrwZjC1qxezexvqiegYcBtoQ5DeFpJFYndjbmQVbkuTeU1OMrUpcKN42zO+hfa/tmYxsZmIwGrHzSbPE6aNNx9y+0F6xVmc2NpkxIKijI1KWr+VZazyN88+5OV0laUHH75BQtaPfDRnmKVJewQ59VXr1d9CyYTTbslE29G2ZmZaGy8dQmoPo715HaKZAlEiiKu5LiV946OpSpXPdZeEusuVvhQSiqt0Nu05iRgK4o5OZmikiVB1dhNJRokKi8nqgVDhdKwlfQJWkazVtYED/slTQK2oqSJJHtAUbQ4q60pPEe/X60oosz6sikJ3MQ92aSz0xhLwFYUY/lYwoZCzSFCstlKQtmqVWpFYU6W3So7pf+wVGib2JQEbEUxJSZzRA0N9VHYCE3mqIeokGZb6l6taLF4YIjC/Bu2fL1hntqmNCsBW1HMSso83RPmSQcoYdg8nZpm7ytVR2DQb0uFG8YpdiW7nb83pLGRWUnAkVWqkzgR38nbo5AZVBIjVRMmfbBhg9roLiDFjwVpz9fh8q0wJybKRIfwjCYthQIxMk8TpwHEpO83ja9XtqSizjqrdVxIUSZFRRpuerPqvdTRK5XejqdLIGsvTTqrjzekti5wNhYTVxFKLsbi4EAHQ0k3bJIn3SUld+/cVNYVl4B/XmCJIrMXzKzQY9R4uGmHb0U8bfLTv+hwsdIdfBewKclwzXdKPhjjKD7jrbfGoUwDYXAH8/1YBz0/XhZ88C5C6eMFxPEj7DbujNPaT2MJ2IpiLJ8Y1j+3+Ta4a9dBUUQtcgix2eVyLdr55qQDcby/vvUqRZGfRNwdh6U9sT3+FBe9YssWbzANNwiorT94JlPkl9DQy/VokP8eQXR8eve2yYE4zaz61mWEKb/SLTOlrYSK/9K0ffLb8TT2U18Cto2iL5sYxl/ffD22wz+g1+A4ERZAqiPRyIa6OuaMs2vcXvWsSKkf05zfY9TojcP5E8b7IUKEbwo13suMlITTxhqyxzEPaX6HqGptBbxbAL+r1C3UJSvJnLmt5xgpCeeLUbGKMOnF2fMOenncDsYSsEcUA/nU1bUV9hOpGa1qvAFZAiUI9I7G7b4HEoDBF76P64ILDrh7etxCTc0kCUeBI6k0ZuJ8Z/BLL7W6IxGR6vHhefnrA1vwPNsMTyjx45j6/ZsZ2pOZxlYUg68Pu+SrjCkPGZCoUBBmV7FDnL51a9URFWIEI7EzLEThUz5TAWWOOKG/72zxHjSV4CQlsqdeOh9+4FwH+7oOWhPMN0T2KOwKTeSIAZXlVrJCmV1SVPmalTQnI62tKDpfff2GQ6dh+uLTQeuCsT0eHqbRCTEbidL5VnNXGF1iNc3JRm8rit4Xl6P6lznopQEcPfQnDdB5RUXEg/VQ7mx2HdcuWjTo8s5rCT+6zO0FR51vB5sYBjyavcWAo1u6t65oseJrJaQ7eJ0i0DmUsanwiY1Dvi3Ieb9I6MbdDb6XtdJpwSRZsZR3ggfc3j097fwcjb2ukhCK+sVWFLU8EjHsmXJgdd1yoIyaupQudg1RhN2t9AS5x6mUKNj3G8st9n/MYyUTtrJ2bnMTEem6pm2+X2UqjMgESSbZbWAOCQ67LRgI2J566QmH0Q/1UMZwllFR+CKiElG2Y5p0B7Sj1Igf1KaWyezx2rrmZ2KjjwExEzPnrZ2cKrVTyju0cTaUS8DuRXTaAXWKb5MIlhqth3ajJLPntl4qM/kZKEiBEV0qDqPbUkzR/FjbOV936wmlhnmn8ozH4SJ+N9trXuM8TsQn8/tdre/vvT61bEwhnb6I/KdUuFGcBtzCnUYEmXDo8fqxQtyFD3/USZ17K0KhDzKlGS18wO2ezqiseVS2QHD9o7y/X9XQZs1t5nWZYqW8GKJ1b3+cU39wZpRJWyGrMVZ4JtNiJX6jv8a3WK9h19a1tGCksrTaTqmwpqnB++3kfD4O782lpWUk2HM0vS60oToi16fD9SEOTI3X6qPNYfBhYoRhFiHNbuEwYXQzlOc/vaHoy3gOIM2xyiuVQqSvwhbQ3IAYZKFbkPnPVQWg5EE06gdVsEwRwfG8FsnChR0lx0L9fxqOknC+EPWFjfsDa/Cq2cHBmfA88rhVqwxaMCheVBCER+K4Zo+nhipRXLk0eoFR2lkdllbGSxBwi5eg3mkjQxyv+wz1urQbH6tpcYlP6aZLQSgi3Z77qRcjE/A5l0J5lgY84r5Wj+PLVSHp1ZS8PxLRU1zCo8cj7EbUxW+uwHTX0ksrtzRuT6fuCvb/DB/ttHSMdQjK841Z9YFX92z3vpiaWqDCL3H75JeN9qYlp4HzYW3yPjGRyhPgRLghmWbk39kB5JlQFIWwmYhfY7kc2lrC2ZTBdWKan6AQT16NefQCpyqK8krA4/iR5UqeAAn4hkXB6ViKXjejYc83PjqpY/nq1TTNsKmdF/gCgMtyWiWFPeGfd7gilWfjtqqduOH+u6lwrTjq9WLtNO8qLZwNU0sgr4rCs4JSUyjLtz6qyrL7zco9DtHNV7t1j9hievk+tgR/aldD5Q61eAmZXdcyjSlKYmqTis8+zsqZHPwtRhfY4urQ2OD9LhGElShTUI0ZiPHpFhHogxNKfJfr2Tpa6U5mWO6nXjrSHFSWv3pD0mYdkhMWvGtrxQco3MLZ9c0Xywq9BIe3/LC8+MjRDiX5G6mperZRY0cwP1nYIyvcLinJR+XQCX0KO4XXgXfanjRMy9ZiC/1TOBNzNUb2BVCOItCF0HW9JQrk6Xe3+fbvyUeh8sATPUG6oQu541rO5/KQXRpL2UEP0GaXAHlrBxQwCKThusBgJYq1OaihmJ5shVvubDV05GJwNNwGmV+llSO+xDqrLkMtPnFYXd3R0n7W8wridXFY/Dml2kEuOl/tHf7dM70kFNL9FPGk2k9Kv7enwfcdbaQ1aFtJyXg52m9p7xdGtVVQxumpOaFtSPjm12DMi6bijOIw5vurQ9Lf4jSx78ZwJigpwFO3zheWEg6NVrf7VEaiqnIzIuwFTcadDW2VlYXykUNfSmIPfxTt9YXkx+MwY0Wh9HJfWNb04sQZ8CfPiHR1nCZJygoYSf+ajEt9p9Q53RcOv58K/zjFuYfraLD/r6jTAq16Lbm4kPxkjfqIy7mfaiVHj/FBKruAEeOupobqEbcFB12w3IZLHFqL1wCj7ctoP5+Ox7N96imK4CpYI0eCczlf1P8CTHHVLm5K0VHR+zLmK7AyIrOnk+mg5EcYFa7lMNFBO3Iy9apsa+sHPz4/X97sdnTgdF1C03lGyYEKEh9RPraKcvr8wORjweALqGNWmyqTZWXlHT36D/11gYlLL6u6Q8uhYIWXFVoa7r0C42CaknAeaLzPWuFllVaJBOvR1niHxN3m6YGxi4DBX4Ygp+PBbhx4x0YjRaJ/yomiJGfjFJwPR+WwrqLgNzzSFvxaPI7vYX3Dm8zH9DvFvBt+d0FhO5mnaKO3u1tjgWmAW0uBeC1WZS/U4i1Q+uRw7Sf/Wa2nRyPKXzCqVmnlkW+YwpSvPfPnQDVW7z8/3J+pC7gdSxTKJmYss0Ju1qPBlKy02SN+UQ+vBXdS16uVwWCLFm40YTlXFFJW1kk6D+nWCUNaOA2pKJdDgz+RBjcD4D0JuhPeKdBQb2+LS1jrGF/x48FRTsUBSjIftKq5aJwAjbsB75vjcatP/7zmy5ikPAU+3GgetYDGeUWQRl8/c2HLpW//r68t24KgHneh8zovU3oufr2Az3K/dlevl4IbxJHPAPvxVxSpq8NwcQ7byfl8Ni8BH407Fb4rHTl0c7vLtXhSJNKUl4xSmPrrmr+lyPQH0Ni8u9tTstaMooGeGQqSbbhk4spdO6re1CT6GABxR8FxyH1brCqUjUW9pyVXi9sZ6D73J8O03tEp8KnjGSm4KBwROwdg9L2cjihs6VKx5fk/rknJUBUVBWEfyXIruIqRQQQCmxyl0ksd48fXlnd29hiQDgs1sC0l+BsFvTiUZFi8cp+YVUpE3uyf23JH4w7fz3LPf/Q5VgWjvBM4i5cE0+prYJA/lVwqRugG7OlalgzTev+wqGhiKBpUTYNgXx2GIyLGm6cxVBQM47XNBQ7dOX88Uyrjhz0pm9by/HM3ob3MjMNTn8j8vaq+6KCWpmJzG4eyVPX3dvH1hdW55TzAjf/y79FQ8C+YWmhOGQsLKVl4jofMPcNNxo8TiceD/m0wTCgX46+azy9cU0yuumLI4964J0Luvjf9M1y0qICs+PLQLv3j3Qr54k2HEzyhutjrxB6eVd8ya/ZU79fsxcWEaFQvISU8QwXQiBgqCuh/ROTMLstYX2qiQ0VT0b7RhAp3UoGdolE+fRAjhdindDpuSfkSsta2C5hyDRis1meSHYYrCSPS36EkvlQO6AzI8mUl5KYbSsmYkuxmYuPGiuTUGa4E6+O4w1UrlI4RVHRHjnJLTSMo7FZspCzDhRnLhusRQ41Wwu3bqJGLJZDMyBcxCi+1lMgiMRYkP9viFhZlTCYrFZmabyZFyZiHWQLM9572huWHteixKPSSFtwM7GCh89eSJPMheKhlDSbEIuIMtmiRg27aZLhoaiafOA1fbe+VpFfQU6cpiRPSfOj+crLokwVx8rw+MWqaDpgdXLf+z4HjSPAV04k0CBVReKM6KL2ugbIEgqdzITygltJYJQb3QsxwsrlDIC2r7Lq8NDaGgG78DtUK7133XGdIlSVycn/0bfRwW7SSQ1BC644dY7Rw2cJ6JOVRKMl0rfTfvrNsxJREK/9MMNhSt/CrXjPR2XguAfXvzOR9RMFMxImtBFMP34+ulmTehZvVR2LkoF46XNgwZBzoEZmEY0HvOqxVfFaLfNZMJ/nclUN2RZzm/Q+ipOHtMDl2bGhaVDPVSS6+MCcdXTwb009MVX+BUfHV0bykz3RhR4kQ0+c2hyjAvh36ZnlXFPTqBVjhvANeheWBItf53r7Iu5nq31pSMo5JoWkCMXeFjqzgAuv8juKxIuNchHqLRFJFrr26BOueap189PHj5KFHjqctJfAtLKOnKGxsj4LzKoTAnW094CfzKto8nmrrKdUpJBYd8kKoUVnFij1jNvRE+2ZnlTgpkRil0cnz5+9Pna7nXVGSylCmSNLLEPI5lfgNtyR47JVvasMJROzLYQvlcB8ffWLbc1PpRiteO6/1IibLuh9iwdkeVdHeeTdMHvwZNwlOwMDYrbjH675Nm8zdGJNcAxzqWi8rkWTQqL4HqqoKnF1dJZFoFD8b4OoYdmGwovLh9u1jDxUPzA6cLldo3NGj3YaKgg4S3ijxtUyZY3pTjHn7NBiX18FoXKRLjx/XiZLovwN/YzINVtPvkll0FWBpBnky3Wi+U1lerDdo8YGkYqLa5fu3jXz72wkaGJnU0XeQK/2IuOrzKQWls31ZWGG/yFcekUjwD+B9raGiwKDZ5w1Gt5osxEbQPQZ33O1QmJ/opmHsC+3FxXdN6u2NaT/OQ/8TjMyspgG6eeQBgcWrOXqLivxGLKwSq3I91qXt0lURjWaEyajPR19RRkqEOfd6+cLKA3AFc3etXvBE5eCFHImV8xKMRHnrDfQKkA0c5TxNLx1GfdLXp1aM02aesINjrBrwcejWR6+eJzM8w4iSpWgofQMW7Dl6qXGh21SOC/YduxSjj+ZOW/TPQZwHeBgr/o2Y1KhbYQpjrInegt5+fgo4p9FYeQw48tXzs+o9CYorLy8iTzzZTdrahzwnCaTFl+KinPdnOKRJghaLMUhOf0sF/FrXcANj52Kaft5w2Rimp/QFdNqZf06cMX7ZxOfAa+gDpjDOj6IwNjYlH1UU3qGY1yS2m1eFGYqA5kosRG4Ygui/4eqZJbAf8qooaFlHjTxrGzcHVYpSUCCQp56oIGvWHSNbtoVIl4WpWCSqtoZmTHOST/hd5N3GHBrRuIdNX6IGGJE+6svBgmPAJXwHtRy2ojgFYbvE5PfR4ao2RPIawHZegscRJxHv09ogCxOgPCqF8Bs4bDnotJSkG9tcY20w54oSGDNmLAv3LjZqVKhCrDfDyvkEdZPg1QOWkk6s4ptSkoEUI/E/hU2lVdqBvJ/9r15y8w1jcMpgyKgvHy+SHw+eZJSkobQpXuS0wh9oVm8kcDjw88C/qSBRKNAQF9hFGBayDeA0fA9RtplbTIdqpp1Xhxxioqjsj+7AjZC1Le/t+Qra0z0AJjppvONjsOujRFrW4hb/goZ1vy8k/b3F7YZSSbdHI/03gEZrG0UUHfUvHK7Ceyt7ejp5cXM6prcWOc9QQn1/hnZXGstCiQ/dmvMSpIeT7kQLlDsrdEM/bhdYec8Rouhsy+CNPf4nisYtfOeuMIlEYu1AlZ/TSYkr6Y/zyy5QRXB6NmeXdhRSaWxDQWcTjpeENjZGqsPKgyWFY6YRKqwFPBTH8SckicGFXYajwq/B2bSPsOg+xG/VVBJKnxOo04+dwyviSsJ5GI8o0NBmt3gTJ8wQsMzMpshReWoGuhiaOhz/Q8KYRlCcTYFWaIRSfhrRF5RV26Y16EYMRIn437h45adGGb6+JUS++o1Oct/3xpGiYdgV7Ydk8shjx8ltt1rbJ2pUNjWOvbn7rYkfqmEjG1MIhXta89unF0QQCtEDqeGKklCUOKKsq6sL7ysPFhQ8LMuRe/loghyGhngg0dxmxOmTnxhBNuPGzG8NbN1P778NFQXDM84kJ7PLyfuO+Oq8wNgbKNLtmlxl9iSGy8/BGDuAIhiWAg24zphCMwdLwMaGyhbc6/sWeqKzjRJyW2XJFW3YQTyGLL6okFRNNhSxLqtHH+8mLQEJylJKfN4cD7CUPKObcQYEjlTc2uJ2XJ6BLAOa1aDF8lOt5gKOFKcRcptRJ1SuXHkwsOb7v0KbmIx8FuuQJcAYnd7HtO0/2LiJ75DW+GQngY69GN7CoiYdfgwTBZk4xHN8/dHtnNvh8vLi0PEjXBHGDZ+7NgfRXTS+qqcHJ93wY/Bu8acQ3AotSgjrFri2f66Fi8NwT9YCWZZfh96iKubC+PECKcd5FD7CxG2TkmJKJlcOKBC/eeVwh0zefidMJLVpksigqIhCWRwkk/eL2zA7d2Uw+CndN2WCd86GDTStR05kiBd0Uq+hUxi2sZ3M08w79lgtnByU3kimRVleRFkuSYZBmlcyyqbAzZ2yyZO58XFmok0VJdObfMepRrIPa2bdKnqBvpZdd6fiYjoSQeU+XzWoJDzVhI6OXtxP/G1st37UNJdRJMQdvW/Mqmv+JXqqG80Wo7MTvzGAv+GEvj5G9uy1dDWWTnY4wSMIX8mkJDqJ8w5GI1UKPCWNJHgsJS+WNl2iTmG/IimL0GmleTuNRip0VrBRyHTQaNnn2MBL/KkTGIw2H2oRpxQyF1G6hTrEc6si8rOp3Lwh+TEU/usQUl8q7kSMT5ngW4Hyvngils24TBSeTnpz07aqV4zpRhFLhQfGHjum2iDHj+nCrpieXCq0FZmVTfy/ZJjRO+glDOfPEVE4D7OGmUxwzcBl5g8hjSovIx75UxRKmmEg/Rxn5C/EueUFmG5t0ysICv8TKrpnojKPoVK7QafyWuilGw04741pje8zKOuvRyP/rPKkpIeK9PrGHd7Hskqf/0QhjHTf93qn3J2aVUQOXZYKw9Rop7e1NYi2gn/aAQgF9u1WdGp3Fjg8vuqwfFX8wFl1KLTfG5Zuc46vqBQFej2+Je/4DNscbfGIN2hnZQnKT3304Xr8Hmx6OuIpKt0znEsd2OrVQufatRMjzNw2e6OScsOOrl4dm/uwujpne3u7pmU86dRTI6lbq434cpx/XmCJIrP7MFTzfVOWAj7ie/jMO2DuNOGb7sE3b6cOoVuQhTATpWKFCaUwnGuwtX8W6HDXMZuHaUGZlUzQUUnojZ91OoU7d71V1WolLU6Onokpcb7cbrGioD7MoQiHK2bMeI+7eLXK1+ZyzVIEpSIZJyjCocpIZA9+GGoGpbJqZwfniYYfRBtsstIGeds4uHcX/xnAslQFVBxCh65GJhfMfjeWAK4rOhdfh3txzkWDrkGjL4KhyX2MmE7STsxxD+K9GWe491KBNBYK4g6rB6dgzNLac1qnCzLuCVD477Uo09EgqpHnRPAeizw8yBP/4A2ipBH5bnQ4hKetKohxTU9e7P8DizIYlnSVWyAAAAAASUVORK5CYII=").into(image);

            }
        });

        findComponentById(ResourceTable.Id_btn5).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                logBtn(component);

                Uri uri = Uri.getUriFromFile(new File(getExternalCacheDir(), "B.jpg"));

                Timber.d("uri = [ %s ]", uri.getDecodedPath());
                Glide.with(getContext()).load(uri).into(image);
            }
        });

        findComponentById(ResourceTable.Id_btn6).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                logBtn(component);

                //<scheme>://<authority><absolute path>?<query>#<fragment>
                Uri uri = new Uri.Builder()
                        .scheme(ContentResolver.SCHEME_FILE)
                        .decodedAuthority("")
                        .decodedPath("resources/rawfile/B.jpg").build();
                Timber.d("uri = [ %s ]", uri);
                Timber.d("uri.getDecodedPath() = [ %s ]", uri.getDecodedPath());

                Glide.with(getContext()).load(uri).into(image);


            }
        });

        findComponentById(ResourceTable.Id_btn7).setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                logBtn(component);

                Glide.with(getContext()).load("resources/rawfile/B.jpg").into(image);
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

    private void logBtn(Component component) {
        if (component instanceof Text) {
            Text btn = (Text)component;
            Timber.i(btn.getText()+"...");
            Log.d("Glide", "logBtn() called with: component = "+btn.getText());
        }
    }

    private void copyPic2Local() {
        new Thread(() -> {
            RawFileEntry rawFileEntry = getResourceManager().getRawFileEntry("resources/rawfile/B.jpg");

            try {
                Resource resource = rawFileEntry.openRawFile();
//                        {
//                            ImageSource imageSource = ImageSource.create(resource, null);
//                            PixelMap pixelMap = imageSource.createPixelmap(null);
//                            getUITaskDispatcher().asyncDispatch(() -> {
//                                image.setPixelMap(pixelMap);
//                            });
//                        }


                File file = new File(getExternalCacheDir(), "B.jpg");

                //   /storage/emulated/0/Android/data/com.istone.myapplication/cache/B.jpg
                Timber.d("生成文件 = [ %s ]", file.getAbsolutePath());

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
}
