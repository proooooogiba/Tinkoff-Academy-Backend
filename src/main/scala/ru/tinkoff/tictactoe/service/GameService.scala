package ru.tinkoff.tictactoe.service

//trait GameService[F[_]] {
//  def create(createOrder: CreateOrder): F[OrderResponse]
//
//  def list: F[List[OrderResponse]]
//
//  def get(id: UUID): F[Option[OrderResponse]]
//
//  def delete(id: UUID): F[Option[OrderResponse]]
//}
//
//case class RepositoryGameService[F[_]: UUIDGen: FlatMap: Clock](
//    orderRepository: OrderRepository[F],
//) extends GameService[F] {
//  def create(createOrder: CreateOrder): F[OrderResponse] =
//    for {
//      id <- UUIDGen[F].randomUUID
//      now <- Clock[F].realTimeInstant
//      order = Order.fromCreateOrder(id, now, createOrder)
//      _ <- orderRepository.create(order)
//    } yield order.toResponse
//
//  override def list: F[List[OrderResponse]] =
//    orderRepository.list
//      .map(_.map(_.toResponse))
//
//  override def get(id: UUID): F[Option[OrderResponse]] =
//    orderRepository
//      .get(id)
//      .map(_.map(_.toResponse))
//
//  override def delete(id: UUID): F[Option[OrderResponse]] =
//    orderRepository
//      .delete(id)
//      .map(_.map(_.toResponse))
//}
