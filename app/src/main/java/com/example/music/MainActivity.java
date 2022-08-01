package com.example.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_music_bottom;
    private ImageView iv_music_bottom_back;
    private ImageView iv_music_bottom_next;
    private ImageView iv_music_bottom_play;
    private RecyclerView rv;
    //數據源
    List<LocalMusicBean> mList;
    private LocalMusicAdapter adapter;
    //記錄當前正在撥放的音樂位置
    int currentPlayPosition = -1;
    //記錄暫停音樂時進度條的位置
    int currentPausePositionInSong = 0;

    MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mediaPlayer = new MediaPlayer();
        mList = new ArrayList<>();
        //創建適配器對象
        adapter = new LocalMusicAdapter(this, mList);
        rv.setAdapter(adapter);
        //設置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);

        //加載本地數據源
        loadLocalData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //設置每一項的點擊事件
        setEventListener();
    }

    private void setEventListener() {
        //設置每一項的點擊事件
        adapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                currentPlayPosition = position;
                LocalMusicBean musicBean = mList.get(position);
                playMusicInMusicBean(musicBean);
            }
        });
    }

    //根據傳入對向撥放音樂
    private void playMusicInMusicBean(LocalMusicBean musicBean) {
        //設置底部顯示的歌曲名
        tv_music_bottom.setText(musicBean.getSong());
        stopMusic();
        //重置多媒體撥放器
        mediaPlayer.reset();
        //設置新的播放路徑
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //點擊按鈕撥放音樂，或者暫停重新撥放
    //播放音樂有兩種情況  1.從暫停到撥放  2.從停止到撥放
    private void playMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            if (currentPausePositionInSong == 0){
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                //從暫停到撥放
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
            }
            iv_music_bottom_play.setImageResource(R.mipmap.pause);
        }
    }

    //暫停音樂
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            iv_music_bottom_play.setImageResource(R.mipmap.play);
        }

    }

    //停止音樂
    private void stopMusic() {
        if (mediaPlayer != null){
            currentPausePositionInSong = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
            iv_music_bottom_play.setImageResource(R.mipmap.play);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        mediaPlayer.release();
    }

    @SuppressLint("Range")
    private void loadLocalData() {
        //加載本地存儲當中的音樂文件到集合中
        //1.獲取ContentResolver對象
        ContentResolver resolver = getContentResolver();
        //2.獲取本地存儲音樂的Uri地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //3.開始查詢地址
        Cursor cursor = resolver.query(uri, null, null, null, null);
        Log.d("fan", String.valueOf(cursor));
        //4.遍歷Cursor
        int number = 0;
        while (cursor.moveToNext()) {
           String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
           String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
           String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
           number++;
           String sid = String.valueOf(number);
           String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
           long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
           SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
           String time_format = sdf.format(new Date(time));
           //將一行當中的數據封裝到對向當中
           LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time_format, path);
           mList.add(bean);
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        //初始化控件
        tv_music_bottom = findViewById(R.id.tv_music_bottom);
        iv_music_bottom_back = findViewById(R.id.iv_music_bottom_back);
        iv_music_bottom_next = findViewById(R.id.iv_music_bottom_next);
        iv_music_bottom_play = findViewById(R.id.iv_music_bottom_play);
        iv_music_bottom_back.setOnClickListener(this);
        iv_music_bottom_next.setOnClickListener(this);
        iv_music_bottom_play.setOnClickListener(this);
        rv = findViewById(R.id.rv);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_music_bottom_back:
                if (currentPlayPosition == 0) {
                    Toast.makeText(this,"已經是第一首歌曲了!",Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition - 1;
                LocalMusicBean backBean = mList.get(currentPlayPosition);
                playMusicInMusicBean(backBean);
                break;
            case R.id.iv_music_bottom_next:
                if (currentPlayPosition == mList.size() - 1) {
                    Toast.makeText(this,"已經是最後一首歌曲了!",Toast.LENGTH_SHORT).show();
                    return;
                }
                currentPlayPosition = currentPlayPosition + 1;
                LocalMusicBean nextBean = mList.get(currentPlayPosition);
                playMusicInMusicBean(nextBean);
                break;
            case R.id.iv_music_bottom_play:
                if (currentPlayPosition == -1){
                    //沒有選中要撥放的音樂
                    Toast.makeText(this,"請選擇想要撥放的音樂",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    //此時處於撥放狀態，需要暫停音樂
                    pauseMusic();
                }else{
                    //此時沒有撥放音樂，點即開始撥放音樂
                    playMusic();
                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mList = queryFromDbByTitle(newText);
                adapter.refreshData(mList);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("Range")
    public List<LocalMusicBean> queryFromDbByTitle(String songName) {
        if (TextUtils.isEmpty(songName)) {
            return queryAll();
        }

        List<LocalMusicBean> musicList = new ArrayList<>();

        //1.獲取ContentResolver對象
        ContentResolver resolver = getContentResolver();
        //2.獲取本地存儲音樂的Uri地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //3.開始查詢地址
        Cursor cursor = resolver.query(uri, null,  "title like ?", new String[]{"%" + songName + "%"}, null);
        Log.d("fan", String.valueOf(cursor));
        //4.遍歷Cursor
        int number = 0;
        while (cursor.moveToNext()) {
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            number++;
            String sid = String.valueOf(number);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time_format = sdf.format(new Date(time));
            //將一行當中的數據封裝到對向當中
            LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time_format, path);
            musicList.add(bean);
        }
        return musicList;
    }

    @SuppressLint("Range")
    public List<LocalMusicBean> queryAll() {

        List<LocalMusicBean> musicList = new ArrayList<>();

        //1.獲取ContentResolver對象
        ContentResolver resolver = getContentResolver();
        //2.獲取本地存儲音樂的Uri地址
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //3.開始查詢地址
        Cursor cursor = resolver.query(uri, null, null, null, null);
        Log.d("fan", String.valueOf(cursor));
        //4.遍歷Cursor
        int number = 0;
        while (cursor.moveToNext()) {
            String song = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            number++;
            String sid = String.valueOf(number);
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time_format = sdf.format(new Date(time));
            //將一行當中的數據封裝到對向當中
            LocalMusicBean bean = new LocalMusicBean(sid, song, singer, album, time_format, path);
            musicList.add(bean);
        }
        return musicList;
    }
}