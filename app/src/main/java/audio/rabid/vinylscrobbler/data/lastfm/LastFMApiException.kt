//package audio.rabid.vinylscrobbler.lastfm
//
//import org.json.JSONException
//import org.json.JSONObject
//import retrofit2.HttpException
//
//class LastFMApiException
//    private constructor(val statusCode: Int, val code: Int, message: String, cause: HttpException)
//    : Exception(message, cause) {
////    @JsonClass(generateAdapter = true)
////    data class Response(
////        @Json(name = "error") val code: Int,
////        @Json(name = "message") val message: String
////    )
//
//    companion object {
//
//        fun Exception.convertToApiExceptionIfPossible(): Exception {
//            val responseBody = (this as? HttpException)?.response()?.errorBody() ?: return this
//            return try {
//                val json = JSONObject(responseBody.string())
//                LastFMApiException(code(), json.getInt("error"), json.getString("message"), this)
//            } catch (e: JSONException) {
//                this
//            }
//        }
//
//        inline fun <T> wrap(block: () -> T): T {
//            try {
//                return block.invoke()
//            } catch (e: Exception) {
//                throw e.convertToApiExceptionIfPossible()
//            }
//        }
//    }
//}
