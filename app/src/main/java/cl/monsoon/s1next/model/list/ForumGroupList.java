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
 * &#x6e38;&#x620f;&#x8bba;&#x575b;
 * &#x624b;&#x6e38;&#x9875;&#x6e38;
 * &#x52a8;&#x6f2b;&#x8bba;&#x575b;
 * &hellip;
 * </pre>
 * <p>
 * {@link #forumGroupNameList}:
 * <pre>
 * &#x4e3b;&#x8bba;&#x575b;
 * &#x4e3b;&#x9898;&#x516c;&#x56ed;
 * &#x5b50;&#x8bba;&#x575b;
 * </pre>
 * <p>
 * {@link #forumGroupList}:
 * <pre>
 * &#x4e3b;&#x8bba;&#x575b;
 *   &#x70ed;&#x8840;&#x9b54;&#x517d;
 *   DOTA
 *   &hellip;
 * &#x4e3b;&#x8bba;&#x575b;
 *   &#x6e38;&#x620f;&#x8bba;&#x575b;
 *   &#x624b;&#x6e38;&#x9875;&#x6e38;
 *   &hellip;
 * &#x4e3b;&#x9898;&#x516c;&#x56ed;
 *   &#x4efb;&#x5929;&#x5802;&#x4e13;&#x533a;
 *   &#x5f02;&#x5ea6;&#x4f20;&#x8bf4;
 *   &hellip;
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ForumGroupList extends Account {

    @JsonIgnore
    private final List<Forum> forumList;

    @JsonIgnore
    private final List<String> forumGroupNameList;

    @JsonIgnore
    private final List<ForumGroup> forumGroupList;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public ForumGroupList(
            @JsonProperty("catlist") List<ForumGroup> forumGroupList,
            @JsonProperty("forumlist") List<Forum> forumList
    ) {
        Collections.sort(forumList, Collections.reverseOrder());
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

        this.forumGroupList = forumGroupList;
    }

    public List<Forum> getForumList() {
        return Collections.unmodifiableList(forumList);
    }

    public List<String> getForumGroupNameList() {
        return Collections.unmodifiableList(forumGroupNameList);
    }

    public List<ForumGroup> getForumGroupList() {
        return Collections.unmodifiableList(forumGroupList);
    }
}
