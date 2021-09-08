package com.github.zjjfly.db.message

case class ParseArticle(uri: String)
case class ParseHtmlArticle(uri: String, rawContent: String)
