package com.justlift.mihai.lift;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mihai on 16-03-31.
 */
public class prefManager {
    private static prefManager mInstance;
    private Context mContext;
    private SharedPreferences mMyPreferences;

    private prefManager(){}

    public static prefManager getInstance(){
        if (mInstance == null) mInstance = new prefManager();
        return mInstance;
    }

    public void Initalize(Context ctxt){
        mContext = ctxt;
        mMyPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void writePref(String key, String value){
        SharedPreferences.Editor e = mMyPreferences.edit();
        e.putString(key, value);
        e.commit();
    }

    public String getPref(String key, String altval){
        return mMyPreferences.getString(key, altval);
    }
}
