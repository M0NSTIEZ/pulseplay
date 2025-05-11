    package com.example.pulseplay.auth

    import android.content.Context
    import android.content.SharedPreferences
    import com.example.pulseplay.PulsePlayApplication

    object TokenManager {
        private const val TOKEN_KEY = "auth_token"
        private val sharedPrefs: SharedPreferences by lazy {
            PulsePlayApplication.appContext.getSharedPreferences(
                "auth_prefs",
                Context.MODE_PRIVATE
            )
        }

        fun saveToken(token: String) {
            sharedPrefs.edit().putString(TOKEN_KEY, token).apply()
        }

        fun getToken(): String? {
            return sharedPrefs.getString(TOKEN_KEY, null)
        }

        fun clearToken() {
            sharedPrefs.edit().remove(TOKEN_KEY).apply()
        }
    }