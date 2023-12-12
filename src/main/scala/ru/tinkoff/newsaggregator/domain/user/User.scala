package ru.tinkoff.newsaggregator.domain.user

import sun.security.util.Password


case class User(id: Long, name: String, password: Password)
