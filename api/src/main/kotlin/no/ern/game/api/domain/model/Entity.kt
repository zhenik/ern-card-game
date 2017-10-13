package no.ern.game.api.domain.model

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Size

/**
 * game
 * NIK on 13/10/2017
 */
@Entity
data class Entity(
        @get: NotBlank @get:Size(max=32)
        var field1: String,

        @get: NotBlank @get:Size(max=32)
        var field2: String,

        @get:Id @get:GeneratedValue
        var id: Long? = null
){
     //need empty constructor for jpa and JSON -parsing
    constructor():this("","")

    fun fullName(): String = field1+field2
}