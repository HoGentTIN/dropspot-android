package com.example.dropspot.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.dropspot.AuthActivity
import com.example.dropspot.MainActivity
import com.example.dropspot.databinding.FragmentLoginBinding
import com.example.dropspot.utils.Constants.AUTH_ENC_SHARED_PREF_KEY
import com.example.dropspot.utils.InputLayoutTextWatcher
import com.example.dropspot.utils.MyValidationListener
import com.example.dropspot.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var binding: FragmentLoginBinding
    private val validator = Validator(this)
    private val args: LoginFragmentArgs by navArgs()
    private lateinit var sharedPreferences: SharedPreferences

    // UI components
    private lateinit var button_register: Button
    private lateinit var button_login: Button

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
        binding.vm = loginViewModel
        binding.lifecycleOwner = this
        input_email = binding.inputEmail
        input_email.addTextChangedListener(InputLayoutTextWatcher(binding.fieldEmail))
        input_password = binding.inputPassword
        input_password.addTextChangedListener(InputLayoutTextWatcher(binding.fieldPassword))
        button_login = binding.buttonLogin
        button_register = binding.buttonRegister

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

        val spec = KeyGenParameterSpec.Builder(
            MasterKey.DEFAULT_MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
            .build()

        val masterKey = MasterKey.Builder(requireContext())
            .setKeyGenParameterSpec(spec)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            requireContext(),
            AUTH_ENC_SHARED_PREF_KEY,
            masterKey,
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

        loginViewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            if (it.success) {
                val token = it.token
                saveSharedPref(token)
                startMainActivity(token)
            } else {
                Snackbar.make(this.requireView(), it.message, Snackbar.LENGTH_SHORT).show()
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
        loginViewModel.login(
            input_email.text.toString().trim()
            , input_password.text.toString().trim()
        )
    }

}