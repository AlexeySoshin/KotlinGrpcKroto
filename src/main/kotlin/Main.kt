
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking

import wix.api.*
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils

fun main() {

    val service = SomeServiceCoroutineGrpc

    println("KROTO")

    val localhost = ManagedChannelBuilder.forAddress("localhost", 18080 + 1)
        .usePlaintext()
        .build()

    // val interceptor = HeaderClientInterceptor()
    // val channel = ClientInterceptors.intercept(localhost, interceptor)
    var someService = SomeServiceGrpc.newStub(localhost)

    someService = someService.addBinaryHeader(
        header = "wix-request-context-bin",
        bytes = (RequestContext {}).toByteArray()
    )

    runBlocking {
        val response = someService.sayHello {
            hello = "kroto"
        }

        println(response)
    }
}

fun SomeServiceGrpc.SomeServiceStub.addBinaryHeader(
    header: String,
    bytes: ByteArray
): SomeServiceGrpc.SomeServiceStub {
    val headers = Metadata()
    val key = Metadata.Key.of(header, Metadata.BINARY_BYTE_MARSHALLER)
    headers.put(key, bytes)

    return MetadataUtils.attachHeaders(this, headers)
}
