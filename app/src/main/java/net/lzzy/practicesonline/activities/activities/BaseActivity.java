package net.lzzy.practicesonline.activities.activities;

import android.os.Bundle;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.lzzy.practicesonline.activities.utils.AppUtils;

/**
 * @author lzzy_gxy
 * @date 2019/4/11
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Fragment fragment;

    public BaseActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayout());
        AppUtils.addActivity(this);
        FragmentManager manager = getSupportFragmentManager();
        fragment = manager.findFragmentById(getContainerId());
        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction().add(getContainerId(), fragment).commit();
            //事务 manager.beginTransaction()
        }

    }

    protected Fragment getFragment(){
        return fragment;
    }

    /**
     * 绑定布局
     * @return id
     */
    public abstract int getLayout();

    /**
     * 绑定容器id
     * @return id
     */
    public abstract int getContainerId();

    /**创建新的Fragment对象
     * @return null
     */
    public abstract Fragment createFragment();


    //region 赋值

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.setRunning(getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.setStopped(getLocalClassName());
    }

    //endregion

}
