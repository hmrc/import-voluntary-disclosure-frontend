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

package models

import java.time.LocalDate

case class IVDSubmission(
                        userType: UserType,
                        numEntries: NumberOfEntries,
                        acceptanceDate: Boolean,
//                        additionalInfo: String = "Not captured", // TODO: Not implemented yet. Maps to amendmentReason
                        entryDetails: EntryDetails,
                        originalCpc: String,
//                        amendedCpc: Option[String], // TODO: Not yet implemented
                        traderContactDetails: TraderContactDetails, // TODO: This should be Declarant details
                        traderAddress: TraderAddress,
                        declarantDate: LocalDate = LocalDate.now,
//                        defermentType: Option[String] = None, // TODO: Not captured yet
//                        defermentAccountNumber: Option[String] = None, // TODO: Not captured yet
//                        additionalDefermentNumber: Option[String] = None, // TODO: Not captured yet
//                        underpaymentReasons: Option[Seq[UnderpaymentReason]] = None, // TODO: Not captured yet (box changes)
                        underpaymentDetails: Option[Seq[UnderpaymentDetail]] = None // TODO: Other duties will need to be refactored into this
//                        documentList: Option[Seq[String]] = None, // TODO: List of documents the user claims to have uploaded (not the actual docs)
//                        traderList: Seq[TraderDetails] // TODO: I assume this is only relevant to bulk upload? Need model to bring all data together
                        )
