package net.lzzy.practicesonline.activities.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.models.FavoriteFactory;
import net.lzzy.practicesonline.activities.models.Option;
import net.lzzy.practicesonline.activities.models.Question;
import net.lzzy.practicesonline.activities.models.QuestionFactory;
import net.lzzy.practicesonline.activities.models.UserCookies;
import net.lzzy.practicesonline.activities.models.views.QuestionType;

import java.util.List;

/**
 * @author lzzy_gxy
 * @date 2019/4/30
 * Description:
 */
public class QuestionFragment extends BaseFragment {
    public static final String ARG_QUESTION_ID = "argQuestionId";
    public static final String ARG_POS = "argPos";
    public static final String ARG_IS_COMMITTED = "argIsCommitted";
    public static final String SP_SETTING = "spSetting";
    public static final String KEY = "key";
    private Question question;
    private int pos;
    private boolean isCommitted;
    private TextView tvSelect;
    private ImageView imageView;
    private TextView tvContent;
    private RadioGroup rgOptions;
    private boolean isMulti;
    private CompoundButton btn;

    public static QuestionFragment newInstance(String questionId, int pos, boolean isCommitted) {
        //静态工厂方法
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION_ID, questionId);
        args.putInt(ARG_POS, pos);
        args.putBoolean(ARG_IS_COMMITTED, isCommitted);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //读静态工厂方法
            pos = getArguments().getInt(ARG_POS);
            isCommitted = getArguments().getBoolean(ARG_IS_COMMITTED);
            question = QuestionFactory.getInstance().getById(getArguments().getString(ARG_QUESTION_ID));

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void populate() {
        initViews();
        displayQuestion();
        generateOptions();

    }

    private void initViews() {
        tvSelect = findViewById(R.id.fragment_question_tv_select);
        imageView = findViewById(R.id.fragment_question_iv_practice);
        tvContent = findViewById(R.id.fragment_question_tv_content);
        rgOptions = findViewById(R.id.fragment_question_option_container);
        rgOptions.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setMessage(question.getAnalysis())
                .show());
    }

    private void displayQuestion() {
        isMulti = question.getType() == QuestionType.MULTI_CHOICE;
        int number = pos + 1;
        String type = number + "." + question.getType().toString();
        tvSelect.setText(type);
        tvContent.setText(question.getContent());

        int starId = FavoriteFactory.getInstance().isQuestionStarred(question.getId().toString()) ?
                android.R.drawable.star_on : android.R.drawable.star_off;

        imageView.setImageResource(starId);

        //region 设置收藏

        imageView.setOnClickListener(v -> {
            FavoriteFactory factory = FavoriteFactory.getInstance();
            if (factory.isQuestionStarred(question.getId().toString())) {
                factory.cancelStarrted(question.getId());
                imageView.setBackgroundResource(android.R.drawable.star_off);
                Toast.makeText(getContext(), "取消收藏", Toast.LENGTH_SHORT).show();
            } else {
                factory.starQuestion(question.getId());
                imageView.setBackgroundResource(android.R.drawable.star_on);
            }
        });
    }

    private void generateOptions() {
        //region 设置选项
        List<Option> options = question.getOptions();
        for (Option option : options) {
            btn = isMulti ? new CheckBox(getContext()) : new RadioButton(getContext());
            String content = option.getLabel() + "." + option.getContent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.setButtonTintList(ColorStateList.valueOf(Color.GRAY));
            }
            btn.setText(content);
            btn.setEnabled(!isCommitted);
            //添加点击监听，选中了就要记录选项到文件SharedPreferences,取消就去掉
            btn.setOnCheckedChangeListener((buttonView, isChecked) ->{
              UserCookies.getInstance().changeOptionState(option,isChecked,isMulti);

            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                btn.setTextAppearance(R.style.Widget_AppCompat_EditText);
            }
            rgOptions.addView(btn);
            /*勾选，到文件中找是否存在该选项的id，存在勾选
            checkBox btn.setChecked(true)
            radioButton rgOptions.check(btn.getId());*/

            boolean shouldCheck=UserCookies.getInstance().isOptionSelected(option);
            if (isMulti){
                btn.setChecked(shouldCheck);
            }else if (shouldCheck){
                rgOptions.check(btn.getId());
            }
            if (isCommitted && option.isAnswer()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btn.setTextColor(getResources().getColor(R.color.colorGreen, null));
                } else {
                    btn.setTextColor(getResources().getColor(R.color.colorGreen));
                }
            }
        }

        //region
       /* if (question.getType().equals(QuestionType.SINGLE_CHOICE)) {
            for (Option option : options) {
                RadioButton radioButton = new RadioButton(getContext());
                String s = option.getLabel() + "、" + option.getContent();
                radioButton.setText(s);
                rgOptions.addView(radioButton);
            }
        } else if (question.getType().equals(QuestionType.MULTI_CHOICE)) {
            for (Option option : options) {
                CheckBox checkBox = new CheckBox(getContext());
                String s = option.getLabel() + "、" + option.getContent();
                checkBox.setText(s);
                rgOptions.addView(checkBox);
            }

        }*/
        //endregion
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_question;
    }

    @Override
    public void search(String kw) {

    }
}
