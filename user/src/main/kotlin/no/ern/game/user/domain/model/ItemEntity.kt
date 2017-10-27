package no.ern.game.user.domain.model

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class ItemEntity(
        @get:NotBlank
        var name: String,

        @get:Id @get:GeneratedValue
        var id: Long? = null
) {
        constructor():this("")
}
