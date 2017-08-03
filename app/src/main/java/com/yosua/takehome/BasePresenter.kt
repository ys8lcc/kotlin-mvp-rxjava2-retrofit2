package com.yosua.takehome

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

interface BasePresenter<V> {
    fun start()
    fun bind(view: V)
    fun unbind()
}
