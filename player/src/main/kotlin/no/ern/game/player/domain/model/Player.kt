package no.ern.game.player.domain.model

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity
data class Player(
        var health: Int = 100,

        @get:Min(1)
        var damage: Int = 10,

        @get:Min(0)
        var currency: Int = 0,

        @get:Min(0)
        var experience: Int = 0,

        @get:Min(1)
        @get:Max(100)
        var level: Int = 1,

        @get:ElementCollection
        var equipment: Collection<Long> = listOf(),

        @get:Id
        var id: Long
)
