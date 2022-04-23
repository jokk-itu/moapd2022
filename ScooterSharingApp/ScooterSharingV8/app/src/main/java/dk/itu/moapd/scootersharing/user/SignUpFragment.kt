package dk.itu.moapd.scootersharing.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.databinding.FragmentSignupBinding

class SignUpFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(layoutInflater)

        binding.signupButton.setOnClickListener {
            val email = binding.emailTextField.editText!!.text.trim().toString()
            val password = binding.passwordTextField.editText!!.text.trim().toString()
            auth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
                    binding.root.findNavController().navigate(action)
                }
                .addOnFailureListener {
                    binding.emailTextField.error = getString(R.string.email_error)
                    binding.passwordTextField.error = getString(R.string.password_error)
                }
        }

        return binding.root
    }
}