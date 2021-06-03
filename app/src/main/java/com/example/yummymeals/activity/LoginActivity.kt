package com.example.yummymeals.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.yummymeals.R
import com.example.yummymeals.util.ConnectionManager
import com.example.yummymeals.util.ConnectionReportAlertDialog
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var etNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var etLogin: Button
    private lateinit var etForgot: TextView
    private lateinit var etRegister: TextView
    lateinit var progressBar: RelativeLayout
    private var isLoggedIn = false
    var nameData: String? = null
    var emailData: String? = null
    var numberData: String? = null
    var addressData: String? = null
    var userId: String? = null
    private lateinit var dataPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataPreference =
            getSharedPreferences(getString(R.string.preferenceFile), Context.MODE_PRIVATE)

        isLoggedIn = dataPreference.getBoolean("loginState", false)
        if (isLoggedIn) {
            login()
        } else {
            setContentView(R.layout.activity_login_page)
            etNumber = findViewById(R.id.login_etNumber)
            etPassword = findViewById(R.id.login_etPassword)
            etLogin = findViewById(R.id.login_btnLogin)
            etForgot = findViewById(R.id.login_txtForgotPwd)
            etRegister = findViewById(R.id.login_txtSignUp)
            progressBar = findViewById(R.id.rlLoading)
            progressBar.visibility = RelativeLayout.GONE
            if (!ConnectionManager().checkConnectivity(this)) {
                ConnectionReportAlertDialog().buildDialog(this)
            }
            etLogin.setOnClickListener {
                val num: String
                val pwd: String
                val number = etNumber.text.toString()
                val password = etPassword.text.toString()
                if (number.length == 10 && password.length >= 4) {
                    num = number
                    pwd = password
                    if (ConnectionManager().checkConnectivity(this)) {
                        progressBar.visibility = RelativeLayout.VISIBLE
                        val queue = Volley.newRequestQueue(this)
                        val url = "http://13.235.250.119/v2/login/fetch_result"
                        val jsonParams = JSONObject()
                        jsonParams.put("mobile_number", num)
                        jsonParams.put("password", pwd)
                        val jsonRequest = object :
                            JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                                try {
                                    val objectData = it.getJSONObject("data")
                                    if (objectData.getBoolean("success")) {
                                        val data = objectData.getJSONObject("data")
                                        userId = data.getString("user_id")
                                        nameData = data.getString("name")
                                        emailData = data.getString("email")
                                        numberData = data.getString("mobile_number")
                                        addressData = data.getString("address")
                                        dataPreference.edit().putString("userId", userId).apply()
                                        dataPreference.edit().putString("name", nameData).apply()
                                        dataPreference.edit().putString("email", emailData).apply()
                                        dataPreference.edit().putString("number", numberData)
                                            .apply()
                                        dataPreference.edit().putString("address", addressData)
                                            .apply()
                                        dataPreference.edit().putBoolean("loginState", true).apply()
                                        progressBar.visibility = RelativeLayout.GONE
                                        login()
                                    } else {
                                        progressBar.visibility = RelativeLayout.GONE
                                        Toast.makeText(
                                            this,
                                            "Incorrect mobile number or password",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    progressBar.visibility = RelativeLayout.GONE
                                    Toast.makeText(this, "'$e' error occurred", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }, Response.ErrorListener {
                                progressBar.visibility = RelativeLayout.GONE
                                Toast.makeText(this, "Some $it error occurred", Toast.LENGTH_SHORT)
                                    .show()

                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val header = HashMap<String, String>()
                                header["Content-Type"] = "application/json"
                                header["token"] = "753effaaf3d43e"
                                return header
                            }

                        }
                        queue.add(jsonRequest)

                    } else {
                        ConnectionReportAlertDialog().buildDialog(this)
                    }
                } else {
                    if (number.length != 10 && password.length < 4)
                        if (number.length != 10)
                            Toast.makeText(this, "Enter valid Mobile number", Toast.LENGTH_SHORT)
                                .show()
                        else
                            Toast.makeText(
                                this,
                                "Minimum 4 characters is required for Password",
                                Toast.LENGTH_SHORT
                            ).show()
                }

                if (number == ("")) {
                    etNumber.error = "This field is blank"
                }
                if (password == ("")) {
                    etPassword.error = "This field is blank"
                }
            }
            etForgot.setOnClickListener {
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
            }
            etRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }

        }
    }

    private fun login() {
        val intent = Intent(
            this,
            HomeDashboardActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}

