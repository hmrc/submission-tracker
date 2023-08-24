package uk.gov.hmrc.submissiontracker.controllers

import play.api.libs.json.Json
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.submissiontracker.domain.Shuttering
import uk.gov.hmrc.submissiontracker.stubs.{AuthStub, ShutteringStub, TrackingStub}
import uk.gov.hmrc.submissiontracker.support.BaseISpec

class SubmissionTrackerControllerISpec extends BaseISpec with FutureAwaits with DefaultAwaitTimeout {

  "GET /tracking/:id/:idType" should {
    "return tracking data" in {
      AuthStub.grantAccess(nino, nino)
      TrackingStub.getUserTrackingData(idType, nino)
      ShutteringStub.stubForShutteringDisabled

      val response = await(
        wsUrl(s"/tracking/$nino/$idType/$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
          .get()
      )

      response.status                                                      shouldBe 200
      ((response.json \ "submissions").head \ "receivedDate").as[String]   shouldBe "20160814"
      ((response.json \ "submissions").head \ "formName").as[String]       shouldBe "Excise duty return and payment of duty for biofuels and other fuel substitutes"
      ((response.json \ "submissions").head \ "formNameCy").as[String]     shouldBe "Datganiad toll ecséis a thalu toll ar gyfer biodanwyddau ac amnewidion tanwydd eraill"
      ((response.json \ "submissions").head \ "completionDate").as[String] shouldBe "20160620"
      ((response.json \ "submissions").head \ "milestone").as[String]      shouldBe "Received"
      ((response.json \ "submissions").head \ "businessArea").isEmpty
    }

    "fail with a 401 if confidence level is low" in {
      AuthStub.grantAccess(nino, nino, 50)

      val response = await(
        wsUrl(s"/tracking/$nino/$idType/$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader)
          .get()
      )

      response.status shouldBe 401
    }

    "fail with a 401 if nino return is empty" in {
      AuthStub.grantAccess(nino, "")

      val response = await(
        wsUrl(s"/tracking/$nino/$idType/$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader)
          .get()
      )

      response.status shouldBe 401
    }

    "fail with a 400 if no journeyId is supplied" in {
      AuthStub.grantAccess(nino, nino)

      val response = await(
        wsUrl(s"/tracking/$nino/$idType")
          .addHttpHeaders(acceptJsonHeader)
          .get()
      )

      response.status shouldBe 400
    }

    "return 400 when invalid journeyId is supplied" in {
      AuthStub.grantAccess(nino, nino)

      val response = await(
        wsUrl(s"/tracking/$nino/$idType/?journeyId=ThisIsAnInvalidJourneyId")
          .addHttpHeaders(acceptJsonHeader)
          .get()
      )

      response.status shouldBe 400
    }

    "return 400 when invalid idType is supplied" in {
      AuthStub.grantAccess(nino, nino)

      val response = await(
        wsUrl(s"/tracking/$nino/invalidType/$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader)
          .get()
      )

      response.status shouldBe 400
    }

    "return SHUTTERED when shuttered" in {
      AuthStub.grantAccess(nino, nino)
      ShutteringStub.stubForShutteringEnabled
      TrackingStub.getUserTrackingData(idType, nino)

      val response = await(
        wsUrl(s"/tracking/$nino/$idType/$journeyIdUrlVar")
          .addHttpHeaders(acceptJsonHeader, authorisationJsonHeader)
          .get()
      )

      response.status shouldBe 521
      val shuttering: Shuttering = Json.parse(response.body).as[Shuttering]
      shuttering.shuttered shouldBe true
      shuttering.title     shouldBe Some("Shuttered")
      shuttering.message   shouldBe Some("Form Tracker is currently not available")
    }
  }
}
