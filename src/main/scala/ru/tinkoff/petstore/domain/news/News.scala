package ru.tinkoff.petstore.domain.news

import ru.tinkoff.petstore.api.news.model.response.NewsResponse

final case class News(
    status: String,
    totalResults: Int,
    articles: List[Article],
) {
  def toResponse: NewsResponse =
    NewsResponse(
      status = status,
      totalResults = totalResults,
      articles = articles,
    )
}
