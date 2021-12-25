/*
 * Copyright (C) 2021-2021 Team Amaze - Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com>. All Rights reserved.
 *
 * This file is part of Amaze File Utilities.
 *
 * 'Amaze File Utilities' is a registered trademark of Team Amaze. All other product
 * and company names mentioned are trademarks or registered trademarks of their respective owners.
 */

package com.amaze.fileutilities.home_page.ui.files

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amaze.fileutilities.R
import com.amaze.fileutilities.databinding.FragmentFilesBinding
import com.amaze.fileutilities.home_page.ui.MediaTypeView
import com.amaze.fileutilities.utilis.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.ramijemli.percentagechartview.callback.AdaptiveColorProvider

class FilesFragment : Fragment() {

    private val filesViewModel: FilesViewModel by activityViewModels()
    private var _binding: FragmentFilesBinding? = null
    private var mediaFileAdapter: MediaFileAdapter? = null
    private var preloader: MediaAdapterPreloader? = null
    private var recyclerViewPreloader: RecyclerViewPreloader<String>? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private val MAX_PRELOAD = 100

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFilesBinding.inflate(
            inflater, container,
            false
        )
        val root: View = binding.root
        binding.storagePercent.isSaveEnabled = false

        filesViewModel.run {
            internalStorageStats.observe(
                viewLifecycleOwner,
                {
                    it?.run {
                        val usedSpace = FileUtils.formatStorageLength(
                            this@FilesFragment.requireContext(),
                            it.usedSpace!!
                        )
                        val freeSpace = FileUtils.formatStorageLength(
                            this@FilesFragment.requireContext(),
                            it.freeSpace!!
                        )
                        binding.usedSpace.setColorAndLabel(
                            colorProvider
                                .provideProgressColor(it.progress.toFloat()),
                            usedSpace
                        )
                        binding.freeSpace.setColorAndLabel(
                            colorProvider
                                .provideBackgroundBarColor(it.progress.toFloat()),
                            freeSpace
                        )
                        binding.storagePercent.setProgress(
                            it.progress.toFloat(),
                            true
                        )
                        if (it.items == 0) {
                            binding.filesAmount.text = resources.getString(
                                R.string.num_of_files,
                                resources.getString(R.string.undetermined)
                            )
                        } else {
                            binding.filesAmount.text =
                                resources.getString(R.string.num_of_files, it.items.toString())
                        }
                    }
                }
            )
            usedImagesSummaryTransformations.observe(
                viewLifecycleOwner,
                {
                    metaInfoAndSummaryPair ->
                    metaInfoAndSummaryPair?.let {
                        val storageSummary = metaInfoAndSummaryPair.first
                        val usedSpace =
                            FileUtils.formatStorageLength(
                                this@FilesFragment.requireContext(),
                                storageSummary.usedSpace!!
                            )
                        binding.imagesTab.setProgress(
                            MediaTypeView.MediaTypeContent(
                                storageSummary.items, usedSpace,
                                storageSummary.progress
                            )
                        )
                    }
                }
            )
            usedAudiosSummaryTransformations.observe(
                viewLifecycleOwner,
                {
                    metaInfoAndSummaryPair ->
                    metaInfoAndSummaryPair?.let {
                        val storageSummary = metaInfoAndSummaryPair.first
                        val usedSpace = FileUtils
                            .formatStorageLength(
                                this@FilesFragment.requireContext(),
                                storageSummary.usedSpace!!
                            )
                        binding.audiosTab.setProgress(
                            MediaTypeView.MediaTypeContent(
                                storageSummary.items, usedSpace,
                                storageSummary.progress
                            )
                        )
                    }
                }
            )
            usedVideosSummaryTransformations.observe(
                viewLifecycleOwner,
                {
                    metaInfoAndSummaryPair ->
                    metaInfoAndSummaryPair?.let {
                        val storageSummary = metaInfoAndSummaryPair.first
                        val usedSpace = FileUtils
                            .formatStorageLength(
                                this@FilesFragment.requireContext(),
                                storageSummary.usedSpace!!
                            )
                        binding.videosTab.setProgress(
                            MediaTypeView
                                .MediaTypeContent(
                                    storageSummary.items,
                                    usedSpace, storageSummary.progress
                                )
                        )
                    }
                }
            )
            usedDocsSummaryTransformations.observe(
                viewLifecycleOwner,
                {
                    metaInfoAndSummaryPair ->
                    metaInfoAndSummaryPair?.let {
                        val storageSummary = metaInfoAndSummaryPair.first
                        val usedSpace = FileUtils.formatStorageLength(
                            this@FilesFragment
                                .requireContext(),
                            storageSummary.usedSpace!!
                        )
                        binding.documentsTab.setProgress(
                            MediaTypeView
                                .MediaTypeContent(
                                    storageSummary.items,
                                    usedSpace, storageSummary.progress
                                )
                        )
                    }
                }
            )
            recentFilesLiveData.observe(
                viewLifecycleOwner,
                {
                    mediaFileInfoList ->
                    mediaFileInfoList?.run {
                        preloader = MediaAdapterPreloader(applicationContext)
                        val sizeProvider = ViewPreloadSizeProvider<String>()
                        recyclerViewPreloader = RecyclerViewPreloader(
                            Glide.with(applicationContext),
                            preloader!!,
                            sizeProvider,
                            MAX_PRELOAD
                        )
                        linearLayoutManager = LinearLayoutManager(context)
                        mediaFileAdapter = MediaFileAdapter(
                            applicationContext,
                            preloader!!,
                            MediaFileListSorter.SortingPreference(
                                MediaFileListSorter.GROUP_NAME,
                                MediaFileListSorter.SORT_SIZE,
                                true,
                                true
                            ),
                            this, true
                        )
                        binding.recentFilesList
                            .addOnScrollListener(recyclerViewPreloader!!)
                        binding.recentFilesList.layoutManager = linearLayoutManager
                        binding.recentFilesList.adapter = mediaFileAdapter
                    }
                }
            )
        }

        binding.storagePercent.setAdaptiveColorProvider(colorProvider)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    var colorProvider: AdaptiveColorProvider = object : AdaptiveColorProvider {
        override fun provideProgressColor(progress: Float): Int {
            return when {
                progress <= 25 -> {
                    resources.getColor(R.color.green)
                }
                progress <= 50 -> {
                    resources.getColor(R.color.yellow)
                }
                progress <= 75 -> {
                    resources.getColor(R.color.orange)
                }
                else -> {
                    resources.getColor(R.color.red)
                }
            }
        }

        override fun provideBackgroundColor(progress: Float): Int {
            // This will provide a bg color that is
            // 80% darker than progress color.
            // return ColorUtils.blendARGB(provideProgressColor(progress),
            // Color.BLACK, .8f)
            return resources.getColor(R.color.white_translucent_2)
        }

        override fun provideTextColor(progress: Float): Int {
            return resources.getColor(R.color.white)
        }

        override fun provideBackgroundBarColor(progress: Float): Int {
            return ColorUtils.blendARGB(
                provideProgressColor(progress),
                Color.BLACK, .5f
            )
        }
    }
}
