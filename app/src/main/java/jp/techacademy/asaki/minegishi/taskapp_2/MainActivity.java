package jp.techacademy.asaki.minegishi.taskapp_2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_TASK = "jp.techacademy.taro.kirameki.taskapp.TASK";

    private Realm mRealm;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        // Realmのデータベースに追加や削除など変化があった場合に呼ばれるリスナー
        @Override
        public void onChange(Object element) {
            reloadListView();
        }
    };
    private ListView mListView;
    private TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent);
                // EXTRA_TASKでidを送らない
            }
        });

        // コンストラクタ
        // Realmの設定
        mRealm = Realm.getDefaultInstance();  // オブジェクト作成→getDefaultInstanceメソッドで取得したRealmクラスのオブジェクトはcloseメソッドで終了させる必要がある
        mRealm.addChangeListener(mRealmListener);  //  mRealmデータベースに追加や削除など変化があった場合に呼ぶリスナーをセット


        // ListViewの設定
        mTaskAdapter = new TaskAdapter(MainActivity.this);  // MainActivity.thisでレイアウトサービスを取得
        mListView = (ListView) findViewById(R.id.listView1);

        // ListViewをタップしたときの処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 入力・編集する画面に遷移させる
                Task task = (Task) parent.getAdapter().getItem(position);
                // タップしたタスクをtaskに代入

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_TASK, task.getId());  // taskのidをEXTRA_TASKに紐付けて送る

                startActivity(intent);
            }
        });

        // ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // タスクを削除する

                final Task task = (Task) parent.getAdapter().getItem(position);
                // タップしたタスクをtaskに代入

                // ダイアログを表示する
                // AlertDialog.Builderクラスを使ってAlertDialogの準備をする
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage(task.getTitle() + "を削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Task> results = mRealm.where(Task.class).equalTo("id", task.getId()).findAll();
                        // idと同じだったタスク全てをresultsに入れる

                        mRealm.beginTransaction();
                        results.deleteAllFromRealm();
                        mRealm.commitTransaction();

                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
                                MainActivity.this,
                                task.getId(),
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(resultPendingIntent);

                        reloadListView();  // 新しいタスク内容を再描画
                    }
                });
                builder.setNegativeButton("CANCEL", null);

                // AlertDialogを作成して表示する
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        reloadListView();  // 新しいタスク内容を再描画
    }

    private void reloadListView() {// TaskAdapterにデータを設定、ListViewにTaskAdapterを設定、再描画する

        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        // taskRealmResultsに、findAll （全てのTaskデータを取得）して、"date" （日時）を Sort.DESCENDING （降順）で並べ替えた結果を返す
        RealmResults<Task> taskRealmResults = mRealm.where(Task.class).findAllSorted("date", Sort.DESCENDING);

        // 上記の結果を、TaskList としてセットする
        mTaskAdapter.setTaskList(mRealm.copyFromRealm(taskRealmResults));  // TaskAdapterにデータを設定

        // TaskのListView用のアダプタに渡す
        mListView.setAdapter(mTaskAdapter);  // ListViewにTaskAdapterを設定

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged();  // データが変わったことを伝えてリストを再描画
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }
}

