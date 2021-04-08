package com.inFlow.moneyManager.ui.add_transaction

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.FragmentAddTransactionBinding
import com.inFlow.moneyManager.databinding.FragmentDashboardBinding

class AddTransaction : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddTransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

}