package ru.tinkoff.petstore.repository.inmemory

import cats.Functor
import cats.syntax.functor._
import ru.tinkoff.petstore.common.cache.Cache
import ru.tinkoff.petstore.domain.pet.{Pet, PetCategory}
import ru.tinkoff.petstore.repository.PetRepository

import java.util.UUID

class PetRepositoryInMemory[F[_]: Functor](cache: Cache[F, UUID, Pet]) extends PetRepository[F] {

  override def create(pet: Pet): F[Long] =
    cache
      .add(pet.id, pet)
      .as(1L)

  override def update(pet: Pet): F[Pet] =
    cache
      .update(pet.id, pet)
      .as(pet)

  override def list: F[List[Pet]] = cache.values

  override def get(id: UUID): F[Option[Pet]] = cache.get(id)

  override def delete(id: UUID): F[Option[Pet]] = cache.remove(id)

  override def listByCategory(category: PetCategory): F[List[Pet]] =
    cache.values
      .map(_.filter(p => p.category == category))
}

object PetRepositoryInMemory {
  def apply[F[_]: Functor](cache: Cache[F, UUID, Pet]) =
    new PetRepositoryInMemory[F](cache)
}
