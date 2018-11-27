package com.hyphenate.easeui.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.hyphenate.easeui.EaseUI;

import java.util.Set;

public class EasePreferenceManager {
    public static SharedPreferences.Editor ed;
    private SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private static final String KEY_AT_GROUPS = "AT_GROUPS"; 
    
    @SuppressLint("CommitPrefEdits")
    private EasePreferenceManager(){
        mSharedPreferences = EaseUI.getInstance().getContext().getSharedPreferences("EM_SP_AT_MESSAGE", Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        ed = editor;
    }
    private static EasePreferenceManager instance;
    
    public synchronized static EasePreferenceManager getInstance(){
        if(instance == null){
            instance = new EasePreferenceManager();
        }
        return instance;

    }
    
    
    public void setAtMeGroups(Set<String> groups) {
        editor.remove(KEY_AT_GROUPS);
        editor.putStringSet(KEY_AT_GROUPS, groups);
        editor.apply();
    }
    
    public Set<String> getAtMeGroups(){
        return mSharedPreferences.getStringSet(KEY_AT_GROUPS, null);
    }


    public static void  onClearSP(){
        ed.clear();
    }

  /*
    ClearSp clearSp;
    public void setClearSp(ClearSp clearSp){
        this.clearSp = clearSp;
    }


    public interface ClearSp{
        void onClearSP( SharedPreferences.Editor editor);
    }
    */
}
