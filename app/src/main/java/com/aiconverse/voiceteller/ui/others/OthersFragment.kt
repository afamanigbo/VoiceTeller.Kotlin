package com.aiconverse.voiceteller.ui.others

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aiconverse.voiceteller.R

class OthersFragment : Fragment() {

    companion object {
        fun newInstance() = OthersFragment()
    }

    private lateinit var viewModel: OthersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.others_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OthersViewModel::class.java)
        // TODO: Use the ViewModel
    }

}