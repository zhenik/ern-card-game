package no.ern.game.match.domain.model

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity
data class Match(

        @get: NotBlank @get:Size(max = 32)
        var username1: String,

        @get: NotBlank @get:Size(max = 32)
        var username2: String,

        @get:Min(0)
        var totalDamage1: Long,
        @get:Min(0)
        var totalDamage2: Long,

        @get:Min(0)
        var remainingHealth1: Long,
        @get:Min(0)
        var remainingHealth2: Long,

        var winnerName: String? = null,

        @get:Id @get:GeneratedValue
        var id: Long? = null
) {}