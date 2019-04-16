package net.lzzy.practicesonline.activities.activities;

import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragment.PracticesFragment;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/16
 * Description:
 */
public class PracticesActivity extends BaseActivity {
    @Override
    public int getLayout() {
        return R.layout.activity_practices;
    }

    @Override
    public int getContainerId() {
        return R.id.fragment_practices;
    }

    @Override
    public Fragment createFragment() {
        return new PracticesFragment();
    }
}
