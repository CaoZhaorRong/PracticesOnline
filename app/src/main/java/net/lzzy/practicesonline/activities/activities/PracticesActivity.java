package net.lzzy.practicesonline.activities.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragment.PracticesFragment;
import net.lzzy.practicesonline.activities.models.PracticeFactory;
import net.lzzy.practicesonline.activities.network.DetectWebService;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.practicesonline.activities.utils.ViewUtils;

/**
 * @author lzzy_gxy
 * @date 2019/4/16
 * Description:
 */
public class PracticesActivity extends BaseActivity implements PracticesFragment.OnQuestionSelectedListener {

    public static final String API_ID = "apiId";
    public static final String EXTRA_LOCAL_COUNT = "extraLocalCount";
    public ServiceConnection connection;
    private boolean refresh=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DetectWebService.DetectWebBinder binder = (DetectWebService.DetectWebBinder) service;
                binder.detect();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        int localCount = PracticeFactory.getInstance().get().size();
        Intent intent = new Intent(this, DetectWebService.class);
        intent.putExtra(EXTRA_LOCAL_COUNT, localCount);
        bindService(intent, connection, BIND_AUTO_CREATE);
        intiView();
        if (getIntent()!=null){
            refresh=getIntent().getBooleanExtra(DetectWebService.EXTRA_REFRESH,false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refresh){
            ((PracticesFragment)getFragment()).startRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("退出应用吗？")
                .setPositiveButton("退出",(dialog, which) -> AppUtils.exit())
        .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private void intiView() {
        SearchView search = findViewById(R.id.bar_searchView);
        search.setQueryHint("请输入关键字查询");
        search.setOnQueryTextListener(new ViewUtils.AbstractQueryListener() {
            @Override
            protected void handleQuery(String kw) {
                ((PracticesFragment) getFragment()).search(kw);
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        SearchView.SearchAutoComplete auto = search.findViewById(R.id.search_src_text);
        auto.setHintTextColor(Color.WHITE);
        auto.setTextColor(Color.WHITE);
        ImageView icon = search.findViewById(R.id.search_button);
        ImageView icx = search.findViewById(R.id.search_close_btn);
        ImageView icg = search.findViewById(R.id.search_go_btn);
        icon.setColorFilter(Color.WHITE);
        icx.setColorFilter(Color.WHITE);
        icg.setColorFilter(Color.WHITE);
    }

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

    @Override
    public void onQuestionSelected(String practicesId, int apiId) {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(API_ID, apiId);
        startActivity(intent);
    }
}
