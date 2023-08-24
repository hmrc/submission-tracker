/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.submissiontracker.stubs

import com.github.tomakehurst.wiremock.client.WireMock._

object TrackingStub {

  val trackingData: String = """{
                               |  "submissions" : [ {
                               |    "formId" : "HO930SUB",
                               |    "formName" : "Excise duty return and payment of duty for biofuels and other fuel substitutes",
                               |    "dfsSubmissionReference" : "E4H-384D-EFZ",
                               |    "businessArea" : "PSD",
                               |    "receivedDate" : "2016-08-14T00:00:00.000+00:00",
                               |    "completionDate" : "2016-06-20T00:00:00.000+00:00",
                               |    "milestones" : [
                               |    {
                               |      "milestone" : "Received",
                               |      "status" : "current"
                               |    },
                               |    {
                               |      "milestone" : "Acquired",
                               |      "status" : "incomplete"
                               |    },
                               |    {
                               |      "milestone" : "InProgress",
                               |      "status" : "incomplete"
                               |    },
                               |    {
                               |      "milestone" : "Done",
                               |      "status" : "incomplete"
                               |    }]
                               |  } ]
                               |}""".stripMargin

  def getUserTrackingData(
    idType: String,
    id:     String
  ): Unit =
    stubFor(
      get(urlEqualTo(s"/tracking-data/user/$idType/$id"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(trackingData)
        )
    )

}
