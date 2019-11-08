package audio.rabid.vinylscrobbler

import com.squareup.moshi.*
import java.lang.reflect.Type

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Wrapped(val name: String)

object WrappedAdapterFactory : JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        val wrappedAnnotation = (type as? Class<*>)?.annotations
            ?.filterIsInstance<Wrapped>()?.firstOrNull() ?: return null
//        val delegateAnnotations = Types.nextAnnotations(annotations, Wrapped::class.java) ?: return null
        val delegate = moshi.nextAdapter<Any>(this, type, annotations)
        return Adapter(delegate, wrappedAnnotation.name)
    }

    private class Adapter<T>(val delegate: JsonAdapter<T>, val name: String) : JsonAdapter<T>() {
        override fun toJson(writer: JsonWriter, value: T?) {
            writer.beginObject()
            writer.name(name)
            delegate.toJson(writer, value)
            writer.endObject()
        }

        override fun fromJson(reader: JsonReader): T? {
            reader.beginObject()
            val nextName = reader.nextName()
            if (name != nextName) throw JsonDataException("Expected key $name but found $nextName")
            val item = delegate.fromJson(reader)
            reader.endObject()
            return item
        }
    }
}
