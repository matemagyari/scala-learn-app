package org.bluecollar.ships

case class Position(longitude: Double, latitude: Double)

case class PortId(value: Int)

class Port(
    val portId: PortId,
    val position: Position,
    var capacity: Int) {
}

trait Radio {
  def getPorts(): Set[Port]
}

class Ship(
    private var fuel: Int = 0, //units in days
    private var food: Int = 0, //same
    private val startPort: Port,
    private val radio: Radio) {

  private var position = startPort.position
  private var nextPort: Port = startPort

  def determineNextPort(): Unit = {
    val ports = radio.getPorts()
    //based on its position, fuel, food and the attributes
    //of ports (distance, capacity) determine the nextPort
  }
}

object World {

  private val ships: Set[Ship] = ???
  private val ports: Set[Ship] = ???

  def aDayPassed() = {

  }
}
