package me.ykrank.s1next.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by ykrank on 2017/1/17.
 */

public class SQLiteUtil {

    public static boolean isValidSQLite(String dbPath) {
        File file = new File(dbPath);

        if (!file.exists() || !file.canRead()) {
            return false;
        }
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            char[] buffer = new char[16];

            fr.read(buffer, 0, 16);
            String str = String.valueOf(buffer);

            return str.equals("SQLite format 3\u0000");

        } catch (Exception e) {
            L.e(e);
            return false;
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    L.e(e);
                }
            }
        }
    }
}
