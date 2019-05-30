package scotty.quantum

import scotty.quantum.QuantumContext.Matrix

sealed trait Op {
  val qubitCount: Int
  val indexes: Seq[Int]
}

sealed trait Gate extends Op {
  val name = getClass.getSimpleName

  lazy val qubitCount = indexes.length

  def isUnitary()(implicit ctx: QuantumContext): Boolean = ctx.isUnitary(this)

  def matrix()(implicit ctx: QuantumContext): Matrix = this match {
    case targetGate: Target => ctx.matrix(targetGate)
    case controlGate: Control => ctx.controlMatrix(controlGate)
  }

  def par(gate: Gate)(implicit ctx: QuantumContext): Matrix = ctx.par(this, gate)

  def toString(implicit ctx: QuantumContext): String = matrix.toList.map(_.toList.mkString(" ")).mkString("\n")
}

trait Target extends Gate {
  val index: Int

  val params = Seq[Double]()
  val indexes = Seq(index)
}

trait Control extends Gate {
  val controlIndex: Int
  val target: Gate

  lazy val indexes = controlIndex +: target.indexes
  lazy val finalTarget: Target = target match {
    case t: Target => t
    case c: Control => c.finalTarget
  }
  lazy val finalTargetIndex = finalTarget.index
  lazy val controlIndexes = indexes.filter(i => i != finalTargetIndex)
  lazy val isAsc = controlIndex < target.indexes(0)
}

case class Controlled(controlIndex: Int, target: Gate) extends Control

case class H(index: Int) extends Target

case class I(index: Int) extends Target

case class X(index: Int) extends Target

case class Y(index: Int) extends Target

case class Z(index: Int) extends Target

case class CNOT(controlIndex: Int, targetIndex: Int) extends Control {
  val target = X(targetIndex)
}

case class CCNOT(controlIndex: Int, controlIndex2: Int, targetIndex: Int) extends Control {
  val target = Controlled(controlIndex2, X(targetIndex))
}