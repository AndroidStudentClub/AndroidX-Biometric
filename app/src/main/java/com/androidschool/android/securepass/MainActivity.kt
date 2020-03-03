package com.androidschool.android.securepass

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val executor = ContextCompat.getMainExecutor(this)
    val biometricManager = BiometricManager.from(this)

    when (biometricManager.canAuthenticate()) {
      BiometricManager.BIOMETRIC_SUCCESS ->
        authUser(executor)
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
        Toast.makeText(
            this,
            getString(R.string.error_msg_no_biometric_hardware),
            Toast.LENGTH_LONG
        ).show()
      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
        Toast.makeText(
            this,
            getString(R.string.error_msg_biometric_hw_unavailable),
            Toast.LENGTH_LONG
        ).show()
      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
        Toast.makeText(
            this,
            getString(R.string.error_msg_biometric_not_setup),
            Toast.LENGTH_LONG
        ).show()
    }


    val sharedPreferences: SharedPreferences =
        getSharedPreferences("user_data", Context.MODE_PRIVATE)
    btnSave.setOnClickListener {
      //1
      val editor = sharedPreferences.edit()
      //2
      editor.putString("Name", etName.text.toString())
      editor.putString("Email", etEmail.text.toString())
      editor.putString("Phone", etPhone.text.toString())
      //3
      editor.apply()
      //4
      etName.setText("")
      etEmail.setText("")
      etPhone.setText("")
    }

    etName.setText(sharedPreferences.getString("Name", ""))
    etEmail.setText(sharedPreferences.getString("Email", ""))
    etPhone.setText(sharedPreferences.getString("Phone", ""))
  }

  // TODO: define authUser
  private fun authUser(executor: Executor) {
    // 1
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        // 2
        .setTitle(getString(R.string.auth_title))
        // 3
        .setSubtitle(getString(R.string.auth_subtitle))
        // 4
        .setDescription(getString(R.string.auth_description))
        // 5
        .setDeviceCredentialAllowed(true)
        // 6
        .build()

    // 1
    val biometricPrompt = BiometricPrompt(this, executor,
        object : BiometricPrompt.AuthenticationCallback() {
          // 2
          override fun onAuthenticationSucceeded(
              result: BiometricPrompt.AuthenticationResult
          ) {
            super.onAuthenticationSucceeded(result)
            main_layout.visibility = View.VISIBLE
          }

          // 3
          override fun onAuthenticationError(
              errorCode: Int, errString: CharSequence
          ) {
            super.onAuthenticationError(errorCode, errString)
            Toast.makeText(
                applicationContext,
                getString(R.string.error_msg_auth_error, errString),
                Toast.LENGTH_SHORT
            ).show()
          }

          // 4
          override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Toast.makeText(applicationContext,
                getString(R.string.error_msg_auth_failed),
                Toast.LENGTH_SHORT
            ).show()
          }
        })

    biometricPrompt.authenticate(promptInfo)
  }
}
