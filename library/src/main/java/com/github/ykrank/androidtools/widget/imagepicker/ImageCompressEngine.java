package com.github.ykrank.androidtools.widget.imagepicker;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.CompressEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnCallbackListener;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.SdkVersionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * 自定义压缩
 * @see <a href="https://github.com/LuckSiege/PictureSelector/blob/bf9977ac48bd1fce2b8c6ced1faf00fc46e8c826/app/src/main/java/com/luck/pictureselector/MainActivity.java#L1364">demo</a>
 */
public class ImageCompressEngine implements CompressEngine {
   public static ImageCompressEngine instance = new ImageCompressEngine();

   @Override
   public void onStartCompress(Context context, ArrayList<LocalMedia> list,
                               OnCallbackListener<ArrayList<LocalMedia>> listener) {
      // 自定义压缩
      List<Uri> compress = new ArrayList<>();
      for (int i = 0; i < list.size(); i++) {
         LocalMedia media = list.get(i);
         String availablePath = media.getAvailablePath();
         Uri uri = PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(availablePath)
                 ? Uri.parse(availablePath)
                 : Uri.fromFile(new File(availablePath));
         compress.add(uri);
      }
      if (compress.size() == 0) {
         listener.onCall(list);
         return;
      }
      Luban.with(context)
              .setFocusAlpha(false)
              .load(compress)
              .ignoreBy(100)
              .filter(new CompressionPredicate() {
                 @Override
                 public boolean apply(String path) {
                    return PictureMimeType.isUrlHasImage(path) && !PictureMimeType.isHasHttp(path);

                 }
              })
              .setRenameListener(new OnRenameListener() {
                 @Override
                 public String rename(String filePath) {
                    int indexOf = filePath.lastIndexOf(".");
                    String postfix = indexOf != -1 ? filePath.substring(indexOf) : ".jpg";
                    return DateUtils.getCreateFileName("CMP_") + postfix;
                 }
              })
              .setCompressListener(new OnCompressListener() {
                 @Override
                 public void onStart() {
                 }

                 @Override
                 public void onSuccess(int index, File compressFile) {
                    LocalMedia media = list.get(index);
                    if (compressFile.exists() && !TextUtils.isEmpty(compressFile.getAbsolutePath())) {
                       media.setCompressed(true);
                       media.setCompressPath(compressFile.getAbsolutePath());
                       media.setSandboxPath(SdkVersionUtils.isQ() ? media.getCompressPath() : null);
                    }
                    if (index == list.size() - 1) {
                       listener.onCall(list);
                    }
                 }

                 @Override
                 public void onError(int index, Throwable e) {
                    if (index != -1) {
                       LocalMedia media = list.get(index);
                       media.setCompressed(false);
                       media.setCompressPath(null);
                       media.setSandboxPath(null);
                       if (index == list.size() - 1) {
                          listener.onCall(list);
                       }
                    }
                 }
              }).launch();
   }
}