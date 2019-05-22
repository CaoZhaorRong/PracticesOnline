package net.lzzy.practicesonline.activities.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragment.view.BarChartView;
import net.lzzy.practicesonline.activities.models.UserCookies;
import net.lzzy.practicesonline.activities.models.views.QuestionResult;
import net.lzzy.practicesonline.activities.models.views.QuestionType;
import net.lzzy.practicesonline.activities.models.views.WrongType;
import net.lzzy.practicesonline.activities.utils.ViewUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * @author lzzy_gxy
 * @date 2019/5/13
 * Description:
 */
public class ChartFragment extends BaseFragment {
    public static final String ARG_QUESTION_RESULT = "argQuestionResult";
    private static final String COLOR_GREEN = "#629755";
    private static final String COLOR_RED = "#D81B60";
    private static final String COLOR_PRIMARY = "#008577";
    private static final String COLOR_BROWN = "#00574B";
    public static final int MIN_DISTANCE = 50;
    private List<QuestionResult> results;
    private OnGoToGridListener listener;
    private static String[] HORIZONTAL_AXIS = new String[]{WrongType.RIGHT_OPTIONS.toString(), WrongType.MISS_OPTIONS.toString(),
            WrongType.WRONG_OPTIONS.toString(), WrongType.EXTRA_OPTIONS.toString()};
    int rightCount = 0;
    protected final String[] parties = new String[]{
            "正确", "少选",
    };
    private PieChart pChart;
    private LineChart lineChart;
    private BarChart barChart;
    private Chart[] charts;
    private float touchX1;
    private float touchX2;
    private int chartIndex = 0;
    private String[] titles = {"正确比例（单位%)", "题目阅读数统计", "题目错误类型统计"};
    private View[] dots;

    public static ChartFragment newInstance(List<QuestionResult> results) {
        //静态工厂方法
        ChartFragment fragment = new ChartFragment();
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
        for (QuestionResult result : results) {
            if (result.isRight()) {
                rightCount++;
            }
        }
    }
//region 对错

    public double getRight() {
        int rCount = 0;

        for (QuestionResult result : results) {
            if (result.isRight()) {
                rCount++;
            }
        }
        return rCount * 1.0 / results.size();
    }

    public double getError() {
        int errorCount = 0;
        for (QuestionResult result : results) {
            if (!result.isRight()) {
                errorCount++;
            }
        }
        return errorCount * 1.0 / results.size();
    }
//endregion

    @Override
    protected void populate() {
        intiView();
        intiChart();
        configPieChart();
        displayPieChart();
        configBarLineChart(barChart);
        displayLineChart();
        displayBarChart();
        View dot1 = findViewById(R.id.fragment_chart_dot1);
        View dot2 = findViewById(R.id.fragment_chart_dot2);
        View dot3 = findViewById(R.id.fragment_chart_dot3);
        dots = new View[]{dot1, dot2, dot3};
        findViewById(R.id.fragment_char_container).setOnTouchListener(new ViewUtils.AbstractTouchLisener() {
            @Override
            public boolean handleTouch(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchX1 = event.getX();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    touchX2 = event.getX();
                    if (Math.abs(touchX2 - touchX1) > MIN_DISTANCE) {
                        if (touchX2 < touchX1) {
                            if (chartIndex < charts.length - 1) {
                                chartIndex++;
                            } else {
                                chartIndex = 0;
                            }
                        } else {
                            if (chartIndex > 0) {
                                chartIndex--;
                            } else {
                                chartIndex = charts.length - 1;
                            }
                        }
                    }
                    switchChart();
                }
                return true;
            }

        });
    }


    //region 柱状图与折线图

    private void displayLineChart() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            entries.add(new Entry(i + 1, UserCookies.getInstance()
                    .getReadCount(results.get(i).getQuestionId().toString())));
        }
        LineDataSet dataSet = new LineDataSet(entries, "查看访问数量");
        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();

        ValueFormatter xFormat = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return " " + (int) value;
            }
        };
        lineChart.getXAxis().setValueFormatter(xFormat);


    }

    private void displayBarChart() {
        ValueFormatter xFormat = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return WrongType.getInstance((int) value).toString();
            }
        };
        barChart.getXAxis().setValueFormatter(xFormat);
        int ok = 0, miss = 0, extra = 0, wrong = 0;
        for (QuestionResult result : results) {
            switch (result.getType()) {
                case WRONG_OPTIONS:
                    wrong++;
                    break;
                case EXTRA_OPTIONS:
                    extra++;
                    break;
                case MISS_OPTIONS:
                    miss++;
                    break;
                case RIGHT_OPTIONS:
                    ok++;
                    break;
                default:
                    break;
            }
        }
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, ok));
        entries.add(new BarEntry(1, miss));
        entries.add(new BarEntry(2, extra));
        entries.add(new BarEntry(3, wrong));
        BarDataSet dataSet = new BarDataSet(entries, "查看类型");
        dataSet.setColors(Color.parseColor(COLOR_PRIMARY), Color.parseColor(COLOR_GREEN)
                , Color.parseColor(COLOR_BROWN), Color.parseColor(COLOR_RED));
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        BarData data = new BarData(dataSets);
        data.setBarWidth(0.8f);
        barChart.setData(data);
        barChart.invalidate();
    }

    private void configBarLineChart(BarLineChartBase charts) {

        XAxis xAxis = charts.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8f);
        xAxis.setGranularity(1f);
        YAxis yAxis = charts.getAxisLeft();
        yAxis.setLabelCount(8, true);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setTextSize(8f);
        yAxis.setGranularity(1f);
        yAxis.setAxisMinimum(0);
        charts.getLegend().setEnabled(false);
        charts.getAxisRight().setEnabled(false);
        charts.setPinchZoom(false);

    }
    private void displayPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(rightCount, "正确"));
        entries.add(new PieEntry(results.size() - rightCount, "错误"));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4A92FC"));
        colors.add(Color.parseColor("#ee6e55"));
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        pChart.setData(data);
        pChart.invalidate();
        pChart.setVisibility(View.VISIBLE);

    }

    private void configPieChart() {
        pChart.setUsePercentValues(true);
        //设置空洞
        pChart.setDrawHoleEnabled(false);
        pChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        pChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        pChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);

    }
    //endregion

