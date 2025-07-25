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

package uk.gov.hmrc.submissiontracker.binder

import play.api.mvc.PathBindable
import uk.gov.hmrc.domain.Nino

object Binders {

  implicit def ninoBinder(implicit stringBinder: PathBindable[String]): PathBindable[Nino] = new PathBindable[Nino] {

    def unbind(
      key: String,
      nino: Nino
    ): String = stringBinder.unbind(key, nino.value)

    def bind(
      key: String,
      value: String
    ): Either[String, Nino] =
      if (Nino.isValid(value)) {
        Right(Nino(value))
      } else {
        Left("ERROR_NINO_INVALID")
      }
  }
}
