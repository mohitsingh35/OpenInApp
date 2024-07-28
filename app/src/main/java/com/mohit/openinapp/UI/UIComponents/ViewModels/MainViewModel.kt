package com.mohit.openinapp.UI.UIComponents.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mohit.openinapp.API.ApiService
import com.mohit.openinapp.Models.LinksData
import com.mohit.openinapp.HelperClasses.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    private val _linksData = MutableLiveData<Result<LinksData>>()
    val linksData: LiveData<Result<LinksData>> = _linksData

    private val _data = MutableLiveData<LinksData>()
    val data: LiveData<LinksData> = _data

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting

    private val _selectedTab = MutableLiveData<TabState>(TabState.TopLinks)
    val selectedTab: LiveData<TabState> = _selectedTab

    private val _viewAllLinksState = MutableLiveData<ViewAllLinksState>(ViewAllLinksState.Collapsed)
    val viewAllLinksState: LiveData<ViewAllLinksState> = _viewAllLinksState

    init {
        getGreetingMessage()
    }

    private var selectedIndex = 0

    fun setSelectedIndex(index: Int) {
        selectedIndex = index
    }

    fun getSelectedIndex(): Int {
        return selectedIndex
    }

    suspend fun getLinksData() {
        _linksData.postValue(Result.loading())
        try {
            val response = apiService.getLinksData()
            if (response.isSuccessful) {
                val data = response.body()
                _linksData.postValue(Result.success(data))
            } else {
                val error = response.errorBody()?.string()
                _linksData.postValue(Result.error(error ?: "Error occurred"))
            }
        } catch (e: HttpException) {
            _linksData.postValue(Result.error(e.localizedMessage ?: "Network error"))
        } catch (e: IOException) {
            _linksData.postValue(Result.error("Connection error"))
        } catch (e: Exception) {
            _linksData.postValue(Result.error(e.localizedMessage ?: "Unexpected error"))
        }
    }

    fun setData(linksData: LinksData) {
        _data.value=linksData
    }

    fun getGreetingMessage() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        when (hourOfDay) {
            in 0..11 -> _greeting.postValue("Good morning")
            in 12..16 ->_greeting.postValue("Good afternoon")

            else -> _greeting.postValue("Good evening")
        }
    }

    fun setTabSelected(tabState: TabState) {
        _selectedTab.value = tabState
        _viewAllLinksState.value= ViewAllLinksState.Collapsed
    }

    fun toggleViewAllLinks() {
        when (_viewAllLinksState.value) {
            ViewAllLinksState.Collapsed -> _viewAllLinksState.value= ViewAllLinksState.Expanded
            ViewAllLinksState.Expanded -> _viewAllLinksState.value= ViewAllLinksState.Collapsed
            else -> null
        }
    }

}

sealed class TabState {
    object TopLinks : TabState()
    object RecentLinks : TabState()
}

sealed class ViewAllLinksState {
    object Collapsed : ViewAllLinksState()
    object Expanded : ViewAllLinksState()
}
