package np.edu.ismt.rishavchudal.ismt_2024_seca.dashboard

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import np.edu.ismt.rishavchudal.ismt_2024_seca.LoginActivity
import np.edu.ismt.rishavchudal.ismt_2024_seca.R
import np.edu.ismt.rishavchudal.ismt_2024_seca.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        setUpViews()
        return binding.root
    }

    private fun setUpViews() {
        binding.tvUserName.text = "Sample User"
        binding.mbLogOut.setOnClickListener {
            setUpLogOutButton()
        }
    }

    private fun setUpLogOutButton() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Alert")
            .setMessage("Are you sure want to logout?")
            .setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialogInterface,
                                                  i ->
                    logOut()
                })
            .setNegativeButton(
                "No",
                DialogInterface.OnClickListener { dialogInterface,
                                                  i ->
                    dialogInterface.dismiss()
                })
            .show()
    }

    private fun logOut() {
        //setting shared preference of login to false
        val sharedPreferences = requireActivity().getSharedPreferences(
            "app",
            Context.MODE_PRIVATE
        )
        val sharedPrefEditor = sharedPreferences.edit()
        sharedPrefEditor.putBoolean("isLoggedIn", false)
        sharedPrefEditor.apply()

        //Navigating to login Page
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}