package com.bangkit.team18.qhope.ui.login.view

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.text.bold
import androidx.lifecycle.LiveData
import com.bangkit.team18.qhope.R
import com.bangkit.team18.qhope.databinding.FragmentOtpBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OtpFragment : BottomSheetDialogFragment(), View.OnClickListener {
  companion object {
    fun newInstance(
      phoneNumber: String,
      countDown: LiveData<Int>,
      resendOtp: (String) -> Unit,
      verifyCode: (String) -> Unit,
      loggedInListener: (EditText, EditText, EditText, EditText, EditText, EditText) -> Unit
    ) =
      OtpFragment().apply {
        this.phoneNumber = phoneNumber
        this.countDown = countDown
        this.resendOtp = resendOtp
        this.verifyCode = verifyCode
        this.loggedInListener = loggedInListener
      }
  }

  private var _binding: FragmentOtpBinding? = null
  private val binding get() = _binding as FragmentOtpBinding

  private lateinit var phoneNumber: String
  private lateinit var countDown: LiveData<Int>
  private lateinit var resendOtp: (String) -> Unit
  private lateinit var verifyCode: (String) -> Unit
  private lateinit var loggedInListener: (EditText, EditText, EditText, EditText, EditText, EditText) -> Unit

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FragmentOtpBinding.inflate(inflater, container, false)
    binding.apply {
      countDown.observe(viewLifecycleOwner, {
        if (it > 0) {
          otpResend.isEnabled = false
          otpResend.text = String.format(getString(R.string.resend_otp_count_down), it)
        } else {
          otpResend.text = getString(R.string.resend_otp_label)
          otpResend.isEnabled = true
        }
      })
      otpResend.setOnClickListener(this@OtpFragment)
      otpDetailInformation.text =
        SpannableStringBuilder().append(getString(R.string.otp_sent_information))
          .append(" ").bold {
            append(
              phoneNumber
            )
          }
      loggedInListener.invoke(otpCode1, otpCode2, otpCode3, otpCode4, otpCode5, otpCode6)
    }
    setupOtpTextWatcher()
    return binding.root
  }

  private fun setupOtpTextWatcher() {
    binding.apply {
      otpCode1.addTextChangedListener(
        OtpTextWatcher(
          currentEditText = otpCode1,
          nextEditText = otpCode2,
          onChangeListener = this@OtpFragment::otpChangedListener
        )
      )
      otpCode2.addTextChangedListener(
        OtpTextWatcher(
          otpCode1,
          otpCode2,
          otpCode3,
          this@OtpFragment::otpChangedListener
        )
      )
      otpCode3.addTextChangedListener(
        OtpTextWatcher(
          otpCode2,
          otpCode3,
          otpCode4,
          this@OtpFragment::otpChangedListener
        )
      )
      otpCode4.addTextChangedListener(
        OtpTextWatcher(
          otpCode3,
          otpCode4,
          otpCode5,
          this@OtpFragment::otpChangedListener
        )
      )
      otpCode5.addTextChangedListener(
        OtpTextWatcher(
          otpCode4,
          otpCode5,
          otpCode6,
          this@OtpFragment::otpChangedListener
        )
      )
      otpCode6.addTextChangedListener(
        OtpTextWatcher(
          previousEditText = otpCode5,
          currentEditText = otpCode6,
          onChangeListener = this@OtpFragment::otpChangedListener
        )
      )
    }
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

  private fun submitOtp() {
    binding.apply {
      val otp =
        otpCode1.text.toString() +
          otpCode2.text.toString() +
          otpCode3.text.toString() +
          otpCode4.text.toString() +
          otpCode5.text.toString() +
          otpCode6.text.toString()
      verifyCode(otp)
    }
  }

  private class OtpTextWatcher(
    private val previousEditText: EditText? = null,
    private val currentEditText: EditText,
    private val nextEditText: EditText? = null,
    private val onChangeListener: () -> Unit
  ) : TextWatcher {
    private val currentKeyListener = View.OnKeyListener { _, keyCode, event ->
      if (event.action == KeyEvent.ACTION_DOWN && currentEditText.isFocused && keyCode == KeyEvent.KEYCODE_DEL) {
        if (currentEditText.length() == 0) {
          previousEditText?.let {
            currentEditText.clearFocus()
            it.requestFocus()
            it.isCursorVisible = true
          }
        }
      }
      false
    }

    init {
      currentEditText.setOnKeyListener(currentKeyListener)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
      if (currentEditText.text.length == 1) {
        nextEditText?.let {
          currentEditText.clearFocus()
          it.requestFocus()
          it.setSelection(it.length())
          it.isCursorVisible = true
        }
      }
      onChangeListener.invoke()
    }

    override fun beforeTextChanged(
      s: CharSequence, start: Int, count: Int,
      after: Int
    ) {
      currentEditText.setOnKeyListener(null)
    }

    override fun afterTextChanged(s: Editable) {
      currentEditText.setOnKeyListener(currentKeyListener)
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.otp_resend -> resendOtp.invoke(phoneNumber)
    }
  }
}