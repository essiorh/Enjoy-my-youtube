package com.example.iliamaltsev.enjoyingamovie;

import android.app.ActionBar;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class VideoListActivity extends AppCompatActivity {
    private static String FRAGMENT_INSTANCE_NAME = "fragment";
    VideoListActivityFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //для портретного режима
        /*Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if (toolbar!=null){
            setSupportActionBar(toolbar);
        }*/

        //getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, VideoListActivityFragment.newInstance()).commit();

        // Восстанавливаем уже созданный фрагмент
        FragmentManager fm = getSupportFragmentManager();
        fragment = (VideoListActivityFragment) fm.findFragmentByTag(FRAGMENT_INSTANCE_NAME);
        // Если фрагмент не сохранен, создаем новый экземпляр
        if(fragment == null){
            fragment = new VideoListActivityFragment();
            fm.beginTransaction().add(R.id.main_fragment, fragment, FRAGMENT_INSTANCE_NAME).commit();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
