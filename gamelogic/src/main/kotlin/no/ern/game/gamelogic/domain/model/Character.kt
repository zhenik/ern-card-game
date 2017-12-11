package no.ern.game.gamelogic.domain.model

data class Character(
        var playerId: String,
        var username: String,
        var health: Int,
        var damage: Int,
        var remainingHealth: Int
)
