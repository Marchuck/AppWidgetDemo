package pl.marczak.appwidgetdemo.pokesample

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import timber.log.Timber

class PokeClient constructor(gson: Gson) {

    private val api = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(API::class.java)

    suspend fun getPokemon(id: Int): PokemonResponse {
        return api.getPokemon(id).let {
            Timber.w("poke response $it")
            it
        }
    }
}

interface API {
    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokemonResponse
}

data class PokemonResponse(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val types: List<PokeType>
)

data class Sprites(val front_default: String)

data class PokeType(val slot: Int, val type: NamedResource)

data class NamedResource(val name: String)
