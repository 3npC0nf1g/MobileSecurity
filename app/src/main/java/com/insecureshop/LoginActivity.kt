package com.insecureshop

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.insecureshop.databinding.ActivityLoginBinding
import com.insecureshop.util.Util

class LoginActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityLoginBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), 100
        )
    }

    fun onLogin(view: View) {
        val username = mBinding.edtUserName.text.toString()
        val password = mBinding.edtPassword.text.toString()

        if (BuildConfig.DEBUG) {
            Log.d("LoginActivity", "Login attempt detected")
        }

        val auth = Util.verifyUserNamePassword(username, password)
        if (auth) {
            Util.saveProductList(this)
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        } else {
            for (info in packageManager.getInstalledPackages(0)) {
                val packageName = info.packageName
                if (packageName.startsWith("com.insecureshopapp")) {
                    try {
                        val packageContext = createPackageContext(
                            packageName,
                            Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
                        )
                        val value: Any = packageContext.classLoader
                            .loadClass("com.insecureshopapp.MainInterface")
                            .getMethod("getInstance", Context::class.java)
                            .invoke(null, this)
                        if (BuildConfig.DEBUG) {
                            Log.d("LoginActivity", "Object from dynamic load: $value")
                        }
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                }
            }

            Toast.makeText(
                applicationContext,
                "Invalid username and password",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
