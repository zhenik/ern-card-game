package no.ern.game.item.repository

import no.ern.game.item.domain.model.Item
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Blob
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
interface ItemRepository : CrudRepository<Item,Long>, ItemRepositoryCustom

@Transactional
interface ItemRepositoryCustom {
    fun createItem(name: String,
                   description: String,
                   type: String,
                   bonusDamage: Long,
                   bonusHealth: Long): Long
}

open class ItemRepositoryImpl : ItemRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createItem(name: String,
                            description: String,
                            type: String,
                            bonusDamage: Long,
                            bonusHealth: Long
    ): Long {

        val item = Item(
                name,
                description,
                type,
                bonusDamage,
                bonusHealth
        )
        em.persist(item)
        return item.id!!
    }

}