package com.bangkit.team18.qhope.ui.main.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.team18.qhope.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
  }
}