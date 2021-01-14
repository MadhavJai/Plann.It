package mj.example.teemmanage.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.activity_signup.*
import mj.example.teemmanage.R

class SignupActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //Firebase.initializeApp(this)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        // use deprecated layout params if the build version is less than android R
        else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        // register user when sign up is clicked
        btn_sign_up.setOnClickListener{
            registerUser()
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }


    }

    /** function to register user to firebase **/
    private fun registerUser() {

        // trims any extra space in input for name, email and password
        // NOTE: This will cause problems if user intended a space in password
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        hideProgressDialog()

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Registered Email
                            val registeredEmail = firebaseUser.email!!

                            Toast.makeText(
                                this@SignupActivity,
                                "Hello $name, you have successfully registered with the email: $registeredEmail.",
                                Toast.LENGTH_SHORT
                            ).show()

                            /**
                             * When new user is registered, they are automatically signed-in. So we sign out the user from firebase and send user
                             * back to intro screen where they can log in again.
                             */

                            FirebaseAuth.getInstance().signOut()
                            // Finish the Sign-Up Screen
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignupActivity,
                                "Registration failed: ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }

    }

    /** function to validate user input **/
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter your display name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter am email address.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password.")
                false
            }
            else -> {
                true
            }
        }
    }
}