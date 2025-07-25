package uk.gov.hmrc.submissiontracker.controllers

import java.time.LocalDate

import com.github.tomakehurst.wiremock.client.WireMock._
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.api.sandbox.FileResource
import uk.gov.hmrc.submissiontracker.support.BaseISpec
import play.api.libs.ws.DefaultBodyReadables.readableAsString

class SandboxSubmissionTrackerControllerISpec
    extends BaseISpec
    with FileResource
    with FutureAwaits
    with DefaultAwaitTimeout {

  val resource: String = findResource(s"/resources/SandboxTrackingData.json")
    .getOrElse(throw new IllegalArgumentException("Resource not found!"))
    .replace("<RECEIVED_DATE>", "\"" + LocalDate.now().minusDays(3).toString.replace("-", "") + "\"")
    .replace("<COMPLETION_DATE>", "\"" + LocalDate.now().plusDays(5).toString.replace("-", "") + "\"")

  "GET /sandbox/tracking/:id/:idType" should {

    "return valid tracking data from sandbox with accept header" in {
      val response = await(
        wsUrl(s"/tracking/$nino/$idType$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader, mobileUserIdHeader)
          .get()
      )

      verify(0, postRequestedFor(urlEqualTo("/auth/authorise")))
      verify(0, postRequestedFor(urlEqualTo(s"/tracking-data/user/$idType/$nino$journeyIdUrlVar")))

      response.status shouldBe 200
      response.body   shouldBe resource
    }

    "return valid tracking data from sandbox with accept header when have no forms" in {
      val response = await(
        wsUrl(s"/tracking/$nino/$idType$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader, mobileUserIdHeader, "SANDBOX-CONTROL" -> "NO-FORMS")
          .get()
      )

      verify(0, postRequestedFor(urlEqualTo("/auth/authorise")))
      verify(0, postRequestedFor(urlEqualTo(s"/tracking-data/user/$idType/$nino$journeyIdUrlVar")))

      response.status shouldBe 200
      response.body   shouldBe """{"submissions":[]}"""
    }

    "return 406 when missing accept header" in {
      val response = await(
        wsUrl(s"/tracking/$nino/$idType$journeyIdUrlVar")
          .addHttpHeaders(mobileUserIdHeader)
          .get()
      )

      response.status shouldBe 406
    }

    "return 401 if unauthenticated where SANDBOX-CONTROL is ERROR-401" in {
      await(
        wsUrl(s"/tracking/$nino/$idType$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader, mobileUserIdHeader, "SANDBOX-CONTROL" -> "ERROR-401")
          .get()
      ).status shouldBe 401
    }

    "return 401 if unauthenticated where SANDBOX-CONTROL is ERROR-403" in {
      await(
        wsUrl(s"/tracking/$nino/$idType$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader, mobileUserIdHeader, "SANDBOX-CONTROL" -> "ERROR-403")
          .get()
      ).status shouldBe 403
    }

    "return 401 if unauthenticated where SANDBOX-CONTROL is ERROR-500" in {
      await(
        wsUrl(s"/tracking/$nino/$idType$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader, mobileUserIdHeader, "SANDBOX-CONTROL" -> "ERROR-500")
          .get()
      ).status shouldBe 500
    }

    "return 400 if no joruneyId is supplied" in {
      val response = await(
        wsUrl(s"/tracking/$nino/$idType")
          .addHttpHeaders(acceptJsonHeader, mobileUserIdHeader)
          .get()
      )

      response.status shouldBe 400
    }
  }
}
