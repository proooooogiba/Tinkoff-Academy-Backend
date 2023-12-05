package ru.tinkoff.petstore.domain.news

import ru.tinkoff.petstore.domain.news.request.CreateNewsRequest

import java.time.ZonedDateTime
import java.util.UUID

final case class News(
    id: UUID,
    source_id: Option[String],
    source_name: String,
    author: Option[String],
    title: String,
    description: Option[String],
    url: String,
    urlToImage: Option[String],
    publishedAt: ZonedDateTime,
    content: Option[String],
) {
  def toResponse: NewsResponse =
    NewsResponse(
      id,
      source_id,
      source_name,
      author,
      title,
      description,
      url,
      urlToImage,
      publishedAt,
      content,
    )
}

object News {
  def fromCreateNews(id: UUID, createNews: CreateNewsRequest): News =
    News(
      id,
      createNews.source_id,
      createNews.source_name,
      createNews.author,
      createNews.title,
      createNews.description,
      createNews.url,
      createNews.urlToImage,
      createNews.publishedAt,
      createNews.content,
    )
}
