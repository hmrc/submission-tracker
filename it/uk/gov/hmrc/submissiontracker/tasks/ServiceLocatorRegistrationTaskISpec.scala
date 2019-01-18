package uk.gov.hmrc.submissiontracker.tasks

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlMatching, verify}
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import play.api.libs.json.Json
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.api.domain.Registration
import uk.gov.hmrc.submissiontracker.stubs.ServiceLocatorStub._
import uk.gov.hmrc.submissiontracker.support.BaseISpec

class ServiceLocatorRegistrationTaskISpec extends BaseISpec with Eventually with ScalaFutures with FutureAwaits with DefaultAwaitTimeout {

  def regPayloadStringFor(serviceName: String, serviceUrl: String): String =
    Json.toJson(Registration(serviceName, serviceUrl, Some(Map("third-party-api" -> "true")))).toString

  "ServiceLocatorRegistrationTask" should {
    val task = app.injector.instanceOf[ServiceLocatorRegistrationTask]

    "register with the api platform" in {
      registrationWillSucceed()
      await(task.register) shouldBe true
      verify(
        1,
        postRequestedFor(urlMatching("/registration"))
          .withHeader("content-type", equalTo("application/json"))
          .withRequestBody(equalTo(regPayloadStringFor("submission-tracker", "https://submission-tracker.protected.mdtp")))
      )
    }

    "handle errors" in {
      registrationWillFail()
      await(task.register) shouldBe false
    }
  }
}
