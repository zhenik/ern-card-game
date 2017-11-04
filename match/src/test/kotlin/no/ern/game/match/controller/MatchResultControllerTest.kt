package no.ern.game.match.controller

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.ern.game.schema.dto.MatchResultDto
import no.ern.game.schema.dto.PlayerDto
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.Assert.*


class MatchResultControllerTest : ControllerTestBase(){

    @Test
    fun testCleanDB() {
        RestAssured.given().get().then()
                .statusCode(200)
                .body("size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testCreateMatchResult(){
        // POST /matches
        var dto = getValidMatchResultDto()

        // valid dto
        val id = RestAssured.given().contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(201)
                .extract().`as`(Long::class.java)
        assertNotNull(id)

        // invalid dto
        RestAssured.given().contentType(ContentType.JSON)
                .body("dasd")
                .post()
                .then()
                .statusCode(400)

        // constraint failure
        dto.attacker!!.health = -1
        RestAssured.given().contentType(ContentType.JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(422)
    }

    @Test
    fun testGetMatchesResults(){
        //Arrange
        val dto = getValidMatchResultDto()
        val id = postNewMatchResultValid(dto)

        // GET /matches
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get().then().statusCode(200)
                .body("size()", CoreMatchers.equalTo(1))

        // GET /mathces?username=exist
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .param("username",dto.attacker!!.username)
                .get().then().statusCode(200)
                .body("size()", CoreMatchers.equalTo(1))

        //GET /matches?username=nonexist
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .param("username","nonexist")
                .get().then()
                .statusCode(200).body("size()", CoreMatchers.equalTo(0))
    }

    @Test
    fun testGetResultMatch(){
        // Arrange
        val dto = getValidMatchResultDto()
        val id = postNewMatchResultValid(dto)

        // GET /matches/:id
        val dtoRespone1 = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(MatchResultDto::class.java)
        assertTrue(dto.attacker!!.username==dtoRespone1.attacker!!.username)

        //GET /matchers/invalid_input
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id","invalid_input")
                .get("/{id}")
                .then()
                .statusCode(400)

        // not found in db
        //GET /matchers/555
        val notExistMatchId = 555
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",notExistMatchId)
                .get("{id}")
                .then()
                .statusCode(404)
    }

    @Test
    fun testDeleteMatchResult(){
        // Arrange
        val dto = getValidMatchResultDto()
        val id = postNewMatchResultValid(dto)

        // DELETE /matches/:id
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .delete("/{id}")
                .then()
                .statusCode(204)

        // DELETE /matches/invalid_input
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id","invalid_input")
                .delete("/{id}")
                .then()
                .statusCode(400)

        //DELETE /matchers/555
        val notExistMatchId = 555
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",notExistMatchId)
                .delete("{id}")
                .then()
                .statusCode(404)
    }

    @Test
    fun testUpdateMatchResult_Success(){
        // Arrange
        val id = postNewMatchResultValid(getValidMatchResultDto())
        val dto2 = MatchResultDto(
                PlayerDto("superman", 30, 28, 5),
                PlayerDto("batman", 25, 25, -3),
                "superman",
                id.toString())


        //PUT matches/:id
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .body(dto2)
                .put("/{id}")
                .then()
                .statusCode(204)

        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("attacker.username", CoreMatchers.equalTo("superman"))
                .body("defender.username", CoreMatchers.equalTo("batman"))
    }

    @Test
    fun testUpdateMatchResult_Failed(){
        // Arrange
        val id = postNewMatchResultValid(getValidMatchResultDto())
        val dto = MatchResultDto(
                PlayerDto("superman", 30, 28, 5),
                PlayerDto("batman", 25, 25, -3),
                "superman",
                id.toString())



        // dto id=null
        dto.id=null
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .body(dto)
                .put("/{id}")
                .then()
                .statusCode(404)


        // dto.id != pathParam
        dto.id= 123123123.toString()
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .body(dto)
                .put("/{id}")
                .then()
                .statusCode(409)

        // nonexisting entity
        dto.id= 123123123.toString()
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", dto.id)
                .body(dto)
                .put("/{id}")
                .then()
                .statusCode(404)

        // entity constraint (total health negative)
        dto.id=id.toString()
        dto.attacker!!.username=""
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .body(dto)
                .put("/{id}")
                .then()
                .statusCode(400)
    }

    @Test
    fun testUpdateWinnerName(){
        // Arrange
        val dto = MatchResultDto(
                PlayerDto("superman", 30, 28, 5),
                PlayerDto("batman", 25, 25, -3),
                "superman")
        val id = postNewMatchResultValid(dto)
        val newWinnerName = "batman"

        assertNotEquals(dto.winnerName, newWinnerName)

        // Act
            // not valid -> wrong paramValue
        RestAssured.given()
                .pathParam("id", "abrakadabra")
                .body(newWinnerName)
                .patch("/{id}")
                .then()
                .statusCode(400)

            // not valid -> non-existing matchResult with given id
        RestAssured.given()
                .pathParam("id", 123123123)
                .body(newWinnerName)
                .patch("/{id}")
                .then()
                .statusCode(404)

            // not valid -> empty newWinnerName
        RestAssured.given()
                .pathParam("id", id)
                .body("")
                .patch("/{id}")
                .then()
                .statusCode(400)

            // valid
        RestAssured.given()
                .pathParam("id", id)
                .body(newWinnerName)
                .patch("/{id}")
                .then()
                .statusCode(204)

        // Assert
        val dtoResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .pathParam("id",id)
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .`as`(MatchResultDto::class.java)

        assertEquals(newWinnerName,dtoResponse.winnerName)

    }

}