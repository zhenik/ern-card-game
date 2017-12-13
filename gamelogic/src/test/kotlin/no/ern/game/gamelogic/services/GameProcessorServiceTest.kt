//package no.ern.game.gamelogic.services
//
//import no.ern.game.gamelogic.domain.model.Character
//import no.ern.game.schema.dto.gamelogic.FightResultLogDto
//import org.junit.After
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Test
//
//
//class GameProcessorServiceTest {
//
//    private var hero1 : Character? = null
//    private var hero2 : Character? = null
//
//    @Before
//    fun init() {
//        hero1 = Character("1","A",100, 10, 100)
//        hero2 = Character("2","B",80, 8, 80)
//    }
//
//    @After
//    fun tearDown(){
//        hero1 = null
//        hero2 = null
//    }
//
//    @Test
//    fun fightTest1() {
//        // Arrange
//        val gameProcessor = GameProcessorService()
//
//        // Act
//        val fightResult: FightResultLogDto = gameProcessor.fight(attacker = hero1!!, defender = hero2!!)
//
//        // Assert
//        assertTrue(fightResult.gameLog!!.size>3)
//        assertTrue(fightResult.winner!! == "A" || fightResult.winner!! == "B")
//    }
//
//}