package com.peagainc.hutech.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.peagainc.hutech.CheckLocationActivity
import com.peagainc.hutech.R
import com.peagainc.hutech.adapter.CourseAdapter.ViewHolder
import com.peagainc.hutech.model.Course
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime

class CourseAdapter(private val courses: MutableList<Course>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val customCourseList: View = layoutInflater.inflate(
            R.layout.custom_course_list, parent, false
        )
        return ViewHolder(customCourseList)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course: Course = courses[position]
        val duration: Duration? = Duration.between(LocalTime.parse(course.startTime) , LocalTime.parse(course.endTime))
        holder.txtCourseName.text = course.courseName
        holder.txtTimeStart.text = course.startTime
        holder.txtTimeEnd.text = course.endTime
        holder.txtDuration.text = "${duration!!.toMinutes()} mins"
        holder.txtLocation.text= course.location?.locationName
        holder.txtStatus.text= "Pending"
        holder.itemView.setOnClickListener(){
            Toast.makeText(this.context, course.courseName, Toast.LENGTH_LONG).show()
            val intent: Intent = Intent(this.context, CheckLocationActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return courses.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imvThumb: ImageView = itemView.findViewById(R.id.imvThumb)
        var txtCourseName: com.google.android.material.textview.MaterialTextView = itemView.findViewById(R.id.txtCourseName)
        var txtTimeStart: com.google.android.material.textview.MaterialTextView = itemView.findViewById(R.id.txtTimeStart)
        var txtTimeEnd: com.google.android.material.textview.MaterialTextView = itemView.findViewById(R.id.txtTimeEnd)
        var txtDuration: com.google.android.material.textview.MaterialTextView = itemView.findViewById(R.id.txtDuration)
        var txtLocation: com.google.android.material.textview.MaterialTextView = itemView.findViewById(R.id.txtLocation)
        var txtStatus: com.google.android.material.textview.MaterialTextView = itemView.findViewById(R.id.txtStatus)
        init {

        }
    }

}