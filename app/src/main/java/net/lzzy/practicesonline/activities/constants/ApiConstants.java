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
}
