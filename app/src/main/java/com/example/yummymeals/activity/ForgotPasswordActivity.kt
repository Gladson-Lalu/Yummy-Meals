package com.example.yummymeals.activity

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


class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var etNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var next: Button
    private lateinit var progressBar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        progressBar = findViewById(R.id.rlLoading)
        etNumber = findViewById(R.id.otp_etOtp)
        etEmail = findViewById(R.id.otp_etConfirmPassword)
        next = findViewById(R.id.forgot_btnNext)
        progressBar.visibility = RelativeLayout.GONE

        next.setOnClickListener {
            val num = etNumber.text.toString()
            val email = etEmail.text.toString()
            if (num != "" && email != "") {
                if (num.length == 10) {
                    if (ConnectionManager().checkConnectivity(this)) {
                        progressBar.visibility = RelativeLayout.VISIBLE
                        val queue = Volley.newRequestQueue(this)
                        val url = " http://13.235.250.119/v2/forgot_password/fetch_result "
                        val jsonParams = JSONObject()
                        jsonParams.put("mobile_number", num)
                        jsonParams.put("email", email)
                        val jsonRequest =
                            object :
                                JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                                    try {
                                        val objectData = it.getJSONObject("data")
                                        val success = objectData.getBoolean("success")
                                        if (success) {
                                            val intent =
                                                Intent(this, ResetPasswordActivity::class.java)
                                            val bundle = Bundle()
                                            val firstTry = objectData.getBoolean("first_try")
                                            bundle.putString("number", num)
                                            bundle.putBoolean("firstTry", firstTry)
                                            intent.putExtra("number", bundle)
                                            progressBar.visibility = RelativeLayout.GONE
                                            startActivity(intent)
                                        } else {
                                            progressBar.visibility = RelativeLayout.GONE
                                            Toast.makeText(
                                                this,
                                                "Mobile number or Email is not Registered!",
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
                    etNumber.error = "Required valid number"
                }
            }
            if (num == ("")) {
                etNumber.error = "This field is blank"
            }
            if (email == ("")) {
                etEmail.error = "This field is blank"
            }
        }
    }
}

