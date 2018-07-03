package uk.gov.hmrc.submissiontracker

import uk.gov.hmrc.submissiontracker.stubs.{AuthStub, TrackingStub}
import uk.gov.hmrc.submissiontracker.support.BaseISpec
import com.github.tomakehurst.wiremock.client.WireMock._

class SubmissionTrackerISpec extends BaseISpec {

  "GET /tracking/:id/:idType" should {
    "return tracking data" in {
      AuthStub.grantAccess(nino, nino)
      TrackingStub.getUserTrackingData(idType, nino)

      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders(acceptJsonHeader)
        .get())

      response.status shouldBe 200
    }

    "override to sandbox when using sandbox user, avoiding auth call" in {
      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders(acceptJsonHeader, mobileUserIdHeader)
        .get())

      verify(0, postRequestedFor(urlEqualTo("/auth/authorise")))
      verify(0, postRequestedFor(urlEqualTo(s"/tracking-data/user/$idType/$nino")))

      response.status shouldBe 200
    }

    "fail with a 401 if confidence level is low" in {
      AuthStub.grantAccess(nino, nino, 100)

      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders(acceptJsonHeader)
        .get())

      response.status shouldBe 401
    }

    "fail with a 401 if nino return is empty" in {
      AuthStub.grantAccess(nino, "")

      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders(acceptJsonHeader)
        .get())

      response.status shouldBe 401
    }
  }

}
