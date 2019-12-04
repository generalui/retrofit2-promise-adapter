package com.genui.retrofit2_promise_adapter

import com.google.common.reflect.TypeToken
import com.google.common.truth.Truth.assertThat
import com.shopify.promises.Promise
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit

class PromiseCallAdapterFactoryTest {
    private val NO_ANNOTATIONS = emptyArray<Annotation>()

    @get:Rule
    val server = MockWebServer()

    private val factory = PromiseCallAdapterFactory()
    private var retrofit: Retrofit? = null

    @Before
    fun setUp() {
        retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(StringConverterFactory())
            .addCallAdapterFactory(factory)
            .build()
    }

    @Test
    fun responseType() {
        val bodyClass = object : TypeToken<Promise<String, Exception>>() {

        }.type
        assertThat(factory.get(bodyClass, NO_ANNOTATIONS, retrofit!!)!!.responseType())
            .isEqualTo(String::class.java)
        val bodyWildcard = object : TypeToken<Promise<out String, Exception>>() {

        }.type
        assertThat(factory.get(bodyWildcard, NO_ANNOTATIONS, retrofit!!)!!.responseType())
            .isEqualTo(String::class.java)
        val bodyGeneric = object : TypeToken<Promise<List<String>, Exception>>() {

        }.type
        assertThat(factory.get(bodyGeneric, NO_ANNOTATIONS, retrofit!!)!!.responseType())
            .isEqualTo(object : TypeToken<List<String>>() {

            }.type)
    }

    @Test
    fun nonListenableFutureReturnsNull() {
        val adapter = factory.get(String::class.java, NO_ANNOTATIONS, retrofit!!)
        assertThat(adapter).isNull()
    }
}
