package cron4s.datetime

import cron4s.CronField

sealed trait DateTimeError
final case class UnsupportedField(field: CronField) extends DateTimeError
final case class InvalidFieldValue(field: CronField, value: Int) extends DateTimeError