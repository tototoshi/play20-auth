package jp.t2v.lab.play2.auth.social.providers.facebook

import jp.t2v.lab.play2.auth.social.core.OAuthProviderUserSupport
import play.api.Logger
import play.api.libs.ws.{ WS, WSResponse }
import play.api.Play.current
import play.api.mvc.Controller

import scala.concurrent.{ ExecutionContext, Future }

trait FacebookProviderUserSupport extends OAuthProviderUserSupport[String] {

  type ProviderUser = FacebookUser

  private def readProviderUser(accessToken: String, response: WSResponse): ProviderUser = {
    val j = response.json
    FacebookUser(
      (j \ "id").as[String],
      (j \ "name").as[String],
      (j \ "email").as[String],
      (j \ "picture" \ "data" \ "url").as[String],
      accessToken
    )
  }

  def retrieveProviderUser(accessToken: String)(implicit ctx: ExecutionContext): Future[ProviderUser] = {
    for {
      response <- WS.url("https://graph.facebook.com/me")
        .withQueryString("access_token" -> accessToken, "fields" -> "name,first_name,last_name,picture.type(large),email")
        .get()
    } yield {
      Logger(getClass).debug("Retrieving user info from provider API: " + response.body)
      readProviderUser(accessToken, response)
    }
  }

}
