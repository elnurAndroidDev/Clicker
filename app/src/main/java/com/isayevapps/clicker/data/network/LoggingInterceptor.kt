import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        // Логирование информации о запросе
        Log.d("OkHttp", "--> ${request.method} ${request.url}")
        Log.d("OkHttp", "Request Headers: ${request.headers}") // Добавлено "Request" для ясности

        // Логирование тела запроса (если оно есть) - *без изменений, так как это RequestType*
        request.body?.let { requestBody ->
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val requestBodyString = buffer.readUtf8()
            Log.d("OkHttp", "Request Body: $requestBodyString") // Добавлено "Request" для ясности
        }

        // Отправляем запрос и получаем ответ
        val response: Response = chain.proceed(request)

        // Логирование информации об ответе
        Log.d("OkHttp", "<-- ${response.code} ${response.message} ${response.request.url}")
        Log.d("OkHttp", "Response Headers: ${response.headers}") // Добавлено "Response" для ясности


        // Получаем тело ответа как строку (если оно есть)
        val responseBodyString = response.body?.string() ?: ""

        // Пробуем десериализовать в объект Response (и выводим, если успешно)
        try {
            val gson = com.google.gson.Gson() // Создаем экземпляр Gson
            val responseBodyObject = gson.fromJson(responseBodyString, com.isayevapps.clicker.data.network.Response::class.java) // Замените на полный путь к вашему классу Response
            Log.d("OkHttp", "Response Body (parsed): $responseBodyObject")
        } catch (e: Exception) {
            // Если не удалось распарсить, просто выводим тело как строку
            Log.w("OkHttp", "Response Body (raw): $responseBodyString")
            Log.w("OkHttp", "Error parsing JSON: ${e.message}") // Выводим ошибку парсинга для отладки
        }

        // Важно: необходимо вернуть тело обратно в ответ
        val contentType = response.body?.contentType()
        return response.newBuilder()
            .body(responseBodyString.toResponseBody(contentType))
            .build()
    }
}