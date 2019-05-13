package net.lzzy.practicesonline.activities.fragment;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.models.Practice;
import net.lzzy.practicesonline.activities.models.PracticeFactory;
import net.lzzy.practicesonline.activities.models.Question;
import net.lzzy.practicesonline.activities.models.UserCookies;
import net.lzzy.practicesonline.activities.network.DetectWebService;
import net.lzzy.practicesonline.activities.network.PracticeService;
import net.lzzy.practicesonline.activities.network.QuestionService;
import net.lzzy.practicesonline.activities.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.practicesonline.activities.utils.DateTimeUtils;
import net.lzzy.practicesonline.activities.utils.ViewUtils;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author lzzy_gxy
 * @date 2019/4/16
 * Description:
 */
public class PracticesFragment extends BaseFragment {

    private static final int WHAT_PRACTICE_DOWN = 0;
    public static final int WHAT_EXCEPTION = 1;
    public static final int MNT_DISTANCE = 100;
    public static final int WHAT_QUESTION_DONE = 2;
    public static final int WHAT_QUESTION_EXCEPTION = 3;
    private ListView lv;
    private SwipeRefreshLayout swipe;
    private TextView tvHint;
    private TextView tvTime;
    List<Practice> practices;
    private float touchX1;
    private float touchX2;
    private boolean isDelete = false;
    private GenericAdapter<Practice> adapter;
    private PracticeFactory factory = PracticeFactory.getInstance();
    private OnQuestionSelectedListener listener;

    //region 线程基本用法

    private ThreadPoolExecutor executor = AppUtils.getExecutor();
    private DownloadHandler handler = new DownloadHandler(this);

    private static class DownloadHandler extends AbstractStaticHandler<PracticesFragment> {

