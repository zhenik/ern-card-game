package no.ern.game.gateway.domain.model

import org.hibernate.validator.constraints.NotBlank
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotNull

/**
 * game
 * NIK on 11/12/2017
 */
class UserDto(
    var username: String?=null,
    var password: String?=null,
    var roles: Set<String>? = null,
    var enabled: Boolean? = null,
    var id: Long? = null

):Serializable