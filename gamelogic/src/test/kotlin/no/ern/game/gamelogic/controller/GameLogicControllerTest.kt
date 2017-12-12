package no.ern.game.gamelogic.controller

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.ern.game.schema.dto.gamelogic.FightResultLogDto
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto
import no.ern.game.schema.dto.gamelogic.PlayersFightIdsDto
import org.junit.Assert.*
import org.junit.Test

class GameLogicControllerTest : ControllerTestBase() {

    @Test
    fun testFindOpponent_Valid() {
        // Arrange
        val json = getMockedJson_PlayerSearch()
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players"))
                            .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(json)))

        // Act
        val response = RestAssured.given().accept(ContentType.JSON).get("/enemy")
        val playerSearchDto = response.`as`(PlayerSearchDto::class.java)

        // Assert
        assertEquals(200, response.statusCode)
        assertEquals("name", playerSearchDto.username)
        assertEquals("1", playerSearchDto.id)
        assertEquals(1, playerSearchDto.level)
    }


    @Test
    fun testFindOpponent_NotPlayersFound() {
        // Arrange
        val json = getMockedJson_EmptyArray()
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(json)))

        // Act
        val response = RestAssured.given().accept(ContentType.JSON).get("/enemy")


        // Assert
        assertEquals(404, response.statusCode)
    }


    /**
     * FIGHT cases
     * */

    @Test
    fun fight_GivenPayloadInvalid(){

        // Arrange
        val invalidFigthIdsDto = PlayersFightIdsDto("1","1")

        // Act
        val response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(invalidFigthIdsDto)
                .post("/fight")

        // Act
        assertEquals(400, response.statusCode)

    }

    @Test
    fun fight_OnePlayerNotFound() {

        // Arrange
        val player1Dto = getPlayerDto("1", "name1","")
        val playersFightIdsDto = PlayersFightIdsDto("1","2")

        // found player 1
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(player1Dto)))
        // NOT found player 2
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withStatus(404)))

        // Act
        val response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(playersFightIdsDto)
                .post("/fight")

        // Assert
        assertEquals(404, response.statusCode)
    }

    @Test
    fun fight_NoItems_Success() {

        // Arrange
        val playerDto1 = getPlayerDto("1", "name1", "")
        val playerDto2 = getPlayerDto("2", "name2", "")
        val playersFightIdsDto = PlayersFightIdsDto("1","2")

        // found player 1
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(playerDto1)))
        // found player 2
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(playerDto2)))

        // new match result created
        wiremockServerMatch.stubFor(
                WireMock.post(
                        urlMatching(".*/matches"))
                        .willReturn(WireMock.aResponse().withStatus(201)))


        // Act
        val response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(playersFightIdsDto)
                .post("/fight")
        // get fight log
        val fightResultLogDto = response.then().extract().`as`(FightResultLogDto::class.java)


        // Assert
        assertEquals(200, response.statusCode)
        assertTrue(fightResultLogDto.winner=="name1" || fightResultLogDto.winner=="name2")
    }

    @Test
    fun fight_WithItems_Success() {
        // Arrange
        val query1 = "1"
        val query2 = "1,2"
        val playerDto1 = getPlayerDto("1", "name1", query1)
        val playerDto2 = getPlayerDto("2", "name2", query2)
        val playersFightIdsDto = PlayersFightIdsDto("1","2")
        val items1 = getJsonOneItems()
        val items2 = getJsonTwoItems()

        // found player 1
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(playerDto1)))
        // found player 2
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(playerDto2)))

        // items found for player 1 (I can't use `?` because its spec symbol in regEx)
        wiremockServerItem.stubFor(
                WireMock.get(
                        urlMatching(".*1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(items1))
        )
        // items found for player 2
        wiremockServerItem.stubFor(
                WireMock.get(
                        urlMatching(".*1,2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(items2))
        )



        // new match result created
        wiremockServerMatch.stubFor(
                WireMock.post(
                        urlMatching(".*/matches"))
                        .willReturn(WireMock.aResponse().withStatus(201)))


        // Act
        val response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(playersFightIdsDto)
                .post("/fight")

        // get fight log
        val fightResultLogDto = response.then().extract().`as`(FightResultLogDto::class.java)


        // Assert
        assertEquals(200, response.statusCode)
        assertTrue(fightResultLogDto.winner=="name1" || fightResultLogDto.winner=="name2")
        // player 2 total life is 112 (because of items bonus), and base damage for both players 15 (10 + 5 (items bonus) )
        assertTrue(fightResultLogDto.toString().contains("/112") && fightResultLogDto.toString().contains("[15]"))
    }

    // items present on player, but have not found on item module
    @Test
    fun fight_WithItems_ItemsConflict() {
        // Arrange
        val query1 = "1"
        val query2 = "1,2"
        val playerDto1 = getPlayerDto("1", "name1", query1)
        val playerDto2 = getPlayerDto("2", "name2", query2)
        val playersFightIdsDto = PlayersFightIdsDto("1","2")
        val items1 = getJsonOneItems()

        // found player 1
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(playerDto1)))
        // found player 2
        wiremockServerPlayer.stubFor(
                WireMock.get(
                        urlMatching(".*/players/2"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(playerDto2)))

        // items found for player 1 (I can't use `?` because its spec symbol in regEx)
        wiremockServerItem.stubFor(
                WireMock.get(
                        urlMatching(".*1"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(items1))
        )
        // items found for player 2
        wiremockServerItem.stubFor(
                WireMock.get(
                        urlMatching(".*1,2"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(404))
        )


        // Act
        val response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(playersFightIdsDto)
                .post("/fight")

        // Assert
        assertEquals(404, response.statusCode)

    }


}