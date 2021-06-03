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

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var etOTP: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSubmit: Button
    private lateinit var bundle: Bundle
    private lateinit var progressBar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        etOTP = findViewById(R.id.otp_etOtp)
        etPassword = findViewById(R.id.otp_etNewPassword)
        progressBar = findViewById(R.id.rlLoading)
        progressBar.visibility = RelativeLayout.GONE
        etConfirmPassword = findViewById(R.id.otp_etConfirmPassword)
        btnSubmit = findViewById(R.id.otp_btnSubmit)
        btnSubmit.setOnClickListener {
            val otp = etOTP.text.toString()
            val pwd = etPassword.text.toString()
            val cPwd = etConfirmPassword.text.toString()
            bundle = intent.getBundleExtra("number")
            val number: String? = bundle.getString("number", null)
            if (otp.length == 4) {
                if (cPwd == pwd) {
                    if (ConnectionManager().checkConnectivity(this)) {
                        progressBar.visibility = RelativeLayout.VISIBLE
                        val queue = Volley.newRequestQueue(this)
                        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                        val jsonParams = JSONObject()
                        jsonParams.put("mobile_number", number)
                        jsonParams.put("password", pwd)
                        jsonParams.put("otp", otp)
                        val jsonRequest =
                            object :
                                JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                                    try {
                                        val objectData = it.getJSONObject("data")
                                        val success = objectData.getBoolean("success")
                                        if (success) {
                                            Toast.makeText(
                                                this,
                                                objectData.getString("successMessage"),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            val preferences = getSharedPreferences(
                                                getString(R.string.preferenceFile),
                                                Context.MODE_PRIVATE
                                            )
                                            preferences.edit().clear().apply()
                                            val intent = Intent(this, LoginActivity::class.java)
                                            progressBar.visibility = RelativeLayout.GONE
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            progressBar.visibility = RelativeLayout.GONE
                                            otpErrorMessage()
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
                otpErrorMessage()
            }
            if (otp == ("")) {
                etOTP.error = "This field is blank"
            }
            if (pwd == ("")) {
                etPassword.error = "This field is blank"
            }
            if (cPwd == ("")) {
                etConfirmPassword.error = "This field is blank"
            }
        }
    }

    fun otpErrorMessage() {
        etOTP.error = "Incorrect OTP"
        if (bundle.getBoolean("firstTry")) {
            Toast.makeText(this, "Please refer previous email for the OTP", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, "Please refer email for the OTP", Toast.LENGTH_SHORT)
                .show()
        }
    }
}