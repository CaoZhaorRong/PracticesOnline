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
public class FavoriteFactory {
    private static final FavoriteFactory OUR_INSTANCE = new FavoriteFactory();
    private static SqlRepository<Favorite> repository;

    public static FavoriteFactory getInstance() {
        return OUR_INSTANCE;
    }

    private FavoriteFactory() {
        repository = new SqlRepository<>(AppUtils.getContext(), Favorite.class, DbConstants.packager);
    }

    public List<Favorite> get() {
        return repository.get();
    }

    public Favorite getById(String id) {
        return repository.getById(id);
    }


    public List<Favorite> searchFavorite(String kw) {
        try {
            return repository.getByKeyword(kw, new String[]{Favorite.STATION_ID}, false);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }


    }


    public boolean isQuestionStarred(String id) {
        try {
            List<Favorite> favorites = repository.getByKeyword(id,new  String[]{Favorite.STATION_ID},true);
            return favorites.size()>0;
        } catch (IllegalAccessException |InstantiationException e) {
            e.printStackTrace();
        }
      return false;
    }

    public void starQuestion(UUID questionId){
        Favorite favorite= getByQuestion(questionId.toString());
        if (favorite==null){
            favorite=new Favorite();
            favorite.setQuestionId(questionId);
            repository.insert(favorite);
        }
    }
    public void cancelStarrted(UUID questionId){
        Favorite favorite= getByQuestion(questionId.toString());
        if (favorite!=null){
            repository.delete(favorite);
        }
    }


    public boolean insert(Favorite favorite) {

        try {
            repository.insert(favorite);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public String delete(String favoriteId) {
        Favorite favorite = getByQuestion(favoriteId);
        return favorite == null ? null : repository.getDeleteString(favorite);
    }


    private Favorite getByQuestion(String questionId) {

        try {
            List<Favorite> favorites = repository.getByKeyword(questionId,
                    new String[]{Favorite.STATION_ID}, true);
            if (favorites.size() > 0) {
                return favorites.get(0);
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
