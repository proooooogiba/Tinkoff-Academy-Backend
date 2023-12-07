package ru.tinkoff.newsaggregator.domain.news.response

import ru.tinkoff.newsaggregator.domain.news.NewsSource

import java.time.ZonedDateTime

object NewsAPIResponseExample {
  val okAPIExample = NewsAPIResponse(
    "ok",
    10401,
    List(
      ArticleAPIResponse(
        NewsSource(
          Some("business-insider"),
          "business-insider",
        ),
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
      ),
      ArticleAPIResponse(
        NewsSource(
          None,
          "ReadWrite",
        ),
        Some("Kliment Dukovski"),
        "How to Buy Bitcoin Minetrix in 2023 – Complete Guide",
        Some(
          "Bitcoin Minetrix is a crypto project aiming to bring Bitcoin mining to the masses using an innovative feature called stake-to-mine. […]\\nThe post How to Buy Bitcoin Minetrix in 2023 – Complete Guide appeared first on ReadWrite.",
        ),
        "https://readwrite.com/cryptocurrency/how-to-buy-bitcoin-minetrix/",
        Some(" \"https://readwrite.com/wp-content/uploads/2023/11/Bitcoin-Minetrix.jpg"),
        ZonedDateTime.parse("2023-11-27T11:55:20Z"),
        Some(
          "Bitcoin Minetrix is a crypto project aiming to bring Bitcoin mining to the masses using an innovative feature called stake-to-mine. You no longer need expensive Bitcoin mining hardware and electricit… [+12517 chars]",
        ),
      ),
    ),
  )
}
