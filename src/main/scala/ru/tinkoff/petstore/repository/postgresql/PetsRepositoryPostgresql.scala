package ru.tinkoff.petstore.repository.postgresql

import cats.effect.kernel.MonadCancelThrow
import cats.implicits.toFunctorOps
import doobie.Transactor
import doobie.implicits._
import io.getquill.SnakeCase
import io.getquill.doobie.DoobieContext
import ru.tinkoff.petstore.domain.pet.{Pet, PetCategory}
import ru.tinkoff.petstore.repository.PetRepository

import java.util.UUID

class PetsRepositoryPostgresql[F[_]: MonadCancelThrow](implicit tr: Transactor[F])
    extends PetRepository[F]
    with QuillInstances {

  private val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def create(pet: Pet): F[Long] = run {
    quote {
      query[Pet].insertValue(lift(pet))
    }
  }.transact(tr)

  override def update(pet: Pet): F[Pet] = run {
    quote(
      query[Pet].filter(_.id == lift(pet.id)).updateValue(lift(pet)).returning(r => r),
    )
  }.transact(tr)

  override def list: F[List[Pet]] = run {
    quote(
      query[Pet],
    )
  }.transact(tr)

  override def get(id: UUID): F[Option[Pet]] = run {
    quote(
      query[Pet].filter(_.id == lift(id)),
    )
  }.transact(tr).map(_.headOption)

  override def delete(id: UUID): F[Option[Pet]] = run {
    quote(
      query[Pet].filter(_.id == lift(id)).delete.returningMany(x => x),
    )
  }.transact(tr).map(_.headOption)

  override def listByCategory(category: PetCategory): F[List[Pet]] = run {
    quote(
      query[Pet].filter(_.category == lift(category)),
    )
  }.transact(tr)
}
