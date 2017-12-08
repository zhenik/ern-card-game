package no.ern.game.player.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ern.game.schema.dto.PlayerDto
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PlayerControllerTest : TestBase() {

    @Before
    fun assertThatDatabaseIsEmpty() {
        given().get().then().statusCode(200).body("size()", equalTo(0))
    }

    @Test
    fun createAndGetPlayer_Valid() {
        val playerDto1 = getValidPlayerDtos()[0]

        val savedId = given().contentType(ContentType.JSON)
                .body(playerDto1)
                .post()
                .then()
                .statusCode(201)
                .extract().`as`(Long::class.java)

        given().get().then().statusCode(200).body("size()", equalTo(1))

        val foundPlayer1 = given().contentType(ContentType.JSON)
                .pathParam("id", savedId)
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(PlayerDto::class.java)

        assertEquals(playerDto1.health, foundPlayer1.health)
        assertEquals(playerDto1.currency, foundPlayer1.currency)
    }

    @Test
    fun createAndGetPlayer_Invalid() {
        val playerDto1 = getValidPlayerDtos()[0]
        playerDto1.currency = -20

        val savedId = given().contentType(ContentType.JSON)
                .body(playerDto1)
                .post()
                .then()
                .statusCode(400)

        // Check that nothing was saved
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
        assertEquals(foundPlayer[0].health, playerDto1.health)
        assertEquals(foundPlayer[0].currency, playerDto1.currency)
        assertEquals(foundPlayer[0].experience, playerDto1.experience)
    }

    @Test
    fun getPlayerByUsername() {
        val playerDto1 = getValidPlayerDtos()[0]
        postPlayerDto(playerDto1, 201)

        given().get().then().statusCode(200).body("size()", equalTo(1))


        // Check username that doesnt exist
        val firstResult = given().contentType(ContentType.JSON)
                .param("username", playerDto1.username?.repeat(10))
                .get()
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<PlayerDto>::class.java)
        assertEquals(0, firstResult.count())

        val secondResult = given().contentType(ContentType.JSON)
                .param("username", playerDto1.username)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .`as`(Array<PlayerDto>::class.java)

        assertEquals(1, secondResult.count())
        assertEquals(playerDto1.currency, secondResult[0].currency)
        assertEquals(playerDto1.health, secondResult[0].health)
    }

    @Test
    fun updatePlayer() {
        val playerDto1 = getValidPlayerDtos()[0]
        val playerDto2 = getValidPlayerDtos()[1]

        val savedId = given().contentType(ContentType.JSON)
                .body(playerDto1)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .`as`(Long::class.java)

        // Set id to match id after creating player.
        playerDto1.id = savedId.toString()
        playerDto2.id = savedId.toString()
        given().get().then().statusCode(200).body("size()", equalTo(1))


        // Update data to be like playerDto2
        given().pathParam("id", savedId)
                .contentType(ContentType.JSON)
                .body(playerDto2)
                .put("/{id}")
                .then()
                .statusCode(204)

        // Validate that it changed
        given().pathParam("id", savedId)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("currency", equalTo(playerDto2.currency))
                .body("health", equalTo(playerDto2.health))
                .body("experience", equalTo(playerDto2.experience))

        given().get().then().statusCode(200).body("size()", equalTo(1))
    }


    @Test
    fun updateCurrency() {
        val playerDto = getValidPlayerDtos()[0]

        val savedId = given().contentType(ContentType.JSON)
                .body(playerDto)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .`as`(Long::class.java)

        // Cannot have negative id
        given().contentType(ContentType.TEXT)
                .pathParam("id", savedId)
                .body(-20L)
                .patch("/{id}")
                .then()
                .statusCode(400)

        // Empty body
        given().contentType(ContentType.TEXT)
                .pathParam("id", savedId)
                .body(" ")
                .patch("/{id}")
                .then()
                .statusCode(400)

        // Verify that it did not change
        given().pathParam("id", savedId)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("currency", equalTo(playerDto.currency))
                .body("health", equalTo(playerDto.health))
                .body("experience", equalTo(playerDto.experience))

    }

    @Test
    fun deletePlayer() {
        val playerDto = getValidPlayerDtos()[0]
        val savedId = given().contentType(ContentType.JSON)
                .body(playerDto)
                .post()
                .then()
                .statusCode(201)
                .extract()
                .`as`(Long::class.java)
        given().get().then().statusCode(200).body("size()", equalTo(1))

        given().contentType(ContentType.JSON)
                .pathParam("id", savedId)
                .delete("/{id}")
                .then()
                .statusCode(204)
        given().get().then().statusCode(200).body("size()", equalTo(0))
    }

    private fun postPlayerDto(playerDto: PlayerDto, expectedStatusCode: Int) {
        given().contentType(ContentType.JSON)
                .body(playerDto)
                .post()
                .then()
                .statusCode(expectedStatusCode)
    }

    private fun getValidPlayerDtos(): List<PlayerDto> {
        return listOf(
                PlayerDto(
                        "1",
                        "Bob",
                        null,
                        120,
                        44,
                        30,
                        40,
                        1,
                        listOf(1L, 2L, 3L)
                ),
                PlayerDto(
                        "5",
                        "Robert",
                        null,
                        122,
                        46,
                        33,
                        47,
                        23,
                        listOf(1L, 3L, 2L)
                ),
                PlayerDto(
                        "10",
                        "Carl the man",
                        null,
                        132,
                        56,
                        38,
                        68,
                        68,
                        listOf(1L, 2L, 3L)
                )
        )
    }

}