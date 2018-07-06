package uk.gov.hmrc.submissiontracker.controllers

import com.github.tomakehurst.wiremock.client.WireMock._
import uk.gov.hmrc.api.sandbox.FileResource
import uk.gov.hmrc.submissiontracker.support.BaseISpec

class SandboxSubmissionTrackerControllerISpec extends BaseISpec with FileResource {

  val resource: String = findResource(s"/resources/SandboxTrackingData.json")
    .getOrElse(throw new IllegalArgumentException("Resource not found!"))

  "GET /sandbox/tracking/:id/:idType" should {

    "return valid tracking data from sandbox with accept header" in {
      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders(acceptJsonHeader, mobileUserIdHeader)
        .get())

      verify(0, postRequestedFor(urlEqualTo("/auth/authorise")))
      verify(0, postRequestedFor(urlEqualTo(s"/tracking-data/user/$idType/$nino")))

      response.status shouldBe 200
      response.body shouldBe resource
    }

    "return 406 when missing accept header" in {
      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders(mobileUserIdHeader)
        .get())

      response.status shouldBe 406
    }

    "return 401 if unauthenticated where SANDBOX-CONTROL is ERROR-401" in {
      await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders(acceptJsonHeader, mobileUserIdHeader, "SANDBOX-CONTROL" -> "ERROR-401")
        .get()).status shouldBe 401
    }

    "return 401 if unauthenticated where SANDBOX-CONTROL is ERROR-403" in {
      await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders(acceptJsonHeader, mobileUserIdHeader, "SANDBOX-CONTROL" -> "ERROR-403")
        .get()).status shouldBe 403
    }

    "return 401 if unauthenticated where SANDBOX-CONTROL is ERROR-500" in {
      await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders(acceptJsonHeader, mobileUserIdHeader, "SANDBOX-CONTROL" -> "ERROR-500")
        .get()).status shouldBe 500
    }
  }
}