package jp.techacademy.asaki.minegishi.taskapp_2;

import android.app.Application;

import io.realm.Realm;

// データベースを準備
// アプリ起動時にデータベース準備
public class TaskApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);  // Realmを初期化
    }
}