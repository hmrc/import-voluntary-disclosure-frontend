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

import base.SpecBase
import pages._
import services.submissionService.SubmissionServiceTestData

class UserAnswersSpec extends SpecBase with SubmissionServiceTestData {
  "Calling .preserve" should {
    "preserve known pages stored as JsObjects" in {
      val pagesToPreserve: Seq[QuestionPage[_]] = Seq(KnownEoriDetailsPage, UserTypePage)
      val trimmedAnswers: UserAnswers = UserAnswers(completeUserAnswers.id)
        .set(KnownEoriDetailsPage, completeSubmission.knownDetails).success.value
        .set(UserTypePage, completeSubmission.userType).success.value

      completeUserAnswers.preserve(pagesToPreserve).data mustBe trimmedAnswers.data
    }

    "preserve known pages stored as simple JsValues e.g. Strings or Booleans" in {
      val pagesToPreserve: Seq[QuestionPage[_]] = Seq(TraderAddressCorrectPage, MoreInformationPage)
      val trimmedAnswers: UserAnswers = UserAnswers(completeUserAnswers.id)
        .set(TraderAddressCorrectPage, completeSubmission.traderAddressCorrect).success.value
        .set(MoreInformationPage, completeSubmission.additionalInfo.getOrElse("Additional information")).success.value

      completeUserAnswers.preserve(pagesToPreserve).data mustBe trimmedAnswers.data
    }

    "preserve known pages stored at a path deeper than 1 level" in {
      val pagesToPreserve: Seq[QuestionPage[_]] = Seq(EnterCustomsProcedureCodePage)
      val trimmedAnswers: UserAnswers = UserAnswers(completeUserAnswers.id)
        .set(EnterCustomsProcedureCodePage, completeSubmission.originalCpc.getOrElse("cpcError")).success.value

      completeUserAnswers.preserve(pagesToPreserve).data mustBe trimmedAnswers.data
    }

    "preserve known pages stored as JsArrays" in {
      val pagesToPreserve: Seq[QuestionPage[_]] = Seq(UnderpaymentReasonsPage)
      val trimmedAnswers: UserAnswers = UserAnswers(completeUserAnswers.id)
        .set(UnderpaymentReasonsPage, completeSubmission.amendedItems.get).success.value

      completeUserAnswers.preserve(pagesToPreserve).data mustBe trimmedAnswers.data
    }

    "NOT preserve pages if they are not present in user answers" in {

      val pagesToPreserve: Seq[QuestionPage[_]] = Seq(UnderpaymentReasonsPage)
      val answersWithoutExpectedAnswer = completeUserAnswers.remove(UnderpaymentReasonsPage).success.value
      val emptyAnswers: UserAnswers = UserAnswers(completeUserAnswers.id)

      answersWithoutExpectedAnswer.preserve(pagesToPreserve).data mustBe emptyAnswers.data
    }
  }

  "Calling removeMany" should {
    "remove the pages from userAnswers" in {
      val pagesToRemove: Seq[QuestionPage[_]] = Seq(ImporterNamePage, ImporterEORIExistsPage)
      val result = completeUserAnswers.removeMany(pagesToRemove)
      result.get(ImporterNamePage) mustBe None
      result.get(ImporterEORIExistsPage) mustBe None
    }

    "not fail when specified pages don't exist" in {
      val pagesToRemove: Seq[QuestionPage[_]] = Seq(ImporterNamePage, ImporterEORIExistsPage)
      val answers = (for {
        answers <- new UserAnswers("some-cred-id").set(UserTypePage, completeSubmission.userType)
        answers <- answers.set(KnownEoriDetailsPage, completeSubmission.knownDetails)
        answers <- answers.set(NumberOfEntriesPage, completeSubmission.numEntries)
        answers <- answers.set(AcceptanceDatePage, completeSubmission.acceptedBeforeBrexit)
        answers <- answers.set(EntryDetailsPage, completeSubmission.entryDetails.get)
        answers <- answers.set(OneCustomsProcedureCodePage, completeSubmission.oneCpc.get)
      } yield answers).getOrElse(new UserAnswers("some-cred-id"))
      val result = answers.removeMany(pagesToRemove)
      result.get(ImporterNamePage) mustBe None
      result.get(ImporterEORIExistsPage) mustBe None
    }
  }
}
