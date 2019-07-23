package com.gamecodeschool.subhunter;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

class GameResources {

    private GifDrawable mExplosionGif;

    // stuff for sound
    private MediaPlayer mMediaPlayer;
    private final SoundPool mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    private int mBoomSample;
    private int mStreamId;

    private final SubHunterActivity mParentActivity;

    GameResources(final SubHunterActivity activity) {
        mParentActivity = activity;
    }

    void setUpExplosionGif() {
        try {
            mExplosionGif = new GifDrawable(mParentActivity.getResources(), R.raw.explosion);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mExplosionGif.stop();    // the GifDrawable starts in a running state
        mExplosionGif.setLoopCount(1);
        mExplosionGif.addAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationCompleted(int loopNumber) {
                mParentActivity.drawEndScreen();
            }
        });
    }

    void loadSounds(final int songResourceId) {
        mMediaPlayer = MediaPlayer.create(mParentActivity, songResourceId);
        mMediaPlayer.setLooping(true);

        mBoomSample = mSoundPool.load(mParentActivity, R.raw.boom, 0);
    }

    void playSong() {
        mMediaPlayer.seekTo(0);
        mMediaPlayer.start();
    }

    void playGif() {
        mExplosionGif.seekTo(0);
        mExplosionGif.start();
    }

    void playBoomSound() {
        mMediaPlayer.stop();
        try {
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mStreamId = mSoundPool.play(mBoomSample, 1, 1, 0, 0, 1);
    }

    void stopBoomSound() {
        mSoundPool.stop(mStreamId);
    }

    Drawable getExplosionGif() {
        return mExplosionGif;
    }

    void recycle() {
        mExplosionGif.recycle();
        mMediaPlayer.release();
        mSoundPool.release();
    }
}
