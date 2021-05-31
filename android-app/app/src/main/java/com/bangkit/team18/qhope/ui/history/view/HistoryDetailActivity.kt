package com.bangkit.team18.qhope.ui.history.view

import android.os.Bundle
import android.view.View
import com.bangkit.team18.core.domain.model.history.HistoryStatus
import com.bangkit.team18.core.domain.model.history.UserHistory
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
        this@HistoryDetailActivity
      )
      layoutHistoryDetailBookingData.textViewBookingDataHospitalName.setOnClickListener(
        this@HistoryDetailActivity
      )
      actionBar?.title = intent?.getStringExtra(Router.PARAM_HISTORY_ID)
    }
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.initializeHistoryId(intent?.getStringExtra(Router.PARAM_HISTORY_ID))
    viewModel.fetchUserBookingHistory()
    viewModel.bookingHistory.observe(this, {
      it?.let { historyDetail ->
        setBookingDataMainInfo(historyDetail.id, historyDetail.bookedAt, historyDetail.status)
        setBookingDataHospitalInfo(
          historyDetail.hospitalImagePath, historyDetail.hospitalName,
          historyDetail.hospitalAddress, historyDetail.hospitalType
        )
        setBookingOtherInfo(
          historyDetail.startDate, historyDetail.endDate,
          historyDetail.roomType, historyDetail.roomCostPerDay
        )
        setBookingReferralLetterData(
          historyDetail.referralLetterFileName,
          historyDetail.referralLetterFileUrl
        )
        setBookingUserData(historyDetail.user)
      }
    })
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        layoutHistoryDetailBookingData.imageViewBookingDataHospital -> goToHospitalDetail()
        layoutHistoryDetailBookingData.textViewBookingDataHospitalName -> goToHospitalDetail()
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
      Router.goToHospitalDetails(this, id)
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

  private fun setBookingDataHospitalInfo(
    hospitalImage: String, hospitalName: String,
    hospitalType: String, hospitalAddress: String
  ) {
    binding.layoutHistoryDetailBookingData.apply {
      imageViewBookingDataHospital.loadImageFromStorage(
        this@HistoryDetailActivity, hospitalImage,
        R.drawable.drawable_hospital_placeholder
      )
      textViewBookingDataHospitalName.text = hospitalName
      textViewBookingDataHospitalType.text = hospitalType
      textViewBookingDataHospitalAddress.text = hospitalAddress
    }
  }

  private fun setBookingOtherInfo(
    checkIn: String,
    checkOut: String?,
    roomType: String,
    cost: String
  ) {
    binding.layoutHistoryDetailBookingData.apply {
      groupBookingDataCheckOut.showOrRemove(checkOut.isNullOrBlank().not())
      textViewBookingDataCheckIn.text = checkIn
      textViewBookingDataCheckOut.text = checkOut
      textViewBookingDataRoomType.text = roomType
      textViewBookingDataRoomCost.text = cost
    }
  }

  private fun setBookingUserData(user: UserHistory) {
    binding.layoutHistoryDetailUserData.apply {
      textViewBookingUserName.text = user.name
      textViewBookingUserBirthDate.text = user.birthDate
      textViewBookingUserGender.text = user.gender
      textViewBookingUserNoKtp.text = user.ktpNumber
      textViewBookingUserPhoneNumber.text = user.phoneNumber
    }
  }

  private fun setBookingReferralLetterData(fileName: String, fileUrl: String) {
    binding.layoutHistoryDetailUserData.cardBookingUserReferralLetter.apply {
      setFileName(fileName)
      setOnClickListener {
        Router.openPdfFile(this@HistoryDetailActivity, fileUrl)
      }
    }
  }
}