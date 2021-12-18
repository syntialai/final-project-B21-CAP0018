package com.qhope.app.ui.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.qhope.core.utils.view.ViewUtils.loadImageFromStorage

abstract class BaseAdapter<T : Any, VB : ViewBinding>(diffCallback: BaseDiffCallback<T>) :
  ListAdapter<T, BaseAdapter<T, VB>.BaseViewHolder>(diffCallback) {

  abstract val inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

  abstract fun getViewHolder(binding: VB): BaseViewHolder

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
    return getViewHolder(inflater.invoke(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  abstract inner class BaseViewHolder(protected val binding: VB) :
    RecyclerView.ViewHolder(binding.root) {

    protected val mContext = binding.root.context

    abstract fun bind(data: T)

    protected fun <IV> ImageView.loadImage(image: IV, @DrawableRes placeholder: Int? = null) {
      loadImageFromStorage(mContext, image, placeholder)
    }
  }
}