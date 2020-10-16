package com.github.mugku.lockscreensample;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.github.dubu.lockscreensample.R;
import com.github.dubu.lockscreenusingservice.Lockscreen;
import com.github.dubu.lockscreenusingservice.SharedPreferencesUtil;

//import rx.functions.Action1;

public class MainActivity extends ActionBarActivity {
    private SwitchCompat mSwitchd = null;
    private static Context mContext = null;

    private static final int REQUEST_OVERLAY = 5004;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
        Lockscreen.getInstance(mContext).startLockscreenService();
        if (Build.VERSION.SDK_INT >= 23)
        {

            if (!Settings.canDrawOverlays(this))
            {
                //sdk大于23,需要用户先给悬浮窗
                Toast.makeText(mContext, "请先开启权限才可继续操作!", Toast.LENGTH_SHORT).show();
                String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
                Intent intent = new Intent(ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                this.startActivityForResult(intent, REQUEST_OVERLAY);
            }
            else
            {
                //这里加播放音乐和设置音量代码
                mp3();
                new Thread(new MyRunnable()).start();

            }
        }else{
            //sdk小于可直接播放,这里加播放音乐和设置音量代码
            mp3();
            new Thread(new MyRunnable()).start();
        }



        setContentView(R.layout.activity_main);
        SharedPreferencesUtil.init(mContext);
        mSwitchd = (SwitchCompat) this.findViewById(R.id.switch_locksetting);
        mSwitchd.setTextOn("yes");
        mSwitchd.setTextOff("no");
        boolean lockState = SharedPreferencesUtil.get(Lockscreen.ISLOCK);
        if (lockState) {

//                    mp3();
//                    new Thread(new MyRunnable()).start();

            AudioManager audioManager =
                    (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);

            mSwitchd.setChecked(true);

        } else {

            AudioManager audioManager =
                    (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mSwitchd.setChecked(false);

        }

        mSwitchd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//有时候设置悬浮窗权限返回后,不会自动播放音乐,设置点击事件
                    mp3();
                    new Thread(new MyRunnable()).start();
                    SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
                    Lockscreen.getInstance(mContext).startLockscreenService();



                } else {
                    SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, false);
                    Lockscreen.getInstance(mContext).stopLockscreenService();
                }

            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Toast.makeText(activity, "onActivityResult设置权限！", Toast.LENGTH_SHORT).show();
        if (requestCode == REQUEST_OVERLAY)		// 从应用权限设置界面返回
        {
            if(resultCode == RESULT_OK)
            {
                mp3();
                new Thread(new MyRunnable()).start();// 设置标识为可显示悬浮窗
            }

        }
    }

    private AudioManager audioMa;
    private MediaPlayer player = null;

    public void mp3(){
//播放音乐
        AudioManager audioManager =
                (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
        //音乐文件
        player = MediaPlayer.create(this, R.raw.xnftj);

        player.start();

        // 监听音频播放完的代码，实现音频的自动循环播放
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                player.start();
                player.setLooping(true);
            }
        });


    }
    public class MyRunnable implements Runnable{
        private final static String TAG = "My Runnable ===> ";

        @Override
        public void run() {

            while (true){
//无限循环设置音量MAX
                audioMa = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                audioMa.setStreamVolume(AudioManager.STREAM_MUSIC,audioMa.getStreamMaxVolume
                        (AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
