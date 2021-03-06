package com.teammealky.mealky.presentation.commons.presenter

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.teammealky.mealky.R
import timber.log.Timber
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager
import com.teammealky.mealky.domain.model.APIError

abstract class BaseFragment<P : Presenter<V>, in V, VM : BaseViewModel<P>> : Fragment(), BaseUI {

    @Inject open lateinit var vmFactory: ViewModelProvider.Factory
    abstract val vmClass: Class<VM>

    protected var presenter: P? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vm = ViewModelProviders.of(this, vmFactory).get(vmClass)
        presenter = vm.presenter
    }

    @Suppress("UNCHECKED_CAST")
    private fun getPresenterView(): V = this as V

    override fun onResume() {
        super.onResume()
        presenter?.attach(getPresenterView())
    }

    override fun onPause() {
        presenter?.detach()
        try {
            alertDialog?.dismiss()
        } catch (ignored: Exception) {

        }
        super.onPause()
    }

    override fun showErrorMessage(retry: () -> Unit, e: Throwable, cancelable: Boolean) {
        Timber.e("KUBA_LOG Method:showErrorMessage ***** $e *****")
        val message = if(e is APIError) e.message else getString(R.string.service_unavailable)
        alertDialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.just_a_moment)
                .setMessage(message)
                .setPositiveButton(R.string.retry) { _, _ ->
                    try {
                        retry.invoke()
                    } catch (ignored: Exception) {
                    }
                }
                .setNegativeButton(R.string.exit) { _, _ -> activity?.finish() }
                .setCancelable(cancelable)
                .show()
    }

    override fun hideKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        this.activity?.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
    }
}