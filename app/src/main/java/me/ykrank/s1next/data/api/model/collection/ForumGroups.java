package me.ykrank.s1next.data.api.model.collection;

import android.util.SparseArray;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.data.api.model.ForumCategoryByIds;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ForumGroups extends Account {

    @JsonIgnore
    private final List<Forum> forumList;

    @JsonIgnore
    private final List<String> forumGroupNameList;

    @JsonIgnore
    private final List<List<Forum>> forumGroupList;

    /**
     * Sorts Forums by {@link Forum#getTodayPosts()} desc
     * and groups Forums by category.
     */
    @JsonCreator
    @SuppressWarnings("UnusedDeclaration")
    public ForumGroups(@JsonProperty("catlist") List<ForumCategoryByIds> forumGroupByIdsList,
                       @JsonProperty("forumlist") List<Forum> forumList) {
        // sort forum list by today's post count in descending order
        Collections.sort(forumList, (lhs, rhs) -> -(lhs.getTodayPosts() - rhs.getTodayPosts()));
        this.forumList = forumList;

        SparseArray<Forum> forumSparseArray = new SparseArray<>(forumList.size());
        for (Forum forum : forumList) {
            forumSparseArray.put(Integer.parseInt(forum.getId()), forum);
        }

        this.forumGroupNameList = new ArrayList<>(forumGroupByIdsList.size());
        this.forumGroupList = new ArrayList<>();
        for (ForumCategoryByIds forumCategoryByIds : forumGroupByIdsList) {
            this.forumGroupNameList.add(forumCategoryByIds.getName());

            List<Forum> oneCategoryForumList = new ArrayList<>(forumCategoryByIds.forumIds.size());
            for (Integer id : forumCategoryByIds.forumIds) {
                oneCategoryForumList.add(forumSparseArray.get(id));
            }
            forumGroupList.add(oneCategoryForumList);
        }
    }

    public List<Forum> getForumList() {
        return forumList;
    }

    public List<String> getForumGroupNameList() {
        return forumGroupNameList;
    }

    public List<List<Forum>> getForumGroupList() {
        return forumGroupList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ForumGroups that = (ForumGroups) o;
        return Objects.equal(forumList, that.forumList) &&
                Objects.equal(forumGroupNameList, that.forumGroupNameList) &&
                Objects.equal(forumGroupList, that.forumGroupList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), forumList, forumGroupNameList, forumGroupList);
    }
}
