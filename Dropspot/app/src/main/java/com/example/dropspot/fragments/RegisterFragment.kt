package com.example.dropspot.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.dropspot.AuthActivity
import com.example.dropspot.databinding.FragmentRegisterBinding
import com.example.dropspot.utils.InputLayoutTextWatcher
import com.example.dropspot.utils.MyValidationListener
import com.example.dropspot.viewmodels.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private val registerViewModel: RegisterViewModel by viewModel()
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

    @NotEmpty(message = "Password confirmation is required")
    @ConfirmPassword
    private lateinit var input_passwordConfirm: EditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.vm = registerViewModel
        binding.lifecycleOwner = this
        input_firstName = binding.inputFirstname
        input_firstName.addTextChangedListener(InputLayoutTextWatcher(binding.fieldFirstname))
        input_lastName = binding.inputLastname
        input_lastName.addTextChangedListener(InputLayoutTextWatcher(binding.fieldLastname))
        input_email = binding.inputEmail
        input_email.addTextChangedListener(InputLayoutTextWatcher(binding.fieldEmail))
        input_username = binding.inputUsername
        input_username.addTextChangedListener(InputLayoutTextWatcher(binding.fieldUsername))
        input_password = binding.inputPassword
        input_password.addTextChangedListener(InputLayoutTextWatcher(binding.fieldPassword))
        input_passwordConfirm = binding.inputPasswordConfirm
        input_passwordConfirm.addTextChangedListener(InputLayoutTextWatcher(binding.fieldPasswordConfirm))
        button_register = binding.buttonRegister

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListenersObservers()
        validator.setValidationListener(object :
            MyValidationListener(this.requireContext(), this.requireView()) {
            override fun onValidationSucceeded() {
                register()
            }

        })
    }

    private fun setupListenersObservers() {
        button_register.setOnClickListener {
            validator.validate()
        }

        input_passwordConfirm.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    validator.validate()
                    (this.activity as AuthActivity).hideKeyboard(this.requireView())
                }
            }
            false
        }

        registerViewModel.registerResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                navigateToLogin()
            } else {
                Snackbar.make(this.requireView(), it.message, Snackbar.LENGTH_SHORT).show()
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
        registerViewModel.register(
            input_firstname.text.toString().trim()
            , input_lastname.text.toString().trim()
            , input_username.text.toString().trim()
            , input_email.text.toString().trim()
            , input_password.text.toString().trim()
        )
    }
}