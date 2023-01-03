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

package models.audit

import models.{SubmissionResponse, UpdateCaseResponse}

trait AuditTestData {
  val completeSubmissionJson: String =
    """{
      |   "customsProcessingCode":"4000C09",
      |   "importer":{
      |      "address":{
      |         "addressLine1":"99 Avenue Road",
      |         "city":"Anyold Town",
      |         "postalCode":"99JZ 1AA",
      |         "countryCode":"GB"
      |      },
      |      "contactDetails":{
      |         "fullName":"Joe Bloggs",
      |         "email":"notsupplied@example.com",
      |         "phoneNumber":"000000000"
      |      },
      |      "eori":"GB987654321000",
      |      "vatNumber":"987654321"
      |   },
      |   "additionalDefermentAccountNumber":"B1234567",
      |   "supportingDocumentTypes":[
      |      "OriginalC88",
      |      "OriginalC2",
      |      "AmendedSubstituteEntryWorksheet",
      |      "AmendedC88",
      |      "AmendedC2",
      |      "InvoiceAirwayBillPreferenceCertificate",
      |      "Other",
      |      "DefermentAuthorisation"
      |   ],
      |   "isBulkEntry":false,
      |   "defermentType":"B",
      |   "defermentAccountNumber":"B1234567",
      |   "amendedItems":[
      |      {
      |         "boxNumber":22,
      |         "itemNumber":0,
      |         "original":"GBP100",
      |         "amended":"GBP200"
      |      },
      |      {
      |         "boxNumber":33,
      |         "itemNumber":1,
      |         "original":"2204109400X411",
      |         "amended":"2204109400X412"
      |      }
      |   ],
      |   "entryDetails":{
      |      "epu":"123",
      |      "entryNumber":"123456Q",
      |      "entryDate":"2020-12-12"
      |   },
      |   "declarantContactDetails":{
      |      "fullName":"John Smith",
      |      "email":"test@test.com",
      |      "phoneNumber":"0123456789"
      |   },
      |   "isEuropeanUnionDuty":true,
      |   "additionalInfo":"Additional information",
      |   "userType":"representative",
      |   "representative":{
      |      "eori":"GB987654321000",
      |      "contactDetails":{
      |         "fullName":"John Smith",
      |         "email":"test@test.com",
      |         "phoneNumber":"0123456789"
      |      },
      |      "address":{
      |         "addressLine1":"99 Avenue Road",
      |         "city":"Anyold Town",
      |         "postalCode":"99JZ 1AA",
      |         "countryCode":"GB"
      |      }
      |   },
      |   "underpaymentDetails":[
      |      {
      |         "duty":"B00",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"A00",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"E00",
      |         "original":0,
      |         "amended":1
      |      }
      |   ],
      |   "supportingDocuments":[
      |      {
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      },
      |      {
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      }
      |   ]
      |}""".stripMargin

  val auditOutputJson: String =
    """{
      |   "caseId":"1234567890",
      |   "credentialId":"credId",
      |   "customsProcessingCode":"4000C09",
      |   "importer":{
      |      "address":{
      |         "addressLine1":"99 Avenue Road",
      |         "city":"Anyold Town",
      |         "postalCode":"99JZ 1AA",
      |         "countryCode":"GB"
      |      },
      |      "contactDetails":{
      |         "fullName":"Joe Bloggs",
      |         "email":"notsupplied@example.com",
      |         "phoneNumber":"000000000"
      |      },
      |      "eori":"GB987654321000",
      |      "vatNumber":"987654321"
      |   },
      |   "additionalDefermentAccountNumber":"B1234567",
      |   "supportingDocumentTypes":[
      |      "OriginalC88",
      |      "OriginalC2",
      |      "AmendedSubstituteEntryWorksheet",
      |      "AmendedC88",
      |      "AmendedC2",
      |      "InvoiceAirwayBillPreferenceCertificate",
      |      "Other",
      |      "DefermentAuthorisation"
      |   ],
      |   "isBulkEntry":false,
      |   "defermentType":"B",
      |   "defermentAccountNumber":"B1234567",
      |   "amendedItems":[
      |      {
      |         "boxNumber":22,
      |         "itemNumber":0,
      |         "original":"GBP100",
      |         "amended":"GBP200"
      |      },
      |      {
      |         "boxNumber":33,
      |         "itemNumber":1,
      |         "original":"2204109400X411",
      |         "amended":"2204109400X412"
      |      }
      |   ],
      |   "entryDetails":{
      |      "epu":"123",
      |      "entryNumber":"123456Q",
      |      "entryDate":"2020-12-12"
      |   },
      |   "declarantContactDetails":{
      |      "fullName":"John Smith",
      |      "email":"test@test.com",
      |      "phoneNumber":"0123456789"
      |   },
      |   "isEuropeanUnionDuty":true,
      |   "additionalInfo":"Additional information",
      |   "userType":"representative",
      |   "representative":{
      |      "eori":"GB987654321000",
      |      "contactDetails":{
      |         "fullName":"John Smith",
      |         "email":"test@test.com",
      |         "phoneNumber":"0123456789"
      |      },
      |      "address":{
      |         "addressLine1":"99 Avenue Road",
      |         "city":"Anyold Town",
      |         "postalCode":"99JZ 1AA",
      |         "countryCode":"GB"
      |      }
      |   },
      |   "underpaymentDetails":[
      |      {
      |         "duty":"B00",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"A00",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"E00",
      |         "original":0,
      |         "amended":1
      |      }
      |   ],
      |   "supportingDocuments":[
      |      {
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      },
      |      {
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      }
      |   ]
      |}""".stripMargin

  val updateSubmissionJson: String =
    """
      |{
      |        "caseId":"1234567890",
      |        "supportingDocuments" : [
      |            {
      |                "downloadUrl" : "https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      |                "uploadTimestamp" : "2021-07-28T13:58:46.431",
      |                "checksum" : "4642463acfd2c19a5f2f0154c2b953cb6de13e90f0ed83d314528b89b8400ec5",
      |                "fileMimeType" : "application/pdf",
      |                "reference" : "file-ref-1",
      |                "fileName" : "Example.pdf"
      |            }
      |        ],
      |        "additionalInfo" : "fewfew"
      |    }
      |""".stripMargin

  val updateCaseOutputJson: String =
    """
      |{
      |   "caseId":"1234567890",
      |   "eori": "eori",
      |   "credentialId":"credId",
      |   "additionalInfo":"fewfew",
      |   "supportingDocuments":[
      |      {
      |         "reference":"file-ref-1",
      |         "fileName":"Example.pdf",
      |         "downloadUrl":"https://bucketName.s3.eu-west-2.amazonaws.com?1235676",
      |         "uploadTimestamp":"2021-07-28T13:58:46.431",
      |         "checksum":"4642463acfd2c19a5f2f0154c2b953cb6de13e90f0ed83d314528b89b8400ec5",
      |         "fileMimeType":"application/pdf"
      |      }
      |   ]
      |}
      |""".stripMargin

  val submissionResponse: SubmissionResponse = SubmissionResponse(id = "1234567890")

  val updateCaseResponse: UpdateCaseResponse = UpdateCaseResponse(id = "1234567890")
}
