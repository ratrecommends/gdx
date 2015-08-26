package com.ratrecommends.gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net.{HttpRequest, HttpResponseListener}
import com.badlogic.gdx.net.HttpStatus

import scala.concurrent.Promise
import scala.concurrent.duration.Duration.Infinite
import scala.concurrent.duration.{Duration, FiniteDuration}

trait GdxNet {

  def sendHttpRequest(url: String,
                      content: String = null,
                      method: HttpMethod = HttpGet,
                      headers: Map[String, String] = Map.empty,
                      timeout: Duration = Duration.Inf,
                      followRedirects: Boolean = true) = {
    val p = Promise[HttpResponse]()
    val request = new HttpRequest()
    request.setUrl(url)
    if (content != null) request.setContent(content)
    request.setMethod(method match {
      case HttpGet => "GET"
      case HttpPost => "POST"
      case HttpPut => "PUT"
      case HttpDelete => "DELETE"
    })
    headers.foreach {
      case (k, v) => request.setHeader(k, v)
    }
    request.setTimeOut(timeout match {
      case _: Infinite => 0
      case f: FiniteDuration => f.toMillis.toInt
    })
    request.setFollowRedirects(followRedirects)

    Gdx.net.sendHttpRequest(request, new HttpResponseListener {
      override def handleHttpResponse(response: com.badlogic.gdx.Net.HttpResponse): Unit = {
        //todo
      }

      override def cancelled(): Unit = p.failure(new RuntimeException("cancelled"))

      override def failed(t: Throwable): Unit = p.failure(t)
    })
    p
  }
}

sealed trait HttpMethod

case object HttpGet extends HttpMethod

case object HttpPost extends HttpMethod

case object HttpPut extends HttpMethod

case object HttpDelete extends HttpMethod

case class HttpResponse(status: HttpStatus)