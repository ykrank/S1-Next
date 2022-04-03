package com.github.ykrank.androidtools.widget.imagepicker;

import android.content.Context;

import com.github.ykrank.androidtools.R;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.style.BottomNavBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.style.TitleBarStyle;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.utils.PictureFileUtils;

import androidx.core.content.ContextCompat;

public class ImagePicker {

    public static void pickImage(androidx.fragment.app.Fragment fragment, int requestCode, int maxSelect, boolean compress) {
        PictureSelector.create(fragment)
                .openGallery(SelectMimeType.ofImage())
                .setSelectorUIStyle(createWechatStyle(fragment.getContext()))
                .setSelectionMode(maxSelect == 1 ? SelectModeConfig.SINGLE : SelectModeConfig.MULTIPLE)
                .isDirectReturnSingle(true)
                .isDisplayCamera(true)
                .isSelectZoomAnim(true)
                .setMaxSelectNum(maxSelect)
                .setImageSpanCount(4)
                .isGif(true)
                .isPreviewImage(true)
                .setCompressEngine(ImageCompressEngine.instance)
                .isEmptyResultReturn(false)
                .setLanguage(LanguageConfig.CHINESE)
                .isMaxSelectEnabledMask(true)
                .isAutomaticTitleRecyclerTop(true)
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(requestCode);
    }

    /**
     * @see <a href="https://github.com/LuckSiege/PictureSelector/blob/bf9977ac48bd1fce2b8c6ced1faf00fc46e8c826/app/src/main/java/com/luck/pictureselector/MainActivity.java#L1720">demo</a>
     */
    static private PictureSelectorStyle createWechatStyle(Context context) {
        PictureSelectorStyle selectorStyle = new PictureSelectorStyle();
        // 主体风格
        SelectMainStyle numberSelectMainStyle = new SelectMainStyle();
        numberSelectMainStyle.setSelectNumberStyle(true);
        numberSelectMainStyle.setPreviewSelectNumberStyle(false);
        numberSelectMainStyle.setPreviewDisplaySelectGallery(true);
        numberSelectMainStyle.setSelectBackground(R.drawable.ps_default_num_selector);
        numberSelectMainStyle.setPreviewSelectBackground(R.drawable.ps_preview_checkbox_selector);
        numberSelectMainStyle.setSelectNormalBackgroundResources(R.drawable.ps_select_complete_normal_bg);
        numberSelectMainStyle.setSelectNormalTextColor(ContextCompat.getColor(context, R.color.ps_color_53575e));
        numberSelectMainStyle.setSelectNormalText(context.getString(R.string.ps_send));
        numberSelectMainStyle.setAdapterPreviewGalleryBackgroundResource(R.drawable.ps_preview_gallery_bg);
        numberSelectMainStyle.setAdapterPreviewGalleryItemSize(DensityUtil.dip2px(context, 52));
        numberSelectMainStyle.setPreviewSelectText(context.getString(R.string.ps_select));
        numberSelectMainStyle.setPreviewSelectTextSize(14);
        numberSelectMainStyle.setPreviewSelectTextColor(ContextCompat.getColor(context, R.color.ps_color_white));
        numberSelectMainStyle.setPreviewSelectMarginRight(DensityUtil.dip2px(context, 6));
        numberSelectMainStyle.setSelectBackgroundResources(R.drawable.ps_select_complete_bg);
        numberSelectMainStyle.setSelectText(context.getString(R.string.ps_send_num));
        numberSelectMainStyle.setSelectTextColor(ContextCompat.getColor(context, R.color.ps_color_white));
        numberSelectMainStyle.setMainListBackgroundColor(ContextCompat.getColor(context, R.color.ps_color_black));
        numberSelectMainStyle.setCompleteSelectRelativeTop(true);
        numberSelectMainStyle.setPreviewSelectRelativeBottom(true);
        numberSelectMainStyle.setAdapterItemIncludeEdge(false);

        // 头部TitleBar 风格
        TitleBarStyle numberTitleBarStyle = new TitleBarStyle();
        numberTitleBarStyle.setHideCancelButton(true);
        numberTitleBarStyle.setAlbumTitleRelativeLeft(true);
        numberTitleBarStyle.setTitleAlbumBackgroundResource(R.drawable.ps_album_bg);
        numberTitleBarStyle.setTitleDrawableRightResource(R.drawable.ps_ic_grey_arrow);
        numberTitleBarStyle.setPreviewTitleLeftBackResource(R.drawable.ps_ic_normal_back);

        // 底部NavBar 风格
        BottomNavBarStyle numberBottomNavBarStyle = new BottomNavBarStyle();
        numberBottomNavBarStyle.setBottomPreviewNarBarBackgroundColor(ContextCompat.getColor(context, R.color.ps_color_half_grey));
        numberBottomNavBarStyle.setBottomPreviewNormalText(context.getString(R.string.ps_preview));
        numberBottomNavBarStyle.setBottomPreviewNormalTextColor(ContextCompat.getColor(context, R.color.ps_color_9b));
        numberBottomNavBarStyle.setBottomPreviewNormalTextSize(16);
        numberBottomNavBarStyle.setCompleteCountTips(false);
        numberBottomNavBarStyle.setBottomPreviewSelectText(context.getString(R.string.ps_preview_num));
        numberBottomNavBarStyle.setBottomPreviewSelectTextColor(ContextCompat.getColor(context, R.color.ps_color_white));

        selectorStyle.setTitleBarStyle(numberTitleBarStyle);
        selectorStyle.setBottomBarStyle(numberBottomNavBarStyle);
        selectorStyle.setSelectMainStyle(numberSelectMainStyle);
        return selectorStyle;
    }

    public static void clearCache(Context context) {
        PictureFileUtils.deleteAllCacheDirFile(context);
    }
}