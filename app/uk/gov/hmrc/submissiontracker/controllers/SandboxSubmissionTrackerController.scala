/*
 * Copyright 2020 HM Revenue & Customs
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

import com.google.inject.Singleton
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, BodyParser, ControllerComponents}
import uk.gov.hmrc.api.controllers._
import uk.gov.hmrc.api.sandbox.FileResource
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import uk.gov.hmrc.submissiontracker.domain.types.ModelTypes.{IdType, JourneyId}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SandboxSubmissionTrackerController @Inject()(
                                                    cc: ControllerComponents
                                                  )(implicit val executionContext: ExecutionContext)
  extends BackendController(cc)
    with HeaderValidator
    with FileResource {

  def trackingData(
                    id: String,
                    idType: IdType,
                    journeyId: JourneyId
                  ): Action[AnyContent] =
    validateAccept(acceptHeaderValidationRules).async { implicit request =>
      Future successful (request.headers.get("SANDBOX-CONTROL") match {
        case Some("ERROR-401") => Unauthorized
        case Some("ERROR-403") => Forbidden
        case Some("ERROR-500") => InternalServerError
        case _ =>
          val resource: String = findResource(s"/resources/SandboxTrackingData.json")
            .getOrElse(throw new IllegalArgumentException("Resource not found!"))
          Ok(resource)
      })
    }
  override def parser: BodyParser[AnyContent] = cc.parsers.anyContent
}
