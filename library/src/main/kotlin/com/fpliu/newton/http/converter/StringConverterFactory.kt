package com.fpliu.newton.http.converter

import com.fpliu.newton.http.ContentType
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @author 792793182@qq.com 2017-06-28.
 * Body注解的是String类型，可以使用被正确处理；返回的是String类型也可以被正确处理
 */
class StringConverterFactory : Converter.Factory() {

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<String, RequestBody>? {
        return if (String::class.java == type) {
            var contentTypeStr = (parameterAnnotations?.filter { it is ContentType }?.takeIf { it.size > 0 }?.get(0) as? ContentType)?.value?.takeIf { it != null && "" != it }
                ?: "application/json;charset=UTF-8"
            val mediaType = MediaType.parse(contentTypeStr)
            return Converter { RequestBody.create(mediaType, it) }
        } else null
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, String>? {
        return if (String::class.java == type) {
            Converter { responseBody -> responseBody.string() }
        } else null
    }
}
