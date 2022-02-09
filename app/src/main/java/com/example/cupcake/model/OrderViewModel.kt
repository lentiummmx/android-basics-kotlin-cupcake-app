package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.lang.NumberFormatException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private const val PRICE_PER_CUPCAKE = 2.00
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

class OrderViewModel: ViewModel() {

    private val _orderQuantity = MutableLiveData<Int>()
    val orderQuantity: LiveData<Int>
        get() = _orderQuantity

    private val _cupcakeFlavor = MutableLiveData<String>()
    val cupcakeFlavor: LiveData<String>
        get() = _cupcakeFlavor

    private val _pickupDate = MutableLiveData<String>()
    val pickupDate: LiveData<String>
        get() = _pickupDate

    private val _price = MutableLiveData<Double>()
    //val price: LiveData<Double>
    //    get() = _price
    val price: LiveData<String> = Transformations.map(_price) {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).format(it)
    }

    val dateOptions = getPickupOptions()

    fun setOrderQuantity(numberCupcakes: Int) {
        _orderQuantity.value = numberCupcakes
        updatePrice()
    }

    fun setCupcakeFlavor(desiredFlavor: String) {
        _cupcakeFlavor.value = desiredFlavor
    }

    fun setPickupDate(pickupDate: String) {
        _pickupDate.value = pickupDate
        updatePrice()
    }

    fun hasNoFlavorSet(): Boolean {
        return _cupcakeFlavor.value.isNullOrEmpty()
    }

    fun getPickupOptions(): List<String> {
        val options =  mutableListOf<String>()
        val sDateFormat = SimpleDateFormat("E MMM d", Locale.getDefault())
        val cal = Calendar.getInstance()
        repeat(4) {
            options.add(sDateFormat.format(cal.time))
            cal.add(Calendar.DATE, 1)
        }
        return options
    }

    private fun updatePrice() {
        var calculatedPrice = (_orderQuantity.value ?: 0) * PRICE_PER_CUPCAKE

        // If the user selected the first option (today) for pickup, add the surcharge
        if (dateOptions[0] == _pickupDate.value) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatedPrice
    }

    fun resetOrder() {
        _orderQuantity.value = 0
        _cupcakeFlavor.value = ""
        _pickupDate.value = dateOptions[0]
        _price.value = 0.0
    }

    init {
        resetOrder()
    }

}