package net.lzzy.practicesonline.activities.models;

import net.lzzy.practicesonline.activities.models.views.QuestionType;
import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Sqlitable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/16
 * Description:
 */
public class Question extends BaseEntity implements Sqlitable {

    private String content;
    //持久化使用
    @Ignored
    public static final String COL_PRACTICE_ID = "practiceId";
    private QuestionType type;
    //显示使用

    private int dbType;

    private String analysis;
    private UUID practiceId;
    @Ignored
    private List<Option> options;

    public Question(){
        options=new ArrayList<>();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public QuestionType getType() {
        return type;
    }

    public int getDbType() {
        return dbType;
    }

    public void setDbType(int dbType) {
        this.dbType = dbType;
        type=QuestionType.getInstance(dbType);
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public UUID getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(UUID practiceId) {
        this.practiceId = practiceId;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options.clear();
        this.options.addAll(options);
    }

    @Override
    public boolean needUpdate() {
        return false;
    }
}
