package com.aiconverse.voiceteller.ui.wallet

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.repository.profile.ProfileRepository
import com.aiconverse.voiceteller.repository.wallet.CardModel
import com.aiconverse.voiceteller.repository.wallet.WalletModel
import com.aiconverse.voiceteller.repository.wallet.WalletRepository
import com.aiconverse.voiceteller.repository.wallet.WalletRequest
import kotlinx.coroutines.launch
import timber.log.Timber

class WalletViewModel : ViewModel() {

    private val _apiCardModel = MutableLiveData<CardModel?>()
    val apiCardModel: LiveData<CardModel?>
        get() = _apiCardModel

    fun createCard(jwt: String, walletRequest: WalletRequest){
        viewModelScope.launch {
            try{
                _apiCardModel.value = WalletRepository.createCard(jwt, walletRequest)
            }
            catch (e: Exception){
                Timber.d("M: Error creating card ${e.toString()}")
                _apiCardModel.value = null
            }
        }
    }

    fun addCardToWallet(id: String, applicationContext: Context, cardModel: CardModel){
        ProfileRepository.addCardToWallet(id, applicationContext, cardModel)
    }

    fun getCardsFromWallet(id: String, applicationContext: Context): MutableList<CardModel>? {
        return ProfileRepository.getCardsFromWallet(id, applicationContext)
    }
}