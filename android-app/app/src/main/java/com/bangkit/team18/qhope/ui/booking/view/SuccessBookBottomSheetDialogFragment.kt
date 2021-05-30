package com.bangkit.team18.qhope.ui.booking.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bangkit.team18.qhope.databinding.FragmentSuccessBookBottomSheetDialogBinding
import com.bangkit.team18.qhope.ui.main.adapter.MainAdapter
import com.bangkit.team18.qhope.utils.Router
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SuccessBookBottomSheetDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

  companion object {
    const val OPEN_SUCCESS_BOOK_BOTTOM_SHEET = "OPEN_SUCCESS_BOOK_BOTTOM_SHEET"

    fun newInstance() = SuccessBookBottomSheetDialogFragment()
  }

  private var _binding: FragmentSuccessBookBottomSheetDialogBinding? = null
  private val binding: FragmentSuccessBookBottomSheetDialogBinding
    get() = _binding as FragmentSuccessBookBottomSheetDialogBinding

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
          Router.goToMain(requireContext())
          dismiss()
        }
        buttonGoToHistory -> {
          Router.goToMain(requireContext(), MainAdapter.HISTORY_FRAGMENT_INDEX)
          dismiss()
        }
      }
    }
  }
}