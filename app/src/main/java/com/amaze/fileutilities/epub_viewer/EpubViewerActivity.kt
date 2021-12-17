package com.amaze.fileutilities.epub_viewer

import android.os.Bundle
import android.util.Log
import com.amaze.fileutilities.PermissionActivity
import com.amaze.fileutilities.R
import com.amaze.fileutilities.databinding.EpubViewerActivityBinding
import com.amaze.fileutilities.utilis.getFileFromUri
import com.folioreader.Config
import com.folioreader.FolioReader


class EpubViewerActivity: PermissionActivity() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        EpubViewerActivityBinding.inflate(layoutInflater)
    }
    private lateinit var epubModel: LocalEpubModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        if (savedInstanceState == null) {
            val mimeType = intent.type
            val epubUri = intent.data
            Log.i(javaClass.simpleName, "Loading epub from path ${epubUri?.path} " +
                    "and mimetype $mimeType")
            epubModel = LocalEpubModel(uri = epubUri!!, mimeType = mimeType!!)
            val config: Config = Config()
                .setDirection(Config.Direction.HORIZONTAL)
                .setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL)
                .setFontSize(1)
                .setNightMode(false)
                .setThemeColorInt(resources.getColor(R.color.blue))
                .setShowTts(false)
            FolioReader.get()
                .setConfig(config, true)
                .openBook(epubUri.getFileFromUri(this)!!.canonicalPath)
        }
    }
}