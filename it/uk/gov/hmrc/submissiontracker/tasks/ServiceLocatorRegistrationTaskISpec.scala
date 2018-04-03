package uk.gov.hmrc.submissiontracker.tasks

import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, postRequestedFor, urlMatching, verify}
import it.utils.WiremockServiceLocatorSugar
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import play.api.test.PlayRunners
import uk.gov.hmrc.submissiontracker.stubs.ServiceLocatorStub._
import uk.gov.hmrc.submissiontracker.support.BaseISpec

class ServiceLocatorRegistrationTaskISpec extends BaseISpec with Eventually with WiremockServiceLocatorSugar with ScalaFutures with PlayRunners {
  "ServiceLocatorRegistrationTask" should {
    val task = app.injector.instanceOf[ServiceLocatorRegistrationTask]

    "register with the api platform" in {
      registrationWillSucceed()
      await(task.register) shouldBe true
      verify(1,
        postRequestedFor(urlMatching("/registration")).withHeader("content-type", equalTo("application/json")).
          withRequestBody(equalTo(regPayloadStringFor("submission-tracker", "http://submission-tracker.protected.mdtp")))
      )
    }

    "handle errors" in {
      registrationWillFail()
      await(task.register) shouldBe false
    }
  }
}