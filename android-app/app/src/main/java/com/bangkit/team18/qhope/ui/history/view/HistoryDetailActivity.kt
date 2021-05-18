package com.bangkit.team18.qhope.ui.history.view

import android.os.Bundle
import android.view.View
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityHistoryDetailBinding
import com.bangkit.team18.qhope.model.history.HistoryStatus
import com.bangkit.team18.qhope.model.home.Hospital
import com.bangkit.team18.qhope.ui.base.view.BaseActivity
import com.bangkit.team18.qhope.utils.view.DataUtils.getText
import com.bangkit.team18.qhope.utils.view.ViewUtils.loadImage
import com.bangkit.team18.qhope.utils.view.ViewUtils.showOrRemove

class HistoryDetailActivity :
    BaseActivity<ActivityHistoryDetailBinding>(ActivityHistoryDetailBinding::inflate) {

  override fun setupViews(savedInstanceState: Bundle?) {
    binding.apply {
      layoutHistoryDetailBookingData.imageViewBookingDataHospital.setOnClickListener(
          this@HistoryDetailActivity)
      layoutHistoryDetailBookingData.textViewBookingDataHospitalName.setOnClickListener(
          this@HistoryDetailActivity)
      layoutHistoryDetailUserData.cardBookingUserReferralLetter.setOnClickListener(
          this@HistoryDetailActivity)
    }
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        layoutHistoryDetailBookingData.imageViewBookingDataHospital -> goToHospitalDetail()
        layoutHistoryDetailBookingData.textViewBookingDataHospitalName -> goToHospitalDetail()
        layoutHistoryDetailUserData.cardBookingUserReferralLetter -> {
          // TODO: Open or Download file
        }
      }
    }
  }

  private fun goToHospitalDetail() {
    // TODO
  }

  private fun setBookingDataMainInfo(id: String, createdAt: String, status: HistoryStatus?) {
    binding.layoutHistoryDetailBookingData.apply {
      textViewBookingDataId.text = id
      textViewBookingDataDate.text = createdAt
      textViewBookingDataStatus.text = status.getText()
      if (status == HistoryStatus.COMPLETED) {
        textViewBookingDataStatus.setTextColor(getColor(R.color.green_700))
      } else if (status == HistoryStatus.CANCELLED) {
        textViewBookingDataStatus.setTextColor(getColor(R.color.red_700))
      }
    }
  }

  private fun setBookingDataHospitalInfo(hospital: Hospital) {
    binding.layoutHistoryDetailBookingData.apply {
      imageViewBookingDataHospital.loadImage(this@HistoryDetailActivity, hospital.image,
          R.drawable.drawable_hospital_placeholder)
      textViewBookingDataHospitalName.text = hospital.name
      textViewBookingDataHospitalType.text = hospital.type
      textViewBookingDataHospitalAddress.text = hospital.address
    }
  }

  private fun setBookingOtherInfo(checkIn: String, checkOut: String?, cost: String) {
    binding.layoutHistoryDetailBookingData.apply {
      groupBookingDataCheckOut.showOrRemove(checkOut.isNullOrBlank().not())
      textViewBookingDataCheckIn.text = checkIn
      textViewBookingDataCheckOut.text = checkOut
      textViewBookingDataRoomCost.text = cost
    }
  }

  private fun setBookingUserData() {
    // TODO: bind user model
    binding.layoutHistoryDetailUserData.apply {
      textViewBookingUserName.text
      textViewBookingUserAddress.text
      textViewBookingUserBirthDate.text
      textViewBookingUserEmail.text
      textViewBookingUserGender.text
      textViewBookingUserNoKtp.text
      textViewBookingUserPhoneNumber.text
    }
  }

  private fun setBookingReferralLetterData(fileType: Int, fileName: String) {
    binding.layoutHistoryDetailUserData.cardBookingUserReferralLetter.apply {
      setImage(fileType)
      setFileName(fileName)
    }
  }

  private fun setLoadingState(isLoading: Boolean) {
    with(binding) {
      spinKitLoadHistoryDetail.showOrRemove(isLoading)
      layoutHistoryDetailBookingData.root.showOrRemove(isLoading.not())
      layoutHistoryDetailUserData.root.showOrRemove(isLoading.not())
    }
  }
}