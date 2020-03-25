package base.engineering.motosumiyoshi.volleymanageapp.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    /**
     * 現在日時をyyyy/MM/dd HH:mm:ss形式で取得する.<br>
     */
    public static String getNowDateByNormalFormat(){
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

}
