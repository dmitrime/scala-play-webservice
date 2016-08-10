package services

import javax.inject._

import models.Transaction

/**
 * Represents a transaction that can be stored, read, summed and grouped  * by type.
 */
trait TransactionService {
  //def store(transactionId: Long)
  def retrieve(transactionId: Long): Option[Transaction]
  def listType(typeName: String): List[Long]
  //def sum(parentId: Long): Double
}

/**
 * This class is a concrete implementation of the [[TransactionService]] trait.
 * It is configured for Guice dependency injection in the [[Module]]
 * class.
 *
 * This class has a `Singleton` annotation because we need to make
 * sure we only use one counter per application. Without this
 * annotation we would get a new instance every time a [[TransactionService]] is
 * injected.
 */
@Singleton
class TransactionServiceImpl extends TransactionService {  
  val transactions = scala.collection.mutable.Map(
    1L -> Transaction(1, "A-type", 4.2, Some(10L)), 
    2L -> Transaction(2, "B-type", 1.2, Some(20L)),
    3L -> Transaction(3, "B-type", 3.2, None))

  val transactionTypes = scala.collection.mutable.Map(
    "A-type" -> List(1L),
    "B-type" -> List(2L, 3L))

  override def retrieve(transactionId: Long): Option[Transaction] = 
    if (transactions.contains(transactionId)) Some(transactions(transactionId)) else None

  override def listType(typeName: String): List[Long] = 
    if (transactionTypes.contains(typeName)) transactionTypes(typeName) else List()

}
