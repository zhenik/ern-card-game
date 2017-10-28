package no.ern.game.user.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ern.game.user.domain.dto.UserDto
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test

class UserControllerTest : TestBase() {

    @Test
    fun createUser() {
        given().get().then().statusCode(200).body("size()", equalTo(0))

        val userDto = getValidUserDtos()[0]

        val id = given().contentType(ContentType.JSON)
                .body(userDto)
                .post()
                .then()
                .statusCode(201)
                .extract().asString()

        given().get().then().statusCode(200).body("size()", equalTo(1))

        given().pathParam("username", userDto.username)
                .get("/{username}")
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("username", equalTo(userDto.username))
                .body("password", equalTo(userDto.password))
                .body("experience", equalTo(userDto.experience))

    }

    @Test
    fun createUserWithId() {
        given().get().then().statusCode(200).body("size()", equalTo(0))

        val userDto = getValidUserDtos()[0]
        userDto.id = "12312312"

        given().contentType(ContentType.JSON)
                .body(userDto)
                .post()
                .then()
                .statusCode(400)
                .extract().asString()

        given().get().then().statusCode(200).body("size()", equalTo(0))

    }

    @Test
    fun getAllUsersByLevel() {

    }

    private fun getValidUserDtos(): List<UserDto> {
        return listOf(
                UserDto(
                        null,
                        "Ruby",
                        "ThisIsAHash",
                        "ThisIsSomeSalt",
                        120,
                        44,
                        30,
                        40,
                        1,
                        listOf()
                ),
                UserDto(
                        null,
                        "Kotlin",
                        "Spicy language..",
                        "Thisshouldalsobesalted",
                        122,
                        46,
                        33,
                        47,
                        23,
                        listOf()
                )
        )
    }

}