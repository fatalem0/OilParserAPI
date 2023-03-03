package config

import com.typesafe.config.ConfigFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._

final case class Config(host: String, port: Int)

object Config {
  def load(): Config = ConfigSource.fromConfig(ConfigFactory.load()).loadOrThrow[Config]
}
