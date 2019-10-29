# Keep fragment name
-keepnames class me.ykrank.s1next.** implements android.support.v4.app.Fragment
-keepnames class me.ykrank.s1next.** implements android.app.Fragment

# Jackson Model
-keep public class me.ykrank.s1next.data.api.model.** { *; }
-keep public class me.ykrank.s1next.data.api.app.model.** { *; }
-keep public class me.ykrank.s1next.data.cache.** { *; }
-keep public class me.ykrank.s1next.data.db.dbmodel.ReadProgress { *; }
# GreenDao model
-keep public class me.ykrank.s1next.data.db.dbmodel.** { *; }

# OkDownload
-keepnames class com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection