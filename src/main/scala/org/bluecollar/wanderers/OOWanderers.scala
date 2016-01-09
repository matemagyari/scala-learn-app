package org.bluecollar.wanderers

import scala.collection.immutable.Seq

object OOWanderers extends App {

  val maxCrossCount = 10
  val numOfWanderers = 100
  val margin = 0.9

  class City(
      val name: String,
      var inhabitants: Set[Wanderer] = Set.empty) {

    var neighbours: Set[City] = Set.empty

    def numberOfInhabitants = inhabitants.size

    def enter(wanderer: Wanderer) = {
      inhabitants += wanderer
    }

    def leave(wanderer: Wanderer) = {
      inhabitants -= wanderer
    }
  }

  class Wanderer(private var city: City) {

    private var crossCount: Int = 0

    city.enter(this)

    def isAttractive(target: City) =
      (target.numberOfInhabitants.toDouble / city.numberOfInhabitants.toDouble) < margin

    def move() = {
      if (crossCount < maxCrossCount) {
        val attractiveCities = city.neighbours.filter(isAttractive)
        if (attractiveCities.size > 0) {
          city.leave(this)
          city = attractiveCities.toList((math.random * attractiveCities.size).toInt)
          city.enter(this)
          crossCount += 1
        }
      }
    }

  }


  def createCityNet(n: Int) = {
    val cities = for {i ← (0 to n)} yield new City(name = s"city$i")
    cities foreach { city ⇒
      city.neighbours = cities.filterNot(_ == city).toSet
    }
    cities.to[Seq]
  }

  val cities = createCityNet(4)
  cities foreach { city ⇒ println(s"{$city.name} - ${city.neighbours.map(_.name)}") }
  val wanderers = for {i ← (0 to numOfWanderers)} yield new Wanderer(city = cities(0))

  for (i ← (0 to 10)) {
    cities foreach { city ⇒
      city.inhabitants foreach { w ⇒ w.move() }
    }
    cities foreach { city ⇒ println(s"${city.name} - ${city.numberOfInhabitants}") }
  }
}




