package com.aiconverse.voiceteller.ui.profile.voice

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aiconverse.voiceteller.BusyDialogFragment
import com.aiconverse.voiceteller.R
import com.aiconverse.voiceteller.databinding.VoiceProfileFragmentBinding
import com.aiconverse.voiceteller.repository.profile.ProfileModel
import com.aiconverse.voiceteller.repository.voiceprofile.VoiceProfileModel
import com.aiconverse.voiceteller.repository.voiceprofile.VoiceProfileRequest
import com.aiconverse.voiceteller.util.OnlyAudioRecorder
import com.aiconverse.voiceteller.util.UtilManager
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class VoiceProfileFragment : Fragment() {

    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var profileModel: ProfileModel
    private lateinit var audioRecord: OnlyAudioRecorder
    private var count = 0
    private lateinit var txtCountDown: TextView

    private lateinit var panelVoiceProfile: LinearLayout
    private lateinit var panelVoiceRecording: LinearLayout

    private lateinit var txtEnrollmentStatus: TextView
    private lateinit var txtCreated: TextView
    private lateinit var txtLastUpdated: TextView
    private lateinit var txtSpeechLength: TextView

    private var pcmPath: String = ""
    private var wavPath: String = ""

    companion object {
        fun newInstance() = VoiceProfileFragment()
        private const val RECORD_AUDIO_PERMISSION_CODE = 700
    }

    private val viewModel: VoiceProfileViewModel by lazy {
        ViewModelProvider(this).get(VoiceProfileViewModel::class.java)
    }

    val timer = object : CountDownTimer(10000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000)
            count ++
            txtCountDown.text = count.toString()
        }

        override fun onFinish() {
            //mTextField.setText("done!")
            try {
                audioRecord.stopRecord()//Stop recording

                // invoke file upload web service
                profileModel.voiceProfileModel?.let {
                    enrollVoice(profileModel.id, it.profileId, pcmPath)
                }
            }
            catch(e: Exception ){
                Timber.e(e.stackTraceToString())
            }
        }
    }

    fun checkForPermissions(
        permissions: Array<String?>,
        permRequestCode: Int
    ): Boolean {
        val permissionsNeeded: MutableList<String?> = ArrayList()
        for (i in permissions.indices) {
            val perm = permissions[i]
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permissions[i]!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(permissions[i]!!)) {

                    var builder = AlertDialog.Builder(activity)
                    builder.setTitle("Permission Request.")
                    builder.setMessage("This permission is needed to record audio.")
                    builder.setPositiveButton("Accept") { dialog, _ ->
                        permissionsNeeded.add(perm)
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("Reject") { dialog, _ ->
                        dialog.cancel()
                    }

                    var alert = builder.create()
                    alert.show()

                } else {
                    // add the request.
                    permissionsNeeded.add(perm)
                }
            }
        }
        return if (permissionsNeeded.size > 0) {
            // go ahead and request permissions
            requestPermissions(permissionsNeeded.toTypedArray(), permRequestCode)
            false
        } else {
            // no permission need to be asked so all good...we have them all.
            true
        }
    }

    // This function is called when the user accepts or decline the permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {

                    setCreateProfileVisibility(true)

                    if(profileModel.voiceProfileModel != null){
                        populateVoiceProfile(profileModel?.voiceProfileModel!!)
                        setCreateRecordingVisibility(true)
                    }

                    Toast.makeText(requireContext(),
                        "Record audio Permission Granted", Toast.LENGTH_SHORT).show()
                }
            else
            {
                Toast.makeText(requireContext(),
                    "Record audio Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: VoiceProfileFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.voice_profile_fragment, container, false)

        txtCountDown = binding.txtCountDown
        txtEnrollmentStatus = binding.txtEnrollmentStatus
        txtCreated = binding.txtCreated
        txtLastUpdated = binding.txtLastUpdated
        txtSpeechLength = binding.txtSpeechLength

        panelVoiceProfile = binding.panelVoiceProfile
        panelVoiceRecording = binding.panelVoiceRecording

        // load profile
        profileModel = viewModel.getUserSharedPreferences(user?.email!!,requireContext())!!

        // set initial form status
        binding.cmdConfirm.isChecked = profileModel.voiceConfirmation
        binding.txtStatus.text =
            setVoiceProfileStatus(profileModel.voiceConfirmation)

        if(profileModel.voiceProfileModel != null){
            //disableButton(binding.cmdCreateProfile, binding.txtEnrollmentStatus.currentTextColor)
        }

        // set to false till we get permissions
        setCreateProfileVisibility(false)
        setCreateRecordingVisibility(false)
        binding.cmdConfirm.isEnabled = false

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // handle click listeners
        binding.cmdStartRecording.setOnClickListener{

            pcmPath = context?.getExternalFilesDir(null)?.path + "/vt/rawAudio.pcm"
            wavPath = context?.getExternalFilesDir(null)?.path + "/vt/finalAudio.wav"

            Timber.d("M: pcmPath: %s", pcmPath)
            Timber.d("M: wavPath: %s", wavPath)

            audioRecord = OnlyAudioRecorder.getInstance(pcmPath, wavPath)

            audioRecord.startRecord()

            timer.start()

        }

        binding.cmdCreateProfile.setOnClickListener{
            getOrCreateVoiceProfile("CREATE")
        }

        binding.cmdConfirm.setOnCheckedChangeListener { buttonView, isChecked ->

            updateProfile(isChecked)

            binding.txtStatus.text =
                setVoiceProfileStatus(isChecked)

            setCreateProfileVisibility(isChecked)
        }

        // Check that App has the right permissions!
        if (checkForPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), RECORD_AUDIO_PERMISSION_CODE
            )
        )
        {
            // IT does!!
            binding.cmdConfirm.isEnabled = true
            setCreateProfileVisibility(true)

            if(profileModel.voiceProfileModel != null){
                populateVoiceProfile(profileModel.voiceProfileModel!!)
                setCreateRecordingVisibility(true)
            }

        }

        return binding.root
    }

    private fun getOrCreateVoiceProfile(action: String){
        user?.getIdToken(false)?.addOnSuccessListener { rsp ->

            val id = user.email!!
            val jwt = rsp.token.toString()
            val locale = "en-us"

            val profileId = when(action){
                "CREATE" -> ""
                else -> profileModel.voiceProfileModel?.profileId
            }

            Timber.d("M: profileId %s", profileId)

            val voiceProfileRequest = VoiceProfileRequest(id, locale, profileId!!)

            val busyDialog = BusyDialogFragment.show(parentFragmentManager)

            if(action == "CREATE"){
                viewModel.createProfile(jwt, voiceProfileRequest)
            }
            else if(action == "GET"){
                viewModel.getProfile(jwt, voiceProfileRequest)
            }

            viewModel.apiVoiceProfileModel.observe(viewLifecycleOwner, Observer{ rsp ->
                if(rsp != null){
                    profileModel.voiceProfileModel = rsp
                    viewModel.updateVoiceProfile(id, requireContext(), rsp)
                    viewModel.setDiffStatus(id, "PUSH", requireContext())

                    populateVoiceProfile(rsp)
                    setCreateRecordingVisibility(true)

                    busyDialog.dismiss()

                    Toast.makeText(context, "Voice profile $action successfully.",
                        Toast.LENGTH_LONG).show()
                }
                else{
                    busyDialog.dismiss()
                    Toast.makeText(context,
                        "Error while $action voice profile.",
                        Toast.LENGTH_LONG)
                        .show()
                }
            })
        }
    }

    private fun enrollVoice(id: String, profileId: String, fileName: String){

        Timber.d("M: Enroll Using audio file: %s", fileName)
        Timber.d("M: Enroll id %s", id)
        Timber.d("M: Enroll profileId %s", profileId)

        user?.getIdToken(false)?.addOnSuccessListener { rsp ->

            val jwt = rsp.token.toString()

            Timber.d("M: Enroll jwt %s", jwt)

            val busyDialog = BusyDialogFragment.show(parentFragmentManager)

            viewModel.enrollVoice(jwt, id, profileId, fileName)

            viewModel.apiVoiceEnrollmentModel.observe(viewLifecycleOwner, Observer { rsp ->
                if (rsp != null) {

                    profileModel.voiceEnrollmentModel = rsp
                    viewModel.updateVoiceEnrollment(id, requireContext(), rsp)
                    viewModel.setDiffStatus(id, "PUSH", requireContext())

                    // get new profile updates
                    getOrCreateVoiceProfile("GET")

                    busyDialog.dismiss()

                    Toast.makeText(context, "Audio enrolled successfully.",
                        Toast.LENGTH_LONG).show()
                }
                else{
                    busyDialog.dismiss()
                    Toast.makeText(context,
                        "Error while enrolling audio.",
                        Toast.LENGTH_LONG)
                        .show()
                }
            })
        }

    }

    private fun disableButton(button: Button, textColor: Int) {
        button.isEnabled = false
        button.isClickable = false
        button.setTextColor(textColor)
        button.setBackgroundColor(Color.LTGRAY)
    }

    private fun updateProfile(isChecked: Boolean){
        profileModel.voiceConfirmation = isChecked

        if(isChecked){
            profileModel.fingerprintConfirmation = false
        }

        viewModel.setUserSharedPreferences(profileModel, requireContext())
        viewModel.setDiffStatus(profileModel.id, "PUSH", requireContext())

    }

    private fun setVoiceProfileStatus(isChecked:Boolean): String{
        return if(isChecked) "Voice confirmation enabled."
        else{
            "Voice confirmation disabled."
        }
    }

    private fun setCreateProfileVisibility(visible: Boolean){

        val visibility = if(visible) View.VISIBLE else View.INVISIBLE

        panelVoiceProfile.visibility = visibility


    }

    private fun setCreateRecordingVisibility(visible: Boolean){

        val visibility = if(visible) View.VISIBLE else View.INVISIBLE
        panelVoiceRecording.visibility = visibility

    }


    private fun populateVoiceProfile(voiceProfileModel: VoiceProfileModel){

        voiceProfileModel.apply {
            txtEnrollmentStatus.text = "Enrollment status: " +
                    this.enrollmentStatus
            txtCreated.text = "Created at: " +
                    UtilManager.formatDate(this.createdDateTime!!)

            if(this.lastUpdatedDateTime != null) {
                if ((this.lastUpdatedDateTime != "null") &&
                    (this.lastUpdatedDateTime != "")
                ) {
                    txtLastUpdated.text = "Last updated at: " +
                            UtilManager.formatDate(this.lastUpdatedDateTime!!)
                }
            }

            txtSpeechLength.text = "Speech duration: " +
                    this.enrollmentsSpeechLength + " seconds"
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}