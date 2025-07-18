/*
 * Copyright 2024 HM Revenue & Customs
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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.DataEvent

import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class AuditService @Inject() (
  @Named("appName") val appName: String,
  val auditConnector: AuditConnector
)(implicit val executionContext: ExecutionContext) {

  lazy val auditType: String = "ServiceResponseSent"

  def withAudit[T](
    service: String,
    details: Map[String, String]
  )(func: Future[T])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[T] = {
    audit(service, details) // No need to wait!
    func
  }

  def audit(
    service: String,
    details: Map[String, String]
  )(implicit hc: HeaderCarrier): Future[AuditResult] =
    auditConnector.sendEvent(DataEvent(appName, auditType, tags = Map("transactionName" -> service), detail = details))

}
