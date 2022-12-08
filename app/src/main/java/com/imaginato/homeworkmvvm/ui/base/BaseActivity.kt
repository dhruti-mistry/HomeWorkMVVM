package com.imaginato.homeworkmvvm.ui.base

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.imaginato.homeworkmvvm.R

open class BaseActivity: AppCompatActivity() {

    private lateinit var mViewModel: BaseViewModel
    private lateinit var mProgress: ProgressDialog
    private lateinit var mActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = this

        mProgress = ProgressDialog(mActivity)
        mProgress.setCancelable(false)
    }

    protected fun setupViewModel(viewModel: BaseViewModel) {
        this.mViewModel = viewModel
        mViewModel.mProgress.observe(this) {
            if (it.isNullOrBlank()) {
                mProgress.dismiss()
            } else {
                mProgress.setMessage(it)
                mProgress.show()
            }
        }
        mViewModel.mError.observe(this) {
            Toast.makeText(this, it ?: resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
        }
        mViewModel.isUnauthorized.observe(this) {
            if (it == true) {
                // clear shared pref
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(resources.getString(R.string.unauthorized))
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                    }.setCancelable(false).show()
            }
        }
    }

}