package com.gamecodeschool.subhunter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends Activity {

    private final int SONG_REQUEST_CODE = 423;
    private int mSongResourceId;
    static final String SONG_RESOURCE_ID = "songToPlay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VisibilityManager.hideSystemUI(this);

        mSongResourceId = R.raw.fugees;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            VisibilityManager.hideSystemUI(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Drawable drawable = getRandomSharkImage(getResources(), getPackageName());
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(drawable);
    }

    // I want to call this from SongSelectionActivity too. Since MainActivity may be destroyed while
    // stopped, I must make it static. Moreover, I have to pass Resources and PackageName to it, cause
    // I can't call getResources()/getPackageManager() from a static context.
    static Drawable getRandomSharkImage(final Resources resources, final String packageName) {
        Random random = new Random();
        int randomInt = random.nextInt(3) + 1;

        int imageId = resources.getIdentifier("shark" + randomInt, "drawable", packageName);
        return resources.getDrawable(imageId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SONG_REQUEST_CODE && resultCode == RESULT_OK) {
            mSongResourceId = data.getIntExtra(SONG_RESOURCE_ID, R.raw.fugees);
        }
    }

    public void onClickButton(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.button_play:
                intent = new Intent(this, SubHunterActivity.class);
                intent.putExtra(SONG_RESOURCE_ID, mSongResourceId);
                startActivity(intent);
                break;
            case R.id.button_select_music:
                intent = new Intent(this, SongSelectionActivity.class);
                startActivityForResult(intent, SONG_REQUEST_CODE);
                break;
        }
    }
}
