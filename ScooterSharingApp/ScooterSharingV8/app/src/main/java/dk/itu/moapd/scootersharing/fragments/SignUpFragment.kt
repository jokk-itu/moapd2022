package dk.itu.moapd.scootersharing.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.activities.login.LoginListener
import dk.itu.moapd.scootersharing.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)

        binding.signupButton.setOnClickListener {
            binding.signupButton.isEnabled = false
            val name = binding.nameTextField.editText!!.text.trim().toString()
            if(name.isBlank()) {
                binding.nameTextField.error = getString(R.string.name_error)
                return@setOnClickListener
            }
            val email = binding.emailTextField.editText!!.text.trim().toString()
            val password = binding.passwordTextField.editText!!.text.trim().toString()
            auth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val request = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                    FirebaseAuth.getInstance().currentUser!!.updateProfile(request)
                    (requireActivity() as LoginListener).onLogin()
                }
                .addOnFailureListener {
                    binding.emailTextField.error = getString(R.string.email_error)
                    binding.passwordTextField.error = getString(R.string.password_error)
                    binding.signupButton.isEnabled = true
                }
        }

        return binding.root
    }
}