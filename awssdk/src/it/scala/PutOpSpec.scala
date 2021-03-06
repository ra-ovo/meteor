package meteor

import cats.effect.IO
import cats.implicits._

class PutOpSpec extends ITSpec {

  behavior.of("put operation")

  it should "success inserting item with both keys" in forAll {
    test: TestData =>
      val tableName = Table("test_primary_keys")
      val result = Client.resource[IO].use { client =>
        client.put[TestData](tableName, test)
      }
      result.unsafeRunSync() shouldBe an[Unit]
  }

  it should "return old value after successfully inserting item with both keys" in forAll {
    old: TestData =>
      val tableName = Table("test_primary_keys")
      val updated = old.copy(str = old.str + "-updated")
      val result =
        Client.resource[IO].use { client =>
          client.put[TestData](tableName, old) >> client.put[
            TestData,
            TestData
          ](tableName, updated)
        }
      result.unsafeToFuture().futureValue shouldEqual Some(old)
  }

  it should "success inserting item without sort key" in forAll {
    test: TestDataSimple =>
      val tableName = Table("test_partition_key_only")
      val result = Client.resource[IO].use { client =>
        client.put[TestDataSimple](tableName, test)
      }
      result.unsafeToFuture().futureValue shouldBe an[Unit]
  }

  it should "return old value after successfully inserting item without sort key" in forAll {
    old: TestDataSimple =>
      val tableName = Table("test_partition_key_only")
      val updated = old.copy(data = old.data + "-updated")
      val result = Client.resource[IO].use { client =>
        client.put[TestDataSimple](tableName, old) >>
          client.put[TestDataSimple, TestDataSimple](
            tableName,
            updated
          )
      }
      result.unsafeToFuture().futureValue shouldEqual Some(old)
  }
}
