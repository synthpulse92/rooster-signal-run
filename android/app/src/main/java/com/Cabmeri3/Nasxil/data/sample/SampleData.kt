package com.Cabmeri3.Nasxil.data.sample

import com.Cabmeri3.Nasxil.domain.model.LayerType
import com.Cabmeri3.Nasxil.domain.model.Upgrade
import com.Cabmeri3.Nasxil.domain.model.UpgradeId

object SampleData {

    val upgradeCatalogue = listOf(
        Upgrade(UpgradeId.REFLECTIVE_VEST, 40),
        Upgrade(UpgradeId.LAMP_TIMER, 60),
        Upgrade(UpgradeId.FERRY_CLEATS, 80)
    )

    val masteryOrder = listOf(LayerType.ROAD, LayerType.RAIL, LayerType.FERRY)

    fun upgradeOf(id: UpgradeId): Upgrade = upgradeCatalogue.first { it.id == id }
}
