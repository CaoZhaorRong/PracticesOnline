package net.lzzy.practicesonline.activities.models;

import android.text.TextUtils;

import net.lzzy.practicesonline.activities.constants.DbConstants;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzzy_gxy
 * @date 2019/4/17
 * Description:
 */
public class QuestionFactory {
    private static final QuestionFactory OUR_INSTANCE = new QuestionFactory();
    private static SqlRepository<Question> repository;
    private static SqlRepository<Option> optionRepository;

    public static QuestionFactory getInstance() {
        return OUR_INSTANCE;
    }

    private QuestionFactory() {
        optionRepository = new SqlRepository<>(AppUtils.getContext(), Option.class, DbConstants.packager);
        repository = new SqlRepository<>(AppUtils.getContext(), Question.class, DbConstants.packager);
    }

    public List<Question> get() {
        return repository.get();
    }

    private void completeQuestion(Question question) throws InstantiationException, IllegalAccessException {
        List<Option> options = optionRepository.getByKeyword(question.getId().toString(),
                new String[]{""}, true);
        question.setOptions(options);
        question.setDbType(question.getDbType());
    }

    public Question getById(String id) {

        try {
            Question question = repository.getById(id);
            completeQuestion(question);
            return question;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Question question) {
        String g = repository.getInsertString(question);
        List<String> sqlActions = new ArrayList<>();

        for (Option option : question.getOptions()) {
            sqlActions.add(optionRepository.getInsertString(option));
        }
        sqlActions.add(g);
        repository.exeSqls(sqlActions);

    }

    public List<Question> getByPractice(String id) {
        try {
            List<Question> questions = repository.getByKeyword(id, new String[]{""}, true);
            for (Question question : questions) {
                completeQuestion(question);
            }
            return questions;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public List<String> delete(Question question) {
        List<String> sqlActions = new ArrayList<>();
        sqlActions.add(repository.getDeleteString(question));
        for (Option option : question.getOptions()) {
            sqlActions.add(optionRepository.getDeleteString(option));
        }
        String f = FavoriteFactory.getInstance().delete(question.getId().toString());
        if (!TextUtils.isEmpty(f)) {
            sqlActions.add(f);
        }
        return sqlActions;
    }


}
