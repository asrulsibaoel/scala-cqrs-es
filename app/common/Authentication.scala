package common

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import play.api.mvc.Action

/**
  * Created by asrulsibaoel on 23/02/17.
  */
object Authentication {

  val jwtSecretKey = "BisMillAh"
  val jwtSecretAlgo = "HS256"

  def createToken(payload: String): String = {
    val header = JwtHeader(jwtSecretAlgo)
    val claimSet = JwtClaimsSet(payload)
    JsonWebToken(header, claimSet, jwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean = {
    JsonWebToken.validate(jwtToken, jwtSecretKey)
  }

  def decodePayload(jwtToken: String) :Option[String] = jwtToken match {
    case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asJsonString)
    case _ => None
  }
}