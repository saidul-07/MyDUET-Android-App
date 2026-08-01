package com.example.myduet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myduet.R
import com.example.myduet.databinding.FragmentAdmissionBinding

class AdmissionFragment : Fragment() {

    private var _binding: FragmentAdmissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdmissionBinding.inflate(inflater, container, false)
        return binding.root as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardSeatPlan.setOnClickListener {
            findNavController().navigate(R.id.action_admissionFragment_to_seatPlanFragment)
        }

        binding.cardAdmissionResult.setOnClickListener {
            findNavController().navigate(R.id.action_admissionFragment_to_admissionResultFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}