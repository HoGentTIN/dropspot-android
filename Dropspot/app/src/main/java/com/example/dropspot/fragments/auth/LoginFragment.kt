package com.example.dropspot.fragments.auth

import android.content.Intent
import android.content.SharedPreferences
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
import androidx.navigation.fragment.navArgs
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.dropspot.AuthActivity
import com.example.dropspot.MainActivity
import com.example.dropspot.databinding.FragmentLoginBinding
import com.example.dropspot.utils.InputLayoutTextWatcher
import com.example.dropspot.utils.MyValidationListener
import com.example.dropspot.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding
    private val validator = Validator(this)
    private val args: LoginFragmentArgs by navArgs()
    private lateinit var sharedPreferences: SharedPreferences

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.vm = authViewModel
        binding.lifecycleOwner = this
        input_email = binding.inputEmail
        input_email.addTextChangedListener(InputLayoutTextWatcher(binding.fieldEmail))
        input_password = binding.inputPassword
        input_password.addTextChangedListener(InputLayoutTextWatcher(binding.fieldPassword))
        button_login = binding.buttonLogin
        button_register = binding.buttonRegister
        progressBar_loading = binding.progressBarLoading

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSharedPref()
        checkIfLoggedIn()
        setupListenersObservers()
        setupUI()
        validator.setValidationListener(object :
            MyValidationListener(this.requireContext(), this.requireView()) {
            override fun onValidationSucceeded() {
                login()
            }

        })
    }

    private fun setupSharedPref() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        sharedPreferences = EncryptedSharedPreferences.create(
            "AUTH_ENCRYPT",
            masterKeyAlias,
            this.requireContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun setupListenersObservers() {
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
                    (this.activity as AuthActivity).hideKeyboard(this.requireView())
                }
            }
            false
        }

        authViewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                val token = it.token
                saveSharedPref(token)
                startMainActivity(token)
            } else {
                Snackbar.make(this.requireView(), it.message, Snackbar.LENGTH_SHORT).show()
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

    private fun saveSharedPref(token: String) {
        sharedPreferences
            .edit()
            .putString("TOKEN", token)
            .apply()
    }

    private fun setupUI() {
        // handling successful registration
        if (args.emailOrUsername.isNotBlank() && args.password.isNotBlank()) {
            binding.inputEmail.setText(args.emailOrUsername)
            binding.inputPassword.setText(args.password)
        }
    }

    private fun checkIfLoggedIn() {
        val token = sharedPreferences.getString("TOKEN", null)

        if (token != null) {
            startMainActivity(
                token
            )
        }

    }


    private fun startMainActivity(token: String) {
        val intent = Intent(this.context, MainActivity::class.java)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
        this.activity!!.finish()
    }

    private fun login() {
        authViewModel.login(
            input_email.text.toString().trim()
            , input_password.text.toString().trim()
        )
    }

}