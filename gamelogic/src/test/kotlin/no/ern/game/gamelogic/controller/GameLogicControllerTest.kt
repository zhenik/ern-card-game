package no.ern.game.gamelogic.controller

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.ern.game.schema.dto.gamelogic.PlayerSearchDto
import org.junit.Assert.*
import org.junit.Test
import org.springframework.beans.factory.annotation.Value

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
        val json = getMockedJson_Empty()
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

}