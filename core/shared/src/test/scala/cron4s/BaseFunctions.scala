package cron4s

/**
  * Created by alonsodomin on 31/07/2016.
  */
trait BaseFunctions {

  def consumeStep(stepSize: Int): Int = {
    if (stepSize > 0) stepSize - 1
    else if (stepSize < 0) stepSize + 1
    else stepSize
  }

}
