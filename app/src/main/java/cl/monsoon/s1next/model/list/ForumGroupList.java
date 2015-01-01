package cl.monsoon.s1next.model.list;

import android.util.SparseArray;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cl.monsoon.s1next.model.Account;
import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.model.ForumGroup;

/**
 * Sort Forums by {@link cl.monsoon.s1next.model.Forum#getTodayPosts()} desc
 * and group Forums by category.
 * <p>
 * {@link #forumList}:
 * <pre>
 * 游戏论坛
 * 手游页游
 * 动漫论坛
 * …
 * </pre>
 * <p>
 * {@link #forumGroupNameList}:
 * <pre>
 * 主论坛
 * 主题公园
 * 子论坛
 * </pre>
 * <p>
 * {@link #data}:
 * <pre>
 * 主论坛
 *   热血魔兽
 *   DOTA
 *   …
 * 主论坛
 *   游戏论坛
 *   手游页游
 *   …
 * 主题公园
 *   任天堂专区
 *   异度传说
 *   …
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ForumGroupList extends Account {

    @JsonIgnore
    private final List<Forum> forumList;

    @JsonIgnore
    private final List<String> forumGroupNameList;

    @JsonIgnore
    private final List<ForumGroup> data;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public ForumGroupList(
            @JsonProperty("catlist") List<ForumGroup> forumGroupList,
            @JsonProperty("forumlist") List<Forum> forumList) {
        Collections.sort(forumList, (lhs, rhs) -> -(lhs.getTodayPosts() - rhs.getTodayPosts()));
        this.forumList = forumList;

        SparseArray<Forum> forumSparseArray = new SparseArray<>();
        for (Forum forum : forumList) {
            forumSparseArray.put(Integer.parseInt(forum.getId()), forum);
        }

        this.forumGroupNameList = new ArrayList<>();
        for (ForumGroup forumGroup : forumGroupList) {
            this.forumGroupNameList.add(forumGroup.getName());

            List<Forum> forumOfGroupList = new ArrayList<>();
            for (Integer id : forumGroup.getForumIds()) {
                forumOfGroupList.add(forumSparseArray.get(id));
            }
            forumGroup.setForumList(forumOfGroupList);
        }

        this.data = forumGroupList;
    }

    public List<Forum> getForumList() {
        return Collections.unmodifiableList(forumList);
    }

    public List<String> getForumGroupNameList() {
        return Collections.unmodifiableList(forumGroupNameList);
    }

    public List<ForumGroup> getData() {
        return Collections.unmodifiableList(data);
    }
}
