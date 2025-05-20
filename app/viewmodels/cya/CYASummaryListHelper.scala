/*
 * Copyright 2025 HM Revenue & Customs
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

package viewmodels.cya

import play.api.i18n.Messages
import models.requests.DataRequest
import java.time.LocalDateTime

trait CYASummaryListHelper
    extends CYADisclosureSummaryListHelper
    with CYAImporterDetailsSummaryListHelper
    with CYAEntryDetailsSummaryListHelper
    with CYAUnderpaymentDetailsSummaryListHelper
    with CYAYourDetailsSummaryListHelper
    with CYAPaymentDetailsSummaryListHelper
    with CYADefermentDutyDetailsSummaryListHelper
    with CYADefermentImportVATDetailsSummaryListHelper {

  def buildFullSummaryList(implicit messages: Messages, request: DataRequest[_]): Seq[CYASummaryList] =
    buildImporterDetailsSummaryList ++
      buildEntryDetailsSummaryList ++
      buildUnderpaymentDetailsSummaryList ++
      buildYourDetailsSummaryList ++
      buildPaymentDetailsSummaryList ++
      buildDefermentDutySummaryList ++
      buildDefermentImportVatSummaryList

  def buildSummaryListForPrint(caseId: String, date: LocalDateTime)(implicit
    messages: Messages,
    request: DataRequest[_]
  ): Seq[CYASummaryList] = {
    val existing = buildFullSummaryList.map { list =>
      list.copy(summaryList = list.summaryList.copy(rows = list.summaryList.rows.map(row => row.copy(actions = None))))
    }
    buildDisclosureSummaryList(caseId, date) +: existing
  }
}
