package com.example.dropspot.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.dropspot.AuthActivity
import com.example.dropspot.R
import com.example.dropspot.databinding.FragmentRegisterBinding
import com.example.dropspot.utils.InputLayoutTextWatcher
import com.example.dropspot.utils.MyValidationListener
import com.example.dropspot.utils.Variables
import com.example.dropspot.viewmodels.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private val registerViewModel: RegisterViewModel by viewModel()
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var buttonRegister: Button
    private val validator = Validator(this)

    @NotEmpty(message = "First name is required")
    @Length(max = 50, message = "First name max length 50")
    private lateinit var inputFirstName: EditText

    @NotEmpty(message = "Last name is required")
    @Length(max = 50, message = "Last name max length 50")
    private lateinit var inputLastName: EditText

    @NotEmpty(message = "Username is required")
    @Length(min = 5, max = 35, message = "Username must have between 5 and 35 characters.")
    private lateinit var inputUsername: EditText

    @Email(message = "Must be an email address")
    @NotEmpty(message = "Email is required")
    @Length(max = 100, message = "Email max length 100.")
    private lateinit var inputEmail: EditText

    @NotEmpty(message = "Password is required")
    @Password(min = 6, message = "password min length 6")
    private lateinit var inputPassword: EditText

    @NotEmpty(message = "Password confirmation is required")
    @ConfirmPassword
    private lateinit var inputPasswordConfirm: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.vm = registerViewModel
        binding.lifecycleOwner = this
        inputFirstName = binding.inputFirstname
        inputFirstName.addTextChangedListener(InputLayoutTextWatcher(binding.fieldFirstname))
        inputLastName = binding.inputLastname
        inputLastName.addTextChangedListener(InputLayoutTextWatcher(binding.fieldLastname))
        inputEmail = binding.inputEmail
        inputEmail.addTextChangedListener(InputLayoutTextWatcher(binding.fieldEmail))
        inputUsername = binding.inputUsername
        inputUsername.addTextChangedListener(InputLayoutTextWatcher(binding.fieldUsername))
        inputPassword = binding.inputPassword
        inputPassword.addTextChangedListener(InputLayoutTextWatcher(binding.fieldPassword))
        inputPasswordConfirm = binding.inputPasswordConfirm
        inputPasswordConfirm.addTextChangedListener(InputLayoutTextWatcher(binding.fieldPasswordConfirm))
        buttonRegister = binding.buttonRegister

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
        buttonRegister.setOnClickListener {
            validator.validate()
        }

        inputPasswordConfirm.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    validator.validate()
                    (this.activity as AuthActivity).hideKeyboard(this.requireView())
                }
            }
            false
        }

        registerViewModel.registerResponse.observe(viewLifecycleOwner, Observer {
            it?.let {
                registerViewModel.resetResponses()
                if (it.success) navigateToLogin() else showErrorMessage(it.message)
            }
        })

    }

    private fun showErrorMessage(extraMessage: String) {

        Snackbar.make(this.requireView(), resources.getString(R.string.register_failed)
                + extraMessage, Snackbar.LENGTH_SHORT).show()

    }

    private fun navigateToLogin() {

        findNavController().navigate(
            RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(
                inputUsername.text.toString().trim(), inputPassword.text.toString().trim()
            )
        )

    }

    private fun register() {

        if (Variables.isNetworkConnected.value!!){
            registerViewModel.register(
                input_firstname.text.toString().trim()
                , input_lastname.text.toString().trim()
                , inputUsername.text.toString().trim()
                , inputEmail.text.toString().trim()
                , inputPassword.text.toString().trim()
            )
        } else {
            showErrorMessage(resources.getString(R.string.no_connection))
        }

    }

}