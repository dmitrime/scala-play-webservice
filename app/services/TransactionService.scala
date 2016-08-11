package services

import javax.inject._
import scala.collection._

import models.Transaction

/**
 * Represents a transaction that can be stored, read, summed and grouped  * by type.
 */
trait TransactionService {
  def store(transaction: Transaction)
  def retrieve(transactionId: Long): Option[Transaction]
  def listType(typeName: String): List[Long]
  def sum(transactionId: Long): Option[Double]
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
  private val transactions : concurrent.Map[Long, Transaction] = concurrent.TrieMap()
  private val transactionTypes : concurrent.Map[String, mutable.ListBuffer[Long]] = concurrent.TrieMap()
  private val childTransactions : concurrent.Map[Long, mutable.ListBuffer[Long]] = concurrent.TrieMap()

  /*
   * Do a depth-first search starting from id and visit all its decendents.
   */
  private def searchAndSum(id: Long) : Double = {
    val visited = mutable.SortedSet[Long]()
    def _search(id: Long) : Double = {
      visited += id
      childTransactions.getOrElse(id, List()).toList match {
        case Nil => transactions(id).amount
        case xs  => transactions(id).amount + xs.filter(_id => !visited(_id)).map(_search).sum
      }
    }
    _search(id)
  }

  /*
   * Store a new transaction by adding it to the transaction map,
   * its ID to the transaction type map under its type key,
   * and to child map under its parent ID key.
   */
  override def store(transaction: Transaction) = {
    transactions += (transaction.id -> transaction)
    transactionTypes.getOrElseUpdate(transaction.typeName, 
                                     mutable.ListBuffer()) += transaction.id
    transaction.parentId match {
      case Some(parent) => childTransactions.getOrElseUpdate(
                               parent, mutable.ListBuffer()) += transaction.id
      case None => Unit
    }
  }

  /*
   * Lookup and return the transaction by ID, if it exists.
   */
  override def retrieve(transactionId: Long): Option[Transaction] = 
    transactions.get(transactionId)

  /*
   * List all transaction IDs having the same typeName.
   */
  override def listType(typeName: String): List[Long] = 
    transactionTypes.getOrElse(typeName, List()).toList

  /*
   * Sum up the amounts of the transaction and all its children, recursively.
   */
  override def sum(transactionId: Long): Option[Double] = 
    if (transactions.contains(transactionId)) Some(searchAndSum(transactionId)) else None
}
