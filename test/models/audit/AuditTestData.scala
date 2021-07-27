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

package models.audit

import models.{SubmissionResponse, UpdateCaseError}
import play.api.libs.json.JsValue

trait AuditTestData {
  val completeSubmissionJson =
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

  val auditOutputJson =
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

  val updateCaseOutputJson =
    """
      |{
      |   "caseID":"C181234567891234567891",
      |   "description":"dewdew",
      |   "uploadedFiles":[
      |      {
      |         "reference":"34349f41-8b70-4967-bb9c-6fa67b04fb75",
      |         "fileName":"Test doc – attachment for underpayment.docx",
      |         "downloadUrl":"http://localhost:9570/upscan/download/c8ecf665-3c54-4470-881d-3c9cd6e0ebc9",
      |         "uploadTimestamp":"2021-07-26T15:42:32.035",
      |         "checksum":"db8d51ba206c1bc972a1755f6c7870603768b3e2147f6396ed0d7cb7e3ad622c",
      |         "fileMimeType":"application/binary"
      |      },
      |      {
      |         "reference":"c2edfd16-d5a3-481d-aefd-30faaf53cb16",
      |         "fileName":"Test doc attachment for underpayment.docx",
      |         "downloadUrl":"http://localhost:9570/upscan/download/9d34be19-f14f-4699-b74e-5219f15f7fac",
      |         "uploadTimestamp":"2021-07-26T15:42:38.723",
      |         "checksum":"db8d51ba206c1bc972a1755f6c7870603768b3e2147f6396ed0d7cb7e3ad622c",
      |         "fileMimeType":"application/binary"
      |      }
      |   ],
      |   "credentialId":"Ext-bb5e40ca-1c0a-40bd-8509-a9cb0e195abe",
      |   "declarantEORI":"GB987654321000",
      |   "numberOfFilesUploaded":2
      |}
      |""".stripMargin

  val submissionResponse = SubmissionResponse(id = "1234567890")
}
