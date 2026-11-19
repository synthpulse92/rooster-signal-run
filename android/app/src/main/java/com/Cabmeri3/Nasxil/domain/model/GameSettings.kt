package com.Cabmeri3.Nasxil.domain.model

enum class NightMode {
    AUTO,
    ALWAYS_ON,
    OFF
}

data class GameSettings(
    val nightMode: NightMode = NightMode.AUTO,
    val ghostEnabled: Boolean = true,
    val rookieAssist: Boolean = false
)
