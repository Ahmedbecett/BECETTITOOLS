package com.becetti.tools

import android.app.Application
import androidx.room.Room
import com.becetti.tools.data.local.database.BecettiDatabase
import com.becetti.tools.data.local.store.ActivityStore
import com.becetti.tools.data.local.store.BusinessStore
import com.becetti.tools.data.local.store.ProjectStore
import com.becetti.tools.data.repository.ToolRepository
import com.becetti.tools.monetization.AdsManager
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class BecettiToolsApp : Application() {
    lateinit var database: BecettiDatabase
        private set
    lateinit var adsManager: AdsManager
        private set

    /** Project manager state (projects, kanban tasks, budgets, attachments). */
    val projectStore: ProjectStore by lazy { ProjectStore(this) }

    /** Business toolkit state (customers, products, invoices, quotes, expenses). */
    val businessStore: BusinessStore by lazy { BusinessStore(this) }

    /** Recent activity trail for the profile/dashboard surfaces. */
    val activityStore: ActivityStore by lazy { ActivityStore(this) }

    /** Favorites, recents, calculation history and notes (Room-backed). */
    val toolRepository: ToolRepository by lazy {
        ToolRepository(
            database.favoriteDao(),
            database.historyDao(),
            database.recentToolDao(),
            database.noteDao()
        )
    }

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
