package no.ern.game.api.repository

import no.ern.game.api.domain.model.Entity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Repository
interface EntityRepository : CrudRepository<Entity, Long>, EntityRepositoryCustom {}

@Transactional
interface EntityRepositoryCustom {
    fun createEntity(field1: String, field2: String): Long
}

open class EntityRepositoryImpl : EntityRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createEntity(field1: String, field2: String): Long {
        val entity = Entity(field1, field2)
        em.persist(entity)
        return entity.id!!
    }
}
