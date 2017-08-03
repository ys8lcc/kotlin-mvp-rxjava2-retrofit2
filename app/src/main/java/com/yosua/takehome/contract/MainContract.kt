package com.yosua.takehome.contract

import com.yosua.takehome.BasePresenter
import com.yosua.takehome.BaseView
import com.yosua.takehome.model.dao.UserDao

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

interface MainContract {
    interface View : BaseView<Presenter> {

        fun showProgress(isLoading: Boolean)
        fun updateUserList(userListDao: UserDao)
        fun showError(e: Throwable)
        fun hideError()
        fun showLimitError()
    }

    interface Presenter : BasePresenter<View> {

        fun getUser(keyword: String)
        fun getUserList(): MutableList<UserDao.ItemDao>
        fun isNewLoading(): Boolean
        fun setNewLoading(isNewLoading: Boolean)
        fun isFirstLoading(): Boolean
        fun setFistLoading(isFirstLoading: Boolean)
        fun addPage()
        fun isIncomplete(): Boolean
    }
}
