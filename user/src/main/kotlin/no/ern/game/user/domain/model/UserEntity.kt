package no.ern.game.user.domain.model

import org.hibernate.validator.constraints.NotBlank
import java.sql.Blob
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
data class UserEntity(

        @get:NotBlank @get:Size(max = 50)
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

        //@get:NotNull
        //var equipment: Collection<Item> = listOf(),

        @get:Id @get:GeneratedValue
        var id: Long? = null
)

/*
- password
- salt (Ask ANDREA)
- health
- damage
______________
- matches [Collection<Match> -> Match_State)]
- avatar (binary || URL)
- currency
- experience
- level
- equipment [Collection<Item>]
 */