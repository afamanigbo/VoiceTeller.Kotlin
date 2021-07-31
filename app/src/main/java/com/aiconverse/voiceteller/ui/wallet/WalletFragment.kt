package com.aiconverse.voiceteller.ui.wallet

import RecyclerViewAdapter
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiconverse.voiceteller.BusyDialogFragment
import com.aiconverse.voiceteller.R
import com.aiconverse.voiceteller.databinding.WalletFragmentBinding
import com.aiconverse.voiceteller.repository.wallet.CardModel
import com.aiconverse.voiceteller.repository.wallet.WalletRequest
import com.aiconverse.voiceteller.util.UtilManager
import com.google.firebase.auth.FirebaseAuth
import com.interswitchng.iswmobilesdk.IswMobileSdk
import com.interswitchng.iswmobilesdk.shared.models.core.IswPaymentInfo
import com.interswitchng.iswmobilesdk.shared.models.core.IswPaymentResult

class WalletFragment : Fragment() , IswMobileSdk.IswPaymentCallback{

    private lateinit var adapter: RecyclerViewAdapter

    private val user = FirebaseAuth.getInstance().currentUser
    var data: ArrayList<CardModel> = ArrayList()

    companion object {
        fun newInstance() = WalletFragment()
    }

    private val viewModel: WalletViewModel by lazy {
        ViewModelProvider(this).get(WalletViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: WalletFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.wallet_fragment, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        binding.cmdAddCard.setOnClickListener{
            var builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirm card addition.")
            builder.setMessage("To register a card on the system, a minimal fee of 10 naira will be charged to your account. \n\nThis will be reversed within 24 hours. \n\nWould you like to continue?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                // interswitch add card SDK
                startISWCardSDK()
                dialog.cancel()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

            var alert = builder.create()
            alert.show()
        }

        adapter = RecyclerViewAdapter(
            R.layout.wallet_card_item,
            onBind = { view, item, position ->

                val imageCard = view.findViewById<ImageView>(R.id.imageCard)
                imageCard.setImageResource(UtilManager.cardToImage(data[position].cardType))

                val txtCardDetails = view.findViewById<TextView>(R.id.txtCardDetails)
                txtCardDetails.text = data[position].cardType + " **** " + data[position].panLast4Digits
            },
            onViewClicked = { view, item, position ->

                val bundle = Bundle()

                bundle.putString("txnref", data[position].txnref)
                bundle.putString("id", user?.email!!)

                val editCardFragment = EditCardFragment()
                editCardFragment.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.frameLayout_Container, editCardFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            })

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        val cardsModel = viewModel.getCardsFromWallet(user?.email!!, requireContext())

        cardsModel?.let {
            data.addAll(it)
            adapter.addWalletCardItems(it)
        }

        return binding.root
    }

    private fun startISWCardSDK() {

        val txnref = "VT_ISW_ACCESS_CODE_" + UtilManager.getUniqueReference()
        val cardAmount = UtilManager.minorDenomination(10)

        // create payment info
        user.let {
            val iswPaymentInfo = IswPaymentInfo(
                customerId = it?.email!!,
                customerName = it?.displayName!!,
                customerEmail = it?.email!!,
                customerMobile = "",
                reference = txnref,
                amount = cardAmount
            )

            iswPaymentInfo.tokenizeCard(true)

            IswMobileSdk.instance.pay(iswPaymentInfo, this)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onPaymentCompleted(result: IswPaymentResult) {

        if(result.isSuccessful){

            // get JWT
            user?.getIdToken(false)?.addOnSuccessListener { rsp ->

                val id = user.email!!
                val jwt = rsp.token.toString()
                val txnref = result.transactionReference

                val walletRequest = WalletRequest(id, txnref)

                val busyDialog = BusyDialogFragment.show(parentFragmentManager)

                viewModel.createCard(jwt, walletRequest)

                viewModel.apiCardModel.observe(this, Observer{ rsp ->
                    if(rsp != null){

                        // add to profile model
                        viewModel.addCardToWallet(id, requireContext(), rsp)

                        data.add(rsp)
                        adapter.addItem(rsp)

                        busyDialog.dismiss()

                        Toast.makeText(context, "Card added successfully.", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(requireContext(), "Error while adding card.", Toast.LENGTH_LONG)
                            .show()
                    }
                })

            }


        }
        else{
            Toast.makeText(context, "Card addition failed.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onUserCancel() {
        Toast.makeText(context, "Card addition cancelled.", Toast.LENGTH_LONG).show()
    }

}