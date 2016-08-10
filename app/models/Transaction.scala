package models

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class Transaction(id: Long, typeName: String, amount: Double, parentId: Option[Long])

object Transaction {
  implicit val transactionWrites = new Writes[Transaction] {
    def writes(tx: Transaction) = Json.obj(
      "transaction_id" -> tx.id,
      "type" -> tx.typeName,
      "amount" -> tx.amount,
      "parent_id" -> tx.parentId
    )
  }

  implicit val transactionReads: Reads[Transaction] = (
    (JsPath \ "transaction_id").read[Long] and
    (JsPath \ "type").read[String] and
    (JsPath \ "amount").read[Double] and
    (JsPath \ "parent_id").readNullable[Long]
  )(Transaction.apply _)
}

/*
object Transaction {
    implicit object TransactionFormat extends Format[Transaction] {
            def reads(json: JsValue) = JsSuccess(Transaction(
                (json \ "amount").as[Double],
                (json \ "type").as[String],
                (json \ "parent_id").as[Long]
            ))

            def writes(tx: Transaction): JsValue = JsObject(Seq(
                "id" -> JsNumber(tx.id),
                "type" -> JsString(tx.typeName),
                "amount" -> JsNumber(tx.amount),
                "parent_id" -> JsNumber(tx.parentId)
            ))
    }
}
*/
