package scotty.quantum

import scotty.quantum.math.{Complex, MathUtils}
import scotty.quantum.QuantumContext._

trait QuantumContext {
  def run(circuit: Circuit): State

  def controlMatrix(gate: Control): Matrix

  def par(gate1: Gate, gate2: Gate): Matrix

  def isUnitary(gate: Gate): Boolean

  def targetMatrix(target: Target): Matrix

  def runAndMeasure(circuit: Circuit): Collapsed = {
    run(circuit) match {
      case s: Superposition => s.measure
      case s: Collapsed => s
    }
  }
}

object QuantumContext {
  type Vector = Array[Complex]
  type Matrix = Array[Array[Complex]]

  case class QuantumException(message: String) extends Exception(message)

  case class Qubit(a: Complex, b: Complex) {
    require(Qubit.areAmplitudesValid(this), "Amplitudes have to add up to 1")
  }

  object Qubit {
    def one: Qubit = Qubit(Complex(0), Complex(1))

    def zero: Qubit = Qubit(Complex(1), Complex(0))

    def fiftyFifty: Qubit = this(Complex(1 / Math.sqrt(2.0)), Complex(1 / Math.sqrt(2.0)))

    def areAmplitudesValid(q: Qubit): Boolean = MathUtils.isProbabilityValid(q.a.abs, q.b.abs)
  }

  sealed trait Register[T] {
    val values: Seq[T]

    def size = values.length
  }

  case class QuantumRegister(values: Qubit*) extends Register[Qubit]

  case class BinaryRegister(values: Int*) extends Register[Int]
}