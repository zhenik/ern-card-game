package no.ern.game.player.domain.model

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
data class Player(

        @get:NotBlank
        @get:Size(max = 50)
        @get:Column(unique = true)
        var username: String,

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
        var items: MutableSet<Long> = mutableSetOf(),

        @get:Id
        @get: GeneratedValue
        var id: Long? = null
)
