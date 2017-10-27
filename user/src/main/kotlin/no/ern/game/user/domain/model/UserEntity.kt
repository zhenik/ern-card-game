package no.ern.game.user.domain.model

import org.hibernate.validator.constraints.NotBlank
import java.sql.Blob
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
data class UserEntity(

        @get:NotBlank
        @get:Size(max = 50)
        @get:Column(unique = true)
        var username: String,

        @get:NotBlank
        var password: String,

        var salt: String = "",

        @get:NotBlank
        var health: Int = 100,

        @get:NotBlank
        var damage: Int = 100,

        var avatar: Blob? = null,

        @get:NotBlank
        @get:Size(min = 0)
        var currency: Int = 0,

        @get:NotBlank
        @get:Size(min = 0)
        var experience: Int = 0,

        @get:NotBlank
        @get:Size(min = 0)
        var level: Int = 1,

        @get:NotNull @get:ElementCollection
        var equipment: Collection<ItemEntity> = listOf(),

        @get:Id @get:GeneratedValue
        var id: Long? = null
) {
        constructor():this("", "", "", 0, 0, null, 0, 0, 1)
}
