package com.example.dropspot.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dropspot.databinding.FragmentRegisterBinding
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

    @NotEmpty(message = "Firstname is required")
    @Length(max = 50)
    private lateinit var input_firstName: EditText

    @NotEmpty(message = "Lastname is required")
    @Length(max = 50)
    private lateinit var input_lastName: EditText

    @NotEmpty(message = "Username is required")
    @Length(min = 5, max = 35, message = "Username must have between {min} and {max} characters.")
    private lateinit var input_username: EditText

    @Email(message = "Must be an email address")
    @NotEmpty(message = "Email is required")
    @Length(max = 100, message = "Email max length {max}.")
    private lateinit var input_email: EditText

    @NotEmpty(message = "Password is required")
    @Password(min = 6)
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
        input_passwordConfirm = binding.inputPasswordconfirm
        button_register = binding.buttonRegister
        progressBar_loading = binding.progressBarLoading
    }


    private fun setupListeners() {
        button_register.setOnClickListener {
            validator.validate()
        }
    }

    private fun register() {
        authViewModel.register(
            input_firstname.text.toString()
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