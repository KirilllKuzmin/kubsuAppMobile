package com.kubsu.cubehub.ui.main.accounting.accountingGroup

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kubsu.cubehub.R
import com.kubsu.cubehub.adapter.AccountingAdapter
import com.kubsu.cubehub.adapter.AccountingGroupAdapter
import com.kubsu.cubehub.data.model.Course
import com.kubsu.cubehub.data.model.Group
import com.kubsu.cubehub.data.network.MainService
import com.kubsu.cubehub.databinding.FragmentAccountingGroupBinding
import com.kubsu.cubehub.databinding.FragmentNotificationsBinding
import com.kubsu.cubehub.ui.auth.LoginViewModel
import com.kubsu.cubehub.ui.main.MainActivity
import com.kubsu.cubehub.ui.main.notifications.NotificationsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AccountingGroupFragment : Fragment() {

    private val TAG = "AccountingGroupFragment"

    private var _binding: FragmentAccountingGroupBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: AccountingGroupAdapter

    private lateinit var mainService: MainService

    private val loginViewModel by viewModels<LoginViewModel>()

    private lateinit var courses: List<Course>

    private lateinit var groups: List<Group>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val accountingGroupViewModelViewModel =
            ViewModelProvider(this).get(AccountingGroupViewModel::class.java)

        _binding = FragmentAccountingGroupBinding.inflate(inflater, container, false)
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

        val coursePosition = arguments?.getInt("coursePosition", -1)

        initRetrofit()
        initRcView()

        loginViewModel.token.observe(viewLifecycleOwner) {token ->
            CoroutineScope(Dispatchers.IO).launch {
                courses = mainService.getLecturerCourses("Bearer " + token)
                    .sortedBy { it.name }

                groups = mainService.getCourseGroups("Bearer " + token, courses[coursePosition!!].id)

                requireActivity().runOnUiThread {
                    adapter.submitList(groups)
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
        adapter = AccountingGroupAdapter()
        rcView.layoutManager = LinearLayoutManager(context)
        rcView.adapter = adapter
        adapter.setOnItemClickListener(object : AccountingGroupAdapter.onItemClickListener {
            @SuppressLint("ResourceType")
            override fun onItemClick(position: Int) {

                val bundle = Bundle()
                bundle.putInt("groupPosition", position)

                val coursePosition = arguments?.getInt("coursePosition", -1)
                bundle.putInt("coursePosition", coursePosition!!)

                findNavController().navigate(R.id.action_AccountingGroupFragment_to_AccountingStudentFragment, bundle)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}