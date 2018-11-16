package com.megogo.net.linkerd.namer.static

import com.twitter.finagle.Addr
import com.twitter.finagle.Address
import com.twitter.finagle.Name
import com.twitter.finagle.NameTree
import com.twitter.finagle.Namer
import com.twitter.finagle.Path
import com.twitter.finagle.addr.WeightedAddress
import com.twitter.util.Activity
import com.twitter.util.Var

class ConfigNamer(services: Option[Set[Service]]) extends Namer {

  override def lookup(path: Path): Activity[NameTree[Name]] = {
    services match {
      case Some(srvs) if srvs.isEmpty =>
        Activity.value(NameTree.Neg)

      case Some(srvs) =>
        lookup(path, srvs)

      case None =>
        Activity.exception(new RuntimeException("Empty services"))
    }
  }

  private def lookup(path: Path, srvs: Set[Service]): Activity[NameTree[Name]] = {
    path.take(1) match {
      case id@Path.Utf8(serviceName) =>
        srvs.groupBy(_.name).get(serviceName) match {
          case Some(availableServices) =>
            val addresses = availableServices.collect {
              case Service(_, host, port, None) =>
                Address(host, port)
              case Service(_, host, port, Some(weight)) =>
                WeightedAddress(Address(host, port), weight)
            }.toSeq
            Activity.value(NameTree.Leaf(Name.Bound(Var(Addr.Bound(addresses: _*)), id, path.drop(1))))

          case None => Activity.value(NameTree.Neg)
        }

      case _ =>
        Activity.value(NameTree.Neg)
    }
  }

}
