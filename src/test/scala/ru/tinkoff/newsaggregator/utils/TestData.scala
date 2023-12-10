package ru.tinkoff.newsaggregator.utils

import ru.tinkoff.newsaggregator.domain.news.request.CreateNewsRequest
import ru.tinkoff.newsaggregator.domain.news.response.NewsResponse

import java.time.ZonedDateTime
import java.util.UUID

object TestData {

  val newsId: UUID = UUID.fromString("93e9c52a-6a88-11ee-8c99-0242ac120002")

  val newsId2: UUID = UUID.fromString("3e89a792-95d2-11ee-b9d1-0242ac120002")

  val creationRequest: CreateNewsRequest = CreateNewsRequest(
    source_id = Some("business-insider"),
    source_name = "business-insider",
    author = Some("prosen@insider.com (Phil Rosen)"),
    title =
      "We have no intention of selling': El Salvador's millennial president touts the country's bitcoin investment as the token soars",
    description = Some(
      "Nayib Bukele said if El Salvador sold all its bitcoin at current prices, it would recover 100% of its investment and see a profit more than $3,600,000.",
    ),
    url =
      "https://markets.businessinsider.com/news/currencies/bitcoin-price-el-salvador-bukele-millennial-president-profits-crypto-btc-2023-12",
    urlToImage = Some("https://i.insider.com/656de75b58e7c0c29a29222b?width=1200&format=jpeg"),
    publishedAt = ZonedDateTime.parse("2023-12-04T15:52:51Z"),
    content = Some(
      "Bitcoin breached $42,000 on Monday for the first time in 20 months, and El Salvador's millennial president Nayib " +
        "Bukele took to X to tout his country's investment in the crypto amid the big gains. \\r\\n… [+1237 chars]",
    ),
  )

  val testGetByIdExample: NewsResponse = NewsResponse(
    newsId,
    Some("business-insider"),
    "business-insider",
    Some("Dan DeFrancesco"),
    "Bitcoin is back (sort of)",
    Some(
      "A recent surge in bitcoin's price is the latest win for what has quietly been a strong year for crypto.",
    ),
    "https://www.businessinsider.com/news-today-november-10-bitcoin-etf-market-ether-crypto-2023-11",
    Some("https://i.insider.com/654d5b6f3cc84b4dfaffcb5e?width=1200&format=jpeg"),
    ZonedDateTime.parse("2023-11-10T13:43:28Z"),
    Some(
      "Malte Mueller/Getty Images\r\n<ul>\n<li>This post originally appeared in the Insider Today newsletter.</li>\n<li>You can sign up for Insider's daily newsletter here.</li>\n</ul>Happy Friday! A bit of insp… [+7494 chars]",
    ),
  )

  val testDeleteByIdExample: NewsResponse = NewsResponse(
    newsId2,
    Some("engadget"),
    "Engadget",
    Some("Mat Smith"),
    "The Morning After: Google’s Gemini is the company’s answer to ChatGPT",
    Some(
      "Google officially introduced its most capable large language model to date, Gemini. CEO Sundar Pichai said it’s the first of “a new generation of AI models, inspired by the way people understand and interact with the world.” Of course, it’s all very complex, …",
    ),
    "https://www.engadget.com/the-morning-after-googles-gemini-is-the-companys-answer-to-chatgpt-121531424.html",
    Some(
      "https://s.yimg.com/os/creatr-uploaded-images/2023-12/0a0832d0-94f2-11ee-befb-57b42f3ce0d7",
    ),
    ZonedDateTime.parse("2023-12-07T12:15:31Z"),
    Some(
      "Google officially introduced its most capable large language model to date, Gemini. CEO Sundar Pichai said its the first of a new generation of AI models, inspired by the way people understand and in… [+4342 chars]",
    ),
  )

  val testNotGetByKeyWordExample: NewsResponse = NewsResponse(
    newsId2,
    Some("engadget"),
    "Engadget",
    Some("Mat Smith"),
    "The Morning After: Google’s Gemini is the company’s answer to ChatGPT",
    Some(
      "Google officially introduced its most capable large language model to date, Gemini. CEO Sundar Pichai said it’s the first of “a new generation of AI models, inspired by the way people understand and interact with the world.” Of course, it’s all very complex, …",
    ),
    "https://www.engadget.com/the-morning-after-googles-gemini-is-the-companys-answer-to-chatgpt-121531424.html",
    Some(
      "https://s.yimg.com/os/creatr-uploaded-images/2023-12/0a0832d0-94f2-11ee-befb-57b42f3ce0d7",
    ),
    ZonedDateTime.parse("2023-12-07T12:15:31Z"),
    Some(
      "Google officially introduced its most capable large language model to date, Gemini. CEO Sundar Pichai said its the first of a new generation of AI models, inspired by the way people understand and in… [+4342 chars]",
    ),
  )

  val testAllExample: List[NewsResponse] = List(testGetByIdExample, testDeleteByIdExample)

  val testGetByKeyWordExample: NewsResponse = NewsResponse(
    newsId,
    Some("business-insider"),
    "business-insider",
    Some("Dan DeFrancesco"),
    "Bitcoin is back (sort of)",
    Some(
      "A recent surge in bitcoin's price is the latest win for what has quietly been a strong year for crypto.",
    ),
    "https://www.businessinsider.com/news-today-november-10-bitcoin-etf-market-ether-crypto-2023-11",
    Some("https://i.insider.com/654d5b6f3cc84b4dfaffcb5e?width=1200&format=jpeg"),
    ZonedDateTime.parse("2023-11-10T13:43:28Z"),
    Some(
      "Malte Mueller/Getty Images\r\n<ul>\n<li>This post originally appeared in the Insider Today newsletter.</li>\n<li>You can sign up for Insider's daily newsletter here.</li>\n</ul>Happy Friday! A bit of insp… [+7494 chars]",
    ),
  )

  val testByDateRangeExample: NewsResponse = NewsResponse(
    newsId,
    Some("business-insider"),
    "business-insider",
    Some("Dan DeFrancesco"),
    "Bitcoin is back (sort of)",
    Some(
      "A recent surge in bitcoin's price is the latest win for what has quietly been a strong year for crypto.",
    ),
    "https://www.businessinsider.com/news-today-november-10-bitcoin-etf-market-ether-crypto-2023-11",
    Some("https://i.insider.com/654d5b6f3cc84b4dfaffcb5e?width=1200&format=jpeg"),
    ZonedDateTime.parse("2023-11-10T13:43:28Z"),
    Some(
      "Malte Mueller/Getty Images\r\n<ul>\n<li>This post originally appeared in the Insider Today newsletter.</li>\n<li>You can sign up for Insider's daily newsletter here.</li>\n</ul>Happy Friday! A bit of insp… [+7494 chars]",
    ),
  )

  val newsResponseString: String =
    s"""
       |{
       | "status": "ok",
       | "totalResults": "${0}",
       | "articles": []
       |}
       |""".stripMargin

}
