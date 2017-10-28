package no.ern.game.item.domain.model


import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Size

@Entity
data class Item(

        @get: NotBlank @get:Size(max=32)
        var name: String,
        @get: NotBlank @get:Size(max=200)
        var description: String,
        //TODO: Change type to enum
        @get: NotBlank @get:Size(max=8)
        var type: String,
        var damageBonus: Long?= null,
        var healthBonus: Long?= null,
        var price: Int,
        var levelRequirement: Int,

        @get:Id @get:GeneratedValue
        var id: Long? = null



){
    //need empty constructor for jpa and JSON -parsing
    constructor():this("","", "", 0, 0, 0, 0)
}