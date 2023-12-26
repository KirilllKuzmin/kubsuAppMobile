package com.kubsu.cubehub.ui.main.accounting.accountingGroup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.kubsu.cubehub.R
import com.kubsu.cubehub.databinding.FragmentAccountingGroupBinding
import com.kubsu.cubehub.databinding.FragmentNotificationsBinding
import com.kubsu.cubehub.ui.main.notifications.NotificationsViewModel

class AccountingGroupFragment : Fragment() {

    private var _binding: FragmentAccountingGroupBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val accountingGroupViewModelViewModel =
            ViewModelProvider(this).get(AccountingGroupViewModel::class.java)

        _binding = FragmentAccountingGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGroup
        accountingGroupViewModelViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}