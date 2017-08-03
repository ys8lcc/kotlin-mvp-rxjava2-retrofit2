package com.yosua.takehome.presenter

import android.content.Context
import android.os.Handler
import com.yosua.takehome.BuildConfig
import com.yosua.takehome.contract.MainContract
import com.yosua.takehome.model.dao.UserDao
import com.yosua.takehome.model.remote.ApiServiceBuilder
import com.yosua.takehome.model.remote.InteractorImpl
import com.yosua.takehome.util.LogUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

class MainPresenter(context: Context?) : MainContract.Presenter {
    private val TAG: String = "MainPresenter"
    private val REQUEST_COUNT_TIME: Long = 1000 * 60
    private var mView: MainContract.View? = null
    private var mContext: Context? = context
    private var mInteractor: InteractorImpl? = null
    private var mList: MutableList<UserDao.ItemDao>? = ArrayList()
    private var mIsNewLoading: Boolean = true
    private var mIsFirstLoading: Boolean = true
    private var mPage: Int = 1
    private var mRequestCount: Int = 0
    private val mHandler: Handler = Handler()
    private var mIsInCcmplete: Boolean? = true

    init {
        when (mInteractor) {
            null -> mInteractor = InteractorImpl(ApiServiceBuilder().provideApiServiceWithCache(
                    mContext!!, BuildConfig.BASE_URL))
        }
    }

    override fun start() {
        startRequestCount()
    }

    override fun bind(view: MainContract.View) {
        mView = view
    }

    override fun unbind() {
        mView = null
    }

    override fun getUser(keyword: String) {
        when {
            mRequestCount < 10 -> mRequestCount++.also {
                when {
                    mIsNewLoading -> mView!!.showProgress(true)
                }
                mView!!.hideError()
                mInteractor!!.getUserData(keyword, mPage)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(this::showUserList, this::showErrorView)
            }
            else -> mView!!.showLimitError()
        }
    }

    override fun getUserList(): MutableList<UserDao.ItemDao> {
        return mList!!
    }

    override fun isNewLoading(): Boolean {
        return mIsNewLoading
    }

    override fun setNewLoading(isNewLoading: Boolean) {
        mIsNewLoading = isNewLoading
        when (isNewLoading) {
            true -> mPage = 1
        }
    }

    override fun isFirstLoading(): Boolean {
        return mIsFirstLoading
    }

    override fun setFistLoading(isFirstLoading: Boolean) {
        mIsFirstLoading = isFirstLoading
    }

    private fun showUserList(userListDao: UserDao) {
        when {
            mIsNewLoading -> mList!!.clear()
        }
        mList!!.addAll(userListDao.userItemList!!)
        when {
            mList!!.size < 30 -> mIsInCcmplete = false
            else -> mIsInCcmplete = true
        }
        mView!!.updateUserList(userListDao)
        mView!!.showProgress(false)
    }

    private fun showErrorView(e: Throwable) {
        LogUtil.errorLog(TAG, e.message)
        if (mView != null) {
            mView!!.showProgress(false)
            mView!!.showError(e)
        }
    }

    override fun addPage() {
        mPage++
    }

    override fun isIncomplete(): Boolean {
        return mIsInCcmplete!!
    }

    private var mRunnable = Runnable {
        mRequestCount = 0
        stopRequestCount()
        startRequestCount()
    }

    fun startRequestCount() {
        mHandler.postDelayed(mRunnable, REQUEST_COUNT_TIME)
    }

    fun stopRequestCount() {
        mHandler.removeCallbacks(mRunnable)
    }
}
