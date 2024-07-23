# 将.class信息中的类名重新定义为"SourceFile"字符串
-renamesourcefileattribute SourceFile
# 并保留源文件名为"Proguard"字符串，而非原始的类名 并保留行号 // blog from sodino.com
-keepattributes SourceFile,LineNumberTable

# Keep fragment name
-keepnames class me.ykrank.s1next.** implements androidx.fragment.app.Fragment
-keepnames class me.ykrank.s1next.** implements android.app.Fragment

# Jackson Model
-keep public class me.ykrank.s1next.data.api.model.** { *; }
-keep public class me.ykrank.s1next.data.api.app.model.** { *; }
-keep public class me.ykrank.s1next.data.cache.** { *; }
-keep public class me.ykrank.s1next.data.db.dbmodel.ReadProgress { *; }
-keep public class me.ykrank.s1next.widget.uploadimg.model.** { *; }
# db model
-keep public class me.ykrank.s1next.data.db.dbmodel.** { *; }
-keep public class me.ykrank.s1next.data.cache.dbmodel.Cache { *; }

