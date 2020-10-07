package com.example.dropspot.ui.me

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.dropspot.R
import com.example.dropspot.databinding.MeFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class meFragment : Fragment() {
    private val viewModel: MeViewModel by viewModel()
    private lateinit var binding: MeFragmentBinding


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.me_fragment, container, false)
        binding.lifecycleOwner = this
        binding.meViewModel = viewModel

        return binding.root
    }


}
