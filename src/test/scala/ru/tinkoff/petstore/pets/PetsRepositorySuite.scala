package ru.tinkoff.petstore.pets

import cats.effect.IO
import cats.effect.std.UUIDGen
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import ru.tinkoff.petstore.domain.pet.{Pet, PetCategory}
import ru.tinkoff.petstore.repository.inmemory.PetRepositoryInMemory

import java.util.UUID

// Test with F[_]
class PetsRepositorySuite extends AnyFunSuite with Matchers with MockFactory {

//  test("PetRepository should create pet") {
//    val pet: Pet = Pet(UUID.randomUUID(), "Cat", PetCategory.Mammal, "description")
//
//    implicit val uuidGen: UUIDGen[IO] = mock[UUIDGen[IO]] // works only in mock
//    val id = UUID.randomUUID()
//
//    (uuidGen.randomUUID _).expects().returns(IO.pure(id))
//
//    val repo = new PetRepositoryInMemory[IO]
//    repo.create(pet) shouldBe pet
//  }
}
