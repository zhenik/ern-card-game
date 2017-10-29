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

        postUserDto(userDto, 400)

        given().get().then().statusCode(200).body("size()", equalTo(0))

    }

    @Test
    fun getAllUsersByLevel() {
        given().get().then().statusCode(200).body("size()", equalTo(0))

        val userDto1 = getValidUserDtos()[0]
        val userDto2 = getValidUserDtos()[1]
        val unusedLevel = getValidUserDtos()[2].level
        postUserDto(userDto1, 201)
        postUserDto(userDto2, 201)

        given().get().then().statusCode(200).body("size()", equalTo(2))


        given().param("level", userDto1.level)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))

        given().param("level", userDto2.level)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))

        given().param("level", unusedLevel)
                .get()
                .then()
                .statusCode(200)
                .body("size()", equalTo(0))

        //Why does this get all fields as an "array" ??? [username] instead of username
        /*given().contentType(ContentType.JSON)
                .param("level", userDto1.level)
                .get()
                .then()
                .statusCode(200)
                .body("username", equalTo(userDto1.username))
                .body("password", equalTo(userDto1.password))
                .body("experience", equalTo(userDto1.experience))*/
    }

    private fun postUserDto(userDto2: UserDto, expectedStatusCode: Int): String {
        return given().contentType(ContentType.JSON)
                .body(userDto2)
                .post()
                .then()
                .statusCode(expectedStatusCode)
                .extract().asString()
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
                ),
                UserDto(
                        null,
                        "Another language",
                        "Its actually PHP...",
                        "yes, there should be salt here",
                        132,
                        56,
                        38,
                        68,
                        68,
                        listOf()
                )
        )
    }

}