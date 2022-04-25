package loginandattend

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://devapi.vaitcampus.com/") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val feeder = csv("login.csv").circular  //queue :- will move step by step 
  // TODO add login csv with credentials
  //circular :- it iterate over whatever number of rows available.

  val login = scenario("Login and Attend Class Test")
  .exec(http("Login")
    .get("/login"))
  .pause(1)
  .feed(feeder)
  .exec(http("authorization")
    .post("/account/login")
    .queryParam("""username""", "${email}")
    .queryParam("""password""", "${password}")
    .check(status.is("ok")))
  .pause(1)

  val goToClass = exec(http("GoToClass")
      .get("/classroom/live/20423")
      .check(status.is(200))
      )

  // setUp(
  //   login.inject(
  //     rampUsers(10).during(5)
  //   ).protocols(httpProtocol)
  // )

  setUp(login.inject(atOnceUsers(1)).protocols(httpProtocol))

}
