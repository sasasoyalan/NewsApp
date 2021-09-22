package com.sssoyalan.newsapp

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.sssoyalan.newsapp.models.Resource
import com.sssoyalan.newsapp.models.*
import com.sssoyalan.newsapp.models.today.Today
import com.sssoyalan.newsapp.models.users.UserModel
import com.sssoyalan.newsapp.models.weather.WeatherResponse
import com.sssoyalan.newsapp.source.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainViewModel(private val dataRepository : DataRepository) : ViewModel() {

    val aliveData = MutableLiveData<Resource<ModelNews>>()
    val aliveDataBorsa = MutableLiveData<List<modelInside>>()
    val aliveDataWeather = MutableLiveData<Resource<WeatherResponse>>()

    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var _users : MutableLiveData<ArrayList<UserModel>> = MutableLiveData<ArrayList<UserModel>>()
    var _messages : MutableLiveData<ArrayList<MessageModel>> = MutableLiveData<ArrayList<MessageModel>>()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        listenToUsers()
        listenToMessages()
    }

    fun saveUser(userModel: UserModel, contex: Context) {
        firestore.collection("users")
            .document(userModel.userId)
            .set(userModel)
            .addOnSuccessListener {
            }
    }

    fun saveMessage(messageModel: MessageModel) {
        firestore.collection("messages")
            .document()
            .set(messageModel)
            .addOnSuccessListener {
            }
    }

    private fun listenToUsers() {
        firestore.collection("users").addSnapshotListener{
            snapshot, e ->
            if (e!=null){
                return@addSnapshotListener
            }
            if (snapshot!=null) {
                val allUsers = ArrayList<UserModel>()

                val documents = snapshot.documents
                documents.forEach{
                    val user =it.toObject(UserModel::class.java)
                    user?.let {
                        allUsers.add(user)
                    }
                }
                _users.value = allUsers
            }
        }
    }

    private fun listenToMessages() {
        firestore.collection("messages").orderBy("messageTime", Query.Direction.ASCENDING).addSnapshotListener{
                snapshot, e ->
            if (e!=null){
                return@addSnapshotListener
            }
            if (snapshot!=null) {
                val allMessages = ArrayList<MessageModel>()

                val documents = snapshot.documents
                documents.forEach{
                    val message =it.toObject(MessageModel::class.java)
                    message?.let {
                        allMessages.add(message)
                    }
                }
                _messages.value = allMessages
            }
        }
    }

    fun getNewsCategory(category: String){
        getNews(category)
    }

    fun getNews(category: String) = viewModelScope.launch {
        aliveData.postValue(Resource.Loading())
        val  response = dataRepository.getNews(category)
        aliveData.postValue(handleNewsResponse(response))
    }

    fun getWeatherData(lat : String, lon : String){
        getWeather(lat, lon)
    }

    fun getWeather(lat : String, lon : String) = viewModelScope.launch {
        aliveDataWeather.postValue(Resource.Loading())
        val  response = dataRepository.getWeather(lat,lon)
        aliveDataWeather.postValue(handleweatherResponse(response))
    }

    private fun handleNewsResponse(response: Response<ModelNews>) : Resource<ModelNews> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleweatherResponse(response: Response<WeatherResponse>) : Resource<WeatherResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun fetchBorsa() {
        viewModelScope.launch {
            val borsa : BorsaModel = withContext(Dispatchers.IO){
                dataRepository.getBorsa()
            }

            val list: MutableList<modelInside> = ArrayList()
            var model : modelInside = borsa.EUR
            list.add(model)
            model =borsa.USD
            list.add(model)
            model =borsa.BTC
            list.add(model)
            model =borsa.GBP
            list.add(model)
            model =borsa.BCH
            list.add(model)
            model =borsa.GA
            list.add(model)
            model =borsa.GAG
            list.add(model)
            model =borsa.ETH
            list.add(model)
            model =borsa.LTC
            list.add(model)
            model =borsa.XRP
            list.add(model)
            model =borsa.XU100
            list.add(model)
            aliveDataBorsa.value=list
        }
    }

    fun saveArticle(article: Article) =viewModelScope.launch {
        dataRepository.upsert(article)
    }

    fun getSavedNews() =dataRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        dataRepository.deleteArticle(article)
    }

    fun getAll() = dataRepository.getAll()
}