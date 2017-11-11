package no.ern.game.match.domain.model

import org.hibernate.validator.constraints.NotBlank
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity
@MatchResultConstraint
data class MatchResult(

        @get: NotBlank @get:Size(max = 32)
        var attackerUsername: String,
        @get: NotBlank @get:Size(max = 32)
        var defenderUsername: String,

        @get:Min(0)
        var attackerHealth: Int,
        @get:Min(0)
        var defenderHealth: Int,

        @get:Min(0)
        var attackerTotalDamage: Int,
        @get:Min(0)
        var defenderTotalDamage: Int,

        // can be negative
        var attackerRemainingHealth: Int,
        var defenderRemainingHealth: Int,

        var winnerName: String? = null,

        var creationTime: ZonedDateTime? = null,

        @get:Id @get:GeneratedValue
        var id: Long? = null
)