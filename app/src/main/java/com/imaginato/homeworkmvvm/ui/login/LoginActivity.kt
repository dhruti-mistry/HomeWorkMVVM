package com.imaginato.homeworkmvvm.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.imaginato.homeworkmvvm.R
import com.imaginato.homeworkmvvm.databinding.ActivityLoginBinding
import com.imaginato.homeworkmvvm.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var viewModel:LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        super.setupViewModel(viewModel)
        initObserve()

        binding.btnLogin.setOnClickListener {
            if (isDataValid(binding.editUserName.text.toString(),binding.editPass.text.toString())){
                viewModel.doLogin(binding.editUserName.text.toString(),
                    binding.editPass.text.toString())
            }
        }

    }

    private fun initObserve() {
        viewModel.progress.observe(this) {
            binding.pbLoading.isVisible = it
        }

        viewModel.resultLiveData.observe(this){

        }
    }

    private fun isDataValid(username: String, password: String):Boolean {
        if (username.isNullOrEmpty()) {
            Toast.makeText(
                this@LoginActivity,
                resources.getString(R.string.add_username),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (password.isNullOrEmpty()) {
            Toast.makeText(
                this@LoginActivity,
                resources.getString(R.string.add_password),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (password.length<5){
            Toast.makeText(
                this@LoginActivity,
                resources.getString(R.string.invalid_password),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }
}