package com.example.dropspot.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.dropspot.databinding.EditSpotDetailFragmentBinding
import com.example.dropspot.viewmodels.EditSpotDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditSpotDetailFragment : Fragment() {

    private val editSpotDetailViewModel: EditSpotDetailViewModel by viewModel()
    private val args: EditSpotDetailFragmentArgs by navArgs()
    private lateinit var binding: EditSpotDetailFragmentBinding

    companion object {
        private const val TAG = "edit_spot_detail_frag"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditSpotDetailFragmentBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = editSpotDetailViewModel
        val spotDetail = args.spotDetail
        binding.spotDetail = spotDetail

        return binding.root
    }

}