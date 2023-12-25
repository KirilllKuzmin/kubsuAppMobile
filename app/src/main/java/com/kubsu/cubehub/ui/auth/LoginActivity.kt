package com.kubsu.cubehub.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.kubsu.cubehub.ui.main.MainActivity
import com.kubsu.cubehub.R
import com.kubsu.cubehub.common.User
import com.kubsu.cubehub.common.auth.LoginFormState
import com.kubsu.cubehub.utils.Constants.CIPHERTEXT_WRAPPER
import com.kubsu.cubehub.utils.Constants.SHARED_PREFS_FILENAME
import com.kubsu.cubehub.databinding.ActivityLoginBinding
import com.kubsu.cubehub.security.CryptographyManagerImpl
import com.kubsu.cubehub.utils.BiometricPromptUtils

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManagerImpl()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )
    private lateinit var binding: ActivityLoginBinding
    private val loginWithPasswordViewModel by viewModels<LoginWithPasswordViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        User.init(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.useBiometrics.setOnClickListener {
            if (ciphertextWrapper != null) {
                showBiometricPromptForDecryption()
            } else {
                startActivity(Intent(this, EnableBiometricLoginActivity::class.java))
            }
        }
        setupForLoginWithPassword()
        if (User.username != null) {
            binding.username.setText(User.username)
        }
    }

    /**
     * The logic is kept inside onResume instead of onCreate so that authorizing biometrics takes
     * immediate effect.
     */
    override fun onResume() {
        super.onResume()

        if (ciphertextWrapper != null) {
//            if (SampleAppUser.password == null) {
//                showBiometricPromptForDecryption()
//            }
            showBiometricPromptForDecryption()
        }
    }

    // BIOMETRICS SECTION

    private fun showBiometricPromptForDecryption() {
        ciphertextWrapper?.let { textWrapper ->
            val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
            if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                val secretKeyName = getString(R.string.secret_key_name)
                val cipher = cryptographyManager.getInitializedCipherForDecryption(
                    secretKeyName, textWrapper.initializationVector
                )
                biometricPrompt =
                    BiometricPromptUtils.createBiometricPrompt(
                        this,
                        ::decryptServerTokenFromStorage
                    )
                val promptInfo = BiometricPromptUtils.createPromptInfo(this)
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
        }
    }

    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val plaintext =
                    cryptographyManager.decryptData(textWrapper.ciphertext, it)
                User.password = plaintext

                loginWithPasswordViewModel.login (
                    User.username.toString(),
                    User.password.toString()
                )

                loginWithPasswordViewModel.loginResult.observe(this, Observer {
                    val loginResult = it ?: return@Observer
                    if (loginResult.success) {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        applicationContext.startActivity(intent)
                    }
                })

            }
        }
    }

    // USERNAME + PASSWORD SECTION

    private fun setupForLoginWithPassword() {
        loginWithPasswordViewModel.loginWithPasswordFormState.observe(this, Observer { formState ->
            val loginState = formState ?: return@Observer
            when (loginState) {
                is LoginFormState.SuccessfulLoginFormState -> binding.login.isEnabled = loginState.isDataValid
                is LoginFormState.FailedLoginFormState -> {
                    loginState.usernameError?.let { binding.username.error = getString(it) }
                    loginState.passwordError?.let { binding.password.error = getString(it) }
                }
            }
        })
        loginWithPasswordViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer
            if (loginResult.success) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                applicationContext.startActivity(intent)
            }
        })
        binding.username.doAfterTextChanged {
            loginWithPasswordViewModel.onLoginDataChanged (
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.password.doAfterTextChanged {
            loginWithPasswordViewModel.onLoginDataChanged (
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.password.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    loginWithPasswordViewModel.login (
                        binding.username.text.toString(),
                        binding.password.text.toString()
                    )
            }
            false
        }
        binding.login.setOnClickListener {
            loginWithPasswordViewModel.login (
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
    }

    private fun updateApp(successMsg: String) {
        binding.success.text = successMsg
    }
}