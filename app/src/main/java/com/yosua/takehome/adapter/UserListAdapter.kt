package com.yosua.takehome.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.yosua.takehome.R
import com.yosua.takehome.contract.MainContract
import com.yosua.takehome.model.dao.UserDao
import com.yosua.takehome.util.ImageUtil

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

class UserListAdapter(private var mContext: Context?,
                      private var mList: MutableList<UserDao.ItemDao>,
                      private var mPresenter: MainContract.Presenter) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_VIEW: Int = 0
    private val FOOTER_VIEW: Int = 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ItemViewHolder) {
            val viewHolder: ItemViewHolder = holder
            val image: String = mList[position].imageUrl!!
            val name: String = mList[position].username!!
            viewHolder.mTvUserName!!.text = name
            ImageUtil.loadImageWithGlide(mContext!!, image, viewHolder.mIvUserImage!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_VIEW ->
                return ItemViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_user_list, parent, false))
            else ->
                return FooterViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_user_footer, parent, false))
        }
    }

    override fun getItemCount(): Int {
        when {
            mList == null -> return 0
            else -> return mList.size
        }
    }

    private inner class ItemViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var mIvUserImage: ImageView? = null
        internal var mTvUserName: TextView? = null

        init {
            mIvUserImage = itemView.findViewById(R.id.iv_user_image) as ImageView
            mTvUserName = itemView.findViewById(R.id.tv_user_name) as TextView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {

        }
    }

    inner class FooterViewHolder internal constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {}

    override fun getItemViewType(position: Int): Int {
        when {
            isPositionFooter(position) && mPresenter.isIncomplete() -> return FOOTER_VIEW
            else -> return ITEM_VIEW
        }
    }

    private fun isPositionFooter(position: Int): Boolean {
        return position == itemCount - 1 && position != 0
    }

}