package com.YuriFerreira.PortfolioOptimization

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import com.YuriFerreira.PortfolioOptimization.JsonFormats._
import com.YuriFerreira.PortfolioOptimization.Main._


object WebServer {

  // Define the Actor System and the execution context for the future
  implicit val system: ActorSystem = ActorSystem("webServerSystem")
  implicit val executionContext = system.dispatcher

  // Define the route
  val route: Route = {
    path("run-optimization") {
      post {
        entity(as[Config]) { config =>
          // Here you call your Scala logic with the provided config
          runMain(config)
          complete("Optimization Run Successful")
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    // Start the server on localhost and a specified port (e.g., 8080)
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    scala.io.StdIn.readLine() // Let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // Trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}