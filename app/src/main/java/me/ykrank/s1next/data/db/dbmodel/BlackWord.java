package me.ykrank.s1next.data.db.dbmodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.ykrank.s1next.R;

@Entity(nameInDb = "BlackWord")
public class BlackWord implements Parcelable {

    public static final int NORMAL = 0;
    public static final int HIDE = 1;
    public static final int DEL = 2;

    private static final String TimeFormat = "yyyy-MM-dd HH:mm";

    @Id(autoincrement = true)
    @Nullable
    private Long id;

    /**
     * 屏蔽词
     */
    @Property(nameInDb = "Word")
    @Index(name = "IDX_BlackWord_Word", unique = true)
    private String word;

    /**
     * 屏蔽状态
     */
    @Property(nameInDb = "Stat")
    @BlackWordFLag
    private int stat = NORMAL;

    /**
     * 屏蔽时的时间
     */
    @Property(nameInDb = "Timestamp")
    private long timestamp;

    /**
     * 是否已同步
     */
    @Property(nameInDb = "Upload")
    private boolean upload;

    public BlackWord() {
        this.timestamp = System.currentTimeMillis();
    }

    public BlackWord(String word, int stat) {
        this.word = word;
        this.stat = stat;
        this.timestamp = System.currentTimeMillis();
        this.upload = false;
    }

    protected BlackWord(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        word = in.readString();
        stat = in.readInt();
        timestamp = in.readLong();
        upload = in.readByte() != 0;
    }

    @Generated(hash = 1127251493)
    public BlackWord(Long id, String word, int stat, long timestamp, boolean upload) {
        this.id = id;
        this.word = word;
        this.stat = stat;
        this.timestamp = timestamp;
        this.upload = upload;
    }

    public static final Creator<BlackWord> CREATOR = new Creator<BlackWord>() {
        @Override
        public BlackWord createFromParcel(Parcel in) {
            return new BlackWord(in);
        }

        @Override
        public BlackWord[] newArray(int size) {
            return new BlackWord[size];
        }
    };

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    @StringRes
    public int getStatRes() {
        switch (stat) {
            case HIDE:
                return R.string.blacklist_flag_hide;
            case DEL:
                return R.string.blacklist_flag_del;
            default:
                return R.string.blacklist_flag_normal;
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(TimeFormat, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    @Override
    public String toString() {
        return "BlackWord{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", stat=" + stat +
                ", timestamp=" + timestamp +
                ", upload=" + upload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlackWord blackWord = (BlackWord) o;

        if (stat != blackWord.stat) return false;
        if (timestamp != blackWord.timestamp) return false;
        if (upload != blackWord.upload) return false;
        if (id != null ? !id.equals(blackWord.id) : blackWord.id != null) return false;
        return word != null ? word.equals(blackWord.word) : blackWord.word == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (word != null ? word.hashCode() : 0);
        result = 31 * result + stat;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (upload ? 1 : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(word);
        dest.writeInt(stat);
        dest.writeLong(timestamp);
        dest.writeByte((byte) (upload ? 1 : 0));
    }

    public void mergeFrom(@NotNull BlackWord blackWord) {
        this.word = blackWord.word;
        this.stat = blackWord.stat;
        this.timestamp = blackWord.timestamp;
    }

    public boolean getUpload() {
        return this.upload;
    }

    @IntDef({NORMAL, HIDE, DEL})
    public @interface BlackWordFLag {

    }
}
