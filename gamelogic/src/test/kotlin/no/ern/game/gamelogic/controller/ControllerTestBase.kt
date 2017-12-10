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
//@ActiveProfiles("test") //to override properties
abstract class ControllerTestBase {

    private val logger : Logger = Logger.getLogger(ControllerTestBase::class.java.canonicalName)

    companion object {
        lateinit var wiremockServerMatch: WireMockServer
        lateinit var wiremockServerPlayer: WireMockServer
        private lateinit var wiremockServerItem: WireMockServer

        @BeforeClass
        @JvmStatic
        fun initClass() {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 9084
            RestAssured.basePath = "/game/api/play"
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


    fun getMockedJson_PlayerSearch(): String {
        var json = """
            [
                {
                    "userId": "1",
                    "username": "name",
                    "id": "1",
                    "health": 100,
                    "damage": 10,
                    "currency": 100,
                    "experience": 0,
                    "level": 1,
                    "items": []
                }
            ]
        """
        return json
    }

    fun getMockedJson_Empty(): String {
        var json = "[]"
        return json
    }

}
