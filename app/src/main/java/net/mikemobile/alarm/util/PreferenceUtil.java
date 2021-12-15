package net.mikemobile.alarm.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by systena on 16/09/02.
 * メモリにデータを保存するためのクラスです。
 * グループ・キーに紐づいた情報を取得します。
 *
 * 例:データ保存
 * PreferenceUtil.setInteger(context,"group_name","key",100);
 *
 * 例:データ取り出し（一番最後の引数は保存されたデータがなかった場合に返す値となります
 * int num = PreferenceUtil.getInteger(context,"group_name","key",-1);
 */
public class PreferenceUtil {
    public static SharedPreferences getPref(Context context, String group){
        return context.getSharedPreferences(group, Context.MODE_PRIVATE);
    }

    // Integer =====================================================================================
    public static void setInteger(SharedPreferences data, String key, int value){
        SharedPreferences.Editor editor = data.edit();
        editor.putInt(key, value);
        editor.apply();

    }
    public static void setInteger(Context context, String group, String key, int value){
        SharedPreferences data = getPref(context,group);
        setInteger(data,key,value);

    }

    public static int getInteger(Context context, String group, String key){
        return getInteger(context,group,key,-1);
    }
    public static int getInteger(Context context, String group, String key, int def){
        SharedPreferences data = getPref(context,group);
        return getInteger(data,key,def);
    }
    public static int getInteger(SharedPreferences data, String key, int def){
        return data.getInt(key,def);
    }

