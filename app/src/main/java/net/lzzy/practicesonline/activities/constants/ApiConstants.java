package net.lzzy.practicesonline.activities.constants;

import net.lzzy.practicesonline.activities.utils.AppUtils;



/**
 *
 * @author lzzy_gxy
 * @date 2019/4/15
 * Description:
 */
public class ApiConstants {
    private static final String IP= AppUtils.loadServerSetting(AppUtils.getContext()).first;
    private static final String PORT= AppUtils.loadServerSetting(AppUtils.getContext()).second;
    private static final String PROTOCOl="http://";

    public static final String URL_API=PROTOCOl.concat(IP).concat(":").concat(PORT);

    public static final String ACTION_PRACTICE="/api/practices";
    public  static final String URL_PRACTICES=URL_API.concat(ACTION_PRACTICE);

    public static final String ACTION_QUESTIONS="/api/pquestions?practiceid=";
    public  static final String URL_QUESTIONS=URL_API.concat(ACTION_QUESTIONS);

    /**
     * practice
     */
    public static final String JSON_PRACTICES_API_ID="Id";
    public static final String JSON_PRACTICES_NAME="Name";
    public static final String JSON_PRACTICES_OUT_LINES ="OutLines";
    public static final String JSON_PRACTICES_QUESTION_COUNT ="QuestionCount";


    /**
     * Question
     */
    public static final String JSON_QUESTION_ANALYSIS = "Analysis";
    public static final String JSON_QUESTION_CONTENT = "Content";
    public static final String JSON_QUESTION_TYPE = "QuestionType";
    public static final String JSON_QUESTION_OPTIONS = "Options";
    public static final String JSON_QUESTION_ANSWER = "Answers";

    /**
     * Question
     */
    public static final String JSON_OPTIONS_LABEL ="Label";
    public static final String JSON_OPTIONS_CONTENT="Content";
    public static final String JSON_OPTIONS_API_ID="Id";
    public static final String JSON_ANSWER_OPTION_ID="OptionId";

    /**
     * 提交结果
     */
    private static final String ACTION_RESULT="/api/result/PracticeResult";
    public static final String URL_RESULT=URL_API.concat(ACTION_RESULT);
    /**
     * PraticeResult字段
     */
    public static final String JSON_RESULT_API_ID = "PracticeID";
    public static final String JSON_RESULT_SCRORE_RATIO = "ScroreRatio";
    public static final String JSON_RESULT_WRONG_QUESTION_IDS = "WrongQuestionIds";
    public static final String JSON_RESULT_PHONE_NO = "PhoneNo";


}
