package com.example.bricklist.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bricklist.R
import com.example.bricklist.activities.ProjectActivity
import kotlinx.android.synthetic.main.fragment_project_name.*


class ProjectNameFragment : Fragment() {

    private var projectId: Int? = null
    private var projectName: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.projectId = arguments?.getInt("id")
        this.projectName = arguments?.getString("name")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        projectNameTextView.text = projectName
        projectNameTextView.setOnClickListener {
            val intent = Intent(activity, ProjectActivity::class.java)
            intent.putExtra("projectID", projectId)
            intent.putExtra("projectName", projectName)
            startActivity(intent)
        }

    }
}
