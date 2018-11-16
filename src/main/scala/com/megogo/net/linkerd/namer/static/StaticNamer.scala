package com.megogo.net.linkerd.namer.static

import com.megogo.net.linkerd.namer.static.StaticNamer.MalformedServices
import com.twitter.finagle.Addr
import com.twitter.finagle.Address
import com.twitter.finagle.Name
import com.twitter.finagle.NameTree
import com.twitter.finagle.Namer
import com.twitter.finagle.Path
import com.twitter.finagle.addr.WeightedAddress
import com.twitter.util.Activity
import com.twitter.util.Return
import com.twitter.util.Throw
import com.twitter.util.Try
import com.twitter.util.Var

class StaticNamer(services: Seq[String]) extends Namer {

  val servicesAddr: Map[String, Addr] = services.map { txt =>
    txt.split(":") match {
      case Array(name, addresses) => name -> addresses
      case _ => throw MalformedServices(txt)
    }
  }.toMap.mapValues { txt =>
    Try.collect(txt.split(",").map(t => StaticNamer.txtToAddress(t.trim))) match {
      case Return(addrs) => Addr.Bound(addrs.toSet, Addr.Metadata.empty)
      case Throw(e) => Addr.Failed(e)
    }
  }

  override def lookup(path: Path): Activity[NameTree[Name]] = {
    path.take(1) match {
      case id@Path.Utf8(serviceName) =>
        servicesAddr.get(serviceName) match {
          case Some(addr) =>
            Activity.value(NameTree.Leaf(Name.Bound(Var(addr), id, path.drop(1))))

          case None =>
            Activity.value(NameTree.Neg)
        }

      case _ =>
        Activity.value(NameTree.Neg)
    }
  }

}

object StaticNamer {

  case class MalformedServices(text: String)
    extends IllegalArgumentException(s"malformed services format: $text")

  case class MalformedAddress(text: String)
    extends IllegalArgumentException(s"malformed address: $text")

  private[this] val Whitespace = """\s+""".r

  /**
    * lines are in the format:
    * host port
    */
  private def txtToAddress(txt: String): Try[Address] = Whitespace.split(txt) match {
    case Array(host, PortNum(port)) => Try(Address(host, port))
    case Array(host, PortNum(port), "*", WeightNum(weight)) => Try(WeightedAddress(Address(host, port), weight))
    case _ => Throw(MalformedAddress(txt))
  }

  private object PortNum {
    val Max = math.pow(2, 16) - 1

    def unapply(s: String): Option[Int] =
      Try(s.toInt).toOption.filter { p => 0 < p && p <= Max }
  }

  private object WeightNum {
    def unapply(s: String): Option[Double] = Try(s.toDouble).toOption
  }

}
