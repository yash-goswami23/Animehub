package com.example.animehub.ui_layer


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animehub.Model.Anime
import com.example.animehub.data.AnimeRepository
import com.example.animehub.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeViewModel @Inject constructor(
    private val repository: AnimeRepository
): ViewModel() {


    private val _animeList = MutableStateFlow<Resource<List<Anime>?>>(Resource.Failure("Search"))
    val animeList: StateFlow<Resource<List<Anime>?>> = _animeList


    fun fetchAnimeList(query:String){
        viewModelScope.launch {
            _animeList.value = Resource.Loading
            try {
                val response = repository.getAnime(query,true)
                if (response == null) {
                    _animeList.value = Resource.Failure("Anime Not Found")
                }else{
                    _animeList.value = Resource.Success(response.data)
                }
            }catch (e:Exception){
                _animeList.value = Resource.Failure(e.localizedMessage)
            }
        }
    }

}