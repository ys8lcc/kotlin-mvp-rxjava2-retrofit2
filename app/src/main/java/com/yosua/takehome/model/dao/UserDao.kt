package com.yosua.takehome.model.dao

import com.google.gson.annotations.SerializedName

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

class UserDao {
    @SerializedName("total_count")
    var totalCount: String? = null
        get

    @SerializedName("incomplete_results")
    var isIncomplete: Boolean = false
        get

    @SerializedName("items")
    var userItemList: List<ItemDao>? = null
        get

    class ItemDao {
        @SerializedName("login")
        var username: String? = null
            get

        @SerializedName("avatar_url")
        var imageUrl: String? = null
            get

        @SerializedName("html_url")
        var htmlUrl: String? = null
            get
    }
}
