package no.ern.game.gamelogic.domain.model

data class Character(
        var username: String,
        var health: Int,
        var damage: Int,
        var remainingHealth: Int
)
