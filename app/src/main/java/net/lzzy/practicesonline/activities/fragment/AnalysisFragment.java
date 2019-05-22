package net.lzzy.practicesonline.activities.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragment.view.BarChartView;
import net.lzzy.practicesonline.activities.models.views.QuestionResult;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lzzy_gxy
 * @date 2019/5/16
 * Description:
 */
public class AnalysisFragment extends BaseFragment {
    public static final String ARG_QUESTION_RESULT = "argQuestionResult";
    private List<QuestionResult> results;
    private ChartFragment.OnGoToGridListener listener;


    private static String[] HORIZONTAL_AXIS=new String[]{"正确","少选","多选","错选"};


    public static AnalysisFragment newInstance(List<QuestionResult> results) {
        //静态工厂方法
        AnalysisFragment fragment = new AnalysisFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_QUESTION_RESULT, (ArrayList<? extends Parcelable>) results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            //读静态工厂方法
            results = getArguments().getParcelableArrayList(ARG_QUESTION_RESULT);
        }
    }

    @Override
    protected void populate() {
        intiView();
        getBarChartView();
    }

    private void getBarChartView() {
        int extra = 0, miss = 0, wrong = 0, right = 0;
        for (QuestionResult result : results) {
            switch (result.getType()) {
                case RIGHT_OPTIONS:
                    right++;
                    break;
                case EXTRA_OPTIONS:
                    extra++;
                    break;
                case MISS_OPTIONS:
                    miss++;
                    break;
                case WRONG_OPTIONS:
                    wrong++;
                    break;
                default:
                    break;
            }
        }
        float[] data=new float[]{right,miss,extra,wrong};
        float max=right;
        for (float f:data){
            if (f>max){
                max=f;
            }
        }
        BarChartView barChartView=findViewById(R.id.bar_chart);
        barChartView.setHorizontalAxis(HORIZONTAL_AXIS);
        barChartView.setDataList(data,(int) max*2);

    }

    private void intiView() {
        TextView tv = findViewById(R.id.fragment_analysis_tv_chart);
        tv.setOnClickListener(v -> listener.onGoToGrid());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChartFragment.OnGoToGridListener) {
            listener = (ChartFragment.OnGoToGridListener) context;
        } else {
            throw new ClassCastException(context.toString() + "必需实现OnGoToGridListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_analysis;
    }

    @Override
    public void search(String kw) {

    }

}
