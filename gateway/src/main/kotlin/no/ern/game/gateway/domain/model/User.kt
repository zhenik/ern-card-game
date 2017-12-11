package no.ern.game.gateway.domain.model

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name="USERS")
class User(

        @get:NotBlank
        @get: Column (unique = true)
        var username: String?,

        @get:NotBlank
        var password: String?,

        @get:ElementCollection
        @get:NotNull
        var roles: Set<String>? = setOf(),

        @get:NotNull
        var enabled: Boolean? = true,

        @get: Id
        @get: GeneratedValue
        var id: Long? = null
)