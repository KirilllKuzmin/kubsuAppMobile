package com.kubsu.cubehub.ui.main.accounting.accountingGroup.accountingStudent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.kubsu.cubehub.R
import com.kubsu.cubehub.adapter.AccountingStudentAdapter
import com.kubsu.cubehub.data.model.Absence
import com.kubsu.cubehub.data.model.AbsenceRequestBody
import com.kubsu.cubehub.data.model.Course
import com.kubsu.cubehub.data.model.Group
import com.kubsu.cubehub.data.model.Student
import com.kubsu.cubehub.data.network.MainService
import com.kubsu.cubehub.databinding.FragmentAccountingStudentBinding
import com.kubsu.cubehub.ui.auth.LoginViewModel
import com.kubsu.cubehub.ui.main.MainActivity
import com.kubsu.cubehub.ui.main.accounting.accountingGroup.AccountingGroupViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AccountingStudentFragment : Fragment() {

    private val TAG = "AccountingGroupFragment"

    private var _binding: FragmentAccountingStudentBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: AccountingStudentAdapter

    private lateinit var mainService: MainService

    private val loginViewModel by viewModels<LoginViewModel>()

    private lateinit var courses: List<Course>

    private lateinit var groups: List<Group>

    private lateinit var students: List<Student>

    private lateinit var absences: List<Absence>

    private val selectedStudents = mutableListOf<Student>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val accountingGroupViewModelViewModel =
            ViewModelProvider(this).get(AccountingGroupViewModel::class.java)

        _binding = FragmentAccountingStudentBinding.inflate(inflater, container, false)
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

        loginViewModel.token.observe(viewLifecycleOwner) {token ->
            CoroutineScope(Dispatchers.IO).launch {
                val coursePosition = arguments?.getInt("coursePosition", -1)

                val groupPosition = arguments?.getInt("groupPosition", -1)

                courses = mainService.getLecturerCourses("Bearer " + token)
                    .sortedBy { it.name }

                val courseId = courses[coursePosition!!].id

                groups = mainService.getCourseGroups("Bearer " + token, courseId)

                val groupId = groups[groupPosition!!].id

                students = mainService.getStudents("Bearer " + token, groupId)

                absences = mainService.getAbsences("Bearer " + token, courseId, groupId)

                requireActivity().runOnUiThread {
                    val tableLayout: TableLayout = view.findViewById(R.id.tableLayout)

                    for (student in students) {

                        val tableRow = TableRow(requireContext())

                        val layoutParams = TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(0, 0, 0, 0)
                        tableRow.setBackgroundResource(R.drawable.row_border)
                        tableRow.layoutParams = layoutParams

                        val studentTextView = TextView(requireContext())
                        studentTextView.text = student.fullName
                        studentTextView.gravity = View.TEXT_ALIGNMENT_CENTER
                        studentTextView.setPadding(8, 8, 8, 8)
                        studentTextView.setBackgroundResource(R.drawable.cell_border)
                        tableRow.addView(studentTextView)

                        val emptyCellTextView = TextView(requireContext())
                        emptyCellTextView.text = if (absences.any { it.student.userId == student.userId }) "Н" else "" //TODO Дата!!
                        emptyCellTextView.gravity = View.TEXT_ALIGNMENT_CENTER
                        emptyCellTextView.setPadding(8, 8, 8, 8)
                        emptyCellTextView.setBackgroundResource(R.drawable.cell_border)
                        tableRow.addView(emptyCellTextView)

                        tableLayout.addView(tableRow)
                    }

                    for (i in 0 until tableLayout.childCount) {
                        val tableRow = tableLayout.getChildAt(i) as TableRow
                        val statusTextView = tableRow.getChildAt(1) as TextView

                        statusTextView.setOnClickListener {
                            toggleLetterN(statusTextView)
                            val student = students[i]
                            if (statusTextView.text == "Н") {
                                selectedStudents.add(student)
                            } else {
                                selectedStudents.remove(student)
                            }
                        }
                    }

                    val sendButton: Button = view.findViewById(R.id.bSendAbsences)
                    sendButton.setOnClickListener {
                        sendRequestToServer(selectedStudents)
                        Toast.makeText(context, "Информация зафиксирована", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun toggleLetterN(textView: TextView) {
        if (textView.text == "") {
            textView.text = "Н"
        } else {
            textView.text = ""
        }
    }

    private fun sendRequestToServer(selectedStudents: List<Student>) {
        loginViewModel.token.observe(viewLifecycleOwner) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                val coursePosition = arguments?.getInt("coursePosition", -1)

                for (student in students) {
                    val checkAbsence = selectedStudents.any { it.userId == student.userId }
                    val absenceTypeId = if (checkAbsence) 3L else ""
                    val response = mainService.setAbsences("Bearer " + token,
                        //TODO должны определять ближайшую дату по дефолту + добавить календарь для выбора даты
                        AbsenceRequestBody("2023-12-28T00:00:00.000Z",
                            absenceTypeId,
                            courses[coursePosition!!].id,
                            student.userId
                        )
                    )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}