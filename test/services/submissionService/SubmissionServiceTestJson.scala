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

package services.submissionService

trait SubmissionServiceTestJson {
  val completeOutputJson =
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
      |      "vatNumber":"987654321000",
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
      |         "reference":"file-ref-1",
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      },
      |      {
      |         "reference":"file-ref-1",
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      }
      |   ]
      |}""".stripMargin

  val importerEntryDetailsJson =
    """{
      |   "customsProcessingCode":"VARIOUS",
      |   "isBulkEntry":false,
      |   "entryDetails":{
      |      "epu":"123",
      |      "entryNumber":"123456Q",
      |      "entryDate":"2020-12-12"
      |   },
      |   "isEuropeanUnionDuty":true,
      |   "userType":"importer"
      |}""".stripMargin

  val underpaymentDetailsJson =
    """{
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
      |      },
      |      {
      |         "duty":"A20",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"A30",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"A35",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"A40",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"A45",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"A10",
      |         "original":0,
      |         "amended":1
      |      },
      |      {
      |         "duty":"D10",
      |         "original":0,
      |         "amended":1
      |      }
      |   ]
      |}""".stripMargin

  val reasonsDetailsJson =
    """{
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
      |   "additionalInfo":"Not Applicable"
      |}""".stripMargin

  val supportingDocumentationJson =
    """{
      |   "supportingDocumentTypes":[
      |      "OriginalC88",
      |      "OriginalC2",
      |      "AmendedSubstituteEntryWorksheet"
      |   ],
      |   "supportingDocuments":[
      |      {
      |         "reference":"file-ref-1",
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      }
      |   ]
      |}""".stripMargin

  val singleUnderpaymentDefermentSupportingDocumentationJson =
    """{
      |   "supportingDocumentTypes":[
      |      "OriginalC88",
      |      "OriginalC2",
      |      "AmendedSubstituteEntryWorksheet",
      |      "DefermentAuthorisation"
      |   ],
      |   "supportingDocuments":[
      |      {
      |         "reference":"file-ref-1",
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      },
      |      {
      |         "reference":"file-ref-1",
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      }
      |   ]
      |}""".stripMargin

  val noSplitDefermentSupportingDocumentationJson =
    """{
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
      |   "supportingDocuments":[
      |      {
      |         "reference":"file-ref-1",
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      },
      |      {
      |         "reference":"file-ref-1",
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      }
      |   ]
      |}""".stripMargin

  val splitDefermentSupportingDocumentationJson =
    """{
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
      |   "supportingDocuments":[
      |      {
      |         "reference":"file-ref-1",
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      },
      |      {
      |         "reference":"file-ref-1",
      |         "fileName":"TestDocument.pdf",
      |         "downloadUrl":"http://some/location",
      |         "uploadTimestamp":"2021-05-14T20:15:13.807",
      |         "checksum":"the file checksum",
      |         "fileMimeType":"application/pdf"
      |      }
      |   ]
      |}""".stripMargin

  val importerDefermentDetailsJson =
    """{
      |   "defermentType":"D",
      |   "defermentAccountNumber":"D1234567"
      |}""".stripMargin

  val representativeNoSplitDefermentDetailsJson =
    """{
      |   "defermentType":"B",
      |   "defermentAccountNumber":"B1234567"
      |}""".stripMargin

  val splitDefermentDetailsJson =
    """{
      |   "defermentType":"B",
      |   "defermentAccountNumber":"B1234567",
      |   "additionalDefermentAccountNumber":"B1234567"
      |}""".stripMargin

  val declarantDetailsJson =
    """{
      |   "declarantContactDetails":{
      |      "fullName":"John Smith",
      |      "email":"test@test.com",
      |      "phoneNumber":"0123456789"
      |   }
      |}""".stripMargin

  val importerDetailsJson =
    """{
      |   "importer":{
      |      "address":{
      |         "addressLine1":"99 Avenue Road",
      |         "city":"Anyold Town",
      |         "postalCode":"99JZ 1AA",
      |         "countryCode":"GB"
      |      },
      |      "contactDetails":{
      |         "fullName":"Fast Food ltd",
      |         "email":"test@test.com",
      |         "phoneNumber":"0123456789"
      |      },
      |      "eori":"GB987654321000",
      |      "vatNumber":"987654321000"
      |   }
      |}""".stripMargin

  val importerDetailsNoEoriJson =
    """{
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
      |      "eori":"GBPR"
      |   }
      |}""".stripMargin

  val representativeDetailsJson =
    """{
      |   "representative":{
      |      "eori":"GB987654321000",
      |      "vatNumber":"987654321000",
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
      |   }
      |}""".stripMargin


}
