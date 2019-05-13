package net.lzzy.practicesonline.activities.activities;

import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragment.ResultFragment;

/**
 *
 * @author lzzy_gxy
 * @date 2019/5/13
 * Description:
 */
public class ResultActivity extends BaseActivity {
    @Override
    public int getLayout() {
        return R.layout.activity_result;
    }

    @Override
    public int getContainerId() {
        return R.id.activity_result;
    }

    @Override
    public Fragment createFragment() {
        return new ResultFragment();
    }
}
