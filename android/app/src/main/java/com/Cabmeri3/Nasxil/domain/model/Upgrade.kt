package com.Cabmeri3.Nasxil.domain.model

enum class UpgradeId {
    REFLECTIVE_VEST,
    LAMP_TIMER,
    FERRY_CLEATS
}

data class Upgrade(
    val id: UpgradeId,
    val cost: Int
)
