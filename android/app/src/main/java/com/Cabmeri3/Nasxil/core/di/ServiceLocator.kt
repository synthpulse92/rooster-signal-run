package com.Cabmeri3.Nasxil.core.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.Cabmeri3.Nasxil.data.local.RunPreferences
import com.Cabmeri3.Nasxil.data.repository.GhostRepositoryImpl
import com.Cabmeri3.Nasxil.data.repository.ProgressRepositoryImpl
import com.Cabmeri3.Nasxil.data.repository.SettingsRepositoryImpl
import com.Cabmeri3.Nasxil.domain.usecase.GenerateRouteUseCase
import com.Cabmeri3.Nasxil.domain.usecase.PurchaseUpgradeUseCase
import com.Cabmeri3.Nasxil.domain.usecase.ResolveCrossingUseCase

object ServiceLocator {

    private var cachedFactory: AppViewModelFactory? = null

    fun init(context: Context) {
        if (cachedFactory != null) return
        val storage = RunPreferences(context.applicationContext)
        cachedFactory = AppViewModelFactory(
            progress = ProgressRepositoryImpl(storage),
            settings = SettingsRepositoryImpl(storage),
            ghost = GhostRepositoryImpl(storage),
            generateRoute = GenerateRouteUseCase(),
            resolveCrossing = ResolveCrossingUseCase(),
            purchaseUpgrade = PurchaseUpgradeUseCase()
        )
    }

    val factory: ViewModelProvider.Factory
        get() = checkNotNull(cachedFactory)
}
