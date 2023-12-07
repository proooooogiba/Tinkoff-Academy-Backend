package ru.tinkoff.newsaggregator.repository

import ru.tinkoff.newsaggregator.domain.pet.{Pet, PetCategory}

import java.util.UUID

trait PetRepository[F[_]] {
  def create(pet: Pet): F[Long]

  def update(pet: Pet): F[Pet]

  def list: F[List[Pet]]

  def get(id: UUID): F[Option[Pet]]

  def delete(id: UUID): F[Option[Pet]]

  def listByCategory(category: PetCategory): F[List[Pet]]
}