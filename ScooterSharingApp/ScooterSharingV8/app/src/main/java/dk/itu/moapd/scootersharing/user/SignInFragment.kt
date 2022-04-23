package dk.itu.moapd.scootersharing.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.R
import dk.itu.moapd.scootersharing.databinding.FragmentSigninBinding


class SignInFragment : Fragment() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentSigninBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSigninBinding.inflate(layoutInflater)
        binding.signinButton.setOnClickListener {
            val email = binding.emailTextField.editText!!.text.trim().toString()
            val password = binding.passwordTextField.editText!!.text.trim().toString()
            auth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val action = SignInFragmentDirections.actionSignInFragmentToScooters()
                    binding.root.findNavController().navigate(action)
                }
                .addOnFailureListener {
                    binding.emailTextField.error = getString(R.string.email_error)
                    binding.passwordTextField.error = getString(R.string.password_error)
                }
        }
        binding.signupButton.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            binding.root.findNavController().navigate(action)
        }
        return binding.root
    }
}