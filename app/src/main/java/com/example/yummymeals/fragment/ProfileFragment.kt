package com.example.yummymeals.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.yummymeals.R

class ProfileFragment : Fragment() {
    lateinit var txtName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    lateinit var txtNumber: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.profile)
        val preferences = activity?.getSharedPreferences("data", Context.MODE_PRIVATE)
        txtName = view.findViewById(R.id.txtUserName)
        txtNumber = view.findViewById(R.id.txtUserNumber)
        txtEmail = view.findViewById(R.id.txtUserEmail)
        txtAddress = view.findViewById(R.id.txtUserAddress)
        if (preferences != null) {
            txtName.text = preferences.getString("name", "Name").capitalize()
            txtNumber.text = preferences.getString("number", "Number")
            txtEmail.text = preferences.getString("email", "Email").capitalize()
            txtAddress.text = preferences.getString("address", "Address").capitalize()
        }
        return view
    }

}