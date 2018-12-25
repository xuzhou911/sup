package sup.modules

import cats.data.OneAnd
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import sup.data.Tagged
import sup.{Health, HealthResult}

object circe {
  implicit val healthEncoder: Encoder[Health] = deriveEncoder
  implicit val healthDecoder: Decoder[Health] = deriveDecoder

  implicit def taggedEncoder[Tag: Encoder, H: Encoder]: Encoder[Tagged[Tag, H]] = deriveEncoder
  implicit def taggedDecoder[Tag: Decoder, H: Decoder]: Decoder[Tagged[Tag, H]] = deriveDecoder

  implicit def reportEncoder[H[_], A: Encoder](implicit H: Encoder[H[A]]): Encoder[OneAnd[H, A]] =
    Encoder.forProduct2("health", "checks")(oa => (oa.head, oa.tail))

  implicit def reportDecoder[H[_], A: Decoder](implicit H: Decoder[H[A]]): Decoder[OneAnd[H, A]] =
    Decoder.forProduct2[OneAnd[H, A], A, H[A]]("health", "checks")(OneAnd(_, _))

  implicit def healthResultEncoder[H[_]](implicit E: Encoder[H[Health]]): Encoder[HealthResult[H]] =
    E.contramap(_.value)

  implicit def healthResultDecoder[H[_]](implicit D: Decoder[H[Health]]): Decoder[HealthResult[H]] =
    D.map(HealthResult(_))
}