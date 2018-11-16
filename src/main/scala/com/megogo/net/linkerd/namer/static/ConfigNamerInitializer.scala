package com.megogo.net.linkerd.namer.static

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.Namer
import com.twitter.finagle.Path
import com.twitter.finagle.Stack
import io.buoyant.namer.NamerConfig
import io.buoyant.namer.NamerInitializer

/**
  * Supports namer configurations in the form:
  *
  * <pre>
  * namers:
  * - kind: io.l5d.static
  *   experimental: true
  *   services:
  *     - web:127.0.0.1 8080,127.0.0.1 8081 * 2,127.0.0.1 8082
  *     - japi:127.0.0.1 8080,127.0.0.1 8081,127.0.0.1 8082
  * </pre>
  */
class StaticNamerInitializer extends NamerInitializer {
  override val configClass = classOf[StaticNamerConfig]

  override def configId: String = "io.l5d.static"
}

object StaticNamerInitializer extends StaticNamerInitializer

class StaticNamerConfig(services: Seq[String]) extends NamerConfig {

  @JsonIgnore
  override def experimentalRequired = true

  @JsonIgnore
  override def defaultPrefix: Path = Path.read("/io.l5d.static")

  override protected def newNamer(params: Stack.Params): Namer = {
    new StaticNamer(services)
  }

}

case class Service(name: String, host: String, port: Int, weight: Option[Double])
