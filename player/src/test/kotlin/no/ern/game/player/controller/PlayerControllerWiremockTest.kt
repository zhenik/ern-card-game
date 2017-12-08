package no.ern.game.player.controller

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import io.restassured.RestAssured
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ern.game.schema.dto.ItemDto
import no.ern.game.schema.dto.PlayerDto
import org.junit.Test

class PlayerControllerWiremockTest : WiremockTestBase() {



    @Test
    fun testWiremockStubResponse() {
        val playerDto1 = PlayerDto(
                "1",
                "Bob",
                null,
                120,
                44,
                30,
                40,
                1,
                listOf(1L, 2L, 3L)
        )
        val savedId = RestAssured.given().contentType(ContentType.JSON)
                .body(playerDto1)
                .post()
                .then()
                .statusCode(201)
                .extract().`as`(Long::class.java)

        wiremockServerItem.stubFor(
                WireMock.get(urlMatching(".*/game/api/items/1"))
                        .willReturn(
                                WireMock.aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json; charset=utf-8")))

        val item = ItemDto(id = "1")

        val response = RestAssured.given()
                .accept(ContentType.JSON)
                .body(item)
                .post("/$savedId/items")

        print(response.print())

        assertEquals(200, response.statusCode)
    }
}