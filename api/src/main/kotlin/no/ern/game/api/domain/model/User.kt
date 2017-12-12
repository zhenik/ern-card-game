package no.ern.game.api.domain.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class User {

    @Id
    @GeneratedValue
    var id: Long? = null

    @Column(unique = true)
    var username: String? = null

    var password: String? = null

    constructor(username: String, password: String) {
        this.username = username
        this.password = password
    }
}
