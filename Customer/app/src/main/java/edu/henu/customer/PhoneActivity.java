package edu.henu.customer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lenovo on 2017/8/10.
 */

public class PhoneActivity {
    public static boolean PhoneActivity(String value){
        /**
         * 手机号号段校验，
         第1位：1；
         第2位：{3、4、5、6、7、8}任意数字；
         第3—11位：0—9任意数字
         * @param value
         * @return
         */
            if (value != null && value.length() == 11) {
                Pattern pattern = Pattern.compile("^1[3|4|5|6|7|8|9][0-9]\\d{8}$");
                Matcher matcher = pattern.matcher(value);
                return matcher.matches();
            }
            return false;
        }
}

