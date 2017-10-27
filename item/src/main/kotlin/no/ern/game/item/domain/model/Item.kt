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
        @get: NotBlank @get:Size(max=64)
        var description: String,
        @get: NotBlank @get:Size(max=8)
        var type: String,
        @get: NotBlank @get:Size(max=32)
        var damageBonus: Long?= null,
        @get: NotBlank @get:Size(max=32)
        var healthBonus: Long?= null,

        @get:Id @get:GeneratedValue
        var id: Long? = null



){
    //need empty constructor for jpa and JSON -parsing
    constructor():this("","", "", 0, 0)
}