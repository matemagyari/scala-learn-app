package org.bluecollar.wanderers

import scala.collection.immutable.Seq

object FPWanderers extends App {

  val maxCrossCount = 10
  val numOfWanderers = 100
  val margin = 0.9

  type CityId = String

  case class City(
      name: CityId,
      inhabitants: Int = 0,
      neighbours: Set[CityId])

  def isMoreAttractiveThan(current: City)(target: City) =
    (target.inhabitants.toDouble / current.inhabitants.toDouble) < margin

  case class Wanderer(crossCount: Int = 0, city: CityId)

  def move(wanderer: Wanderer, cities: Map[CityId, City]): Wanderer =
    if (wanderer.crossCount > maxCrossCount) {
      val currentCity = cities(wanderer.city)
      val neighbours: Set[City] = currentCity.neighbours.map(cities(_))
      val attractiveCities = neighbours.filter(isMoreAttractiveThan(currentCity)).to[Seq]
      if (attractiveCities.size > 0) {
        wanderer.copy(
          crossCount = wanderer.crossCount + 1,
          city = attractiveCities(0).name)
      } else wanderer
    } else wanderer

  val cities: Map[CityId, City] = Seq(
    City(name = "c1", neighbours = Set("c2", "c3")),
    City(name = "c2", neighbours = Set("c1", "c3")),
    City(name = "c3", neighbours = Set("c1", "c2")))
      .foldLeft(Map.empty[CityId, City]) { (acc, c) ⇒ acc + (c.name → c) }

  val wanderers = for {i ← (0 to numOfWanderers).to[Set]} yield new Wanderer(city = cities.head._1)

  def moveThem(wanderers: Set[Wanderer], cities: Map[CityId, City], count: Int = 10): Unit =
    count match {
      case 0 ⇒ println("end")
      case _ ⇒
        val uw = wanderers.map(w ⇒ move(w, cities))
        val wsByCities: Map[CityId, Set[Wanderer]] = uw.groupBy(_.city)
        def citySize(cityId: CityId) = wsByCities.get(cityId).map(_.size) getOrElse 0
        val ucitites = cities map { case(k, v) ⇒ (k, v.copy(inhabitants = citySize(k)))}
        println(ucitites)
        moveThem(wanderers, ucitites, count - 1)
    }

  moveThem(wanderers, cities)


}





