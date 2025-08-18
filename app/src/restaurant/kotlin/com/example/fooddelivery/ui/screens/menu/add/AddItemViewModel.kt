package com.example.fooddelivery.ui.screens.menu.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddelivery.data.FoodApi
import com.example.fooddelivery.data.FoodHubAuthSession
import com.example.fooddelivery.data.modle.FoodItem
import com.example.fooddelivery.data.modle.GenericMsgResponse
import com.example.fooddelivery.data.modle.ImageUploadResponse
import com.example.fooddelivery.data.remote.ApiResponses
import com.example.fooddelivery.data.remote.SafeApiCalls
import com.example.fooddelivery.utils.UiState
import com.example.fooddelivery.utils.handleException
import com.example.fooddelivery.utils.toEmpty
import com.example.fooddelivery.utils.toError
import com.example.fooddelivery.utils.toSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import kotlin.String

@HiltViewModel
class AddItemViewModel @Inject constructor(val foodApi: FoodApi,val session: FoodHubAuthSession,@ApplicationContext val context: Context):
    ViewModel() {

    data class AddItemState(
        val message: GenericMsgResponse?=null,
        val url: ImageUploadResponse?=null
    )
    private val _uiState= MutableStateFlow<UiState<AddItemState>>(UiState.Empty)
    val uiState=_uiState.asStateFlow()

    private val _navigationEvent=MutableSharedFlow<NavigationEvent>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    private val _name=MutableStateFlow("")
    val name=_name.asStateFlow()

    private val _desc=MutableStateFlow("")
    val desc=_desc.asStateFlow()

    private val _imageUrl=MutableStateFlow<Uri?>(null)
    val imageUrl=_imageUrl.asStateFlow()

    private val _price=MutableStateFlow("")
    val price=_price.asStateFlow()

    fun onNameChange(name:String){
        _name.value=name
    }
    fun onPriceChange(price:String){
        _price.value=price
    }
    fun onDescChange(desc:String){
        _desc.value=desc
    }
    fun onImageUrlChange(imageUrl: Uri){
        _imageUrl.value=imageUrl
    }

    fun onTryAgainPressed(){
        _uiState.toEmpty()
    }

    fun addMenuItem(){
        val name=name.value
        val desc=desc.value
        val price=price.value.toDoubleOrNull()?:0.0
        val imageUrl=imageUrl.value
        val restaurantId=session.getRestaurantId()

        viewModelScope.launch {
            val imageUrl=uploadImage(imageUrl!!)
            if(imageUrl==null){
                _uiState.toError("Failed To Upload Image")
                return@launch
            }
            SafeApiCalls { foodApi.addRestaurantMenu(
                restaurantId!!,
                FoodItem(
                    description=desc,
                    imageUrl=imageUrl,
                    name= name,
                    price=price,
                    restaurantId=restaurantId
                )
            ) }.let {
                when(it){
                    is ApiResponses.Error<*> ->{
                        _uiState.toError(it.msg)
                    }
                    is ApiResponses.Exception<*> -> {
                        handleException(it.exception,_uiState)
                    }
                    is ApiResponses.Success<*> -> {
                        _uiState.toSuccess(AddItemState().copy(message = it.data as GenericMsgResponse))
                    }
                }
            }
        }
    }

    suspend fun uploadImage(imageUri:Uri):String?{
        val file=fileFromUri(imageUri)
        val requestBody=file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartBody= MultipartBody.Part.createFormData("image",file.name,requestBody)
        val response= SafeApiCalls { foodApi.uploadImage(multipartBody) }
        when(response){
            is ApiResponses.Success<*> -> {
                val res=AddItemState().copy(url = response.data as ImageUploadResponse)
                return res.url!!.url
            }
            else ->{
                return null
            }
        }
    }

    private fun fileFromUri(imageUri:Uri): File {
        val inputStream =context.contentResolver.openInputStream(imageUri)
        val file= File.createTempFile(
            "temp-${System.currentTimeMillis()}-fooddelivery",
            "jpg",
            context.cacheDir
        )
        inputStream.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        return file
    }

    fun restUi(){
        _name.value=""
        _price.value=""
        _desc.value=""
        _imageUrl.value=null
        _uiState.toEmpty()
    }

    sealed class NavigationEvent(){
        object NavigateBack:NavigationEvent()
    }

}