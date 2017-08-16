package edu.henu.customer;

import java.io.File;

/**
 * Created by lenovo on 2017/8/12.
 */
/**
 * 删除单个文件
 * @return 文件删除成功返回true，否则返回false
 * 用于更新数据后删除原来copy的数据 防止冗余数据的产生
 */

public class DeleCustomer {
    public static boolean deleteFile(String filename) {
        File file = new File(filename);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
}
