/*
 * Copyright 2017 Antonio Alonso Dominguez
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

package cron4s
package doobie

import cats.effect.{IO, ContextShift}

import _root_.doobie._
import _root_.doobie.implicits._
import _root_.doobie.util.invariant._

import org.scalatest._

import scala.concurrent.ExecutionContext

class DoobieSpec extends FlatSpec with Matchers {
  implicit val contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  val xa = Transactor.fromDriverManager[IO](
    "org.h2.Driver",
    "jdbc:h2:mem:refined;DB_CLOSE_DELAY=-1",
    "sa",
    ""
  )

  def insertMeeting(meeting: Meeting) = {
    val createTable = sql"""
       create table meeting(
        meeting_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        subject VARCHAR(255) NOT NULL,
        description VARCHAR(255) NOT NULL,
        frequency VARCHAR(255) NOT NULL
      )
      """

    val insertRecord = sql"""
      insert into meeting(subject, description, frequency)
      values(${meeting.subject}, ${meeting.description}, ${meeting.frequency})
      """

    for {
      _  <- createTable.update.run
      id <- insertRecord.update.withUniqueGeneratedKeys[Long]("meeting_id")
    } yield MeetingId(id)
  }

  def loadMeeting(meetingId: MeetingId) =
    sql"select subject, description, frequency from meeting where meeting_id = $meetingId"
      .query[Meeting]
      .unique

  "Doobie" should "store and retrieve a cron expression as a member of a storable data structure" in {
    val standUpMeeting = Meeting(
      "Daily stand-up",
      "Daily team morning stand-up meeting",
      cron"0 0 10 ? * mon-fri"
    )

    val tx = for {
      meetingId <- insertMeeting(standUpMeeting)
      loaded    <- loadMeeting(meetingId)
    } yield loaded

    val loadedMeeting = tx.transact(xa).unsafeRunSync()
    loadedMeeting shouldBe standUpMeeting
  }

  it should "throw a SecondaryValidationFailed in case the cron expression is invalid" in {
    assertThrows[SecondaryValidationFailed[CronExpr]] {
      sql"select '0- 0 30 * * ?'".query[CronExpr].unique.transact(xa).unsafeRunSync()
    }
  }

}
