# Weather-app
Android app displaying current weather, and next 7 days weather, also details about wind, uvi, humidity etc.
It is implemented 100% in kotlin.
Weather information is coming from https://openweathermap.org via REST API.
This app is a practical example for android development with kotlin, using the latest technologies promoted by google development team
It is implemented following MVVM architecture, using a shared viewmodel to share information between fragments.
Livedata is used to update the UI controls,
Retrofit to use REST API with data.
I use 1 activity and 2 fragments, first fragment is showing current weather details, the second fragment showing next 7 days weather.
I use device location to call weather service.
I allow user to set unit of temperature, Celsius or Farenheit and update the screens accordingly
