package dk.itu.moapd.scootersharing.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.activities.login.LoginListener
import dk.itu.moapd.scootersharing.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        binding.signinButton.setOnClickListener {
            binding.signinButton.isEnabled = false
            binding.signupButton.isEnabled = false
            val email = binding.emailTextField.editText!!.text.trim().toString()
            val password = binding.passwordTextField.editText!!.text.trim().toString()
            auth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    (requireActivity() as LoginListener).onLogin()
                }
                .addOnFailureListener {
                    binding.emailTextField.error = getString(R.string.email_error)
                    binding.passwordTextField.error = getString(R.string.password_error)
                    binding.signinButton.isEnabled = true
                    binding.signupButton.isEnabled = true
                }
        }
        binding.signupButton.setOnClickListener {
            val action = SignInFragmentDirections.actionSigninFragmentToSignupFragment()
            binding.root.findNavController().navigate(action)
        }
        return binding.root
    }
}