package net.lzzy.practicesonline.activities.network;

import net.lzzy.practicesonline.activities.models.Question;
import net.lzzy.practicesonline.activities.models.views.QuestionType;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by lzzy_gxy on 2019/4/22.
 * Description:
 */
public class QuestionServiceTest {

    @Test
    public void textGetQuestionOfPracticeFromServer() throws IOException {
        String s=QuestionService.getQuestionOfPracticeFromServer(28);
        assertTrue(s.contains("便捷性与经济性"));
    }

    @Test
    public void testGetQuestions() throws IOException, IllegalAccessException, JSONException, InstantiationException {
        String json = QuestionService.getQuestionOfPracticeFromServer(28);
        List<Question> questions = QuestionService.getQuestions(json, UUID.randomUUID());
        assertEquals(6,questions.size());
        Question question = questions.get(1);
        assertTrue(question.getContent().contains("主要目的在于"));
        assertEquals(QuestionType.SINGLE_CHOICE,question.getType());
        assertEquals(4,question.getOptions().size());
        assertTrue(question.getOptions().get(0).isAnswer());
    }
}