package net.lzzy.practicesonline.activities.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragment.FragmentSplash;
import net.lzzy.practicesonline.activities.utils.AppUtils;

/**
 * @author Administrator
 */
public class SplashActivity extends AppCompatActivity implements FragmentSplash.OnSplashFinishedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        AppUtils.addActivity(this);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        if (fragment==null){
            fragment=new FragmentSplash();
            manager.beginTransaction().add(R.id.fragment_container, fragment).commit();
            //事务 manager.beginTransaction()
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("取消")
                .setPositiveButton("确定",(dialog, which) -> AppUtils.exit())
        .show();
    }

    @Override
    public void canclCount() {

    }
}
