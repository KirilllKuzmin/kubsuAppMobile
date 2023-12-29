package com.kubsu.cubehub.ui.main.accounting

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kubsu.cubehub.R
import com.kubsu.cubehub.adapter.AccountingAdapter
import com.kubsu.cubehub.adapter.TimetableAdapter
import com.kubsu.cubehub.data.model.Course
import com.kubsu.cubehub.data.network.MainService
import com.kubsu.cubehub.databinding.FragmentAccountingBinding
import com.kubsu.cubehub.ui.auth.LoginViewModel
import com.kubsu.cubehub.ui.main.MainActivity
import com.kubsu.cubehub.ui.main.accounting.accountingGroup.AccountingGroupFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AccountingFragment : Fragment() {

    private var _binding: FragmentAccountingBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: AccountingAdapter

    private lateinit var mainService: MainService

    private val loginViewModel by viewModels<LoginViewModel>()

    lateinit var courses: List<Course>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val accountingViewModel =
            ViewModelProvider(this).get(AccountingViewModel::class.java)

        _binding = FragmentAccountingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val activity = activity

        if (activity is MainActivity) {
            val intent = activity.intent
            intent.getStringExtra("token")?.let { loginViewModel.setToken(it) }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRetrofit()
        initRcView()

        loginViewModel.token.observe(viewLifecycleOwner) {token ->
            CoroutineScope(Dispatchers.IO).launch {
                courses = mainService.getLecturerCourses("Bearer " + token)
                    .sortedBy { it.name }

                requireActivity().runOnUiThread {
                    adapter.submitList(courses)
                }
            }
        }

    }

    private fun initRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/").client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mainService = retrofit.create(MainService::class.java)
    }

    private fun initRcView() = with(binding) {
        adapter = AccountingAdapter()
        rcView.layoutManager = LinearLayoutManager(context)
        rcView.adapter = adapter
        adapter.setOnItemClickListener(object : AccountingAdapter.onItemClickListener {
            @SuppressLint("ResourceType")
            override fun onItemClick(position: Int) {

                val bundle = Bundle()
                bundle.putInt("coursePosition", position)

                findNavController().navigate(R.id.action_AccountingFragment_to_AccountingGroupFragment, bundle)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}