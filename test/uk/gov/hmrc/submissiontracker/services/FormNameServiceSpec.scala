/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.submissiontracker.services

import uk.gov.hmrc.submissiontracker.stub.TestSetup

class FormNameServiceSpec extends TestSetup {
  val service = new FormNameService

  "getFormName(formId: String)" should {
    "return the correct form name when provided with a valid formId" in {
      service.getFormName("R39_EN") shouldBe "Claim a tax refund"
    }

    "return the formId as the name when provided with a formId that has no mapping" in {
      service.getFormName("ABC_123") shouldBe "ABC_123"
    }
  }
}
