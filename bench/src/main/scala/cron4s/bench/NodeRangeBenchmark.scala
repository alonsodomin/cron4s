package cron4s.bench

import cron4s.{CronField, CronUnit}
import cron4s.expr._

import org.openjdk.jmh.annotations._

import shapeless.Coproduct

import scalaz.NonEmptyList

/**
  * Created by alonsodomin on 29/12/2016.
  */
@State(Scope.Benchmark)
class NodeRangeBenchmark {
  import CronField._
  import CronUnit._

  val eachNode = EachNode[Minute]
  val constNode = ConstNode[Minute](30)

  val betweenNode: BetweenNode[Minute] = BetweenNode(
    ConstNode(CronUnit.Minutes.min),
    ConstNode(CronUnit.Minutes.max)
  )

  // SeveralNode made of constants and ranges, we ensure the arrays have the same number of elements

  val severalConstNode: SeveralNode[Minute] = {
    val stepSize = Minutes.max / 10
    val minutes = (Minutes.min to Minutes.max by stepSize)
      .map(value => ConstNode[Minute](value))
      .map(Coproduct[SeveralMemberNode[Minute]](_))

    SeveralNode(NonEmptyList(minutes.head, minutes.tail: _*))
  }

  val severalBetweenNode: SeveralNode[Minute] = {
    val unit = CronUnit[CronField.Minute]
    val chunkSize = unit.max / 10

    val minuteRanges = (unit.min to unit.max by chunkSize).map { lower =>
      BetweenNode[Minute](ConstNode(lower), ConstNode(lower + chunkSize - 1))
    }.map(Coproduct[SeveralMemberNode[Minute]](_))

    SeveralNode[Minute](NonEmptyList(minuteRanges.head, minuteRanges.tail: _*))
  }

  val everyEachNode = EveryNode(eachNode, 10)
  val everyConstNode = EveryNode(constNode, 10)
  val everySeveralConstNode = EveryNode(severalConstNode, 10)
  val everySeveralBetweenNode = EveryNode(severalBetweenNode, 10)

  @Benchmark
  def eachNodeRange() = eachNode.range

  @Benchmark
  def constNodeRange() = constNode.range

  @Benchmark
  def betweenNodeRange() = betweenNode.range

  @Benchmark
  def severalConstNodeRange() = severalConstNode.range

  @Benchmark
  def severalBetweenNodeRange() = severalBetweenNode.range

  @Benchmark
  def everyEachNodeRange() = everyEachNode.range

  @Benchmark
  def everyConstNodeRange() = everyConstNode.range

  @Benchmark
  def everySeveralConstNodeRange() = everySeveralConstNode.range

  @Benchmark
  def everySeveralBetweenNodeRange() = everySeveralBetweenNode.range

}
