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

    @Value("\${gameApis.player.path}")
    private lateinit var usersPath: String

    @Value("\${gameApis.match.path}")
    private lateinit var matchPath: String


    @Test
    fun checkEnv(){

        // Act
        val response = RestAssured
                .given()
                .contentType(ContentType.TEXT)
                .get("/me")

        // Assert
        assertEquals("UP",response.body.asString().trim())
        assertEquals(200,response.statusCode)
    }

    @Test
    fun wireMockTest_StubResponse(){
        // Arrange
        val line = "long line"
        wiremockServerMatch.stubFor(
                WireMock.get(
                        urlMatching(".*/string"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withBody(line)))

        //Act
        val response = RestAssured.given().get("/chain")

        // Assert
        assertEquals(200,response.statusCode)
        assertTrue(response.asString().contains(line))

    }

    @Test
    fun testFindOpponent(){
        // Arrange
        val json = getMockedJson_PlayerSearch()
        wiremockServerUser.stubFor(
                WireMock.get(
                        urlMatching(".*/players"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody(json)))

        // Act
        val response = RestAssured.given().accept(ContentType.JSON).get("/hunting")
        val playerSearchDto = response.`as`(PlayerSearchDto::class.java)

        // Assert
        assertEquals(200, response.statusCode)
        assertEquals("guy", playerSearchDto.username)
        assertEquals("1", playerSearchDto.id)
        assertEquals(1, playerSearchDto.level)
    }

}