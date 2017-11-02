package no.ern.game.item.repository

import no.ern.game.item.domain.model.Item
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import no.ern.game.item.domain.enum.Type
import javax.persistence.PersistenceContext

//TODO: Update to reflect DTO and Item

@Repository
interface ItemRepository : CrudRepository<Item,Long>, ItemRepositoryCustom

@Transactional
interface ItemRepositoryCustom {
    fun createItem(
            name: String,
            description: String,
            type: String,
            bonusDamage: Long,
            bonusHealth: Long,
            price: Int,
            levelRequirement: Int
    ): Long

    fun getItemsByType(type: Type): Iterable<Item>
}


open class ItemRepositoryImpl : ItemRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createItem(name: String,
                            description: String,
                            type: String,
                            bonusDamage: Long,
                            bonusHealth: Long,
                            price:Int,
                            levelRequirement: Int
    ): Long {

        if(type == "Weapon")
        {
            val item = Item(
                    name,
                    description,
                    Type.Weapon,
                    bonusDamage,
                    bonusHealth,
                    price,
                    levelRequirement
            )
            em.persist(item)
            return item.id!!
        }

        else
        {
            val item = Item(
                    name,
                    description,
                    Type.Armor,
                    bonusDamage,
                    bonusHealth,
                    price,
                    levelRequirement
            )
            em.persist(item)
            return item.id!!
        }


    }

    override fun getItemsByType(type: Type): Iterable<Item> {
        val query = em.createQuery("select i from Item i where i.type = ?1", Item::class.java)
        query.setParameter(1, type)
        return query.resultList
    }

}