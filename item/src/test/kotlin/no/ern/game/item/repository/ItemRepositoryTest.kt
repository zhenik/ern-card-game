package no.ern.game.item.repository

import no.ern.game.item.domain.enum.Type
import no.ern.game.item.domain.model.Item
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
class MatchResultRepositoryTest {

    @Autowired
    private lateinit var repo: ItemRepository


    @Test
    fun testCreateItemValid()
    {
        val id = createItem("Sword", "Weapon")
        assertNotNull(id)
        assertTrue(id != (-1L))
    }

    @Test
    fun testCreateItemWrongType()
    {
        // Sets incorrect enum values to equal Undefined
        val id = createItem("Sword", "blabla")
        val sword = repo.findOne(id)
        assertEquals(sword!!.type, Type.Undefined)

    }

    @Test
    fun testReplaceItem()
    {
        val id = createItem("Sword", "Weapon")

        assertTrue(replaceItem(id))

        val item = repo.findOne(id)
        assertEquals(item!!.name, "Chest plate")
        assertEquals(item.type, Type.Armor)
        assertEquals(item.price, 200)

    }

    @Test
    fun testUpdateItemName()
    {
        val id = createItem("Sword", "Weapon")
        val item = repo.findOne(id)
        val newName = "Blade"

        assertEquals(item!!.name, "Sword")

        assertTrue(repo.updateItemName(id, newName))

        assertEquals(item.name, newName)
    }

    @Test
    fun testGetItemsByType()
    {
        createItems()

        val weapons = repo.getItemsByType(Type.Weapon)
        val armors = repo.getItemsByType(Type.Armor)
        val restItems = repo.getItemsByType(Type.Undefined)

        assertEquals(3, weapons.count())
        assertEquals(2, armors.count())
        assertEquals(0, restItems.count())


    }

    @Test
    fun testGetItemsByLevel()
    {
        createItems()

        val itemsLevel1To3 = repo.getItemsByLevel(1, 3)
        val itemsLevel1To5 = repo.getItemsByLevel(1, 5)

        // there are 3 items between level 1 and 3
        assertEquals(3, itemsLevel1To3.count())
        // by expanding our reach to level 5, it includes another item
        assertEquals(4, itemsLevel1To5.count())
    }

    @Test
    fun testGetItemsByLevelAndType()
    {
        createItems()

        val itemLevel1To5 = repo.getItemsByLevel(1, 5)
        val weaponsLevel1To5 = repo.getItemsByLevelAndType(1, 5, Type.Weapon)
        val armorsLevel1To5 = repo.getItemsByLevelAndType(1, 5, Type.Armor)

        assertEquals(4, itemLevel1To5.count())
        // by specifying weapon, one armor is filtered away
        assertEquals(3, weaponsLevel1To5.count())
        // by specifying armor, three weapons are filtered away
        assertEquals(1, armorsLevel1To5.count())

    }

    private fun createItems()
    {
        repo.createItem("Blade", "Short, sharp weapon", "Weapon", 5, 10, 700, 1)
        repo.createItem("Sword", "Sharp weapon", "Weapon", 10, 20, 1500, 2)
        repo.createItem("Greatsword", "Long, sharp weapon", "Weapon", 20, 0, 3000, 3)
        repo.createItem("Chestplate", "Armor covering upper region", "Armor", 20, 0, 3000, 5)
        repo.createItem("Chestplate of the gods", "Armor covering upper region, granting incredible boons", "Armor", 50, 50, 30000, 30)

    }

    private fun createItem(name: String, type: String) : Long {
        return repo.createItem(
                name,
                "BlaBla",
                type,
                10,
                20,
                1500,
                2)
    }

    private fun replaceItem(id: Long): Boolean
    {
        return repo.replace("Chest plate", "Armor covering upper region", "Armor", 2, 1, 200, 0, id)
    }
}