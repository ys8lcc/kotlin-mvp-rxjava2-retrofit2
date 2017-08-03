package com.yosua.takehome.model.remote

import com.yosua.takehome.model.dao.UserDao
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

interface ApiService {
    @GET("search/users")
    abstract fun getUserData(
            @Query("q") keyword: String,
            @Query("page") page: Int
    ): Observable<UserDao>
}
