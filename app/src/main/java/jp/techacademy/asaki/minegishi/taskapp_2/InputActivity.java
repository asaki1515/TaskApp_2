package jp.techacademy.asaki.minegishi.taskapp_2;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class InputActivity extends AppCompatActivity {

    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button mDateButton, mTimeButton;
    private EditText mTitleEdit, mContentEdit;
    private Task mTask;
    private View.OnClickListener mOnDateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(InputActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            String dateString = mYear + "/" + String.format("%02d",(mMonth + 1)) + "/" + String.format("%02d", mDay);
                            mDateButton.setText(dateString);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    };

    private View.OnClickListener mOnTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(InputActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;
                            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
                            mTimeButton.setText(timeString);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    };

    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addTask();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // ActionBarを設定する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // UI部品の設定
        mDateButton = (Button)findViewById(R.id.date_button);
        mDateButton.setOnClickListener(mOnDateClickListener);
        mTimeButton = (Button)findViewById(R.id.times_button);
        mTimeButton.setOnClickListener(mOnTimeClickListener);
        findViewById(R.id.done_button).setOnClickListener(mOnDoneClickListener);
        mTitleEdit = (EditText)findViewById(R.id.title_edit_text);
        mContentEdit = (EditText)findViewById(R.id.content_edit_text);

        // EXTRA_TASK から Task の id を取得して、 id から Task のインスタンスを取得する
        Intent intent = getIntent();
        int taskId = intent.getIntExtra(MainActivity.EXTRA_TASK, -1);
        Realm realm = Realm.getDefaultInstance();  // オブジェクト作成
        mTask = realm.where(Task.class).equalTo("id", taskId).findFirst();
        // realmに入っているTaskクラスの"id"がtaskIdと一致した最初のタスクをmTaskに代入
        // できているタスクが選ばれた場合は、そのidのタスクが選ばれ、新規作成ならidが−１でnullにより新しいタスクが作成
        realm.close();

        if (mTask == null) {
            // 新規作成の場合　システムのカレンダーから現在日時を取得
            Calendar calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        } else {
            // 更新の場合
            mTitleEdit.setText(mTask.getTitle());  // タスクのタイトルをedittextに
            mContentEdit.setText(mTask.getContents());  // タスクの内容をedittextに

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mTask.getDate());  // カレンダーの日時にタスクで保存してる日時を入力
            mYear = calendar.get(Calendar.YEAR);  // その時の日時を変数に代入
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);

            String dateString = mYear + "/" + String.format("%02d",(mMonth + 1)) + "/" + String.format("%02d", mDay);
            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
            mDateButton.setText(dateString); // 日セットボタンにタスク日が表示内容
            mTimeButton.setText(timeString); // 時セットボタンにタスク時が表示内容
        }
    }


    private void addTask() {
        Realm realm = Realm.getDefaultInstance();  // Realmオブジェクトを取得

        // Realmでデータを追加、削除など変更を行う場合はbeginTransactionメソッドを呼び出し、削除などの処理、
        // 最後にcommitTransactionメソッドを呼び出す必要がある
        realm.beginTransaction();

        if (mTask == null) {
            //mTaskがnull、つまり新規作成の場合はTaskクラスを生成し、保存されているタスクの中の最大のidの値に1を足したものを設定
            // 新規作成の場合
            mTask = new Task();

            // taskRealmResultsに全てのtaskデータを入力
            RealmResults<Task> taskRealmResults = realm.where(Task.class).findAll();

            int identifier;
            if (taskRealmResults.max("id") != null) {  // 一番大きいidがnullじゃないなら
                identifier = taskRealmResults.max("id").intValue() + 1;
                // 一番大きいidに入っている値に＋１したものをidentifierに入れる
            } else {  // 一番大きいidがnullならタスクがまだ何も入っていないのでidentifierに０を入れる
                identifier = 0;
            }
            mTask.setId(identifier);  // タスクのidにidentifierを入れる
        }

        String title = mTitleEdit.getText().toString();  // edit_textに入っている文字をタスクのタイトルに
        String content = mContentEdit.getText().toString();  // edit_textに入っている文字をタスクの内容に

        mTask.setTitle(title);
        mTask.setContents(content);
        GregorianCalendar calendar = new GregorianCalendar(mYear,mMonth,mDay,mHour,mMinute);
        // GregorianCalendar:現在のシステムで使用されるカレンダーのオブジェクト
        // システムから得られた日時変数に基づいたカレンダーのオブジェクトを作成

        // そのカレンダーの現在の日時を変数に代入
        Date date = calendar.getTime();
        // 現在の日時をタスクに代入
        mTask.setDate(date);


        realm.copyToRealmOrUpdate(mTask);  // データの保存・更新。引数で与えたオブジェクトが存在していれば更新、なければ追加を行うメソッド
                                           // 新しく作成した、もしくは更新したタスクを一気に保存、更新。
        realm.commitTransaction();

        realm.close();

        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
        resultIntent.putExtra(MainActivity.EXTRA_TASK, mTask.getId());  // タスクのidをEXTRA_TASKに紐付けて送る
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(  // PendingIntent:特定のタイミングで後から発行させるIntent
                this,
                mTask.getId(),
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT  // そのままでextraのデータだけ置き換えるという指定
        );

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), resultPendingIntent);

    }
}