    // String ======================================================================================
    public static void setString(SharedPreferences data, String key, String value){
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static void setString(Context context, String group, String key, String value){
        SharedPreferences data = getPref(context,group);
        setString(data,key,value);
    }
    public static String getString(Context context, String group, String key){
        return getString(context,group,key,null);
    }
    public static String getString(Context context, String group, String key, String def){
        SharedPreferences data = getPref(context,group);
        return getString(data,key,def);
    }
    public static String getString(SharedPreferences data, String key, String def){
        return data.getString(key,def);
    }

    // Long ======================================================================================
    public static void setLong(SharedPreferences data, String key, long value){
        SharedPreferences.Editor editor = data.edit();
        editor.putLong(key, value);
        editor.apply();
    }
    public static void setLong(Context context, String group, String key, long value){
        SharedPreferences data = getPref(context,group);
        setLong(data,key,value);
    }

    public static long getLong(Context context, String group, String key){
        return getLong(context,group,key,-1);
    }
    public static long getLong(Context context, String group, String key, long def){
        SharedPreferences data = getPref(context,group);
        return getLong(data,key,def);
    }
    public static long getLong(SharedPreferences data, String key, long def){
        return data.getLong(key,def);
    }

    // float ======================================================================================
    public static void setFloat(Context context, String group, String key, float value){
        SharedPreferences data = getPref(context,group);
        setFloat(data,key,value);
    }
    public static void setFloat(SharedPreferences data, String key, float value){
        SharedPreferences.Editor editor = data.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(Context context, String group, String key){
        return getFloat(context,group,key,-1);
    }
    public static float getFloat(Context context, String group, String key, float def){
        SharedPreferences data = getPref(context,group);
        return getFloat(data,key,def);
    }
    public static float getFloat(SharedPreferences data, String key, float def){
        return data.getFloat(key,def);
    }

    // boolean ======================================================================================
    public static void setBoolean(Context context, String group, String key, boolean value){
        SharedPreferences data = getPref(context,group);
        setBoolean(data,key,value);
    }
    public static void setBoolean(SharedPreferences data, String key, boolean value){
        SharedPreferences.Editor editor = data.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String group, String key){
        return getBoolean(context,group,key,false);
    }

    public static boolean getBoolean(Context context, String group, String key, boolean def){
        SharedPreferences data = getPref(context,group);
        return getBoolean(data,key,def);
    }

    public static boolean getBoolean(SharedPreferences data, String key, boolean def){
        return data.getBoolean(key,def);
    }

    // Integer[] ===================================================================================
    public static void setIntegerBox(SharedPreferences data, String key, int value[]){
        SharedPreferences.Editor editor = data.edit();
        editor.putInt(key + "_max",value.length);

        for(int i=0;i<value.length;i++){
            editor.putInt(key + "[" + i + "]",value[i]);
        }
        editor.apply();

    }
    public static void setIntegerBox(Context context, String group, String key, int value[]){
        SharedPreferences data = getPref(context,group);
        setIntegerBox(data,key,value);

    }

    public static int [] getIntegerBox(Context context, String group, String key){
        SharedPreferences data = getPref(context,group);
        return getIntegerBox(data,key,null);
    }
    public static int [] getIntegerBox(Context context, String group, String key, int[] def){
        SharedPreferences data = getPref(context,group);
        return getIntegerBox(data,key,def);
    }
    public static int[] getIntegerBox(SharedPreferences data, String key, int[] def){
        int max = data.getInt(key + "_max",-1);

        if(max <= 0){
            return def;
        }

        int values[] = new int[max];

        for(int i=0;i<max;i++){
            int val = data.getInt(key + "[" + i + "]",0);
            values[i] = val;
        }
        return values;
    }


    // ArrayList<Integer> ======================================================================================
    public static void setArrayInt(SharedPreferences data, String key, ArrayList<Integer> value){
        SharedPreferences.Editor editor = data.edit();

        String valueList = "";
        String wari = "";
        for(int i=0;i<value.size();i++){
            valueList += wari + value.get(i);

            wari = ",";
        }

        editor.putString(key, valueList);
        editor.apply();
    }
    public static void setArrayInt(Context context, String group, String key, ArrayList<Integer> value){
        SharedPreferences data = getPref(context,group);
        setArrayInt(data,key,value);
    }

    public static ArrayList<Integer> getArrayInt(Context context, String group, String key){
        return getArrayInt(context,group,key,new ArrayList<Integer>());
    }
    public static ArrayList<Integer> getArrayInt(Context context, String group, String key, ArrayList<Integer> def){
        SharedPreferences data = getPref(context,group);
        return getArrayInt(data,key,def);
    }
    public static ArrayList<Integer> getArrayInt(SharedPreferences data, String key, ArrayList<Integer> def){
        String valueList = data.getString(key,null);

        if(valueList == null || valueList.equals("")){
            return def;
        }

        ArrayList<Integer> list = new ArrayList<Integer>();
        String[] vList = valueList.split(",");

        for(int i=0;i<vList.length;i++){
            int integer = -1;
            try{
                integer = Integer.parseInt(vList[i]);
            }catch(Exception e){

            }
            list.add(integer);
        }

        return list;
    }

    // ArrayList<String> ======================================================================================
    public static void setArrayString(SharedPreferences data, String key, ArrayList<String> value){
        SharedPreferences.Editor editor = data.edit();

        String valueList = "";
        String wari = "";
        for(int i=0;i<value.size();i++){
            valueList += wari + value.get(i);

            wari = ",";
        }

        editor.putString(key, valueList);
        editor.apply();
    }
    public static void setArrayString(Context context, String group, String key, ArrayList<String> value){
        SharedPreferences data = getPref(context,group);
        setArrayString(data,key,value);
    }

    public static ArrayList<String> getArrayString(Context context, String group, String key){
        return getArrayString(context,group,key,new ArrayList<String>());
    }
    public static ArrayList<String> getArrayString(Context context, String group, String key, ArrayList<String> def){
        SharedPreferences data = getPref(context,group);
        return getArrayString(data,key,def);
    }
    public static ArrayList<String> getArrayString(SharedPreferences data, String key, ArrayList<String> def){
        String valueList = data.getString(key,null);

        if(valueList == null || valueList.equals("")){
            return def;
        }

        ArrayList<String> list = new ArrayList<String>();
        String[] vList = valueList.split(",");

        for(int i=0;i<vList.length;i++){
            list.add(vList[i]);
        }

        return list;
    }
}
