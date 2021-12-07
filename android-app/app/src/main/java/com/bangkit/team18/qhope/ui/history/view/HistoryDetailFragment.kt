package com.bangkit.team18.qhope.ui.history.view

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bangkit.team18.core.domain.model.history.HistoryStatus
import com.bangkit.team18.core.domain.model.history.UserHistory
import com.bangkit.team18.core.utils.view.DataUtils.getText
import com.bangkit.team18.core.utils.view.ViewUtils.showOrRemove
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentHistoryDetailBinding
import com.bangkit.team18.qhope.ui.base.view.BaseFragment
import com.bangkit.team18.qhope.ui.history.viewmodel.HistoryDetailViewModel
import com.bangkit.team18.qhope.ui.home.view.HomeFragmentDirections
import com.bangkit.team18.qhope.ui.payment.MidtransPayment
import com.bangkit.team18.qhope.ui.payment.PaymentStatusListener
import com.bangkit.team18.qhope.ui.widget.callback.OnBannerActionButtonClickListener
import com.bangkit.team18.qhope.utils.Router
import com.bumptech.glide.Glide

class HistoryDetailFragment :
  BaseFragment<FragmentHistoryDetailBinding, HistoryDetailViewModel>(
    FragmentHistoryDetailBinding::inflate, HistoryDetailViewModel::class
  ), OnBannerActionButtonClickListener, PaymentStatusListener {

  private val args: HistoryDetailFragmentArgs by navArgs()

  private var midtransPayment: MidtransPayment? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    midtransPayment = MidtransPayment(context, this)
  }

  override fun setupViews() {
    binding.apply {
      layoutHistoryDetailBookingData.imageViewBookingDataHospital.setOnClickListener(
        this@HistoryDetailFragment
      )
      layoutHistoryDetailBookingData.textViewBookingDataHospitalName.setOnClickListener(
        this@HistoryDetailFragment
      )
      bannerInfoContinuePayment.setActionButtonOnClickListener(this@HistoryDetailFragment)
    }
  }

  override fun setupObserver() {
    super.setupObserver()

    viewModel.initializeHistoryId(args.id)
    viewModel.fetchUserBookingHistory()
    viewModel.bookingHistory.observe(this, {
      it?.let { historyDetail ->
        setBookingDataMainInfo(historyDetail.id, historyDetail.bookedAt, historyDetail.status)
        setBookingDataHospitalInfo(
          historyDetail.hospitalImagePath, historyDetail.hospitalName,
          historyDetail.hospitalType, historyDetail.hospitalAddress
        )
        setBookingOtherInfo(
          historyDetail.startDate, historyDetail.endDate,
          historyDetail.roomType.name, historyDetail.roomCostPerDay
        )
        setBookingReferralLetterData(
          historyDetail.referralLetterFileName,
          historyDetail.referralLetterFileUrl
        )
        setBookingUserData(historyDetail.user)
        toggleContinuePaymentBanner(true)
      }
    })
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        layoutHistoryDetailBookingData.imageViewBookingDataHospital,
        layoutHistoryDetailBookingData.textViewBookingDataHospitalName -> goToHospitalDetail()
      }
    }
  }

  override fun onBannerButtonClicked() {
    midtransPayment?.setupPayments()
  }

  override fun onPaymentSuccess(transactionId: String) {
    viewModel.fetchUserBookingHistory()
  }

  override fun onPaymentPending(transactionId: String) {
    // TODO
  }

  override fun onPaymentFailed(statusMessage: String) {
    showErrorToast(statusMessage, R.string.unknown_error_message)
  }

  override fun onPaymentCancelled() {
    showErrorToast(null, R.string.payment_cancelled_message)
    goToHome()
  }

  override fun showLoadingState(isLoading: Boolean) {
    with(binding) {
      spinKitLoadHistoryDetail.showOrRemove(isLoading)
      layoutHistoryDetailBookingData.root.showOrRemove(isLoading.not())
      layoutHistoryDetailUserData.root.showOrRemove(isLoading.not())
    }
  }

  private fun goToHome() {
    findNavController().navigate(HomeFragmentDirections.actionGlobalHomeFragment())
  }

  private fun goToHospitalDetail() {
    viewModel.bookingHistory.value?.hospitalId?.let { id ->
      findNavController().navigate(
        HistoryDetailFragmentDirections.actionHistoryDetailFragmentToHospitalDetailFragment(
          id
        )
      )
    }
  }

  private fun setBookingDataMainInfo(id: String, createdAt: String, status: HistoryStatus?) {
    binding.layoutHistoryDetailBookingData.apply {
      textViewBookingDataId.text = id
      textViewBookingDataDate.text = createdAt
      textViewBookingDataStatus.text = status.getText()

      if (status == HistoryStatus.COMPLETED) {
        textViewBookingDataStatus.setTextColor(getColor(mContext, R.color.green_700))
      } else if (status == HistoryStatus.CANCELLED) {
        textViewBookingDataStatus.setTextColor(getColor(mContext, R.color.red_700))
      }
    }
  }

  private fun setBookingDataHospitalInfo(
    hospitalImage: String, hospitalName: String,
    hospitalType: String, hospitalAddress: String
  ) {
    binding.layoutHistoryDetailBookingData.apply {
      Glide.with(mContext)
        .load(hospitalImage)
        .placeholder(R.drawable.drawable_hospital_placeholder)
        .into(imageViewBookingDataHospital)

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
      setOnClick {
        Router.openPdfFile(mContext, fileUrl)
      }
    }
  }

  private fun toggleContinuePaymentBanner(show: Boolean) {
    binding.bannerInfoContinuePayment.showOrRemove(show)
  }
}