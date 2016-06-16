package com.hannasun.pomodororeminder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

/**
 *
 */
public class AboutActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mToolbar = (Toolbar)findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);

       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
          }
      });
    }

    public void aboutLicense(View view) {

        Intent intent = new Intent(this, LicensesActivity.class);
        startActivity(intent);
    }
    public void aboutFeedback(View view) {
        Uri uri = Uri.parse("mailto:sunday29@qq.com");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this," No receiving apps installed!", Toast.LENGTH_SHORT).show();
        }
    }

}