        DownloadHandler(PracticesFragment context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, PracticesFragment fragment) {
            switch (msg.what) {
                case WHAT_PRACTICE_DOWN:
                    fragment.tvTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(new Date()));
                    UserCookies.getInstance().updateLastRefreshTime();
                    try {
                        List<Practice> practices = PracticeService.getPractices(msg.obj.toString());
                        for (Practice practice : practices) {
                            fragment.adapter.add(practice);
                        }
                    } catch (IllegalAccessException | JSONException | java.lang.InstantiationException e) {
                        e.printStackTrace();
                        fragment.handlerPracticeException(e.getMessage());
                    }
                    fragment.finishRefresh();
                    break;
                case WHAT_EXCEPTION:
                    fragment.handlerPracticeException(msg.obj.toString());
                    break;
                case WHAT_QUESTION_DONE:
                    UUID practicesId = fragment.factory.getPracticeId(msg.arg1);
                    fragment.saveQuestion(msg.obj.toString(), practicesId);
                    //region
                    /*try {
                        List<Question> questions = QuestionService.getQuestions(msg.obj.toString(), practicesId);
                        fragment.factory.saveQuestions(questions, practicesId);

                        for (Practice practice : fragment.practices) {
                            if (practice.getId().equals(practicesId)) {
                                practice.setDownloaded(true);
                            }
                        }
                        fragment.adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(fragment.getContext(), "下载失败请重试！",
                                Toast.LENGTH_SHORT).show();
                    }*/
                    //endregion

                    ViewUtils.dismissPragress();
                    break;
                case WHAT_QUESTION_EXCEPTION:
                    Toast.makeText(fragment.getContext(), "下载失败请重试\n" + msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    private SwipeRefreshLayout.OnRefreshListener
            refreshListener = this::downloadPractices;

    private void downloadPractices() {
        tvTime.setVisibility(View.VISIBLE);
        tvHint.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            try {
                String json = PracticeService.getPracticesFromServer();
                handler.sendMessage(handler.obtainMessage(WHAT_PRACTICE_DOWN, json));
            } catch (IOException e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION, e.getMessage()));
            }
        });
    }
    //endregion

    static class PracticeDownloader extends AsyncTask<Void, Void, String> {
        //region解决内存泄漏

        WeakReference<PracticesFragment> fragment;

        PracticeDownloader(PracticesFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }
        //endregion

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return PracticeService.getPracticesFromServer();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //解决内存泄漏
            PracticesFragment fragment = this.fragment.get();
            fragment.tvTime.setVisibility(View.VISIBLE);
            fragment.tvHint.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PracticesFragment fragment = this.fragment.get();
            fragment.tvTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(new Date()));
            UserCookies.getInstance().updateLastRefreshTime();
            try {
                List<Practice> practices = PracticeService.getPractices(s);
                for (Practice practice : practices) {
                    fragment.adapter.add(practice);
                }
                Toast.makeText(fragment.getContext(), "同步完成", Toast.LENGTH_SHORT).show();
                fragment.finishRefresh();
            } catch (IllegalAccessException | JSONException | java.lang.InstantiationException e) {
                e.printStackTrace();
                fragment.handlerPracticeException(e.getMessage());
            }
        }
    }

    private SwipeRefreshLayout.OnRefreshListener
            refreshListeners = this::downloadPracticesAsync;

    private void downloadPracticesAsync() {
        new PracticeDownloader(this).execute();
    }

    private void handlerPracticeException(String message) {
        finishRefresh();
        Snackbar.make(lv, "同步失败\n" + message, Snackbar.LENGTH_LONG)
                .setAction("重试", v -> {
                    swipe.setRefreshing(true);
                    refreshListener.onRefresh();
                }).show();
    }

    private void finishRefresh() {
        //region 随机生成数据
    /*    for (int i = 0; i < 20; i++) {
            Practice practice = new Practice();
            practice.setDownloaded(false);
            practice.setDownloadDate(new Date());
            practice.setName(UUID.randomUUID().toString());
            practice.setOutlines(i + practice.getId().toString());
            practices.add(practice);
        }
        adapter.notifyDataSetChanged();*/

        //endregion

        swipe.setRefreshing(false);
        tvTime.setVisibility(View.GONE);
        tvHint.setVisibility(View.GONE);
        NotificationManager manager = (NotificationManager) Objects.requireNonNull(getContext())
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(DetectWebService.NOTIFICATION_DETECT_ID);
        }
    }

    @Override
    protected void populate() {
        intiView();
        LoadPractice();
        intiSwipe();

    }

    public void startRefresh() {
        swipe.setRefreshing(true);
        refreshListener.onRefresh();
    }

    private void intiSwipe() {
        //设置监听器，执行异步任务

        swipe.setOnRefreshListener(refreshListener);
        swipe.setOnRefreshListener(refreshListeners);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //解决下滑冲突

                boolean isTop = view.getChildCount() == 0 || view.getChildAt(0).getTop() >= 0;
                swipe.setEnabled(isTop);
            }
        });
    }

    private void LoadPractice() {
        practices = factory.get();
        Collections.sort(practices, (o1, o2) ->
                o2.getDownloadDate().compareTo(o1.getDownloadDate()));
        adapter = new GenericAdapter<Practice>(getActivity(), R.layout.practices_item, practices) {
            @Override
            public void populate(ViewHolder holder, Practice practice) {
                holder.setTextView(R.id.item_name, practice.getName());

                TextView tv = holder.getView(R.id.item_is);
                if (practice.isDownloaded()) {
                    tv.setVisibility(View.VISIBLE);
                    tv.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                            .setMessage(practice.getOutlines())
                            .show());
                } else {
                    tv.setVisibility(View.GONE);
                }

                Button btn = holder.getView(R.id.item_btn);
                btn.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                        .setTitle("删除确认")
                        .setMessage("要删除订单吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", (dialog, which) -> {
                            adapter.remove(practice);
                            isDelete = false;
                        }).show());
                int visible = isDelete ? View.VISIBLE : View.GONE;
                btn.setVisibility(visible);
                holder.getConvertView().setOnTouchListener(new ViewUtils.AbstractTouchLisener() {
                    @Override
                    public boolean handleTouch(MotionEvent event) {
                        slideToDelete(event, practice, btn);
                        return true;
                    }
                });
            }

            @Override
            public boolean persistInsert(Practice practice) {
                return factory.add(practice);
            }

            @Override
            public boolean persistDelete(Practice practice) {
                return factory.deletePracticeAndRelated(practice);
            }
        };
        lv.setAdapter(adapter);
    }

    private void slideToDelete(MotionEvent event, Practice practice, Button btn) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                touchX2 = event.getX();
                if (touchX1 - touchX2 > MNT_DISTANCE) {
                    if (!isDelete) {
                        btn.setVisibility(View.VISIBLE);
                        isDelete = true;
                    }
                } else {
                    if (btn.isShown()) {
                        btn.setVisibility(View.GONE);
                        isDelete = false;
                    } else {
                        practiceClick(practice);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void practiceClick(Practice practice) {
        if (practice.isDownloaded() && listener != null) {
            listener.onQuestionSelected(practice.getId().toString(), practice.getApiId());
        } else {
            new AlertDialog.Builder(getContext())
                    .setMessage("下载该章节题目？")
                    /* .setPositiveButton("下载", (dialog, which) -> downloadQuestion(practice.getApiId()))*/
                    .setPositiveButton("下载", (dialog, which) -> downloadQuestionsAsync(practice))
                    .setNegativeButton("取消", null)
                    .show();
        }
    }


    private void downloadQuestionsAsync(Practice practice) {
        new QuestionDownloader(this, practice).execute();

    }

    private void downloadQuestion(int apiId) {
        ViewUtils.showProgress(getContext(), "正在下载题目");
        executor.execute(() -> {
            try {
                String json = QuestionService.getQuestionOfPracticeFromServer(apiId);
                Message msg = handler.obtainMessage(WHAT_QUESTION_DONE, json);
                msg.arg1 = apiId;
                handler.sendMessage(msg);
            } catch (IOException e) {
                handler.sendMessage(handler.obtainMessage(WHAT_QUESTION_EXCEPTION, e.getMessage()));
            }
        });

    }


    static class QuestionDownloader extends AsyncTask<Void, Void, String> {
        //下载章节内容

        Practice practice;
        WeakReference<PracticesFragment> fragment;

        QuestionDownloader(PracticesFragment fragment, Practice practice) {
            this.fragment = new WeakReference<>(fragment);
            this.practice = practice;
        }


        @Override
        protected String doInBackground(Void... voids) {

            try {
                return QuestionService.getQuestionOfPracticeFromServer(practice.getApiId());
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PracticesFragment fragment = this.fragment.get();

            UUID uuid = PracticeFactory.getInstance().getPracticeId(practice.getApiId());
            fragment.saveQuestion(s, uuid);
            ViewUtils.dismissPragress();

    /*        QuestionFactory factory = QuestionFactory.getInstance();
            try {
                List<Question> questions = QuestionService.getQuestions(s, uuid);
                for (Question question : questions) {
                    factory.insert(question);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ViewUtils.showProgress(fragment.get().getContext(), "正在下载题目");
        }
    }

    private void saveQuestion(String json, UUID practicesId) {
        try {
            List<Question> questions = QuestionService.getQuestions(json, practicesId);
            factory.saveQuestions(questions, practicesId);
            for (Practice practice : practices) {
                if (practice.getId().equals(practicesId)) {
                    practice.setDownloaded(true);
                }

            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(getContext(), "下载失败请重试\n" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void intiView() {
        lv = findViewById(R.id.fragment_practices_lv);
        TextView tvZone = findViewById(R.id.fragment_practices_item);
        lv.setEmptyView(tvZone);
        swipe = findViewById(R.id.fragment_practices_swipe);
        tvHint = findViewById(R.id.fragment_practices_hint);
        tvTime = findViewById(R.id.fragment_practices_time);
        tvTime.setText(UserCookies.getInstance().getLastRefreshTime());
        tvHint.setVisibility(View.GONE);
        tvTime.setVisibility(View.GONE);
        findViewById(R.id.fragment_practices_lv).setOnTouchListener(new ViewUtils.AbstractTouchLisener() {
            @Override
            public boolean handleTouch(MotionEvent event) {
                isDelete = false;
                adapter.notifyDataSetChanged();
                return false;
            }
        });


    }

    @Override
    public int getLayout() {
        return R.layout.fragment_practices;
    }

    @Override
    public void search(String kw) {
        practices.clear();
        if (kw.isEmpty()) {
            practices.addAll(factory.get());
        } else {
            practices.addAll(factory.search(kw));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnQuestionSelectedListener) {
            listener = (OnQuestionSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + "必需实现OnQuestionSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnQuestionSelectedListener {
        /**
         * 跳转
         *
         * @param practicesId uuid
         * @param apiId       id
         */
        void onQuestionSelected(String practicesId, int apiId);
    }
}
