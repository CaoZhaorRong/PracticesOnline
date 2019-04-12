package net.lzzy.practicesonline.activities.activities;

import android.os.Bundle;
import android.os.Message;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragment.FragmentSplash;
import net.lzzy.practicesonline.activities.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.activities.utils.AppUtils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Administrator
 */
public class SplashActivity extends BaseActivity implements FragmentSplash.OnSplashFinishedListener {

    int seconds=20;
    private SplashHandler handler=new SplashHandler(this);
    private static class SplashHandler extends AbstractStaticHandler<SplashActivity>{

        public SplashHandler(SplashActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, SplashActivity splashActivity) {

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppUtils.isNetworkAvailable()){
            new AlertDialog.Builder(this)
                    .setMessage("网络不可用，是否继续")
                    .setNegativeButton("退出",(dialog, which) -> AppUtils.exit())
                    .setPositiveButton("确定",(dialog, which) -> gotoMain())
                    .show();
        }else {
            ThreadPoolExecutor executor=AppUtils.getExecutor();
            executor.execute(this::couDown);
        }
    }

    private void couDown() {
        while (seconds>=0){

        }
    }


    private void gotoMain() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public int getContainerId() {
        return R.id.fragment_container;
    }

    @Override
    public Fragment createFragment() {
        return new FragmentSplash();
    }


    @Override
    public void cancelCount() {

    }
}
