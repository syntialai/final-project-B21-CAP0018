package com.bangkit.team18.qhope.ui.login.view

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.text.bold
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentOtpBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class OtpFragment : BottomSheetDialogFragment(), View.OnClickListener {
  companion object {
    fun newInstance(phoneNumber: String, onSubmitListener: (String) -> Unit) =
      OtpFragment().apply {
        this.onSubmitListener = onSubmitListener
        this.phoneNumber = phoneNumber
      }
  }

  private var _binding: FragmentOtpBinding? = null
  private val binding get() = _binding as FragmentOtpBinding

  private lateinit var onSubmitListener: (String) -> Unit
  private lateinit var phoneNumber: String

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FragmentOtpBinding.inflate(inflater, container, false)
    binding.apply {
      otpDetailInformation.text =
        SpannableStringBuilder().append(getString(R.string.otp_sent_information))
          .append(" ").bold {
            append(
              String.format(
                getString(R.string.indonesian_phone_code_pattern),
                phoneNumber
              )
            )
          }
      otpCode1.addTextChangedListener(
        OtpTextWatcher(
          otpCode1,
          otpCode2,
          this@OtpFragment::otpChangedListener
        )
      )
      otpCode2.addTextChangedListener(
        OtpTextWatcher(
          otpCode2,
          otpCode3,
          this@OtpFragment::otpChangedListener
        )
      )
      otpCode3.addTextChangedListener(
        OtpTextWatcher(
          otpCode3,
          otpCode4,
          this@OtpFragment::otpChangedListener
        )
      )
      otpCode4.addTextChangedListener(
        OtpTextWatcher(
          otpCode4,
          otpCode5,
          this@OtpFragment::otpChangedListener
        )
      )
      otpCode5.addTextChangedListener(
        OtpTextWatcher(
          otpCode5,
          otpCode6,
          this@OtpFragment::otpChangedListener
        )
      )
      otpCode6.addTextChangedListener(
        OtpTextWatcher(
          currentEditText = otpCode6,
          onChangeListener = this@OtpFragment::otpChangedListener
        )
      )
    }
    return binding.root
  }

  private fun otpChangedListener() {
    binding.apply {
      val otp =
        otpCode1.text.toString() +
          otpCode2.text.toString() +
          otpCode3.text.toString() +
          otpCode4.text.toString() +
          otpCode5.text.toString() +
          otpCode6.text.toString()

      if (otp.length == 6) {
        submitOtp()
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onClick(v: View?) {
  }

  private fun submitOtp() {
    binding.apply {
      val otp =
        otpCode1.text.toString() +
          otpCode2.text.toString() +
          otpCode3.text.toString() +
          otpCode4.text.toString() +
          otpCode5.text.toString() +
          otpCode6.text.toString()

      onSubmitListener.invoke(otp)
    }
    dismiss()
  }

  private class OtpTextWatcher(
    private val currentEditText: EditText,
    private val nextEditText: EditText? = null,
    private val onChangeListener: () -> Unit
  ) : TextWatcher {
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      if (currentEditText.text.length == 1) {
        nextEditText?.let {
          currentEditText.clearFocus()
          it.requestFocus()
          it.isCursorVisible = true
        }
      }
      onChangeListener.invoke()
    }

    override fun beforeTextChanged(
      s: CharSequence, start: Int, count: Int,
      after: Int
    ) {
    }

    override fun afterTextChanged(s: Editable) {}
  }
}