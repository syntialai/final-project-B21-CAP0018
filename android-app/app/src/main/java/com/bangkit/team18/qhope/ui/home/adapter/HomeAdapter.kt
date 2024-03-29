package com.bangkit.team18.qhope.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bangkit.team18.core.domain.model.home.Hospital
import com.bangkit.team18.core.utils.view.DataUtils.orZero
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.LayoutHospitalItemBinding
import com.bangkit.team18.qhope.ui.base.adapter.BaseAdapter
import com.bangkit.team18.qhope.ui.base.adapter.BaseDiffCallback
import com.bumptech.glide.Glide

class HomeAdapter(private val hospitalItemCallback: HomeHospitalItemCallback) :
  BaseAdapter<Hospital, LayoutHospitalItemBinding>(diffCallback) {

  companion object {
    private val diffCallback = object : BaseDiffCallback<Hospital>() {
      override fun contentEquality(oldItem: Hospital, newItem: Hospital): Boolean {
        return oldItem == newItem
      }
    }
  }

  override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutHospitalItemBinding
    get() = LayoutHospitalItemBinding::inflate

  override fun getViewHolder(binding: LayoutHospitalItemBinding): BaseViewHolder {
    return HomeViewHolder(binding)
  }

  inner class HomeViewHolder(binding: LayoutHospitalItemBinding) : BaseViewHolder(binding) {

    override fun bind(data: Hospital) {
      binding.apply {
        root.setOnClickListener {
          hospitalItemCallback.onClickListener(data.id)
        }

        Glide.with(root.context)
          .load(data.image)
          .placeholder(R.drawable.drawable_hospital_placeholder)
          .into(imageViewHospitalItem)

        textViewHospitalItemName.text = data.name
        textViewHospitalItemAddress.text = data.address
        textViewHospitalItemType.text = data.type

        val roomAvailable = data.availableRoomCount.orZero()
        val isRoomAvailable = isRoomAvailable(roomAvailable)

        chipHospitalItemRoomAvailable.showOrRemove(isRoomAvailable)
        chipHospitalItemRoomNotAvailable.showOrRemove(isRoomAvailable.not())

        val roomAvailableLabel = if (isRoomAvailable.not()) {
          mContext.getString(R.string.not_available_label)
        } else {
          mContext.resources.getQuantityString(
            R.plurals.room_available_label,
            roomAvailable,
            roomAvailable
          )
        }

        chipHospitalItemRoomNotAvailable.setChipIconResource(
          if (isRoomAvailable) {
            R.drawable.ic_info
          } else {
            R.drawable.ic_not_available
          }
        )

        chipHospitalItemRoomNotAvailable.text = roomAvailableLabel
        chipHospitalItemRoomAvailable.text = roomAvailableLabel
      }
    }

    private fun isRoomAvailable(availableRoomCount: Int) = availableRoomCount > 0
  }
}