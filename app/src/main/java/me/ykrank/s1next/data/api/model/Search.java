package me.ykrank.s1next.data.api.model;

/**
 * Created by ykrank on 2016/10/18.
 */

public class Search {
    
    private String content;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Search search = (Search) o;

        return content.equals(search.content);

    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public String toString() {
        return "Search{" +
                "content='" + content + '\'' +
                '}';
    }
}
