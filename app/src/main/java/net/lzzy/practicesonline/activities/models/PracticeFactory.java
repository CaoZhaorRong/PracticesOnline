package net.lzzy.practicesonline.activities.models;

import net.lzzy.practicesonline.activities.constants.DbConstants;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author lzzy_gxy
 * @date 2019/4/17
 * Description:
 */
public class PracticeFactory {
    private static final PracticeFactory OUR_INSTANCE = new PracticeFactory();
    private static SqlRepository<Practice> repository;

    public static PracticeFactory getInstance() {
        return OUR_INSTANCE;
    }

    private PracticeFactory() {
        repository = new SqlRepository<>(AppUtils.getContext(), Practice.class, DbConstants.packager);
    }

    public List<Practice> get() {
        return repository.get();
    }

    public Practice getById(String id) {
        return repository.getById(id);
    }

    public List<Practice> search(String kw) {
        try {
            return repository.getByKeyword(kw, new String[]{Practice.COL_API_ID}, false);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private boolean isPracticeInDb(Practice practice) {
        try {
            return repository.getByKeyword(String.valueOf(practice.getApiId()),
                    new String[]{Practice.COL_API_ID}, true).size() > 0;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return true;
        }
    }

    public UUID getPracticeId(int apiId) {
        List<Practice> practices = null;
        try {
            practices = repository.getByKeyword(String.valueOf(apiId),
                    new String[]{Practice.COL_API_ID}, true);
            if (practices.size() > 0) {
                return practices.get(0).getId();
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPracticeDown(String id) {
        Practice practice = getById(id);
        if (practice != null) {
            practice.setDownloaded(true);
            repository.update(practice);
        }
    }

    public boolean insert(Practice practice) {

            if (isPracticeInDb(practice)){
                return false;
            }
            repository.insert(practice);
            return true;

    }

    public boolean deletePracticeAndRelated(Practice practice) {
        //事务处理 数据库ACID属性 Atomic原理的Consistency一致性
        // Isolation隔离 Durability持久性
        List<String> sqlAction = new ArrayList<>();
        sqlAction.add(repository.getDeleteString(practice));
        QuestionFactory factory = QuestionFactory.getInstance();
        List<Question> questions = factory.getByPractice(practice.getId().toString());

        if (questions.size() > 0) {
            for (Question q :questions ) {
                sqlAction.addAll(factory.delete(q));
            }

        }
        repository.exeSqls(sqlAction);
        if (!isPracticeInDb(practice)) {
            // TODO: 清除
        }
        return true;

    }

    public void update(Practice practice) {
        repository.update(practice);
    }
    public void saveQuestions(List<Question> questions,UUID practiceId) {
        for (Question q:questions){
            QuestionFactory.getInstance().insert(q);
        }
        setPracticeDown(practiceId.toString());
    }
}