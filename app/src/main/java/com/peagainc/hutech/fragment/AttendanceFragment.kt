package com.peagainc.hutech.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.peagainc.hutech.R
import com.peagainc.hutech.adapter.CourseAdapter
import com.peagainc.hutech.adapter.DividerItemDecorator
import com.peagainc.hutech.model.Course
import com.peagainc.hutech.model.Location


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AttendanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AttendanceFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rcvCourse: RecyclerView
    private lateinit var courseAdapter:CourseAdapter
    private lateinit var coursesList : MutableList<Course>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendance, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        addEvent()
    }
    private fun initView(view: View) {
        coursesList = mutableListOf()
        coursesList.add(Course(1,"Phân tích thiết kế HTTT", "xyz","07:30","11:30", Location(0,"Hutech E Campus","HCM",0.0,0.0)))
        coursesList.add(Course(2,"Lập trình trên TBDD", "xyz","12:30","16:30", Location(1,"Hutech A Campus","HCM",0.0,0.0)))
        coursesList.add(Course(3,"Trí tuệ nhân tạo", "xyz","18:00","20:30", Location(2,"Hutech B Campus","HCM",0.0,0.0)))

        val context:Context = view.context
        val layoutManager: LinearLayoutManager = LinearLayoutManager(
            this.context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val courseAdapter: CourseAdapter = CourseAdapter(coursesList, context)
        val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.custom_divider))
        rcvCourse = view.findViewById(R.id.rcvCourse)
        rcvCourse.hasFixedSize()
        rcvCourse.layoutManager = layoutManager
        rcvCourse.addItemDecoration(dividerItemDecoration)
        rcvCourse.itemAnimator = DefaultItemAnimator()
        rcvCourse.adapter = courseAdapter
    }

    private fun addEvent() {

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AttendanceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AttendanceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}