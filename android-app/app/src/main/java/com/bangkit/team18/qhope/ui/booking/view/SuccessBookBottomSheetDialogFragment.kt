package com.bangkit.team18.qhope.ui.booking.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.team18.qhope.databinding.FragmentSuccessBookBottomSheetDialogBinding
import com.bangkit.team18.qhope.ui.booking.callback.RouteToCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SuccessBookBottomSheetDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

  companion object {
    const val OPEN_SUCCESS_BOOK_BOTTOM_SHEET = "OPEN_SUCCESS_BOOK_BOTTOM_SHEET"

    fun newInstance(routeToCallback: RouteToCallback) =
      SuccessBookBottomSheetDialogFragment().apply {
        this.routeToCallback = routeToCallback
      }
  }

  private var _binding: FragmentSuccessBookBottomSheetDialogBinding? = null
  private val binding: FragmentSuccessBookBottomSheetDialogBinding
    get() = _binding as FragmentSuccessBookBottomSheetDialogBinding

  private lateinit var routeToCallback: RouteToCallback

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentSuccessBookBottomSheetDialogBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.apply {
      buttonBackToHome.setOnClickListener(this@SuccessBookBottomSheetDialogFragment)
      buttonGoToHistory.setOnClickListener(this@SuccessBookBottomSheetDialogFragment)
    }
    (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
  }

  override fun onClick(view: View?) {
    with(binding) {
      when (view) {
        buttonBackToHome -> {
          routeToCallback.goToHome()
          dismiss()
        }
        buttonGoToHistory -> {
          routeToCallback.goToHistory()
          dismiss()
        }
      }
    }
  }
}