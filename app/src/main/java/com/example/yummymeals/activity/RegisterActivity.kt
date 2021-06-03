package com.example.yummymeals.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.yummymeals.R
import com.example.yummymeals.util.ConnectionManager
import com.example.yummymeals.util.ConnectionReportAlertDialog
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etNumber: EditText
    private lateinit var etAddress: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_register)
        etName = findViewById(R.id.register_etName)
        etEmail = findViewById(R.id.register_etEmail)
        etNumber = findViewById(R.id.register_etNumber)
        etPassword = findViewById(R.id.register_etPassword)
        etAddress = findViewById(R.id.register_etDelivery)
        progressBar = findViewById(R.id.rlLoading)
        progressBar.visibility = RelativeLayout.GONE
        etConfirmPassword = findViewById(R.id.register_etConfirmPassword)
        btnRegister = findViewById(R.id.register_btnRegister)
        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val num = etNumber.text.toString()
            val pwd = etPassword.text.toString()
            val adr = etAddress.text.toString()
            val cPwd = etConfirmPassword.text.toString()
            if (name.length >= 3 && num.length == 10 && pwd.length >= 4) {
                if (cPwd == pwd) {
                    if (ConnectionManager().checkConnectivity(this)) {
                        progressBar.visibility = RelativeLayout.VISIBLE
                        val queue = Volley.newRequestQueue(this)
                        val url = "http://13.235.250.119/v2/register/fetch_result"
                        val jsonParams = JSONObject()
                        jsonParams.put("name", name)
                        jsonParams.put("mobile_number", num)
                        jsonParams.put("password", pwd)
                        jsonParams.put("address", adr)
                        jsonParams.put("email", email)
                        val jsonRequest =
                            object :
                                JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                                    try {
                                        val objectData = it.getJSONObject("data")
                                        val success = objectData.getBoolean("success")
                                        if (success) {
                                            Toast.makeText(
                                                this,
                                                "Registration Successful",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            val intent =
                                                Intent(this, HomeDashboardActivity::class.java)
                                            val data = objectData.getJSONObject("data")
                                            val nameData = data.getString("name")
                                            val userId  = data.getString("user_id")
                                            val emailData = data.getString("email")
                                            val numberData = data.getString("mobile_number")
                                            val addressData = data.getString("address")
                                            val bundle = Bundle()
                                            val dataPreference = getSharedPreferences(
                                                getString(R.string.preferenceFile),
                                                Context.MODE_PRIVATE
                                            )
                                            bundle.putString("name", nameData)
                                            bundle.putString("email", emailData)
                                            bundle.putString("number", numberData)
                                            bundle.putString("address", addressData)
                                            bundle.putString("userId",userId)
                                            intent.putExtra("data", bundle)
                                            dataPreference.edit().putString("name", nameData)
                                                .apply()
                                            dataPreference.edit().putString("userId",userId).apply()
                                            dataPreference.edit().putString("email", emailData)
                                                .apply()
                                            dataPreference.edit().putString("number", numberData)
                                                .apply()
                                            dataPreference.edit().putString("address", addressData)
                                                .apply()
                                            dataPreference.edit().putBoolean("loginState", true)
                                                .apply()
                                            progressBar.visibility = RelativeLayout.GONE
                                            startActivity(intent)
                                            finish()

                                        } else {
                                            progressBar.visibility = RelativeLayout.GONE
                                            Toast.makeText(
                                                this,
                                                "Mobile number or email is already Registered",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    } catch (e: Exception) {
                                        progressBar.visibility = RelativeLayout.GONE
                                        Toast.makeText(
                                            this,
                                            "'$e' error occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }

                                }, Response.ErrorListener {
                                    progressBar.visibility = RelativeLayout.GONE
                                    Toast.makeText(
                                        this,
                                        "Some $it error Occurred",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-Type"] = "application/json"
                                    headers["token"] = "753effaaf3d43e"
                                    return headers
                                }
                            }
                        queue.add(jsonRequest)

                    } else {
                        ConnectionReportAlertDialog().buildDialog(this)

                    }

                } else {
                    etConfirmPassword.error = "Enter password again"
                    Toast.makeText(this, "Entered Password Mismatch", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (name.length < 3) {
                    etName.error = "Required valid name"
                }
                if (num.length != 10) {
                    etNumber.error = "Required valid number"
                }
                if (pwd.length < 4) {
                    etPassword.error = "Required valid password"
                }
            }
            if (name == ("")) {
                etName.error = "This field is blank"
            }
            if (email == ("")) {
                etEmail.error = "This field is blank"
            }
            if (num == ("")) {
                etNumber.error = "This field is blank"
            }
            if (pwd == ("")) {
                etPassword.error = "This field is blank"
            }
            if (adr == ("")) {
                etAddress.error = "This field is blank"
            }
            if (cPwd == ("")) {
                etConfirmPassword.error = "This field is blank"
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}

