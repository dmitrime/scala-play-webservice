package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json._

import services.TransactionService
import models.Transaction

/**
 * Controller with injected [[TransactionService]] for handling 
 * transaction operations.
 */
@Singleton
class TransactionController @Inject() (transactionService: TransactionService) extends Controller {

  /**
   * Action that stores a new transaction.
   */
  def store(transactionId: Long) = Action(parse.json) { request => 
    val tx = (request.body.as[JsObject] + ("transaction_id" -> Json.toJson(transactionId))).as[Transaction]
    transactionService.store(tx)
    Ok(Json.obj("status" -> "ok")) 
  }
  
  /**
   * Action that retrieves a transaction by id.
   */
  def retrieve(transactionId: Long) = Action {
    Ok(Json.toJson(transactionService.retrieve(transactionId)))
  }

  /**
   * Action that returns a list of all IDs of the given type.
   */
  def listType(typeName: String) = Action {
    Ok(Json.toJson(transactionService.listType(typeName))) 
  }

  /**
   * Action that sums up the amounts of the transaction together with its children, transitively.
   */
  def sum(transactionId: Long) = Action {
    Ok(Json.toJson(transactionService.sum(transactionId)))
  }

}
