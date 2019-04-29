package net.lzzy.practicesonline.activities.network;

import net.lzzy.practicesonline.activities.constants.ApiConstants;
import net.lzzy.practicesonline.activities.models.Practice;
import net.lzzy.sqllib.JsonConverter;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * @author lzzy_gxy
 * @date 2019/4/22
 * Description:
 */
public class PracticeService {

    public static String getPracticesFromServer() throws IOException {
        return ApiService.okGet(ApiConstants.URL_PRACTICES);
    }

    //解析方法

    public static List<Practice> getPractices(String json) throws IllegalAccessException, JSONException, InstantiationException {
        //数组==调用getArray方法 getSingle得到一个单独的Java数据
        JsonConverter<Practice> converter = new JsonConverter<>(Practice.class);
        return converter.getArray(json);
    }
}
