package ru.tinkoff.newsaggregator.controller.news.examples

import ru.tinkoff.newsaggregator.domain.news.request.CreateNewsRequest
import ru.tinkoff.newsaggregator.domain.news.response.NewsResponse

import java.time.ZonedDateTime
import java.util.UUID

object CreateNewsRequestExample {
  val creationRequestExample = CreateNewsRequest(
    source_id = Some("google-news"),
    source_name = "Google News",
    author = Some("finanzen.net"),
    title =
      "Lufthansa-Aktie im Sinkflug: JPMorgan reduziert Lufthansa-Kursziel drastisch - Partnerschaft mit Lufttaxi-Startup Lilium? - finanzen.net",
    description = None,
    url =
      "https://news.google.com/rss/articles/CBMiwwFodHRwczovL3d3dy5maW5hbnplbi5uZXQvbmFjaHJpY2h0L2FrdGllbi9zY2h3YWNoZXItYnJhbmNoZW5hdXNibGljay1sdWZ0aGFuc2EtYWt0aWUtaW0tc2lua2ZsdWctanBtb3JnYW4tcmVkdXppZXJ0LWx1ZnRoYW5zYS1rdXJzemllbC1kcmFzdGlzY2gtcGFydG5lcnNjaGFmdC1taXQtbHVmdHRheGktc3RhcnR1cC1saWxpdW0tMTMwOTg2NzTSAbYBaHR0cHM6Ly93d3cuZmluYW56ZW4ubmV0L2FtcC9zY2h3YWNoZXItYnJhbmNoZW5hdXNibGljay1sdWZ0aGFuc2EtYWt0aWUtaW0tc2lua2ZsdWctanBtb3JnYW4tcmVkdXppZXJ0LWx1ZnRoYW5zYS1rdXJzemllbC1kcmFzdGlzY2gtcGFydG5lcnNjaGFmdC1taXQtbHVmdHRheGktc3RhcnR1cC1saWxpdW0tMTMwOTg2NzQ?oc=5",
    urlToImage = None,
    publishedAt = ZonedDateTime.parse("2023-12-07T16:10:00Z"),
    content = None,
  )

  val newsResponseExample = NewsResponse(
    id = UUID.randomUUID(),
    source_id = Some("google-news"),
    source_name = "Google News",
    author = Some("finanzen.net"),
    title =
      "Lufthansa-Aktie im Sinkflug: JPMorgan reduziert Lufthansa-Kursziel drastisch - Partnerschaft mit Lufttaxi-Startup Lilium? - finanzen.net",
    description = None,
    url =
      "https://news.google.com/rss/articles/CBMiwwFodHRwczovL3d3dy5maW5hbnplbi5uZXQvbmFjaHJpY2h0L2FrdGllbi9zY2h3YWNoZXItYnJhbmNoZW5hdXNibGljay1sdWZ0aGFuc2EtYWt0aWUtaW0tc2lua2ZsdWctanBtb3JnYW4tcmVkdXppZXJ0LWx1ZnRoYW5zYS1rdXJzemllbC1kcmFzdGlzY2gtcGFydG5lcnNjaGFmdC1taXQtbHVmdHRheGktc3RhcnR1cC1saWxpdW0tMTMwOTg2NzTSAbYBaHR0cHM6Ly93d3cuZmluYW56ZW4ubmV0L2FtcC9zY2h3YWNoZXItYnJhbmNoZW5hdXNibGljay1sdWZ0aGFuc2EtYWt0aWUtaW0tc2lua2ZsdWctanBtb3JnYW4tcmVkdXppZXJ0LWx1ZnRoYW5zYS1rdXJzemllbC1kcmFzdGlzY2gtcGFydG5lcnNjaGFmdC1taXQtbHVmdHRheGktc3RhcnR1cC1saWxpdW0tMTMwOTg2NzQ?oc=5",
    urlToImage = None,
    publishedAt = ZonedDateTime.parse("2023-12-07T16:10:00Z"),
    content = None,
  )

}
