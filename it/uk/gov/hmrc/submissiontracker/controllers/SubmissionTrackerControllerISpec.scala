package uk.gov.hmrc.submissiontracker.controllers

import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.submissiontracker.stubs.{AuthStub, TrackingStub}
import uk.gov.hmrc.submissiontracker.support.BaseISpec

class SubmissionTrackerControllerISpec extends BaseISpec with FutureAwaits with DefaultAwaitTimeout {

  "GET /tracking/:id/:idType" should {
    "return tracking data" in {
      AuthStub.grantAccess(nino, nino)
      TrackingStub.getUserTrackingData(idType, nino)

      val response = await(
        wsUrl(s"/tracking/$nino/$idType")
          .addHttpHeaders(acceptJsonHeader)
          .get())

      response.status shouldBe 200
    }

    "fail with a 401 if confidence level is low" in {
      AuthStub.grantAccess(nino, nino, 100)

      val response = await(
        wsUrl(s"/tracking/$nino/$idType")
          .addHttpHeaders(acceptJsonHeader)
          .get())

      response.status shouldBe 401
    }

    "fail with a 401 if nino return is empty" in {
      AuthStub.grantAccess(nino, "")

      val response = await(
        wsUrl(s"/tracking/$nino/$idType")
          .addHttpHeaders(acceptJsonHeader)
          .get())

      response.status shouldBe 401
    }
  }

}
