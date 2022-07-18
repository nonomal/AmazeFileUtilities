/*
 * Copyright (C) 2021-2022 Team Amaze - Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com>. All Rights reserved.
 *
 * This file is part of Amaze File Utilities.
 *
 * 'Amaze File Utilities' is a registered trademark of Team Amaze. All other product
 * and company names mentioned are trademarks or registered trademarks of their respective owners.
 */

package com.amaze.fileutilities.home_page

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amaze.fileutilities.R
import com.amaze.fileutilities.databinding.WelcomePermissionPrivacyLayoutBinding
import com.amaze.fileutilities.utilis.Utils
import com.amaze.fileutilities.utilis.showToastInCenter
import com.stephentuso.welcome.WelcomeFinisher

class PermissionFragmentWelcome : Fragment() {
    private var _binding: WelcomePermissionPrivacyLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WelcomePermissionPrivacyLayoutBinding.inflate(
            inflater, container,
            false
        )
        val root: View = binding.root
        binding.privacyPolicyButton.setOnClickListener {
            Utils.openURL(Utils.URL_PRIVACY_POLICY, requireContext())
        }
        binding.grantStorageButton.setOnClickListener {
            val activity = requireActivity() as WelcomeScreen
            activity.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission(onPermissionGranted, true)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        requestAllFilesAccess(onPermissionGranted)
                    }
                }
            }
        }
        binding.doneButton.setOnClickListener {
            val activity = requireActivity() as WelcomeScreen
            activity.run {
                if (!activity.haveStoragePermissions()) {
                    showToastInCenter(getString(R.string.grantfailed))
                } else if (!binding.licenseAgreementCheckbox.isChecked) {
                    showToastInCenter(getString(R.string.license_agreement_not_accepted))
                } else {
                    WelcomeFinisher(this@PermissionFragmentWelcome).finish()
                    val action = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    action.addCategory(Intent.CATEGORY_LAUNCHER)
                    startActivity(action)
                }
            }
        }
        binding.licenseAgreementText.setOnClickListener {
            binding.licenseAgreementCheckbox.isChecked = !binding.licenseAgreementCheckbox.isChecked
        }
        binding.licenseAgreementText.text = Html.fromHtml(getString(R.string.license_agreement))
        Linkify.addLinks(binding.licenseAgreementText, Linkify.WEB_URLS)
        binding.licenseAgreementText.movementMethod = LinkMovementMethod.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !Utils.isIgnoringBatteryOptimizations(requireContext())
        ) {
            binding.removeOptimizationsParent.visibility = View.VISIBLE
            binding.removeOptimizationsButton.setOnClickListener {
                Utils.invokeNotOptimizeBatteryScreen(requireContext())
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
