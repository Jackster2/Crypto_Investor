package com.example.cryptoinvestor.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.cryptoinvestor.R
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.android.example.github.vo.Status
import com.example.cryptoinvestor.databinding.ActivityAuthBinding
import com.example.cryptoinvestor.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

/*
    Inspiration til login & signup på én side er draget fra:
    https://camposha.info/android-examples/android-login-signup/#gsc.tab=0
 */

//AndroidEntrypPoint tells Hilt that this class should be setup for injection
@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    //Binding to UI
    private lateinit var binding: ActivityAuthBinding

    //Get ViewModel instance
    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        binding.signin.performClick()
        setContentView(binding.root)

        signin_signup_btn.setOnClickListener {
            logIn(it)
        }

        binding.signin.setOnClickListener {
            signup.setTextColor(Color.parseColor("#FFFFFF"))
            signup.setBackgroundColor(this.getColor(R.color.bannerGold))
            signin.setTextColor(this.getColor(R.color.bannerGold))
            signin.setBackgroundResource(R.drawable.border_shape)
            circleImageView.setImageResource(R.mipmap.ic_login)
            signin_signup_txt.text = "Log in"
            signin_signup_btn.text = "Login"
            forgot_password.visibility = View.VISIBLE
            usernameTextInput.visibility = View.GONE
            nameTextInput.visibility = View.GONE
            signin_signup_btn.setOnClickListener {
                logIn(it)
            }
        }
        binding.signup.setOnClickListener {
            signin.setTextColor(Color.parseColor("#FFFFFF"))
            signin.setBackgroundColor(this.getColor(R.color.bannerGold))
            signup.setTextColor(this.getColor(R.color.bannerGold))
            signup.setBackgroundResource(R.drawable.border_shape)
            circleImageView.setImageResource(R.mipmap.ic_signup)
            signin_signup_txt.text = "Sign Up"
            signin_signup_btn.text = "CREATE"
            forgot_password.visibility = View.INVISIBLE
            usernameTextInput.visibility = View.VISIBLE
            nameTextInput.visibility = View.VISIBLE

            signin_signup_btn.setOnClickListener {
                register(it)
            }
        }
    }

    private fun register(view: View) {
        val email = emailAddressEditText.text.toString()
        val password = password.text.toString()
        val username = usernameEditText.text.toString()
        val fullname = nameEditText.text.toString()

        viewModel.signUp(email, password, fullname, username).observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    auth.currentUser?.let { it1 ->
                        viewModel.saveUser(
                            it.data?.email.toString(),
                            it.data?.fullName.toString(),
                            it.data?.userName.toString(),
                            10000.0,
                            it1.uid
                        )
                    }
                    view.showSnackBar("Account registered")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                Status.ERROR -> {
                    view.showSnackBar(it.message!!)
                }
                Status.LOADING -> {
                    view.showSnackBar("...")
                }
            }
        })
    }

    private fun logIn(view: View) {
        val email = emailAddressEditText.text.toString().trim()
        val password = password.text.toString().trim()

        viewModel.signIn(email, password).observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    view.showSnackBar("...")
                }
                Status.SUCCESS -> {
                    view.showSnackBar("Login successfull")
                    val intent = Intent(this, MainActivity::class.java)
                    println("USER LOGGED IN : " + it.data?.userId.toString())
                    startActivity(intent)
                    finish()
                }
                Status.ERROR -> {
                    view.showSnackBar(it.message!!)
                }
            }
        }
    }
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}