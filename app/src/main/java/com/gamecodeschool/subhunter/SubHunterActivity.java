package com.gamecodeschool.subhunter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.Locale;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

// TODO: add Bluetooth/WIFI play!!!
public class SubHunterActivity extends Activity {

    // to make things clearer
    private class Dimensions extends Point {

        Dimensions() {
            super();
        }

        Dimensions(int x, int y) {
            super(x, y);
        }
    }

    private Dimensions mScreenPixels;
    private Dimensions mGridDimensions;
    private float mBlockWidth;
    private float mBlockHeight;
    private Point mUserShot;
    private Point mSubPosition;
    private int mShotsTaken;
    private int mDistanceFromSub;
    private boolean mSubHit;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean DEBUGGING = false;

    private GameResources mGameResources;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Paint mPaint = new Paint();

    private ImageView mGameImageView;
    private GifImageView mGifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VisibilityManager.hideSystemUI(this);

        setUpGridDimensions();

        mGameResources = new GameResources(this);
        mGameResources.setUpExplosionGif();

        final int songResourceId = getIntent().getIntExtra(MainActivity.SONG_RESOURCE_ID, R.raw.fugees);
        mGameResources.loadSounds(songResourceId);

        mBitmap = Bitmap.createBitmap(mScreenPixels.x, mScreenPixels.y, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        mGifImageView = new GifImageView(this);
        mGifImageView.setImageDrawable(mGameResources.getExplosionGif());

        mGameImageView = new ImageView(this);
        setContentView(mGameImageView);

        newGame();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            VisibilityManager.hideSystemUI(this);
        }
    }

    private void setUpGridDimensions() {
        mGridDimensions = new Dimensions(30, 0);

        final Display display = getWindowManager().getDefaultDisplay();
        mScreenPixels = new Dimensions();
        display.getRealSize(mScreenPixels);

        mBlockWidth = (float) mScreenPixels.x / mGridDimensions.x;
        mGridDimensions.y = (int) (mScreenPixels.y / mBlockWidth);
        mBlockHeight = (float) mScreenPixels.y / mGridDimensions.y;
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanUp();
    }

    private void cleanUp() {
        mBitmap.recycle();
        mGameResources.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_UP) {
            if (mSubHit) {
                mGameResources.stopBoomSound();
                finish();
            } else {
                takeShot(event.getX(), event.getY());
            }
        }
        return true;
    }

    private void newGame() {
        final Random random = new Random();
        mSubPosition = new Point(random.nextInt(mGridDimensions.x), random.nextInt(mGridDimensions.y));

        mUserShot = new Point(-100, -100);

        mGameResources.playSong();
        draw();
    }

    private void draw() {
        mGameImageView.setImageBitmap(mBitmap);
        mCanvas.drawColor(Color.WHITE);

        drawGrid();
        drawHUD();

        if (DEBUGGING) {
            printDebuggingText();
        }
    }

    private void drawGrid() {
        mPaint.setColor(Color.BLACK);

        for (int i = 1; i <= mGridDimensions.x; i++) {
            mCanvas.drawLine(mBlockWidth * i, 0,
                    mBlockWidth * i, mScreenPixels.y - 1, mPaint);
        }

        for (int i = 1; i <= mGridDimensions.y; i++) {
            mCanvas.drawLine(0, mBlockHeight * i,
                    mScreenPixels.x - 1, mBlockHeight * i, mPaint);
        }

        // draw hit position
        mCanvas.drawRect(mUserShot.x * mBlockWidth, mUserShot.y * mBlockHeight,
                mUserShot.x * mBlockWidth + mBlockWidth, mUserShot.y * mBlockHeight + mBlockHeight, mPaint);
    }

    private void drawHUD() {
        mPaint.setTextSize(mBlockHeight * 2);
        mPaint.setColor(Color.BLUE);
        mCanvas.drawText(String.format(Locale.getDefault(),
                "Shots taken: %d  Distance: %d", mShotsTaken, mDistanceFromSub),
                mBlockWidth, mBlockHeight * 1.75f, mPaint);
    }

    private void takeShot(float touchX, float touchY) {
        mShotsTaken++;

        mUserShot.x = (int) (touchX / mBlockWidth);
        mUserShot.y = (int) (touchY / mBlockHeight);

        mSubHit = mUserShot.x == mSubPosition.x && mUserShot.y == mSubPosition.y;

        final int horizontalGap = mUserShot.x - mSubPosition.x;
        final int verticalGap = mUserShot.y - mSubPosition.y;

        mDistanceFromSub = (int) Math.sqrt(horizontalGap * horizontalGap + verticalGap * verticalGap);

        if (mSubHit) {
            boom();
        } else {
            draw();
        }
    }

    private void boom() {
        setContentView(mGifImageView);
        mGameResources.playGif();
        mGameResources.playBoomSound();

        // The following is the old implementation of the Boom! screen.
        //
        // Hm... it seems like setImageBitmap must be called every time a new "run" of the program
        // starts. eg when draw() calls it, the mBitmap works both in draw() AND the functions that
        // draw() calls. Then, the activity's "run" stops until I touch the screen, upon which I have
        // to call setImageBitmap AGAIN otherwise it won't draw anything... wtf
//        mGameImageView.setImageBitmap(mBitmap);
//        mCanvas.drawColor(Color.RED);
//
//        mPaint.setColor(Color.WHITE);
//
//        mPaint.setTextSize(mBlockHeight * 10);
//        mCanvas.drawText("BOOM!", mBlockWidth * 4, mBlockHeight * 14, mPaint);
//
//        mPaint.setTextSize(mBlockSize * 2);
//        mCanvas.drawText("Take a shot to start again", mBlockSize * 8, mBlockSize * 18, mPaint);
    }

    private void printDebuggingText() {
        mPaint.setTextSize(mBlockHeight);

        int leftMargin = 50;

        mCanvas.drawText(String.format(Locale.getDefault(),
                "mScreenPixels = %dx%d", mScreenPixels.x, mScreenPixels.y),
                leftMargin, mBlockHeight * 3, mPaint);
        mCanvas.drawText(String.format(Locale.getDefault(),
                "mGridDimensions = %dx%d", mGridDimensions.x, mGridDimensions.y),
                leftMargin, mBlockHeight * 5, mPaint);

        // Add 1 to positions so user sees them as starting at 1 instead of 0 (more intuitive)
        if (mUserShot.x >= 0) {
            mCanvas.drawText(String.format(Locale.getDefault(),
                    "touch = (%d, %d)", mUserShot.x + 1, mUserShot.y + 1),
                    leftMargin, mBlockHeight * 7, mPaint);
        }
        mCanvas.drawText(String.format(Locale.getDefault(),
                "mSubPosition = (%d, %d)", mSubPosition.x + 1, mSubPosition.y + 1),
                leftMargin, mBlockHeight * 9, mPaint);
    }

    void drawEndScreen() {
        mCanvas.drawColor(Color.BLACK);

        mPaint.setTextSize(mBlockHeight * 2);
        String winString = "YOU WIN! Tap to restart";
        final float textWidth = mPaint.measureText(winString);
        mCanvas.drawText(winString, (float) mScreenPixels.x / 2 - textWidth / 2, (float) mScreenPixels.y / 2, mPaint);
        setContentView(mGameImageView);
    }
}
