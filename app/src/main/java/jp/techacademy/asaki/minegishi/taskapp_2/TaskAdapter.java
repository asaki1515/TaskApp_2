package jp.techacademy.asaki.minegishi.taskapp_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater = null;
    private List<Task> mTaskList;  // Taskクラス型のList

    // コンストラクタ
    public TaskAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // contextのシステムレベルのサービスを取得（レイアウトのためのサービス）
        // LayoutInflater：他のxmlリソースのViewを扱うための仕組み
    }

    public void setTaskList(List<Task> taskList) {
        mTaskList = taskList;
    }

    @Override
    public int getCount() {
        return mTaskList.size();
    }

    @Override
    public Object getItem(int position) {
        return mTaskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTaskList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        }

        TextView textView1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);

        textView1.setText(mTaskList.get(position).getTitle());
        // textView1にタイトルをフォーマット

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE);
        // SimpleDateFormat:様々なフォーマットで日付・時刻を表示できるクラス
        // 指定されたパターン、ロケール(場所)、日付フォーマット記号を持つ、オブジェクトを生成
        Date date = mTaskList.get(position).getDate();  // タスクデータをdataに
        textView2.setText(simpleDateFormat.format(date));
        // textView2に、引数に指定されたDate（今は日付と時刻）をSimpleDateFormatオブジェクトのパターン（"yyyy-MM-dd HH:mm"）に従い、フォーマット

        return convertView;
    }
}