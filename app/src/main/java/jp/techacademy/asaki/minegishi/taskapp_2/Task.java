package jp.techacademy.asaki.minegishi.taskapp_2;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// データを表すモデルクラス
public class Task extends RealmObject implements Serializable {
    //implements Serializable:データを丸ごとファイルに保存したり、別のActivityに渡すことができるようにする
    private String title; // タイトル
    private String contents; // 内容
    private Date date; // 日時

    // id をプライマリーキーとして設定
    // 主キーとも呼ばれ、データーベースの一つのテーブルの中でデータを唯一的に確かめるための値
    @PrimaryKey
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}