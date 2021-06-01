package com.bangkit.team18.qhope.ui.history.view

import android.os.Bundle
import android.view.View
import com.bangkit.team18.core.domain.model.history.HistoryStatus
import com.bangkit.team18.core.utils.view.DataUtils.getText
import com.bangkit.team18.core.utils.view.ViewUtils.loadImageFromStorage
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.ActivityHistoryDetailBinding
import com.bangkit.team18.qhope.ui.base.view.BaseActivityViewModel
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryDetailViewModel
import com.bangkit.team18.qhope.utils.Router

class HistoryDetailActivity :
  BaseActivityViewModel<ActivityHistoryDetailBinding, HistoryDetailViewModel>(
    ActivityHistoryDetailBinding::inflate, HistoryDetailViewModel::class
  ) {

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

  override fun setupObserver() {
    super.setupObserver()

    viewModel.initializeHistoryId(intent?.getStringExtra(Router.PARAM_HISTORY_ID))
    viewModel.fetchUserBookingHistory()
    viewModel.bookingHistory.observe(this, {
      it?.let { historyDetail ->
        setBookingDataMainInfo(historyDetail.id, historyDetail.startDate, historyDetail.status)
        setBookingDataHospitalInfo(
          historyDetail.hospitalImagePath, historyDetail.hospitalName,
          historyDetail.hospitalAddress, historyDetail.hospitalType
        )
        setBookingOtherInfo(
          historyDetail.startDate, historyDetail.endDate,
          historyDetail.roomCostPerDay
        )
        setBookingReferralLetterData(
          historyDetail.referralLetterFileName,
          historyDetail.referralLetterFileUrl
        )
      }
    })
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

  override fun showLoadingState(isLoading: Boolean) {
    with(binding) {
      spinKitLoadHistoryDetail.showOrRemove(isLoading)
      layoutHistoryDetailBookingData.root.showOrRemove(isLoading.not())
      layoutHistoryDetailUserData.root.showOrRemove(isLoading.not())
    }
  }

  private fun goToHospitalDetail() {
    viewModel.bookingHistory.value?.hospitalId?.let { id ->
      // TODO: Change this
    }
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

  private fun setBookingDataHospitalInfo(hospitalImage: String, hospitalName: String,
      hospitalType: String, hospitalAddress: String) {
    binding.layoutHistoryDetailBookingData.apply {
      imageViewBookingDataHospital.loadImageFromStorage(this@HistoryDetailActivity, hospitalImage,
          R.drawable.drawable_hospital_placeholder)
      textViewBookingDataHospitalName.text = hospitalName
      textViewBookingDataHospitalType.text = hospitalType
      textViewBookingDataHospitalAddress.text = hospitalAddress
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

  private fun setBookingReferralLetterData(fileName: String, filePath: String) {
    binding.layoutHistoryDetailUserData.cardBookingUserReferralLetter.setFileName(fileName)
  }
}