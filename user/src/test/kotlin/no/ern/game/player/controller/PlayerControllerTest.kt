package no.ern.game.player.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.TestCase.assertNotNull
import no.ern.game.player.domain.model.PlayerDto
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PlayerControllerTest : TestBase() {

    @Before
    fun assertThatDatabaseIsEmpty(){
        given().get().then().statusCode(200).body("size()", equalTo(0))
    }

    @Test
    fun createAndGetPlayerByUsername() {
        val playerDto1 = getValidPlayerDtos()[0]
        val playerDto2 = getValidPlayerDtos()[1]

        postPlayerDto(playerDto1, 201)
        postPlayerDto(playerDto2, 201)

        given().get().then().statusCode(200).body("size()", equalTo(2))

        val foundPlayer1 = given().contentType(ContentType.JSON)
                .pathParam("username", playerDto1.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(PlayerDto::class.java)

        assertEquals(playerDto1.username, foundPlayer1.username)
        assertEquals(playerDto1.password, foundPlayer1.password)
        assertEquals(playerDto1.health, foundPlayer1.health)
        assertEquals(playerDto1.equipment, foundPlayer1.equipment)

        val foundPlayer2 = given().contentType(ContentType.JSON)
                .pathParam("username", playerDto2.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(PlayerDto::class.java)

        assertEquals(playerDto2.username, foundPlayer2.username)
        assertEquals(playerDto2.password, foundPlayer2.password)
        assertEquals(playerDto2.health, foundPlayer2.health)
        assertEquals(playerDto2.equipment, foundPlayer2.equipment)
    }

    @Test
    fun createPlayersWithDuplicateUsername() {
        val playerDto = getValidPlayerDtos()[0]

        postPlayerDto(playerDto, 201)
        postPlayerDto(playerDto, 400)

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    @Test
    fun createPlayerWithIdFails() {
        val playerDto = getValidPlayerDtos()[0]
        playerDto.id = "12312312"

        postPlayerDto(playerDto, 400)

        given().get().then().statusCode(200).body("size()", equalTo(0))

    }

    @Test
    fun getAllPlayersByLevel() {
        val playerDto1 = getValidPlayerDtos()[0]
        val playerDto2 = getValidPlayerDtos()[1]
        val unusedLevel = getValidPlayerDtos()[2].level

        postPlayerDto(playerDto1, 201)
        postPlayerDto(playerDto2, 201)

        given().get().then().statusCode(200).body("size()", equalTo(2))


        given().param("level", playerDto1.level)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))

        given().param("level", playerDto2.level)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))

        given().param("level", unusedLevel)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(0))

        val foundPlayer = given().contentType(ContentType.JSON)
                .param("level", playerDto1.level)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<PlayerDto>::class.java)
        assertEquals(foundPlayer[0].username, playerDto1.username)
        assertEquals(foundPlayer[0].password, playerDto1.password)
        assertEquals(foundPlayer[0].experience, playerDto1.experience)
    }

    @Test
    fun updatePlayer() {
        val playerDto1 = getValidPlayerDtos()[0]
        val playerDto2 = getValidPlayerDtos()[1]

        val postedId = postPlayerDto(playerDto1, 201)

        given().get().then().statusCode(200).body("size()", equalTo(1))
        assertNotNull(postedId)
        playerDto2.id = postedId.toString()

        // Update data to be like playerDto2
        given().pathParam("id", postedId)
                .contentType(ContentType.JSON)
                .body(playerDto2)
                .put("/{id}")
                .then()
                .statusCode(204)

        // Validate that it changed
        given().pathParam("username", playerDto2.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo(playerDto2.username))
                .body("password", equalTo(playerDto2.password))
                .body("health", equalTo(playerDto2.health))
                .body("experience", equalTo(playerDto2.experience))
                .body("id", equalTo(postedId.toString()))

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    @Test
    fun updatePlayerChangedId() {
        val playerDto1 = getValidPlayerDtos()[0]
        val playerDto2 = getValidPlayerDtos()[1]

        val postedId = postPlayerDto(playerDto1, 201)
        assertNotNull(postedId)

        // Change ID in dto, but not path.
        playerDto2.id = (postedId?.times(2)).toString()

        given().pathParam("id", postedId)
                .contentType(ContentType.JSON)
                .body(playerDto2)
                .put("/{id}")
                .then()
                .statusCode(409)

        val foundPlayer = given().contentType(ContentType.JSON)
                .pathParam("username", playerDto1.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(PlayerDto::class.java)

        assertEquals(playerDto1.username, foundPlayer.username)
        assertEquals(playerDto1.password, foundPlayer.password)
        assertEquals(playerDto1.health, foundPlayer.health)
        assertEquals(playerDto1.equipment, foundPlayer.equipment)




        given().pathParam("username", playerDto2.username)
                .get("/{username}")
                .then()
                .statusCode(404)

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    @Test
    fun setUsername() {
        val playerDto = getValidPlayerDtos()[0]
        val newUsername = "newUsername"

        val postedId = postPlayerDto(playerDto, 201)

        given().pathParam("id", postedId)
                .body(newUsername)
                .patch("/{id}")
                .then()
                .statusCode(204)


        val foundUser = given().contentType(ContentType.JSON)
                .pathParam("username", newUsername)
                .get("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(PlayerDto::class.java)

        assertEquals(newUsername, foundUser.username)
        assertEquals(playerDto.password, foundUser.password)
        assertEquals(playerDto.health, foundUser.health)
        assertEquals(playerDto.equipment, foundUser.equipment)
    }

    @Test
    fun setUsernameInvalid() {
        val playerDto = getValidPlayerDtos()[0]
        val postedId = postPlayerDto(playerDto, 201)
        val tooLongUsername = getTooLongUsername()


        // Too long username
        given().pathParam("id", postedId)
                .body(tooLongUsername)
                .patch("/{id}")
                .then()
                .statusCode(400)

        // Empty body
        given().pathParam("id", postedId)
                .body(" ")
                .patch("/{id}")
                .then()
                .statusCode(400)

        given().pathParam("username", playerDto.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo(playerDto.username))
                .body("password", equalTo(playerDto.password))
                .body("health", equalTo(playerDto.health))
                .body("experience", equalTo(playerDto.experience))

    }

    @Test
    fun deletePlayerByUsername() {
        val playerDto1 = getValidPlayerDtos()[0]
        val playerDto2 = getValidPlayerDtos()[1]

        postPlayerDto(playerDto1, 201)
        postPlayerDto(playerDto2, 201)
        given().get().then().statusCode(200).body("size()", equalTo(2))


        given().pathParam("username", playerDto1.username)
                .delete("/{username}")
                .then()
                .statusCode(204)
        given().get().then().statusCode(200).body("size()", equalTo(1))

        given().pathParam("username", playerDto2.username)
                .delete("/{username}")
                .then()
                .statusCode(204)
        given().get().then().statusCode(200).body("size()", equalTo(0))
    }

    @Test
    fun deletePlayerWithEmptyUsername() {
        val playerDto = getValidPlayerDtos()[0]
        postPlayerDto(playerDto, 201)

        given().get().then().statusCode(200).body("size()", equalTo(1))

        // Since delete on /game/api/players is not supported, return a 405: Method Not Allowed.
        // Delete is only allowed on /game/api/players/{username}
        given().pathParam("username", "")
                .delete("/{username}")
                .then()
                .statusCode(405)

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    @Test
    fun deletePlayerWithNonExistingUsername() {
        val playerDto = getValidPlayerDtos()[0]
        postPlayerDto(playerDto, 201)

        given().get().then().statusCode(200).body("size()", equalTo(1))
        given().pathParam("username", "golang")
                .delete("/{username}")
                .then()
                .statusCode(404)

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }

    private fun postPlayerDto(playerDto: PlayerDto, expectedStatusCode: Int): Long? {
        return try {
            given().contentType(ContentType.JSON)
                    .body(playerDto)
                    .post()
                    .then()
                    .statusCode(expectedStatusCode)
                    .extract().`as`(Long::class.java)
        } catch (e: IllegalStateException) {
            null
        }
    }

    private fun getValidPlayerDtos(): List<PlayerDto> {
        return listOf(
                PlayerDto(
                        null,
                        "Ruby",
                        "ThisIsAHash",
                        "ThisIsSomeSalt",
                        120,
                        44,
                        30,
                        40,
                        1,
                        listOf(1L, 2L, 3L)
                ),
                PlayerDto(
                        null,
                        "Kotlin",
                        "Spicy language..",
                        "Thisshouldalsobesalted",
                        122,
                        46,
                        33,
                        47,
                        23,
                        listOf(1L, 3L, 2L)
                ),
                PlayerDto(
                        null,
                        "Another language",
                        "Its actually PHP...",
                        "yes, there should be salt here",
                        132,
                        56,
                        38,
                        68,
                        68,
                        listOf(1L, 2L, 3L)
                )
        )
    }

    private fun getTooLongUsername(): String {
        return "somethingLongerThan50Characters".repeat(20)
    }

}