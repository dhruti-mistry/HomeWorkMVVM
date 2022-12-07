package com.imaginato.homeworkmvvm.ui.base

import android.app.AlertDialog
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.imaginato.homeworkmvvm.R

open class BaseActivity: AppCompatActivity() {

    private lateinit var mViewModel: BaseViewModel

    protected fun setupViewModel(viewModel: BaseViewModel) {
        this.mViewModel = viewModel
        mViewModel.mError.observe(this) {
            Toast.makeText(this, it ?: "Something went wrong", Toast.LENGTH_LONG).show()
        }
        mViewModel.isUnauthorized.observe(this) {
            if (it == true) {
                // clear shared pref
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage("Unauthorized")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }.setCancelable(false).show()
            }
        }
    }

}