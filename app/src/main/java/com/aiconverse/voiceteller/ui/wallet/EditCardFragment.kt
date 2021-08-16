package com.aiconverse.voiceteller.ui.wallet

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aiconverse.voiceteller.R
import com.aiconverse.voiceteller.databinding.EditCardFragmentBinding
import com.aiconverse.voiceteller.databinding.WalletFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class EditCardFragment : Fragment() {

    companion object {
        fun newInstance() = EditCardFragment()
    }

    private val viewModel: EditCardViewModel by lazy {
        ViewModelProvider(this).get(EditCardViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: EditCardFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.edit_card_fragment, container, false)

        val textValue = arguments?.getString("id","")

        binding.txtHeader.text = textValue

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}