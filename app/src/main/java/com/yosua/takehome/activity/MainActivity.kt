package com.yosua.takehome.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.yosua.takehome.R
import com.yosua.takehome.adapter.UserListAdapter
import com.yosua.takehome.contract.MainContract
import com.yosua.takehome.model.dao.UserDao
import com.yosua.takehome.presenter.MainPresenter
import com.yosua.takehome.util.ImageUtil
import com.yosua.takehome.util.KeyboardUtil
import com.yosua.takehome.util.LogUtil
import java.io.IOException

/*This project was written with Kotlin programming language,
MVP Pattern (Model View Presenter), Rx Java2, Retrofit 2 and also
was made by following Google variable naming guidelines. - Yosua Setiawan
*/

class MainActivity : AppCompatActivity(), MainContract.View, View.OnClickListener {
    private val TAG = "MainActivity"
    private var mPresenter: MainContract.Presenter? = null
    private var mTvErrorMessage: TextView? = null
    private var mRvMainUser: RecyclerView? = null
    private var mPbMainSearch: ProgressBar? = null
    private var mIvErrorIcon: ImageView? = null
    private var mLlErrorLayout: LinearLayout? = null
    private var mBtnErrorRetry: Button? = null
    private var mLinearLayoutManager: LinearLayoutManager? = null
    private var mKeyword: String? = null
    private var mUserListAdapter: UserListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.tb_all) as Toolbar
        setSupportActionBar(toolbar)
        mLlErrorLayout = findViewById(R.id.ll_error_layout) as LinearLayout
        mIvErrorIcon = findViewById(R.id.iv_error_icon) as ImageView
        mTvErrorMessage = findViewById(R.id.tv_error_message) as TextView
        val etMainSearch = findViewById(R.id.et_main_search) as EditText
        mPbMainSearch = findViewById(R.id.pb_main_search) as ProgressBar
        mRvMainUser = findViewById(R.id.rv_main_list) as RecyclerView
        mBtnErrorRetry = findViewById(R.id.btn_error_retry) as Button
        mBtnErrorRetry!!.setOnClickListener(this)
        mPresenter = initPresenter()
        mPresenter!!.bind(this)
        mPresenter!!.start()

        etMainSearch.setOnEditorActionListener(TextView.OnEditorActionListener {
            v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH ->
                    KeyboardUtil.hideKeyboard(this, v).also {
                        mKeyword = v.text.toString()
                        when {
                            mKeyword!!.isNotEmpty() ->
                                mPresenter!!.setNewLoading(true).also {
                                    mPresenter!!.getUser(mKeyword!!)
                                }
                        }
                        return@OnEditorActionListener true
                    }
            }
            false
        })
    }

    override fun updateUserList(userListDao: UserDao) {
        when (mPresenter!!.isFirstLoading()) {
            true ->
                mPresenter!!.setFistLoading(false).also {
                    mLinearLayoutManager = LinearLayoutManager(this)
                    mRvMainUser!!.layoutManager = mLinearLayoutManager
                    mUserListAdapter = UserListAdapter(this, mPresenter!!.getUserList(),
                            mPresenter!!)
                    mRvMainUser!!.adapter = mUserListAdapter
                    val dividerItemDecoration = DividerItemDecoration(this,
                            mLinearLayoutManager!!.orientation)
                    mRvMainUser!!.addItemDecoration(dividerItemDecoration)
                    mRvMainUser!!.addOnScrollListener(mRvOnScrollListener)
                }
            else -> mUserListAdapter!!.notifyDataSetChanged()
        }
        when {
            !userListDao.isIncomplete -> mPresenter!!.addPage()
        }
        mPresenter!!.setNewLoading(false)

        when {
            mPresenter!!.getUserList().size == 0 -> showNotFoundError()
        }
    }

    override fun showError(e: Throwable) {
        LogUtil.errorLog(TAG, e.message)
        var errorMessage: String? = null
        var errorDrawable: Int = 0
        when (mPresenter!!.isNewLoading()) {
            false ->
                when {
                    e is IOException -> errorMessage = getString(R.string.error_network)
                    else -> errorMessage = getString(R.string.error_server)
                }.also {
                    Snackbar.make(mRvMainUser!!, errorMessage!!,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.all_retry, View.OnClickListener {
                                mPresenter!!.getUser(mKeyword!!)
                            }).show()
                }
            else ->
                when {
                    e is IOException ->
                        errorMessage = getString(R.string.error_network).also {
                            errorDrawable = R.drawable.ic_portable_wifi_off_black_48dp
                        }
                    else -> errorMessage = getString(R.string.error_server).also {
                        errorDrawable = R.drawable.ic_cloud_off_black_48dp
                    }
                }.also {
                    mTvErrorMessage!!.text = errorMessage.also {
                        ImageUtil.loadImageWithGlide(this, errorDrawable,
                                mIvErrorIcon!!)
                        mBtnErrorRetry!!.visibility = View.VISIBLE
                        mLlErrorLayout!!.visibility = View.VISIBLE
                    }
                }
        }
    }

    fun showNotFoundError() {
        mBtnErrorRetry!!.visibility = View.GONE
        mLlErrorLayout!!.visibility = View.VISIBLE
        ImageUtil.loadImageWithGlide(this, R.drawable.ic_search_black_48dp, mIvErrorIcon!!)
        mTvErrorMessage!!.text = getString(R.string.error_not_found)
    }

    override fun showLimitError() {
        Snackbar.make(mRvMainUser!!, getString(R.string.error_limit), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.all_okay, View.OnClickListener { }).show()
    }

    override fun hideError() {
        mLlErrorLayout!!.visibility = View.GONE
    }

    override fun showProgress(isLoading: Boolean) {
        when (isLoading) {
            true ->
                when (mPbMainSearch!!.visibility) {
                    View.GONE -> mPbMainSearch!!.visibility = View.VISIBLE
                }
            else ->
                when (mPbMainSearch!!.visibility) {
                    View.VISIBLE -> mPbMainSearch!!.visibility = View.GONE
                }
        }
    }

    override fun initPresenter(): MainContract.Presenter {
        return MainPresenter(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter!!.unbind()
    }

    private val mRvOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            when {
                mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()
                        == mUserListAdapter!!.itemCount - 1 ->
                    mPresenter!!.getUser(mKeyword!!)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_error_retry ->
                when {
                    mKeyword!!.isNotEmpty() ->
                        mPresenter!!.setNewLoading(true).also {
                            mPresenter!!.getUser(mKeyword!!)
                        }
                }
        }
    }

}
