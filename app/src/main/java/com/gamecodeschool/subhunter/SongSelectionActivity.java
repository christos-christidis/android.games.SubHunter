package com.gamecodeschool.subhunter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SongSelectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_selection);

        VisibilityManager.hideSystemUI(this);

        final Resources resources = getResources();
        final String packageName = getPackageName();
        Drawable drawable = MainActivity.getRandomSharkImage(resources, packageName);

        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setImageDrawable(drawable);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            VisibilityManager.hideSystemUI(this);
        }
    }

    public void onClickButton(View view) {
        int songResourceId = 0;
        switch (view.getId()) {
            case R.id.button_fugees:
                songResourceId = R.raw.fugees;
                break;
            case R.id.button_enya:
                songResourceId = R.raw.enya;
                break;
            case R.id.button_sonar:
                songResourceId = R.raw.sonar;
                break;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SONG_RESOURCE_ID, songResourceId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
