package net.lzzy.practicesonline.activities.models.views;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/16
 * Description:
 */
public enum QuestionType {

    /**
     * 题目类型
     */
    SINGLE_CHOICE("单项选择"),MULTI_CHOICE("不定项选择"),JUDGE("选择");
    private String name;
    QuestionType(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static QuestionType getInstance(int ordinal){
        for (QuestionType type:QuestionType.values()){
            if (type.ordinal()==ordinal){
                return type;
            }
        }
        return null;
    }

}

