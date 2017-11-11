package no.ern.game.gamelogic.controller

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Test

class GameLogicControllerTest : ControllerTestBase() {

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
}