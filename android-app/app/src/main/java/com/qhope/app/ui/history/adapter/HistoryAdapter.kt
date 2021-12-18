package com.qhope.app.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.qhope.app.R
import com.qhope.app.databinding.LayoutBookingHistoryItemBinding
import com.qhope.app.ui.base.adapter.BaseAdapter
import com.qhope.app.ui.base.adapter.BaseDiffCallback
import com.qhope.core.domain.model.history.History
import com.qhope.core.domain.model.history.HistoryStatus
import com.qhope.core.utils.view.DataUtils.getText

class HistoryAdapter(private val historyItemCallback: HistoryItemCallback) :
  BaseAdapter<History, LayoutBookingHistoryItemBinding>(diffCallback) {

  companion object {
    private val diffCallback = object : BaseDiffCallback<History>() {
      override fun contentEquality(oldItem: History, newItem: History): Boolean {
        return oldItem == newItem
      }
    }
  }

  override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutBookingHistoryItemBinding
    get() = LayoutBookingHistoryItemBinding::inflate

  override fun getViewHolder(binding: LayoutBookingHistoryItemBinding): BaseViewHolder {
    return HistoryViewHolder(binding)
  }

  inner class HistoryViewHolder(binding: LayoutBookingHistoryItemBinding) :
    BaseViewHolder(binding) {

    override fun bind(data: History) {
      binding.apply {
        root.setOnClickListener {
          historyItemCallback.onClickListener(data.id)
        }

        Glide.with(mContext)
          .load(data.hospitalImagePath)
          .placeholder(R.drawable.drawable_hospital_placeholder)
          .into(imageViewHistoryItem)

        textViewHistoryItemName.text = data.hospitalName
        textViewHistoryItemDate.text = data.createdAt
        data.nightCount?.let { nightCount ->
          textViewHistoryItemNight.text = mContext.resources.getQuantityString(
            R.plurals.history_night_state, nightCount, nightCount
          )
        }

        with(textViewHistoryItemStatus) {
          text = data.status.getText()
          setTextColor(getStatusColor(data.status))
        }
      }
    }

    private fun getStatusColor(status: HistoryStatus?) = when (status) {
      HistoryStatus.COMPLETED -> R.color.green_700
      HistoryStatus.CANCELLED -> R.color.red_700
      else -> R.color.black
    }
  }
}