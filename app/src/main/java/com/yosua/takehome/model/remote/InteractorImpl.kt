package com.yosua.takehome.model.remote

import android.content.Context
import com.yosua.takehome.model.dao.UserDao
import io.reactivex.Observable

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

class InteractorImpl(private var mService: ApiService?) {
    private val TAG = "InteractorImpl"
    private val mContext: Context? = null

    fun getUserData(keyword: String, page: Int): Observable<UserDao> {
        return mService!!.getUserData(keyword, page)
    }
}
