package no.ern.game.user.domain.model

import org.hibernate.validator.constraints.NotBlank
import java.sql.Blob
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity
data class User(

        @get:NotBlank
        @get:Size(max = 50)
        @get:Column(unique = true)
        var username: String,

        @get:NotBlank
        var password: String,

        var salt: String = "",

        var health: Int = 100,

        @get:Min(1)
        var damage: Int = 10,

        var avatar: Blob? = null,

        @get:Min(0)
        var currency: Int = 0,

        @get:Min(0)
        var experience: Int = 0,

        @get:Min(1)
        @get:Max(100)
        var level: Int = 1,

        @get:ElementCollection
        var equipment: Collection<ItemEntity> = listOf(),

        @get:Id
        @get:GeneratedValue
        var id: Long? = null
) {
    constructor() : this("", "", "", 0, 0, null, 0, 0, 1)
}
