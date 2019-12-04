package com.genui.retrofit2_promise_adapter

import com.google.common.reflect.TypeToken
import com.shopify.promises.Promise
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class CancelTest {
    private val factory = PromiseCallAdapterFactory()
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://example.com")
        .callFactory { TODO() }
        .build()

    @Test
    fun noCancelOnResponse() {
        val deferredString = typeOf<Promise<String, Exception>>()
        val adapter = factory.get(
            deferredString,
            emptyArray(),
            retrofit
        )!! as CallAdapter<String, Promise<String, Exception>>
        val call = CompletableCall<String>()
        val promise = adapter.adapt(call)
        call.complete("hey")
        assertFalse(call.isCanceled)
        promise.whenComplete {
            when (it) {
                is Promise.Result.Success -> {
                    assertTrue(true)
                }
                is Promise.Result.Error -> {
                    assertTrue(false)
                }
            }
        }

    }

    @Test
    fun noCancelOnError() {
        val promiseString = typeOf<Promise<String, Exception>>()
        val adapter = factory.get(
            promiseString,
            emptyArray(),
            retrofit
        )!! as CallAdapter<String, Promise<String, Exception>>
        val call = CompletableCall<String>()
        val promise = adapter.adapt(call)
        call.completeWithException(IOException())
        assertFalse(call.isCanceled)
        promise.whenComplete {
            when (it) {
                is Promise.Result.Success -> {
                    assertTrue(false)
                }
                is Promise.Result.Error -> {
                    assertTrue(true)
                }
            }
        }
    }

    @Test
    fun cancelOnCancel() {
        val promiseString = typeOf<Promise<String, Exception>>()
        val adapter = factory.get(
            promiseString,
            emptyArray(),
            retrofit
        )!! as CallAdapter<String, Promise<String, Exception>>
        val call = CompletableCall<String>()
        val promise = adapter.adapt(call)
        assertFalse(call.isCanceled)
        promise.whenComplete { }
        promise.cancel()
        assertTrue(call.isCanceled)
    }

    private inline fun <reified T> typeOf(): Type = object : TypeToken<T>() {}.type
}