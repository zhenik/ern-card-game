package no.ern.game.match.repository

import no.ern.game.match.domain.model.MatchResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import javax.validation.ConstraintViolationException

@RunWith(SpringRunner::class)
@DataJpaTest
class MatchResultRepositoryTest {

    @Autowired
    private lateinit var crud: MatchResultRepository

    @Before
    fun setup(){
        assertEquals(0, crud.count())
    }

    private fun createMatchResultValid() : Long {
        return crud.createMatchResult(
                "u1",
                "u2",
                25,
                20,
                20,
                15,
                5,
                0,
                "u1")
    }
    private fun createMatchForUsername(username: String) : Long {
         return crud.createMatchResult(
                username,
                "u2",
                25,
                20,
                20,
                15,
                5,
                0,
                "u1")
    }
    private fun createMatch(match: MatchResult): MatchResult? {
        return crud.save(match)
    }


    @Test
    fun testCreateMatchresult_Valid() {

        //Act
        val id = createMatchResultValid()
        //Assert
        assertNotNull(id)
        assertTrue(id!=(-1L))
    }

    @Test
    fun testCreate3MatchResults_Valid() {

        //Act
        val id1 = createMatchResultValid()
        val id2 = createMatchResultValid()
        val id3 = createMatchResultValid()

        //Assert
        assertNotNull(id1)
        assertNotNull(id2)
        assertNotNull(id3)
        assertEquals(3, crud.count())
    }

    @Test
    fun testCreateMatchResult_TotalDamageNotValid() {


        //Act
        try{
            crud.createMatchResult(
                    "u1",
                    "u2",
                    25,
                    20,
                    -20,
                    15,
                    5,
                    0,
                    "u1")
            fail()
        }
        catch (e : ConstraintViolationException){}

        try{
            crud.createMatchResult(
                    "u1",
                    "u2",
                    25,
                    20,
                    20,
                    -15,
                    5,
                    0,
                    "u1")
            fail()
        }
        catch (e : ConstraintViolationException){}
    }

    @Test
    fun testCreateResultMatch_UsernameNotValid() {

        //Act
        // 1 Blank
        try{
            crud.createMatchResult(
                    "",
                    "u2",
                    25,
                    20,
                    20,
                    -15,
                    5,
                    0,
                    "u1")
            fail()
        }
        catch (e : ConstraintViolationException){}

        // 2 longer than 32
        val longName = "s".repeat(33)
        assertTrue(longName.length>32)
        try{
            crud.createMatchResult(
                    longName,
                    "u2",
                    25,
                    20,
                    20,
                    -15,
                    5,
                    0,
                    "u1")
            fail()
        }
        catch (e : ConstraintViolationException){}
    }

    @Test
    fun testGetMatchesForUsername(){

        //Arrange
        val username = "Alesha"
        createMatchForUsername(username)
        createMatchForUsername(username)
        createMatchForUsername(username)
        createMatchForUsername("someRandomGuy")

        //Act
        // auto-generated
        val list1 : List<MatchResult> = crud.findAllByAttackerUsernameOrDefenderUsername(username, username) as List<MatchResult>
        // custom
        val list2 : List<MatchResult> = crud.getMatchesByUserName(username) as List<MatchResult>

        //Assert
        assertEquals(3, list1.size)
        assertEquals(3, list2.size)
        assertEquals(4, crud.count())
        assertTrue(list1.containsAll(list2))
    }

    @Test
    fun testFindAllByWinnerName(){

        //Arrange
        val winnerName = "yohohoho"
        val match1= MatchResult(
                winnerName,
                "u1",
                25,
                20,
                20,
                15,
                5,
                0,
                winnerName)
        val match2= MatchResult(
                "u1",
                winnerName,
                25,
                20,
                20,
                15,
                5,
                0,
                winnerName)
        crud.save(match1)
        crud.save(match2)
        createMatchResultValid()
        createMatchResultValid()

        //Act
        val list: List<MatchResult> = crud.findAllByWinnerName(winnerName) as List<MatchResult>

        //Assert
        assertEquals(4, crud.count())
        assertEquals(2, list.size)
        assertTrue(list.stream().allMatch { it.winnerName.equals(winnerName) })
    }

