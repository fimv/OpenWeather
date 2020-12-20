package com.example.openweather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WeatherRequest () {
    //Ключевое слово здесь - suspend. Это даст Котлину понять, что это suspend функция и она будет приостанавливать корутину.
    suspend fun makeRequest(httpState: String): String {
        //выполняем необходимую работу в фоновом потоке
        return withContext(Dispatchers.IO) {
            var buffer: BufferedReader? = null

            try {
                //создаем экземпляр класса URL, передаем в него наш адрес

                val url =
                    URL("https://api.openweathermap.org/data/2.5/weather?$httpState&appid=2203150c7ab2fc9af08e38d3dc52a2ff")
                //создаем экземпляр класса HttpsURLConnection
                val httpsURLConnection = url.openConnection() as HttpsURLConnection
                //указываем какой HTTP метод мы будем использовать
                httpsURLConnection.requestMethod = "GET"
                //указываем таймаут на чтение ответа от сервера
                httpsURLConnection.readTimeout = 10000
                //выполняем подключение к серверу
                httpsURLConnection.connect()

                //получаем входящий поток данных и помещаем его в наш буффер
                buffer = BufferedReader(InputStreamReader(httpsURLConnection.inputStream))

                val builder = StringBuilder()
                var line: String? = null

                while (true) {
                    //читаем строку из буффера, если читать больше нечего прерываем цикл
                    line = buffer.readLine() ?: break
                    //добавляем нашу строку в StringBuilder
                    builder.append(line).append("")
                }

                //возвращаем результат выполнения функции
                builder.toString()
            } catch (exc: Exception) {
                buffer?.close()
                exc.message.toString()
            } finally {
                //обязательно выгружаем буффер из памяти
                buffer?.close()
            }
        }
    }




    fun jsonElement(result: String): String {
        var text = ""
        var weatherstext = ""
        var st = ""
        if (result != null && result.isNotEmpty()) {
            try {
            val json = JSONObject(result)

            val coord = json.getJSONObject("coord")
            val lon = coord.optInt("lon", 0)
            val lat = coord.optInt("lat", 0)

            val main = json.getJSONObject("main")
            val temp = main.optInt("temp", 0)
            val feels_temp = main.optInt("feels_like", 0)
            val temp_min = main.optInt("temp_min", 0)
            val temp_max = main.optInt("temp_max", 0)

            val wind = json.getJSONObject("wind")
            val speed = wind.optInt("speed", 0)
            val deg = wind.optInt("deg", 0)

            var orient = ""
            if (deg in 11..80) {
                orient = "Северо-Восточный"
            } else if (deg in 81..100) {
                orient = "Восточный"
            } else if (deg in 101..170) {
                orient = "Юго-Восточный"
            } else if (deg in 171..190) {
                orient = "Южный"
            } else if (deg in 191..260) {
                orient = "Юго-Западный"
            } else if (deg in 261..290) {
                orient = "Западный"
            } else if (deg in 291..350) {
                orient = "Северо-Западный"
            } else if (deg in 351..360 && deg in 0..10) {
                orient = "Северный"
            } else {
                orient = ""
            }


            val dt = json.optInt("dt", 0)
            val name = json.optString("name", "-")
            text =
                    "Город: $name  Координаты: долгота $lon; широта $lat  Температура: ${temp - 273}; чувствуется как ${feels_temp - 273}  Min-Max: ${temp_min - 273}-${temp_max - 273}; Ветер: Направление,гр. $deg $orient Скорость $speed   "


            val weathers = json.getJSONArray("weather")
            for (w in 0 until weathers.length()) {
                val jsonWeatherObject = weathers.getJSONObject(w)
                val id = jsonWeatherObject.optInt("id", 0)
                val description = jsonWeatherObject.optString("description", "-")
                weatherstext += "Осадки/Облачность: $description  id: $id"
            }

            }catch (e: JSONException){
                e.printStackTrace()
            }

            st = text + weatherstext
            return st
        }

        else {
            st = "Check the rigth of yuor enter of city"
            return st
        }
        }




}