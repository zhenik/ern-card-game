package no.ern.game.player.controller

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.restassured.RestAssured
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.logging.Logger

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
abstract class WiremockTestBase {

    private val logger : Logger = Logger.getLogger(WiremockTestBase::class.java.canonicalName)

    companion object {
        lateinit var wiremockServerItem: WireMockServer

        @BeforeClass
        @JvmStatic
        fun initClass() {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 8081
            RestAssured.basePath = "/game/api/players"
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

            wiremockServerItem = WireMockServer(WireMockConfiguration.wireMockConfig().port(8083).notifier(ConsoleNotifier(true)))

            wiremockServerItem.start()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            wiremockServerItem.stop()
        }
    }
}