package com.example.dropspot.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dropspot.R
import com.example.dropspot.databinding.FragmentLoginBinding
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(), Validator.ValidationListener {

    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var button_register: Button
    private lateinit var button_login: Button
    private lateinit var progressBar_loading: ProgressBar


    @NotEmpty(message = "Email or username is required")
    private lateinit var input_email: EditText

    @NotEmpty(message = "Password is required")
    private lateinit var input_password: EditText

    private val validator = Validator(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        validator.setValidationListener(this)
        setupBinding(inflater, container)
        setupListeners()
        return binding.root
    }

    private fun setupBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.vm = authViewModel
        binding.lifecycleOwner = this
        input_email = binding.inputEmail
        input_password = binding.inputPassword
        button_login = binding.buttonLogin
        button_register = binding.buttonRegister
        progressBar_loading = binding.progressBarLoading
    }

    private fun setupListeners() {
        button_login.setOnClickListener {
            validator.validate()
        }

        button_register.setOnClickListener {
            //nav to register
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        input_password.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    validator.validate()
                }
            }
            false
        }
    }

    private fun login() {
        authViewModel.login(
            input_email.text.toString().trim()
            , input_password.text.toString().trim()
        )
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error: ValidationError in errors!!.iterator()) {
            var view: View = error.view
            val message: String = error.getCollatedErrorMessage(this.requireContext())
            if (view is EditText) {
                view.error = message
            } else {
                Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onValidationSucceeded() {
        login()
    }


}