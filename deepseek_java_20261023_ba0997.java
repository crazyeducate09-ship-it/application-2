package com.example.tripleapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final int REQ = 100;
    private ListView songList;
    private SeekBar seekBar;
    private TextView nowPlaying;
    private Button btnPlay, btnPrev, btnNext, btnBack;

    private MediaPlayer player;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Uri> uris = new ArrayList<>();
    private int currentIndex = -1;
    private final Handler handler = new Handler();
    private boolean isPlaying = false;

    private final Runnable progressUpdater = new Runnable() {
        @Override public void run() {
            if (player != null && isPlaying) {
                try {
                    seekBar.setProgress(player.getCurrentPosition());
                } catch (Exception ignored) {}
            }
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        songList = findViewById(R.id.songList);
        seekBar = findViewById(R.id.seekBar);
        nowPlaying = findViewById(R.id.nowPlaying);
        btnPlay = findViewById(R.id.btnPlay);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBackMusic);

        btnBack.setOnClickListener(v -> { stopPlayer(); finish(); });

        btnPlay.setOnClickListener(v -> togglePlay());
        btnNext.setOnClickListener(v -> playAt((currentIndex + 1) % uris.size()));
        btnPrev.setOnClickListener(v -> playAt((currentIndex - 1 + uris.size()) % uris.size()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int p, boolean u) {
                if (u && player != null) player.seekTo(p);
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        songList.setOnItemClickListener((a, v, pos, id) -> playAt(pos));

        requestPermissionAndLoad();
    }

    private void requestPermissionAndLoad() {
        String perm = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_AUDIO
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{perm}, REQ);
        } else {
            loadSongs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] p, @NonNull int[] g) {
        super.onRequestPermissionsResult(requestCode, p, g);
        if (requestCode == REQ && g.length > 0 && g[0] == PackageManager.PERMISSION_GRANTED) {
            loadSongs();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
        }
    }

    private void loadSongs() {
        titles.clear(); uris.clear();
        ContentResolver cr = getContentResolver();
        Uri col = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] proj = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE};

        try (Cursor c = cr.query(col, proj, null, null, MediaStore.Audio.Media.TITLE + " ASC")) {
            if (c != null && c.moveToFirst()) {
                int idIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int tIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                do {
                    long id = c.getLong(idIdx);
                    titles.add(c.getString(tIdx));
                    uris.add(Uri.withAppendedPath(col, String.valueOf(id)));
                } while (c.moveToNext());
            }
        }

        songList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
        if (titles.isEmpty()) {
            Toast.makeText(this, "No music found on device", Toast.LENGTH_LONG).show();
        }
    }

    private void playAt(int index) {
        if (index < 0 || index >= uris.size()) return;
        stopPlayer();
        try {
            player = new MediaPlayer();
            player.setDataSource(this, uris.get(index));
            player.prepare();
            player.start();
            isPlaying = true;
            currentIndex = index;
            btnPlay.setText("⏸");
            nowPlaying.setText("Now playing: " + titles.get(index));
            seekBar.setMax(player.getDuration());
            handler.post(progressUpdater);
            player.setOnCompletionListener(mp -> playAt((currentIndex + 1) % uris.size()));
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void togglePlay() {
        if (player == null) return;
        if (isPlaying) {
            player.pause();
            btnPlay.setText("▶");
        } else {
            player.start();
            btnPlay.setText("⏸");
        }
        isPlaying = !isPlaying;
    }

    private void stopPlayer() {
        handler.removeCallbacks(progressUpdater);
        if (player != null) {
            try { player.stop(); } catch (Exception ignored) {}
            player.release();
            player = null;
        }
        isPlaying = false;
        btnPlay.setText("▶");
        seekBar.setProgress(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayer();
    }
}