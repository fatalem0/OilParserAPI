package data

import io.circe.Encoder

case class MinAndMaxPrices(minPrice: String, maxPrice: String)

object MinAndMaxPrices {
  implicit val encodeMinAndMaxPrices: Encoder[MinAndMaxPrices] =
    Encoder.forProduct2("min_price", "max_price")(prices =>
      (prices.minPrice, prices.maxPrice)
    )
}