package com.fpliu.newton.http.converter

import com.fpliu.newton.http.ContentType
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File
import java.lang.reflect.Type

/**
 * Body注解的是File类型，可以使用被正确处理；返回的是File类型也可以被正确处理
 *
 * @author 792793182@qq.com 2017-06-28.
 */
class FileConverterFactory : Converter.Factory() {

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<File, RequestBody>? {
        return if (File::class.java == type) {
            var contentTypeStr = (parameterAnnotations?.filter { it is ContentType }?.takeIf { it.size > 0 }?.get(0) as? ContentType)?.value?.takeIf { it != null && "" != it }
                ?: "application/octet-stream"
            val mediaType = MediaType.parse(contentTypeStr)
            return Converter { RequestBody.create(mediaType, it) }
        } else null
    }
}
