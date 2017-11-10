package no.ern.game.gamelogic.domain.model

data class Player(
        var username: String,
        var health: Int,
        var damage: Int,
        var remainingHealth: Int
)
