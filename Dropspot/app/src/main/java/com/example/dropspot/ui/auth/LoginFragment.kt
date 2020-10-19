package com.example.dropspot.ui.auth

import android.content.Intent
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
import androidx.navigation.fragment.navArgs
import com.example.dropspot.MainActivity
import com.example.dropspot.data.model.dto.responses.JwtResponse
import com.example.dropspot.databinding.FragmentLoginBinding
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(), Validator.ValidationListener {

    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding
    private val validator = Validator(this)
    private val args: LoginFragmentArgs by navArgs()

    // UI components
    private lateinit var button_register: Button
    private lateinit var button_login: Button
    private lateinit var progressBar_loading: ProgressBar

    @NotEmpty(message = "Email or username is required")
    private lateinit var input_email: EditText

    @NotEmpty(message = "Password is required")
    private lateinit var input_password: EditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        validator.setValidationListener(this)
        setupBinding(inflater, container)
        setupListeners()
        setupUI()
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
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        input_password.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    validator.validate()
                }
            }
            false
        }

        authViewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                startMainActivity(it)
            } else {
                Toast.makeText(this.requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupUI() {
        // handling successful registration
        if (args.emailOrUsername.isNotBlank() && args.password.isNotBlank()) {
            binding.inputEmail.setText(args.emailOrUsername)
            binding.inputPassword.setText(args.password)
        }
    }


    private fun startMainActivity(it: JwtResponse) {
        val intent = Intent(this.context, MainActivity::class.java)
        intent.putExtra("TOKEN", it.token)
        intent.putExtra("ID", it.id)
        intent.putExtra("EMAIL", it.email)
        intent.putExtra("USERNAME", it.username)
        intent.putExtra("PASSWORD", binding.inputPassword.text)
        startActivity(intent)
    }

    private fun login() {
        authViewModel.login(
            input_email.text.toString().trim()
            , input_password.text.toString().trim()
        )
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        for (error: ValidationError in errors!!.iterator()) {
            val view: View = error.view
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