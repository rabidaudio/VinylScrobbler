package audio.rabid.vinylscrobbler.core.adapters

import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.nio.charset.Charset

object JSONObjectConverterFactory : Converter.Factory() {

    private val UTF8 = Charset.forName("UTF-8")

    private object JSONObjectConverter : Converter<ResponseBody, JSONObject> {
        override fun convert(value: ResponseBody): JSONObject? {
            return JSONObject(value.bytes().toString(UTF8))
        }
    }

    private object JSONArrayConverter : Converter<ResponseBody, JSONArray> {
        override fun convert(value: ResponseBody): JSONArray? {
            return JSONArray(value.bytes().toString(UTF8))
        }
    }

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return when (type) {
            JSONObject::class.java -> JSONObjectConverter
            JSONArray::class.java -> JSONArrayConverter
            else -> null
        }
    }
}
