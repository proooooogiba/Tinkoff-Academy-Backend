package ru.tinkoff.newsaggregator.service

import cats.FlatMap
import cats.effect.std.UUIDGen
import ru.tinkoff.newsaggregator.domain.pet.{CreatePet, Pet, PetCategory, PetResponse}
import ru.tinkoff.newsaggregator.repository.PetRepository
import cats.syntax.functor._
import cats.syntax.flatMap._

import java.util.UUID

trait PetService[F[_]] {
  def create(createPet: CreatePet): F[PetResponse]

  def update(id: UUID, createPet: CreatePet): F[PetResponse]

  def list: F[List[PetResponse]]

  def get(id: UUID): F[Option[PetResponse]]

  def delete(id: UUID): F[Option[PetResponse]]

  def listByCategory(category: PetCategory): F[List[PetResponse]]
}

object PetService {
  private class Impl[F[_]: UUIDGen: FlatMap](petRepository: PetRepository[F])
      extends PetService[F] {
    override def create(createPet: CreatePet): F[PetResponse] =
      for {
        id <- UUIDGen[F].randomUUID
        pet = Pet.fromCreatePet(id, createPet)
        _ <- petRepository.create(pet)
      } yield pet.toResponse

    override def update(id: UUID, createPet: CreatePet): F[PetResponse] =
      petRepository
        .update(Pet.fromCreatePet(id, createPet))
        .map(_.toResponse)

    override def list: F[List[PetResponse]] =
      petRepository.list
        .map(_.map(_.toResponse))

    override def get(id: UUID): F[Option[PetResponse]] =
      petRepository
        .get(id)
        .map(_.map(_.toResponse))

    override def delete(id: UUID): F[Option[PetResponse]] =
      petRepository
        .delete(id)
        .map(_.map(_.toResponse))

    override def listByCategory(category: PetCategory): F[List[PetResponse]] =
      petRepository
        .listByCategory(category)
        .map(_.map(_.toResponse))
  }

  def make[F[_]: UUIDGen: FlatMap](petRepository: PetRepository[F]): PetService[F] =
    new Impl[F](petRepository)
}
