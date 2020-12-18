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

package repositories

import models.FileUpload
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import uk.gov.hmrc.mongo.ReactiveRepository
import utils.JsonUtils

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FileUploadRepositoryImpl @Inject()(mongo: ReactiveMongoComponent) extends
  ReactiveRepository[FileUpload, BSONObjectID](
    collectionName = "file-upload",
    mongo = mongo.mongoConnector.db,
    domainFormat = FileUpload.fmt
  ) with FileUploadRepository with JsonUtils {

  override def indexes: Seq[Index] = Seq(
    Index(
      key = Seq("reference" -> IndexType.Ascending),
      name = Some("reference-unique-index"),
      unique = true,
      sparse = false
    )
  )


  override def insertRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean] = {
    collection.insert.one(fileUpload).map(_.ok)
  }

  override def updateRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean] = {
    val selector = Json.obj("reference" -> fileUpload.reference)
    val update = Json.obj("$set" -> fileUpload)
    collection
      .update(ordered = false)
      .one(selector, update, upsert = true)
      .map(_.ok)
  }

  override def deleteRecord(reference: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    val selector = Json.obj("reference" -> reference)
    collection
      .delete(ordered = false)
      .one(selector)
      .map(_.ok)
  }

  override def getLargeEmployerS3DownloadUrl(reference: String)(implicit ec: ExecutionContext): Future[Option[String]] =
    getRecord(reference).map(_.flatMap(_.largeEmployerS3DownloadUrl))

  override def getRecord(reference: String)(implicit ec: ExecutionContext): Future[Option[FileUpload]] = {
    val selector = Json.obj("reference" -> reference)
    collection
      .find(selector, None)
      .one[FileUpload]
  }

}

trait FileUploadRepository {

  def insertRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean]

  def updateRecord(fileUpload: FileUpload)(implicit ec: ExecutionContext): Future[Boolean]

  def deleteRecord(reference: String)(implicit ec: ExecutionContext): Future[Boolean]

  def getRecord(reference: String)(implicit ec: ExecutionContext): Future[Option[FileUpload]]

  def getLargeEmployerS3DownloadUrl(reference: String)(implicit ec: ExecutionContext): Future[Option[String]]

}