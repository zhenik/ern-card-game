package no.ern.game.gamelogic.controller

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.logging.Logger


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
abstract class ControllerTestBase {

    private val logger: Logger = Logger.getLogger(ControllerTestBase::class.java.canonicalName)

    companion object {
        lateinit var wiremockServerMatch: WireMockServer
        lateinit var wiremockServerPlayer: WireMockServer
        lateinit var wiremockServerItem: WireMockServer

        @BeforeClass
        @JvmStatic
        fun initClass() {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 9084
            RestAssured.basePath = "/play"
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

            wiremockServerMatch = WireMockServer(WireMockConfiguration.wireMockConfig().port(8082).notifier(ConsoleNotifier(true)))
            wiremockServerPlayer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8081).notifier(ConsoleNotifier(true)))
            wiremockServerItem = WireMockServer(WireMockConfiguration.wireMockConfig().port(8083).notifier(ConsoleNotifier(true)))


            wiremockServerMatch.start()
            wiremockServerPlayer.start()
            wiremockServerItem.start()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            wiremockServerMatch.stop()
            wiremockServerPlayer.stop()
            wiremockServerItem.stop()
        }
    }


//    fun getMockedJson_PlayerSearch(): String {
//        var json = """
//            [
//                {
//                    "userId": "1",
//                    "username": "foo",
//                    "id": "1",
//                    "health": 100,
//                    "damage": 10,
//                    "currency": 100,
//                    "experience": 0,
//                    "level": 1,
//                    "items": []
//                }
//            ]
//        """
//    }

    fun getMockedJson_FooByUsername(): String {
        var json = """
        [
            {
                "username": "foo",
                "id": "1",
                "health": 100,
                "damage": 5,
                "currency": 20,
                "experience": 20,
                "level": 1,
                "items": []
            }
        ]
        """
        return json
    }

    fun getMockedJson_FooAndBar(): String {
        var json = """
        [
            {
                "username": "foo",
                "id": "1",
                "health": 100,
                "damage": 5,
                "currency": 20,
                "experience": 20,
                "level": 1,
                "items": []
            },
            {
                "username": "bar",
                "id": "2",
                "health": 209,
                "damage": 10,
                "currency": 20,
                "experience": 20,
                "level": 1,
                "items": []
            }
        ]
        """
        return json
    }

    fun getMockedJson_BarById(): String {
        var json = """

            {
                "username": "bar",
                "id": "2",
                "health": 209,
                "damage": 10,
                "currency": 20,
                "experience": 20,
                "level": 1,
                "items": []
            }

        """
        return json
    }

    fun getMockedJson_EmptyArray(): String {
        var json = "[]"
        return json
    }

    // only for tests
    fun getPlayerDto(id: String, username: String, items: String): String {
        var json = """
        {
            "userId": "$id",
            "username": "$username",
            "id": "1",
            "health": 100,
            "damage": 10,
            "currency": 100,
            "experience": 0,
            "level": 1,
            "items": [$items]
        }
        """
        return json
    }

    fun getEmptyObject(): String {
        var json = "{}"
        return json
    }

    fun getJsonOneItems(): String {
        var json = """
            [
                {
                    "name": "Test",
                    "description": "blabla",
                    "type": "Weapon",
                    "damageBonus": 5,
                    "healthBonus": 0,
                    "price": 0,
                    "levelRequirement": 0,
                    "id": "1"
                }
            ]
            """
        return json
    }

    fun getJsonTwoItems(): String {
        var json = """
            [
                {
                    "name": "Test",
                    "description": "blabla",
                    "type": "Weapon",
                    "damageBonus": 5,
                    "healthBonus": 0,
                    "price": 0,
                    "levelRequirement": 0,
                    "id": "1"
                },
                {
                    "name": "Test",
                    "description": "blabla",
                    "type": "Weapon",
                    "damageBonus": 0,
                    "healthBonus": 12,
                    "price": 0,
                    "levelRequirement": 0,
                    "id": "2"
                }
            ]
            """
        return json
    }

}