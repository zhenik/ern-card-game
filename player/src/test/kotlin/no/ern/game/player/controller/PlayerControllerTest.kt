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

//    @Test
//    fun getAllPlayersByLevel() {
//        val playerDto1 = getValidPlayerDtos()[0]
//        val playerDto2 = getValidPlayerDtos()[1]
//        val unusedLevel = getValidPlayerDtos()[2].level
//
//        postPlayerDto(playerDto1, 201)
//        postPlayerDto(playerDto2, 201)
//
//        given().get().then().statusCode(200).body("size()", equalTo(2))
//
//
//        given().param("level", playerDto1.level)
//                .get()
//                .then()
//                .statusCode(200)
//                .body("size()", equalTo(1))
//
//        given().param("level", playerDto2.level)
//                .get()
//                .then()
//                .statusCode(200)
//                .body("size()", equalTo(1))
//
//        given().param("level", unusedLevel)
//                .get()
//                .then()
//                .statusCode(200)
//                .body("size()", equalTo(0))
//
//        val foundPlayer = given().contentType(ContentType.JSON)
//                .param("level", playerDto1.level)
//                .get()
//                .then()
//                .statusCode(200)
//                .extract()
//                .`as`(Array<PlayerDto>::class.java)
//        assertEquals(foundPlayer[0].health, playerDto1.health)
//        assertEquals(foundPlayer[0].currency, playerDto1.currency)
//        assertEquals(foundPlayer[0].experience, playerDto1.experience)
//    }
//
//    @Test
//    fun updatePlayer() {
//        val playerDto1 = getValidPlayerDtos()[0]
//        val playerDto2 = getValidPlayerDtos()[1]
//
//        val wasSuccessful = postPlayerDto(playerDto1, 201)
//        assertEquals(true, wasSuccessful)
//        given().get().then().statusCode(200).body("size()", equalTo(1))
//
//        playerDto2.id = playerDto1.id.toString()
//
//        // Update data to be like playerDto2
//        given().pathParam("id", playerDto1.id)
//                .contentType(ContentType.JSON)
//                .body(playerDto2)
//                .put("/{id}")
//                .then()
//                .statusCode(204)
//
//        // Validate that it changed
//        given().pathParam("id", playerDto2.id)
//                .get("/{id}")
//                .then()
//                .statusCode(200)
//                .body("currency", equalTo(playerDto2.currency))
//                .body("health", equalTo(playerDto2.health))
//                .body("experience", equalTo(playerDto2.experience))
//                .body("id", equalTo(playerDto1.id.toString()))
//
//        given().get().then().statusCode(200).body("size()", equalTo(1))
//    }
//
//    @Test
//    fun updatePlayerChangedId() {
//        val playerDto1 = getValidPlayerDtos()[0]
//        val playerDto2 = getValidPlayerDtos()[1]
//        val player1Id = playerDto1.id?.toLong()
//
//        val wasSuccesful = postPlayerDto(playerDto1, 201)
//        assertEquals(true, wasSuccesful)
//
//
//        // Change ID in dto, but not path.
//        playerDto2.id = (player1Id?.times(2)).toString()
//
//        given().pathParam("id", player1Id)
//                .contentType(ContentType.JSON)
//                .body(playerDto2)
//                .put("/{id}")
//                .then()
//                .statusCode(409)
//
//        val foundPlayer = given().contentType(ContentType.JSON)
//                .pathParam("id", playerDto1.id)
//                .get("/{id}")
//                .then()
//                .statusCode(200)
//                .extract()
//                .`as`(PlayerDto::class.java)
//
//        assertEquals(playerDto1.currency, foundPlayer.currency)
//        assertEquals(playerDto1.health, foundPlayer.health)
//
//        given().pathParam("id", playerDto2.id)
//                .get("/{id}")
//                .then()
//                .statusCode(404)
//
//        given().get().then().statusCode(200).body("size()", equalTo(1))
//    }
//
//
//    @Test
//    fun setCurrencyInvalid() {
//        val playerDto = getValidPlayerDtos()[0]
//        postPlayerDto(playerDto, 201)
//
//
//        // Cannot have negativ id
//        given().pathParam("id", playerDto.id)
//                .body(-20L)
//                .patch("/{id}")
//                .then()
//                .statusCode(400)
//
//        // Empty body
//        given().pathParam("id", playerDto.id)
//                .body(" ")
//                .patch("/{id}")
//                .then()
//                .statusCode(400)
//
//        given().pathParam("id", playerDto.id)
//                .get("/{id}")
//                .then()
//                .statusCode(200)
//                .body("currency", equalTo(playerDto.currency))
//                .body("health", equalTo(playerDto.health))
//                .body("experience", equalTo(playerDto.experience))
//
//    }
//
//    private fun postPlayerDto(playerDto: PlayerDto, expectedStatusCode: Int): Boolean {
//        try {
//            given().contentType(ContentType.JSON)
//                    .body(playerDto)
//                    .post()
//                    .then()
//                    .statusCode(expectedStatusCode)
//
//            return true
//        } catch (e: IllegalStateException) {
//            return false
//        }
//    }

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