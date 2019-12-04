package com.genui.retrofit2_promise_adapter

import com.shopify.promises.Promise
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class PromiseCallAdapterFactory private constructor() : CallAdapter.Factory() {
    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke() = PromiseCallAdapterFactory()
    }

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Promise::class.java) {
            return null
        }

        if (returnType !is ParameterizedType) {
            throw IllegalStateException("CompletableFuture return type must be parameterized" + " as CompletableFuture<Foo> or CompletableFuture<? extends Foo>")
        }
        val innerType = getParameterUpperBound(0, returnType as ParameterizedType)

        if (getRawType(innerType) != Response::class.java) {
            // Generic type is not Response<T>. Use it for body-only adapter.
            return BodyCallAdapter<Any>(innerType)
        }

        // Generic type is Response<T>. Extract T and create the Response version of the adapter.
        if (innerType !is ParameterizedType) {
            throw IllegalStateException("Response must be parameterized" + " as Response<Foo> or Response<? extends Foo>")
        }
        val responseType = getParameterUpperBound(0, innerType)
        return ResponseCallAdapter<Any>(responseType)
    }


    private class BodyCallAdapter<R> internal constructor(private val responseType: Type) :
        CallAdapter<R, Promise<R, Exception>> {

        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<R>): Promise<R, Exception> {

            return Promise {
                onCancel {
                    call.cancel()
                }

                call.enqueue(object : Callback<R> {
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        if (response.isSuccessful) {
                            resolve(response.body()!!)
                        } else {
                            reject(HttpException(response))
                        }
                    }

                    override fun onFailure(call: Call<R>, t: Throwable) {
                        reject(java.lang.Exception(t))
                    }
                })
            }
        }
    }

    private class ResponseCallAdapter<R> internal constructor(private val responseType: Type) :
        CallAdapter<R, Promise<Response<R>, Exception>> {

        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<R>): Promise<Response<R>, Exception> {
            return Promise {
                onCancel {
                    call.cancel()
                }

                call.enqueue(object : Callback<R> {
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        if (response.isSuccessful) {
                            resolve(response)
                        } else {
                            reject(HttpException(response))
                        }
                    }

                    override fun onFailure(call: Call<R>, t: Throwable) {
                        reject(java.lang.Exception(t))
                    }
                })
            }
        }
    }
}