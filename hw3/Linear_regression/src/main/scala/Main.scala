import breeze.linalg.{*, DenseMatrix, DenseVector, csvread, csvwrite, inv, norm, sum}
import breeze.stats.mean
import breeze.numerics.{pow, round}
import java.io.File
import scala.util.Random

class LinearRegression(normalize: Boolean = false) {
  private var w: DenseVector[Double] = DenseVector()
  var is_terminated: Boolean = false

  def fit(x: DenseMatrix[Double], y: DenseVector[Double], cv_num: Int = 5): Map[String, Double] = {
    var x_copied: DenseMatrix[Double] = x.copy
    var cv_values: Map[String, Double] = Map[String, Double]()
    if (!this.is_terminated) {
      if (this.normalize) {
        x_copied = x_copied(::, *).map(col => col / norm(col))
      }
      if (cv_num > 1) {
        val fold_size = round(x_copied.rows / cv_num)
        var indexes = (0 until x_copied.rows).toList
        indexes = Random.shuffle(indexes)
        for (i <- 0 until cv_num) {
          var fold_indexes: List[Int] = List[Int]()
          if (i < cv_num - 1) {
            fold_indexes = indexes.slice(i * fold_size, (i + 1) * fold_size)
          } else {
            fold_indexes = indexes.slice((cv_num - 1) * fold_size, x_copied.rows)
          }
          val train_indexes = indexes.toSet.diff(fold_indexes.toSet).toList
          val x_fold = x_copied(train_indexes, ::).toDenseMatrix
          val y_fold = y(train_indexes).toDenseVector
          var cur_w: DenseVector[Double] = DenseVector()
          cur_w = inv(x_fold.t * x_fold) * x_fold.t * y_fold
          val y_pred = x_copied(fold_indexes, ::).toDenseMatrix * cur_w
          val y_label = y(fold_indexes).toDenseVector
          val score = this.get_r_squared(y_label, y_pred)
          println(s"Score on fold$i: $score")
          cv_values += (s"Fold_$i" -> score)
        }
      }
      this.w = inv(x_copied.t * x_copied) * x_copied.t * y
      this.is_terminated = true
      cv_values
    } else {
      println("Already fitted.")
      cv_values
    }
  }

  def predict(x: DenseMatrix[Double]): DenseVector[Double] = {
    if (!this.is_terminated) {
      println("Model is not fitted. Please call fit() before predict().")
    }
    if (this.normalize) {
      x(::, *).map(col => col / norm(col)) * this.w
    } else {
      x * this.w
    }
  }

  def get_r_squared(y_label: DenseVector[Double],
                    y_pred: DenseVector[Double],
                    adjusted: Boolean = false): Double = {
    val squares_sum = sum(pow(y_label - y_pred, 2))
    val target_mean = mean(y_label)
    val mean_squares_sum = sum(pow(y_label - target_mean, 2))
    if (adjusted) {
      val n = y_label.length
      val p = this.w.length
      1 - (squares_sum / (n - p - 1)) / (mean_squares_sum / (n - 1))
    } else {
      1 - squares_sum / mean_squares_sum
    }
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    val train_path = args(0)
    val test_path = args(1)
    val predict_path = args(2)
    val train_data = csvread(new File(train_path), ';', skipLines = 1)
    val test_data = csvread(new File(test_path), ';', skipLines = 1)

    val linear_regression = new LinearRegression()
    val cv_values = linear_regression.fit(x = train_data(::, 1 to -1), y = train_data(::, 0), cv_num = 5)
    val predictions = linear_regression.predict(x = test_data(::, 1 to -1))

    csvwrite(new File(predict_path), predictions.asDenseMatrix.t)
  }
}