//region
  /*  private void displayBarLineChart() {
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
        float[] aa = new float[]{right, miss, extra, wrong};
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < aa.length; i++) {
            values.add(new BarEntry(i, aa[i]));

        }

        BarDataSet set1 = new BarDataSet(values, "");

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);

        barChart.setData(data);
        barChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.GONE);
    }

    private void configBarLineChart(BarChart barChart) {
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(HORIZONTAL_AXIS));


    }

    private void setLineChartData() {
        */

    /**
     * Entry 坐标点对象  构造函数 第一个参数为x点坐标 第二个为y点
     *//*
        ArrayList<Entry> values1 = new ArrayList<>();
        //ArrayList<Entry> values2 = new ArrayList<>();
        //获取柱形图数据（x,y）
        for (int i = 1; i < results.size(); i++) {
            int readCount = UserCookies.getInstance().
                    getReadCount(results.get(i).getQuestionId().toString());
            values1.add(new Entry(i, readCount));
        }
       *//* values2.add(new Entry(3, 110));
        values2.add(new Entry(6, 115));
        values2.add(new Entry(9, 130));
        values2.add(new Entry(12, 85));
        values2.add(new Entry(15, 90));*//*

        //LineDataSet每一个对象就是一条连接线
        LineDataSet set1;
        LineDataSet set2;

        //判断图表中原来是否有数据
        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            //获取数据1
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values1);
           *//* set2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
            set2.setValues(values2);*//*
            //刷新数据
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            //设置数据1  参数1：数据源 参数2：图例名称
            set1 = new LineDataSet(values1, "测试数据1");
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            //设置线宽
            set1.setCircleRadius(3f);
            //设置焦点圆心的大小
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            //点击后的高亮线的显示样式
            set1.setHighlightLineWidth(2f);
            //设置点击交点后显示高亮线宽
            set1.setHighlightEnabled(true);
            //是否禁用点击高亮线
            set1.setHighLightColor(Color.RED);
            //设置点击交点后显示交高亮线的颜色
            set1.setValueTextSize(9f);
            //设置显示值的文字大小
            set1.setDrawFilled(false);
            //设置禁用范围背景填充

            //格式化显示数据
            final DecimalFormat mFormat = new DecimalFormat("###,###,##0");
            set1.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return mFormat.format(value);
                }
            });
            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.btn_sector_green);
                set1.setFillDrawable(drawable);
                //设置范围背景填充
            } else {
                set1.setFillColor(Color.BLACK);
            }

            //设置数据2
            *//*set2 = new LineDataSet(values2, "测试数据2");
            set2.setColor(Color.GRAY);
            set2.setCircleColor(Color.GRAY);
            set2.setLineWidth(1f);
            set2.setCircleRadius(3f);
            set2.setValueTextSize(10f);*//*

            //保存LineDataSet集合

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
//             add the datasets
//            dataSets.add(set2);
//            创建LineData对象 属于LineChart折线图的数据集合

            LineData data = new LineData(dataSets);
            // 添加到图表中
            lineChart.setData(data);
            //绘制图表
            lineChart.invalidate();
            lineChart.setVisibility(View.VISIBLE);
        }
    }*/
//endregion

    private void switchChart() {
        for (int i = 0; i < charts.length; i++) {
            if (chartIndex == i) {
                charts[i].setVisibility(View.VISIBLE);
                dots[i].setBackgroundResource(R.drawable.dot_fill_style);
            } else {
                charts[i].setVisibility(View.GONE);
                dots[i].setBackgroundResource(R.drawable.dot_style);
            }
        }
    }

    private void getBarChartView() {

        pChart.setUsePercentValues(true);
        pChart.getDescription().setEnabled(false);
        pChart.setExtraOffsets(5, 10, 5, 5);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) getRight(), "正确"));
        entries.add(new PieEntry((float) getError(), "错误"));
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4A92FC"));
        colors.add(Color.parseColor("#ee6e55"));
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        pChart.setData(data);
        pChart.invalidate();
        //region

    }


    private void intiView() {
        TextView tv = findViewById(R.id.fragment_chart_tv_chart);
        tv.setOnClickListener(v -> listener.onGoToGrid());

    }

    private void intiChart() {
        pChart = findViewById(R.id.fragment_char_pie);
        lineChart = findViewById(R.id.fragment_char_line);
        barChart = findViewById(R.id.fragment_char_bar);
        charts = new Chart[]{pChart, lineChart, barChart};
        int i = 0;
        for (Chart chart : charts) {
            chart.setTouchEnabled(false);
            chart.setVisibility(View.GONE);
            //描述这个图表
            Description desc = new Description();
            desc.setText(titles[i++]);
            chart.setDescription(desc);
            //无数据试图文本
            chart.setNoDataText("O(∩_∩)O哈哈~");
            chart.setExtraOffsets(5, 10, 5, 25);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGoToGridListener) {
            listener = (OnGoToGridListener) context;
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
        return R.layout.fragment_chart;
    }

    @Override
    public void search(String kw) {

    }

    public interface OnGoToGridListener {
        /**
         * 点击题目跳转
         *
         * @param
         */
        void onGoToGrid();
    }

}
