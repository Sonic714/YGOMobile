package cn.garymb.ygomobile.settings;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.NativeInitOptions;
import cn.garymb.ygomobile.lite.BuildConfig;
import cn.garymb.ygomobile.utils.SharedPreferencesPlus;

import static cn.garymb.ygomobile.Constants.PREF_DEF_IMMERSIVE_MODE;
import static cn.garymb.ygomobile.Constants.PREF_IMMERSIVE_MODE;
import static cn.garymb.ygomobile.Constants.PREF_LOCK_SCREEN;

public class AppsSettings {
    private static AppsSettings sAppsSettings;
    private Context context;
    private SharedPreferencesPlus mSharedPreferences;
    private float mScreenHeight, mScreenWidth, mDensity;

    public static void init(Context context) {
        if (sAppsSettings == null) {
            sAppsSettings = new AppsSettings(context);
        }
    }

    public static AppsSettings get() {
        return sAppsSettings;
    }

    private AppsSettings(Context context) {
        this.context = context;
        mSharedPreferences = SharedPreferencesPlus.create(context, context.getPackageName() + ".settings");
        mSharedPreferences.setAutoSave(true);
        mDensity = context.getResources().getDisplayMetrics().density;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public SharedPreferencesPlus getSharedPreferences() {
        return mSharedPreferences;
    }

    public float getSmallerSize() {
        return mScreenHeight < mScreenWidth ? mScreenHeight : mScreenWidth;
    }

    public float getScreenWidth() {
        return Math.min(mScreenWidth, mScreenHeight);
    }

    public float getXScale() {
        return getScreenHeight() / (float) Constants.CORE_SKIN_BG_SIZE[0];
    }

    public float getYScale() {
        return getScreenWidth() / (float) Constants.CORE_SKIN_BG_SIZE[1];
    }

    public float getScreenHeight() {
        return Math.max(mScreenWidth, mScreenHeight);
    }

    /**
     * 游戏配置
     */
    public NativeInitOptions getNativeInitOptions() {
        NativeInitOptions options = new NativeInitOptions();
        options.mCacheDir = getResourcePath();
        options.mDBDir = getDataBasePath();
        options.mCoreConfigVersion = getCoreConfigVersion();
        options.mResourcePath = getResourcePath();
        options.mExternalFilePath = getResourcePath();
        options.mCardQuality = getCardQuality();
        options.mIsFontAntiAliasEnabled = isFontAntiAlias();
        options.mIsPendulumScaleEnabled = isPendulumScale();
        options.mIsSoundEffectEnabled = isSoundEffect();
        options.mOpenglVersion = getOpenglVersion();
        if (BuildConfig.DEBUG) {
            Log.i("checker", "" + options.toString());
        }
        return options;
    }

    /***
     * 音效
     */
    public boolean isSoundEffect() {
        return mSharedPreferences.getBoolean(Constants.PREF_SOUND_EFFECT, Constants.PREF_DEF_SOUND_EFFECT);
    }

    /***
     * 音效
     */
    public void setSoundEffect(boolean soundEffect) {
        mSharedPreferences.putBoolean(Constants.PREF_SOUND_EFFECT, soundEffect);
    }

    /***
     * 摇摆数字
     */
    public boolean isPendulumScale() {
        return mSharedPreferences.getBoolean(Constants.PREF_PENDULUM_SCALE, Constants.PREF_DEF_PENDULUM_SCALE);
    }

    /***
     * 摇摆数字
     */
    public void setPendulumScale(boolean pendulumScale) {
        mSharedPreferences.putBoolean(Constants.PREF_PENDULUM_SCALE, pendulumScale);
    }

    /***
     * opengl版本
     */
    public int getOpenglVersion() {
        try {
            return Integer.valueOf(mSharedPreferences.getString(Constants.PREF_OPENGL_VERSION, "" + Constants.PREF_DEF_OPENGL_VERSION));
        }catch (Exception e){
            return  Constants.PREF_DEF_OPENGL_VERSION;
        }
    }

    /***
     * opengl版本
     */
    public void setOpenglVersion(int openglVersion) {
        mSharedPreferences.putString(Constants.PREF_OPENGL_VERSION, ""+openglVersion);
    }

    /***
     * 字体抗锯齿
     */
    public boolean isFontAntiAlias() {
        return mSharedPreferences.getBoolean(Constants.PREF_FONT_ANTIALIAS, Constants.PREF_DEF_FONT_ANTIALIAS);
    }

    /***
     * 字体抗锯齿
     */
    public void setFontAntiAlias(boolean fontAntiAlias) {
        mSharedPreferences.putBoolean(Constants.PREF_FONT_ANTIALIAS, fontAntiAlias);
    }

    /***
     * 游戏版本
     */
    public String getCoreConfigVersion() {
        return mSharedPreferences.getString(Constants.PREF_GAME_VERSION, Constants.PREF_DEF_GAME_VERSION);
    }

    /***
     * 游戏版本
     */
    public void setCoreConfigVersion(String configVersion) {
        mSharedPreferences.putString(Constants.PREF_GAME_VERSION, configVersion);
    }

    /***
     * 图片质量
     */
    public int getCardQuality() {
        try {
            return Integer.valueOf(mSharedPreferences.getString(Constants.PREF_IMAGE_QUALITY, "" + Constants.PREF_DEF_IMAGE_QUALITY));
        }catch (Exception e){
            return Constants.PREF_DEF_IMAGE_QUALITY;
        }
    }

    /***
     * 图片质量
     */
    public void setCardQuality(int quality) {
        mSharedPreferences.putString(Constants.PREF_IMAGE_QUALITY, ""+quality);
    }

    /***
     * 图片文件夹
     */
    public String getCardImagePath() {
        return new File(getResourcePath(), Constants.CORE_IMAGE_PATH).getAbsolutePath();
    }

    /***
     * 当前数据库文件夹
     */
    public String getDataBasePath() {
        if (isUseExtraCards()) {
            return getResourcePath();
        } else {
            return getDataBaseDefault();
        }
    }

    public boolean isLockSreenOrientation() {
        return mSharedPreferences.getBoolean(PREF_LOCK_SCREEN, Constants.PREF_DEF_LOCK_SCREEN);
    }

    public void setLockSreenOrientation(boolean lockSreenOrientation) {
        mSharedPreferences.putBoolean(PREF_LOCK_SCREEN, lockSreenOrientation);
    }

    /***
     * 内置数据库文件夹
     */
    public String getDataBaseDefault() {
        return context.getDatabasePath("test.db").getParent();
    }

    /***
     * 使用外置数据库文件夹
     */
    public boolean isUseExtraCards() {
        return mSharedPreferences.getBoolean(Constants.PREF_USE_EXTRA_CARD_CARDS, Constants.PREF_DEF_USE_EXTRA_CARD_CARDS);
    }

    public String getCoreSkinPath() {
        return new File(getResourcePath(), Constants.CORE_SKIN_PATH).getAbsolutePath();
    }

    /***
     * 使用外置数据库文件夹
     */
    public void setUseExtraCards(boolean useExtraCards) {
        mSharedPreferences.putBoolean(Constants.PREF_USE_EXTRA_CARD_CARDS, useExtraCards);
    }

    /***
     * 字体路径
     */
    public void setFontPath(String font) {
        mSharedPreferences.putString(Constants.PREF_GAME_FONT, font);
    }

    /***
     * 字体路径
     */
    public String getFontPath() {
        return mSharedPreferences.getString(Constants.PREF_GAME_FONT, getFontDefault());
    }

    /**
     * 默认字体
     */
    private String getFontDefault() {
        return new File(getFontDirPath(), Constants.DEFAULT_FONT_NAME).getAbsolutePath();
    }

    /***
     * 字体目录
     */
    public String getFontDirPath() {
        return new File(getResourcePath(), Constants.FONT_DIRECTORY).getAbsolutePath();
    }

    /***
     * 游戏根目录
     */
    public String getResourcePath() {
        String defPath;
        try {
            defPath = new File(Environment.getExternalStorageDirectory(), Constants.PREF_DEF_GAME_DIR).getAbsolutePath();
        } catch (Exception e) {
            defPath = new File(context.getFilesDir(), Constants.PREF_DEF_GAME_DIR).getAbsolutePath();
        }
        return mSharedPreferences.getString(Constants.PREF_GAME_PATH, defPath);
    }

    /**
     * 隐藏底部导航栏
     */
    public boolean isImmerSiveMode() {
        return mSharedPreferences.getBoolean(PREF_IMMERSIVE_MODE, PREF_DEF_IMMERSIVE_MODE);
    }

    /**
     * 隐藏底部导航栏
     */
    public void setImmerSiveMode(boolean immerSiveMode) {
        mSharedPreferences.putBoolean(PREF_IMMERSIVE_MODE, immerSiveMode);
    }

    /***
     * 游戏根目录
     */
    public void setResourcePath(String path) {
        mSharedPreferences.putString(Constants.PREF_GAME_PATH, path);
    }

    /***
     * 最后卡组名
     */
    public void setLastDeck(String name) {
        mSharedPreferences.putString(Constants.PREF_LAST_YDK, name);
    }

    /***
     * 最后卡组名
     */
    public String getLastDeck() {
        return mSharedPreferences.getString(Constants.PREF_LAST_YDK, Constants.PREF_DEF_LAST_YDK);
    }
}
