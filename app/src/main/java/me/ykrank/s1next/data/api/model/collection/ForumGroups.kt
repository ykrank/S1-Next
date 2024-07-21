package me.ykrank.s1next.data.api.model.collection

import android.util.SparseArray
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Objects
import me.ykrank.s1next.data.api.model.Account
import me.ykrank.s1next.data.api.model.Forum
import me.ykrank.s1next.data.api.model.ForumCategoryByIds

@JsonIgnoreProperties(ignoreUnknown = true)
class ForumGroups @JsonCreator constructor(
    @JsonProperty("catlist") forumGroupByIdsList: List<ForumCategoryByIds>?,
    @JsonProperty("forumlist") rawForumList: MutableList<Forum>?
) : Account() {
    @JsonProperty("_forumList")
    val forumList: List<Forum>

    @JsonProperty("_forumGroupNameList")
    val forumGroupNameList: MutableList<String> = mutableListOf()

    @JsonProperty("_forumGroupList")
    val forumGroupList: MutableList<List<Forum>> = mutableListOf()

    /**
     * Sorts Forums by [Forum.getTodayPosts] desc
     * and groups Forums by category.
     */
    init {
        // sort forum list by today's post count in descending order
        if (rawForumList != null) {
            rawForumList.sortByDescending { it.todayPosts }
            val forumSparseArray = SparseArray<Forum>(rawForumList.size)
            for (forum in rawForumList) {
                forumSparseArray.put(forum.id?.toInt() ?: 0, forum)
            }

            if (forumGroupByIdsList != null) {
                for (forumCategoryByIds in forumGroupByIdsList) {
                    forumGroupNameList.add(forumCategoryByIds.name ?: "")
                    val oneCategoryForumList: MutableList<Forum> = ArrayList(
                        forumCategoryByIds.forumIds.size
                    )
                    for (id in forumCategoryByIds.forumIds) {
                        oneCategoryForumList.add(forumSparseArray[id])
                    }
                    forumGroupList.add(oneCategoryForumList)
                }
            }
        }
        this.forumList = rawForumList ?: emptyList()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as ForumGroups
        return Objects.equal(forumList, that.forumList) &&
                Objects.equal(forumGroupNameList, that.forumGroupNameList) &&
                Objects.equal(forumGroupList, that.forumGroupList)
    }

    override fun hashCode(): Int {
        return Objects.hashCode(super.hashCode(), forumList, forumGroupNameList, forumGroupList)
    }
}
