package com.sssoyalan.newsapp.models.users

data class UserModel (
    var userId : String ="",
    var userName : String="",
    var userEmail : String="",
    var userEmailCheck : String ="",
    var userPhotoUrl : String="",
    var online : String ="",
    var onlineCheck : String ="",
    var okunan : Okunan? = null,
    var lastReadArticle : lastReadArticle? = null
) {
    override fun toString(): String {
        return "$userId $userName $userEmail $userEmailCheck $userPhotoUrl $online $onlineCheck"
    }
}

data class Okunan(
    var okunanTotal : Int = 0,
    var okunanSport : Int = 0,
    var okunanBusiness : Int = 0,
    var okunanEntertainment : Int = 0,
    var okunanHealth : Int = 0,
    var okunanScience : Int = 0,
    var okunantechnology: Int = 0,
)

data class lastReadArticle(
    val author: String?="",
    val publishedAt: String?="",
    val title: String?="",
    val urlToImage: String?=""
)

