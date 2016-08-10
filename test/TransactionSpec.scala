import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

import services.{TransactionService, TransactionServiceImpl}
import models.Transaction

class TransactionSpec extends PlaySpec with OneAppPerTest {

  "TransactionService" must {

    "have 3 transactions stored" in  {
      val service : TransactionService = new TransactionServiceImpl()
      service.store(Transaction(1L, "type1", 4.5, None))
      service.store(Transaction(2L, "type2", 5.5, None))
      service.store(Transaction(3L, "type3", 6.5, None))

      service.retrieve(1L) must not be None
      service.retrieve(2L) must not be None
      service.retrieve(3L) must not be None
    }
    "list transactions of the same type" in  {
      val service : TransactionService = new TransactionServiceImpl()
      service.store(Transaction(1L, "type1", 4.5, None))
      service.store(Transaction(2L, "type2", 5.5, None))
      service.store(Transaction(3L, "type2", 6.5, None))

      service.listType("type1") must have size 1
      service.listType("type2") must have size 2
      service.listType("type3") must have size 0
    }
    "correctly sum up transaction amounts" in  {
      val service : TransactionService = new TransactionServiceImpl()
      service.store(Transaction(1L, "type1", 1.0, None))
      System.out.println(service.sum(1L))
      service.sum(1L) must equal (Some(1.0))

      service.store(Transaction(2L, "type2", 2.0, Some(1L)))
      service.sum(1L) must equal (Some(3.0))

      service.store(Transaction(3L, "type3", 3.0, Some(2L)))
      service.sum(1L) must equal (Some(6.0))

      service.sum(2L) must equal (Some(5.0))
      service.sum(3L) must equal (Some(3.0))

      service.sum(4L) must be (None)
    }
  }
}
