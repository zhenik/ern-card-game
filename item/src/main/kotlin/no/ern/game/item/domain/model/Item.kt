package no.ern.game.item.domain.model


import org.hibernate.validator.constraints.NotBlank
import no.ern.game.item.domain.enum.Type
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity
data class Item(

        @get: NotBlank @get:Size(max=32)
        var name: String,
        @get: NotBlank @get:Size(max=200)
        var description: String,
        @Enumerated(EnumType.STRING)
        var type: Type,
        var damageBonus: Int?= null,
        var healthBonus: Int?= null,
        @get:Min(0)
        var price: Int,
        @get:Min(0)
        var levelRequirement: Int,

        @get:Id @get:GeneratedValue
        var id: Long? = null



)