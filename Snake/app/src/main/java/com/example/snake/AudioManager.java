package com.example.snake;

import android.media.SoundPool;

import java.io.IOException;

class AudioManager {
    AudioManager() {
        initialize();

        instance = this;
    }

    static AudioManager instance;

    private SoundPool soundPool;

    static void play(int _i) {
        instance.soundPool.play(_i, 1, 1, 0, 0, 1);
    }

    private void initialize() {
        soundPool = new SoundPool(10, android.media.AudioManager.STREAM_MUSIC, 0);
    }

    int getSample(String _fileName) {
        try {
            return soundPool.load(MainActivity.instance.getAssets().openFd(_fileName), 0);
        } catch(IOException e){
            e.printStackTrace();
        }

        return 0;
    }
}
