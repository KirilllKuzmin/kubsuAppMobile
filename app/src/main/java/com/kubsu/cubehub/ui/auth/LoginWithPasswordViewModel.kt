package com.kubsu.cubehub.ui.auth

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kubsu.cubehub.R
import com.kubsu.cubehub.common.auth.AuthRequest
import com.kubsu.cubehub.common.User
import com.kubsu.cubehub.common.auth.LoginFormState
import com.kubsu.cubehub.common.auth.LoginResult
import com.kubsu.cubehub.data.network.UserService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginWithPasswordViewModel : ViewModel() {

    private val TAG = "LoginWithPasswordViewModel"

    private lateinit var userService : UserService

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginWithPasswordFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun onLoginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value =
                LoginFormState.FailedLoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid()) {
            _loginForm.value =
                LoginFormState.FailedLoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState.SuccessfulLoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(): Boolean {
        return true
    }

    private fun initRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/").client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UserService::class.java)
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                initRetrofit()
                val response = userService.authGetToken(AuthRequest(username, password))
                val user = response.body()

                if (user != null && response.code() == 200) {
                    User.username = username
                    User.password = password
                    User.token = user.token
                    Log.i(TAG, "${user.username}, ${user.token}")
                    _loginResult.value = LoginResult(true, user.token)
                }


                _loginResult.value = LoginResult(false)
            } catch (e: Exception) {
                _loginResult.value = LoginResult(false)
            }
        }
    }
}