package no.ern.game.item.repository

import no.ern.game.item.domain.model.Item
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import no.ern.game.item.domain.enum.Type
import javax.persistence.PersistenceContext


@Repository
interface ItemRepository : CrudRepository<Item,Long>, ItemRepositoryCustom

@Transactional
interface ItemRepositoryCustom {
    fun createItem(
            name: String,
            description: String,
            type: String,
            bonusDamage: Int,
            bonusHealth: Int,
            price: Int,
            levelRequirement: Int
    ): Long

    fun replace(
            name: String,
            description: String,
            type: String,
            bonusDamage: Int,
            bonusHealth: Int,
            price: Int,
            levelRequirement: Int,
            id: Long
    ): Boolean

    fun updateItemName(id: Long, name: String):Boolean
    fun getItemsByType(type: Type): Iterable<Item>
    fun getItemsByLevel(minLevel: Int, maxLevel: Int): Iterable<Item>
    fun getItemsByLevelAndType(minLevel: Int, maxLevel: Int, type: Type): Iterable<Item>
}


open class ItemRepositoryImpl : ItemRepositoryCustom {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun createItem(name: String,
                            description: String,
                            type: String,
                            bonusDamage: Int,
                            bonusHealth: Int,
                            price:Int,
                            levelRequirement: Int
    ): Long {

        val item = Item(
                name,
                description,
                Type.Undefined,
                bonusDamage,
                bonusHealth,
                price,
                levelRequirement
        )

        if (type.toLowerCase() == "Weapon".toLowerCase()) item.type = Type.Weapon
        else if (type.toLowerCase() == "Armor".toLowerCase()) item.type = Type.Armor

        em.persist(item)
        return item.id!!



    }
    override fun replace(
            name: String,
            description: String,
            type: String,
            bonusDamage: Int,
            bonusHealth: Int,
            price: Int,
            levelRequirement: Int,
            id: Long
    ): Boolean {
        val item = em.find(Item::class.java, id) ?: return false
        if (
                name.isNullOrBlank() ||
                description.isNullOrBlank() ||
                !validEnum(type) ||
                price<0 ||
                levelRequirement<0) return false

        item.name = name
        item.description = description
        item.type = Type.valueOf(type)
        item.damageBonus = bonusDamage
        item.healthBonus = bonusHealth
        item.price = price
        item.levelRequirement = levelRequirement
        return true
    }

    override fun updateItemName(
            id: Long,
            name: String
    ): Boolean {
        val item = em.find(Item::class.java, id) ?: return false
        item.name = name
        return true

    }

    override fun getItemsByType(type: Type): Iterable<Item> {
        val query = em.createQuery("select i from Item i where i.type = ?1", Item::class.java)
        query.setParameter(1, type)
        return query.resultList
    }

    override fun getItemsByLevel(minLevel: Int, maxLevel: Int): Iterable<Item>
    {
        val query = em.createQuery("select i from Item i where i.levelRequirement >= ?1 AND i.levelRequirement <= ?2", Item::class.java)
        query.setParameter(1, minLevel)
        query.setParameter(2, maxLevel)
        return query.resultList
    }

    override fun getItemsByLevelAndType(minLevel: Int, maxLevel: Int, type: Type): Iterable<Item>
    {
        val query = em.createQuery("select i from Item i where i.levelRequirement >= ?1 AND i.levelRequirement <= ?2 AND i.type = ?3", Item::class.java)
        query.setParameter(1, minLevel)
        query.setParameter(2, maxLevel)
        query.setParameter(3, type)
        return query.resultList
    }

    fun validEnum(enum: String): Boolean
    {
        if(enum == "Weapon" || enum == "Armor" || enum == "Undefined") return true
        return false
    }

}