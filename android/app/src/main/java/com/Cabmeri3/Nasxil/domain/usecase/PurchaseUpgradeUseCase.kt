package com.Cabmeri3.Nasxil.domain.usecase

import com.Cabmeri3.Nasxil.domain.model.PlayerProgress
import com.Cabmeri3.Nasxil.domain.model.Upgrade

class PurchaseUpgradeUseCase {

    operator fun invoke(progress: PlayerProgress, upgrade: Upgrade): PlayerProgress? {
        if (progress.owned.contains(upgrade.id)) return null
        if (progress.stamps < upgrade.cost) return null
        return progress.copy(
            stamps = progress.stamps - upgrade.cost,
            owned = progress.owned + upgrade.id
        )
    }
}
