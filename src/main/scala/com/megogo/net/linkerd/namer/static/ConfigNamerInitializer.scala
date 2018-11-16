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
  * - kind: io.l5d.config
  *   experimental: true
  *   services:
  *   - name: web
  *     host: 127.0.0.1
  *     port: 8080
  *   - name: web
  *     host: 127.0.0.1
  *     port: 8081
  *   - name: web
  *     host: 127.0.0.1
  *     port: 8082
  * </pre>
  */
class ConfigNamerInitializer extends NamerInitializer {
  override val configClass = classOf[ConfigNamerConfig]
  override def configId: String = "com.megogo.config"
}

class ConfigNamerConfig(services: Option[Set[Service]]) extends NamerConfig {

  @JsonIgnore
  override def experimentalRequired = true

  @JsonIgnore
  override def defaultPrefix: Path = Path.read("/com.megogo.config")

  override protected def newNamer(params: Stack.Params): Namer = {
    new ConfigNamer(services)
  }

}

case class Service(name: String, host: String, port: Int, weight: Option[Double])
