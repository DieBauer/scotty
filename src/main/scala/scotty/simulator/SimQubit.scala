package scotty.simulator

import java.util.UUID

import scotty.quantum.QuantumMachine.{Complex, Qubit}

case class SimQubit(id: String = UUID.randomUUID().toString) extends Qubit

object SimQubit {
  def fiftyFifty: (Complex, Complex) = (Complex(1 / Math.sqrt(2.0)), Complex(1 / Math.sqrt(2.0)))

  def one: (Complex, Complex) = (Complex(0), Complex(1))

  def zero: (Complex, Complex) = (Complex(1), Complex(0))

  def from(n: (Complex, Complex)): Complex = n match {
    case (Complex(1, 0), Complex(0, 0)) => Complex(0)
    case (Complex(0, 0), Complex(1, 0)) => Complex(1)
  }
}