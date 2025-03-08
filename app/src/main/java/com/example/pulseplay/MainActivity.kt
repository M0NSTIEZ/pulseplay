package com.example.pulseplay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pulseplay.api.RetrofitClient
import com.example.pulseplay.models.HealthData
import retrofit2.Callback

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.main)?.let { rootView ->
            ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val loginButton = findViewById<Button>(R.id.btn_login)
        val registerText = findViewById<TextView>(R.id.tv_register)

        registerText.setOnClickListener {
            val intent = Intent(this@MainActivity, Register::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            fetchHealthData("user123") // Replace with actual user ID
        }
    }

    private fun fetchHealthData(userId: String) {
        val call = RetrofitClient.instance.getHealthData(userId)

        call.enqueue(object : Callback<HealthData> {
            override fun onResponse(
                call: retrofit2.Call<HealthData>,
                response: retrofit2.Response<HealthData>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        Log.d("AppleHealth", "Steps: ${data.steps}, Calories: ${data.calories}")
                        navigateToHomePage(data.steps, data.calories)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<HealthData>, t: Throwable) {
                Log.e("AppleHealth", "Error fetching data", t)
                navigateToHomePage(0, 0)
            }
        })
    }

    private fun navigateToHomePage(steps: Int, calories: Int) {
        val intent = Intent(this@MainActivity, HomePage::class.java).apply {
            putExtra("STEPS", steps)
            putExtra("CALORIES", calories)
        }
        startActivity(intent)
        finish()
    }
}
