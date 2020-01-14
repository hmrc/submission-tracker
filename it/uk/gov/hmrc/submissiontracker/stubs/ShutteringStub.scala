package uk.gov.hmrc.submissiontracker.stubs

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlEqualTo}
import com.github.tomakehurst.wiremock.stubbing.StubMapping

object ShutteringStub {

  def stubForShutteringDisabled: StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/mobile-shuttering/service/submission-tracker/shuttered-status?journeyId=decf6382-0c09-4ea8-8225-d59d188db41f"
        )
      ).willReturn(
        aResponse()
          .withStatus(200)
          .withBody(s"""
                       |{
                       |  "shuttered": false,
                       |  "title":     "",
                       |  "message":    ""
                       |}
          """.stripMargin)
      )
    )

  def stubForShutteringEnabled: StubMapping =
    stubFor(
      get(
        urlEqualTo(
          s"/mobile-shuttering/service/submission-tracker/shuttered-status?journeyId=decf6382-0c09-4ea8-8225-d59d188db41f"
        )
      ).willReturn(
        aResponse()
          .withStatus(200)
          .withBody(s"""
                       |{
                       |  "shuttered": true,
                       |  "title":     "Shuttered",
                       |  "message":   "Form Tracker is currently not available"
                       |}
          """.stripMargin)
      )
    )

}
