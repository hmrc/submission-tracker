/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.submissiontracker.controllers

import org.scalatest.TestSuite
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.ConfidenceLevel.{L200, L50}
import uk.gov.hmrc.auth.core.syntax.retrieved.*
import uk.gov.hmrc.submissiontracker.controllers.action.Authorisation
import uk.gov.hmrc.submissiontracker.stub.TestSetup

import scala.concurrent.ExecutionContext.Implicits.global

class AuthorisationSpec extends TestSetup { this: TestSuite =>

  def authorisation(implicit mockAuthConnector: AuthConnector): Authorisation =
    new Authorisation {
      override val confLevel: Int = 200

      override def authConnector: AuthConnector = mockAuthConnector
    }

  "Authorisation grantAccess" should {

    "successfully grant access when nino exists and confidence level is 200" in {
      stubAuthorisationGrantAccess(Some(nino.value) and L200)

      val authority = await(authorisation.grantAccess(nino))
      authority.nino.value shouldBe nino.value
    }

    "error with unauthorised when account has low CL" in {
      stubAuthorisationGrantAccess(Some(nino.value) and L50)

      intercept[AccountWithLowCL] {
        await(authorisation.grantAccess(nino))
      }
    }

    "fail to return authority when no NINO exists" in {
      stubAuthorisationGrantAccess(None and L200)

      intercept[NinoNotFoundOnAccount] {
        await(authorisation.grantAccess(nino))
      }

      stubAuthorisationGrantAccess(Some("") and L200)

      intercept[NinoNotFoundOnAccount] {
        await(authorisation.grantAccess(nino))
      }
    }

    "fail to return authority when auth NINO does not match request NINO" in {
      stubAuthorisationGrantAccess(Some(nino.value) and L200)

      intercept[FailToMatchTaxIdOnAuth] {
        await(authorisation.grantAccess(incorrectNino))
      }
    }
  }
}
