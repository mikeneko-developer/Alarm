package net.mikemobile.alarm.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StrageUtil {
    private static final String TAG = StrageUtil.class.getName();
    /**
     * そのアプリのストレージ（アプリ連動）のルートフォルダの絶対パスを返却
     * @param context
     * 例）/data/user/0/com.softbankrobotics.apps.base
     * @return
     */
    public static String getAppPath(Context context) {
        String path = "";
        try {
            File root = context.getFilesDir();
            path = root.getAbsolutePath();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 端末内保存領域取得
     * @param context
     * /storage/emulated/0/
     * @return
     */
    public static String getMainPath(Context context){
        String path = "";
        try {
            File root = Environment.getExternalStorageDirectory();
            path = root.getAbsolutePath() + "/";
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String getExternalPath(Context context){
        String dirPath = getOutStoregePath();

        return dirPath;
    }

    /**
     * ファイルが存在するか否か
     * @param path チェック先の絶対パス
     * @return
     */
    public static boolean isExistsFile(String path) {
        File storageFile = new File(path);
        return storageFile.exists();
    }

    public static String getOutStoregePath() {
        List<String> mountList = new ArrayList<String>();
        String mount_sdcard = null;


        if (isExistsFile("/storage/9C33-6BBD")){
            return "/storage/9C33-6BBD";
        }



        Scanner scanner = null;
        try {
            // システム設定ファイルにアクセス
            File vold_fstab = new File("/system/etc/vold.fstab");
            scanner = new Scanner(new FileInputStream(vold_fstab));
            // 一行ずつ読み込む
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                Log.i("LogList","========================");
                Log.i("LogList","line : " + line);


                // dev_mountまたはfuse_mountで始まる行の
                if (line.startsWith("dev_mount")
                        || line.startsWith("fuse_mount")
                        || line.startsWith("# dev_mount")//Android4.2.2で新しく追加された項目
                        ) {
                    // 半角スペースではなくタブで区切られている機種もあるらしいので修正して
                    // 半角スペース区切り３つめ（path）を取得

                    int get = 2;
                    if(line.indexOf('#') != -1)get = 3;

                    String path = line.replaceAll("\t", " ").split(" ")[get];

                    Log.i("LogList","path : " + path);

                    Log.i("LogList","重複チェック : " + mountList.contains(path));
                    // 取得したpathを重複しないようにリストに登録
                    if (!mountList.contains(path)){
                        mountList.add(path);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            if (scanner != null) {
                scanner.close();
            }
            return mount_sdcard;
        } catch (Exception e) {
            if (scanner != null) {
                scanner.close();
            }
            return mount_sdcard;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        Log.i("LogList","Build.VERSION.SDK_INT : " + Build.VERSION.SDK_INT);
        Log.i("LogList","Build.VERSION_CODES.GINGERBREAD : " + Build.VERSION_CODES.GINGERBREAD);

        // Environment.isExternalStorageRemovable()はGINGERBREAD以降しか使えない
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            // getExternalStorageDirectory()が罠であれば、そのpathをリストから除外

            Log.i("LogList","Environment.isExternalStorageRemovable() : " + Environment.isExternalStorageRemovable());
            Log.i("LogList","Environment.getExternalStorageDirectory() : " + Environment.getExternalStorageDirectory().getPath());
            if (!Environment.isExternalStorageRemovable()) {// 注1
                mountList.remove(Environment.getExternalStorageDirectory().getPath());
            }
        }

        // マウントされていないpathは除外
        Log.w("LogList","===============================================================");
        for (int i = 0; i < mountList.size(); i++) {
            if (!isMounted(mountList.get(i))){
                mountList.remove(i--);
            }
        }

        Log.i("LogList","mountList.size() : " + mountList.size());

        List<String> lastMountList = new ArrayList<String>();
        String strage = Environment.getExternalStorageDirectory().getPath();//SDカード
        //最後に内部ストレージがリストに残っていれば、除外する
        for (int i = 0; i < mountList.size(); i++) {
            if(!strage.equals(mountList.get(i))){
                lastMountList.add(mountList.get(i));
            }
        }


        // 除外されずに残ったものがSDカードのマウント先
        if(lastMountList.size() > 0){
            mount_sdcard = lastMountList.get(lastMountList.size()-1);
        }


        Log.i("LogList","mount_sdcard : " + mount_sdcard);
        // マウント先をreturn（全て除外された場合はnullをreturn）
        return mount_sdcard;
    }


    public static boolean isMounted(String path){
        boolean isMounted = false;

        java.util.Map envs = System.getenv();
        java.util.Set keys = envs.keySet();
        java.util.Iterator i = keys.iterator();

        while (i.hasNext()) {
            String k = (String) i.next();
            String v = (String) envs.get(k);

            if(v.indexOf(path) != -1){
                isMounted = true;
                break;
            }
        }
        return isMounted;
    }

}

