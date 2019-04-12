package net.lzzy.practicesonline.activities.fragment;

import android.content.Context;
import android.view.View;

import net.lzzy.practicesonline.R;

import java.util.Calendar;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/10
 * Description:
 */
public class FragmentSplash extends BaseFragment {
    private int[] imgs = new int[]{R.drawable.splash1, R.drawable.splash2, R.drawable.splash3};
    private OnSplashFinishedListener listener;

    @Override
    protected void populate() {
        View wall = findViewById(R.id.fragment_splash_container);
        int pos = Calendar.getInstance().get(Calendar.SECOND) % 3;
        wall.setBackgroundResource(imgs[pos]);
        wall.setOnClickListener(v -> {
            listener.cancelCount();
        });

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_splash;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSplashFinishedListener) {
            listener = (OnSplashFinishedListener) context;
        } else {
            throw new ClassCastException(context.toString() + "必需实现OnSplashFinishedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnSplashFinishedListener {
        /**
         * 取消计时
         */
        void cancelCount();
    }
}
