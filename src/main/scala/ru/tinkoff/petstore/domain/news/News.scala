package ru.tinkoff.petstore.domain.news

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
