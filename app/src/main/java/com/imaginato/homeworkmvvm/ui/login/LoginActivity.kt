package com.imaginato.homeworkmvvm.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
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
            if (viewModel.isDataValid(binding.editUserName.text.toString(),binding.editPass.text.toString())){
                viewModel.doLogin(binding.editUserName.text.toString(),
                    binding.editPass.text.toString())
            }
        }
    }

    private fun initObserve() {
        viewModel.uiMessage.observe(this) {
            if (it != -1)
                Toast.makeText(this,resources.getString(it),Toast.LENGTH_SHORT).show()
        }

        viewModel.resultLiveData.observe(this){
            Toast.makeText(this,it.errorMessage,Toast.LENGTH_SHORT).show()
        }
    }
}