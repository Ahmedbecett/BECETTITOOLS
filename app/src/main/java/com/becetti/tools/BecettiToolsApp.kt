package com.becetti.tools

import android.app.Application
import androidx.room.Room
import com.becetti.tools.data.local.database.BecettiDatabase
import com.becetti.tools.monetization.AdsManager
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class BecettiToolsApp : Application() {
    lateinit var database: BecettiDatabase
        private set
    lateinit var adsManager: AdsManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, BecettiDatabase::class.java, "becetti.db")
            .fallbackToDestructiveMigration()
            .build()
        runCatching { PDFBoxResourceLoader.init(applicationContext) }
        adsManager = AdsManager(this)
        adsManager.initialize()
    }
}
