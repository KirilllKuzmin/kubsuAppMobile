package com.kubsu.cubehub.ui.main.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kubsu.cubehub.adapter.TimetableAdapter
import com.kubsu.cubehub.common.User
import com.kubsu.cubehub.data.network.MainService
import com.kubsu.cubehub.databinding.FragmentTimetableBinding
import com.kubsu.cubehub.ui.auth.LoginViewModel
import com.kubsu.cubehub.ui.auth.LoginWithPasswordViewModel
import com.kubsu.cubehub.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TimetableFragment : Fragment() {

    private val TAG = "TimetableFragment"

    private var _binding: FragmentTimetableBinding? = null

    private lateinit var adapter: TimetableAdapter

    private lateinit var mainService: MainService

    private val loginViewModel by viewModels<LoginViewModel>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val timetableViewModel =
            ViewModelProvider(this).get(TimetableViewModel::class.java)

        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
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
                val sysdate = OffsetDateTime.of(LocalDate.now().atStartOfDay(), ZoneOffset.UTC)
                var timetables = mainService.getTimetables("Bearer " + token, sysdate, sysdate)
                    .sortedBy { it.numberTimeClassHeld.startTime }
                initRetrofitAuth()
                val groups = mainService.getGroups("Bearer " + token)

                timetables.forEach { timetable ->
                    timetable.timetableGroup.forEach { timetableGroup ->
                        groups.find { it.id == timetableGroup.groupId }?.let { group ->
                            timetableGroup.name = group.name
                        }
                    }
                }

                requireActivity().runOnUiThread {
                    adapter.submitList(timetables)
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

    private fun initRetrofitAuth() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/").client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        mainService = retrofit.create(MainService::class.java)
    }

    private fun initRcView() = with(binding) {
        adapter = TimetableAdapter()
        rcView.layoutManager = LinearLayoutManager(context)
        rcView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}