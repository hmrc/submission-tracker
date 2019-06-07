/*
 * Copyright 2019 HM Revenue & Customs
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

class FormNameService {

  val FormNames: Map[String, String] = {
    Map(
      "R39_EN"       -> "Claim a tax refund",
      "SPTU"         -> "State Pension Top Up Request",
      "CIS301302"    -> "Construction Industry Scheme - Individual Registration (CIS301 - CIS302)",
      "P85"          -> "Leaving the UK - getting your tax right",
      "CA72A"        -> "Application for deferment of payment of Class 1 National Insurance contributions",
      "CA3821"       -> "Sending employees to work abroad",
      "CA3822"       -> "Employees going to work in EEA",
      "CA5403"       -> "Request National Insurance number in writing",
      "CA5610"       -> "Application for refund of Class 4 National Insurance contributions",
      "CA8421i"      -> "Working in 2 or more countries in the European Economic Area",
      "CA8480"       -> "App for refund of Class 2 National Insurance contributions",
      "CBOCH1702e"   -> "Claimant change of circumstances",
      "CBOptIn"      -> "High Income Child Benefit Tax Charge",
      "CH102"        -> "Child change of circumstances",
      "CBOCH297e"    -> "Staying in full-time, non-advanced education or training",
      "CBOCH299e"    -> "Extension application",
      "CBOCH459e"    -> "Leaving full-time, non-advanced education or training",
      "CIS304"       -> "Register your partnership for Construction Industry Scheme",
      "DMB-SP"       -> "Financial help with statutory payments",
      "DMB-EMP"      -> "Application for funding a tax refund for an employee",
      "EX250"        -> "Bingo Promoter Registration",
      "GD56"         -> "Register for Gaming Duty",
      "GD57"         -> "Inclusion on Gaming Duty Register",
      "GD58"         -> "Notification of premises",
      "GD60"         -> "Gaming Duty Group Treatment",
      "NRL1e"        -> "Non-resident landlord application for an individual",
      "NRL2-3"       -> "Non resident landlord application for companies and trusts",
      "NRL4-5"       -> "Non resident landlord application for letting agents",
      "P350"         -> "PAYE employer annual return election",
      "P46Car"       -> "Car provided to employee for private use",
      "P50"          -> "Claiming tax back when you have stopped working",
      "P50Z"         -> "Claim for repayment of tax when you have stopped working: flexibly accessed pension",
      "P53"          -> "Small pension taken as a lump sum: repayment claim",
      "P53Z"         -> "Flexibly accessed pension lump sum: repayment claim (current tax year)",
      "P55"          -> "Flexibly accessed pension payment: repayment claim (current tax year)",
      "PT_CertOfRes" -> "Request for certificate of residence in the UK",
      "SA303"        -> "Claim to reduce payments on account",
      "SA400"        -> "Register a partner or a partnership for Self Assessment",
      "EX72"         -> "Registered consignor application",
      "TRC1"         -> "Temporary registered consignee application",
      "TES1"         -> "Tell us about income from employment or pension",
      "PT_CertResFI" -> "Request for Certificate of Residence in the UK: Further information",
      "SA4001"       -> "Register a partner or a partnership for Self Assessment"
    )
  }

  def getFormName(formId: String): String = FormNames.getOrElse(formId, "INVALID")

}
