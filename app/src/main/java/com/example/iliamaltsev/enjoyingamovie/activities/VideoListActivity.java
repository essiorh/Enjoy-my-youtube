package com.example.iliamaltsev.enjoyingamovie.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.iliamaltsev.enjoyingamovie.R;
import com.example.iliamaltsev.enjoyingamovie.fragments.VideoListActivityFragment;

/**
 * This is main activity in this Application
 * @see android.support.v7.app.AppCompatActivity
 * @author ilia
 */
public class VideoListActivity extends AppCompatActivity {
    private static String FRAGMENT_INSTANCE_NAME = "fragment";
    private VideoListActivityFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        addFragmentToActivity();
    }

    /**
     * Use it when you need to add fragment to your any activity
     */
    private void addFragmentToActivity() {
        FragmentManager fm = getSupportFragmentManager();
        fragment = (VideoListActivityFragment) fm.findFragmentByTag(FRAGMENT_INSTANCE_NAME);
        if(fragment == null){
            fragment = new VideoListActivityFragment();
            fm.beginTransaction().add(R.id.main_fragment, fragment, FRAGMENT_INSTANCE_NAME).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_video_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
