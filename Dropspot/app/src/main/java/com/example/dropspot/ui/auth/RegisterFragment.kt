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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.dropspot.databinding.FragmentRegisterBinding
import com.example.dropspot.viewmodels.AuthViewModel
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment(), Validator.ValidationListener {

    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var button_register: Button
    private lateinit var progressBar_loading: ProgressBar
    private val validator = Validator(this)

    @NotEmpty(message = "First name is required")
    @Length(max = 50, message = "First name max length 50")
    private lateinit var input_firstName: EditText

    @NotEmpty(message = "Last name is required")
    @Length(max = 50, message = "Last name max length 50")
    private lateinit var input_lastName: EditText

    @NotEmpty(message = "Username is required")
    @Length(min = 5, max = 35, message = "Username must have between 5 and 35 characters.")
    private lateinit var input_username: EditText

    @Email(message = "Must be an email address")
    @NotEmpty(message = "Email is required")
    @Length(max = 100, message = "Email max length 100.")
    private lateinit var input_email: EditText

    @NotEmpty(message = "Password is required")
    @Password(min = 6, message = "password min length 6")
    private lateinit var input_password: EditText

    @ConfirmPassword
    private lateinit var input_passwordConfirm: EditText


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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.vm = authViewModel
        binding.lifecycleOwner = this
        input_firstName = binding.inputFirstname
        input_lastName = binding.inputLastname
        input_email = binding.inputEmail
        input_username = binding.inputUsername
        input_password = binding.inputPassword
        input_passwordConfirm = binding.inputPasswordConfirm
        button_register = binding.buttonRegister
        progressBar_loading = binding.progressBarLoading
    }


    private fun setupListeners() {
        button_register.setOnClickListener {
            validator.validate()
        }

        input_passwordConfirm.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    validator.validate()
                }
            }
            false
        }

        authViewModel.registerResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                navigateToLogin()
            } else {
                Toast.makeText(this.requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.spinner.observe(viewLifecycleOwner, Observer {
            if (it) {
                progressBar_loading.visibility = View.VISIBLE
            } else {
                progressBar_loading.visibility = View.GONE
            }
        })

    }

    private fun navigateToLogin() {
        findNavController().navigate(
            RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(
                input_username.text.toString().trim(), input_password.text.toString().trim()
            )
        )
    }

    private fun register() {
        authViewModel.register(
            input_firstname.text.toString().trim()
            , input_lastname.text.toString().trim()
            , input_username.text.toString().trim()
            , input_email.text.toString().trim()
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
        register()
    }
}