    @Test
    fun testUpdateMatchResult(){
        //Arrange
        val winnerName = "yohohoho"
        val match1= MatchResult(
                winnerName,
                "u1",
                25,
                20,
                20,
                15,
                5,
                0,
                winnerName)
        crud.save(match1)
        val id = match1.id!!

        //Act
        val updated = crud.update(
                "superman",
                "batman",
                25,
                20,
                20,
                15,
                5,
                0,
                "superman",
                id)

        //Assert
        assertTrue(updated)
        assertTrue(crud.findAll().toList().stream().allMatch { "superman"==it.attackerUsername})
        assertTrue(crud.findAll().toList().stream().allMatch { "batman"==it.defenderUsername})
        assertEquals(1,crud.count())
    }

    @Test
    fun testUpdateMatchResult_InvalidInput(){
        //Arrange
        val winnerName = "yohohoho"
        val match1= MatchResult(
                winnerName,
                "u1",
                25,
                20,
                20,
                15,
                5,
                0,
                winnerName)
        crud.save(match1)
        val id = match1.id!!

        //Act



        val updated =crud.update(
                "",
                "batman",
                5,
                20,
                20,
                15,
                5,
                0,
                "superman",
                id)

        //Assert
        assertFalse(updated)
        assertTrue(crud.findAll().toList().stream().allMatch { "yohohoho"==it.attackerUsername})
        assertTrue(crud.findAll().toList().stream().allMatch { "u1"==it.defenderUsername})
        assertEquals(1,crud.count())
    }

    @Test
    fun testUpdateMatchResult_changeName(){
        //Arrange
        val winnerName = "u2"
        val match1 = MatchResult(
                winnerName,
                "u1",
                25,
                20,
                20,
                15,
                5,
                0,
                winnerName)
        crud.save(match1)
        val id = match1.id!!
        assertEquals(1,crud.count())

        // Act
        val winnerNameUpdated = crud.updateWinnerName(id,"batman")

        // Assert
        val matchFromDb = crud.findOne(id)

        assertEquals(1,crud.count())
        assertTrue(winnerNameUpdated)
        assertEquals("batman",matchFromDb.winnerName)
    }

    @Test
    fun testUpdateWinnerNameInvalidName(){
        //Arrange
        val winnerName = "u2"
        val match1 = MatchResult(
                winnerName,
                "u1",
                25,
                20,
                20,
                15,
                5,
                0,
                winnerName)
        crud.save(match1)
        val id = match1.id!!
        assertEquals(1,crud.count())

        // Act
        val winnerNameUpdated = crud.updateWinnerName(id,"")

        // Assert
        val matchFromDb = crud.findOne(id)

        assertEquals(1,crud.count())
        assertFalse(winnerNameUpdated)
        assertEquals("u2",matchFromDb.winnerName)
    }

    @Test
    fun testUpdateWinnerName(){
        // Arrange
        val match1 = MatchResult(
                "u2",
                "u1",
                25,
                20,
                20,
                15,
                5,
                0,
                "u2")
        crud.save(match1)
        val id = match1.id!!
        assertEquals(1,crud.count())


        // Act
            // valid
        val winnerNameUpdated = crud.updateWinnerName(id,"batman")
            // invalid
        val winnerNameUpdated1 = crud.updateWinnerName(id,"")
            // invalid
        val winnerNameUpdated2 = crud.updateWinnerName(123123123123,"superman")


        // Assert
        val matchFromDb = crud.findOne(id)
        assertEquals(1,crud.count())
        assertTrue(winnerNameUpdated)
        assertFalse(winnerNameUpdated1)
        assertFalse(winnerNameUpdated2)
        assertEquals("batman", matchFromDb.winnerName)
    }

}