package com.obidia.todolistapp.presentation.detail

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.obidia.todolistapp.BuildConfig
import com.obidia.todolistapp.R
import com.obidia.todolistapp.databinding.FragmentNoteDetailBinding
import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.presentation.broadcastreceiver.AlarmReceiver
import com.obidia.todolistapp.presentation.datepicker.DatePickerFragment
import com.obidia.todolistapp.presentation.datepicker.TimePickerFragment
import com.obidia.todolistapp.presentation.list.NoteListFragmentDirections
import com.obidia.todolistapp.utils.replaceIfNull
import com.obidia.todolistapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class NoteDetailFragment : Fragment(), DatePickerFragment.DialogDateListener,
    TimePickerFragment.DialogTimeListener {

    private lateinit var binding: FragmentNoteDetailBinding
    private val viewModel: NoteDetailViewModel by viewModels()
    private val alarmReceiver: AlarmReceiver = AlarmReceiver()
    private var isUpdateNote: Boolean = false
    private var noteModel: NoteModel? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Notifications permission granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notifications permission rejected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteDetailBinding.inflate(layoutInflater, container, false)

        setupAnimation()
        setupPermission()
        setupToolBar()
        loadArguments()
        setupView()
        return binding.root
    }

    private fun setupAnimation() {
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        binding.container.transitionName = BuildConfig.APPLICATION_ID
    }

    private fun setupToolBar() {
        binding.headToolBar.let {
            context?.let { color -> it.root.setBackgroundColor(color.getColor(R.color.neutral_10)) }
            it.toolbarMenu.visible(false)
            it.toolbarMenuDelete.visible(false)
            it.imgToolbar.setOnClickListener {
                findNavController().navigateUp()
            }
            it.titleToolbar.visible(false)
        }
    }

    private fun setupPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun loadArguments() {
        isUpdateNote = arguments?.getBoolean(ARGS_IS_UPDATE_NOTE).replaceIfNull()
        noteModel = arguments?.getParcelable(ARGS_DATA_NOTE)
    }

    private fun setupView() {
        binding.run {
            etNoteTitle.setText(if (isUpdateNote) noteModel?.title.replaceIfNull() else "")
            tvDate.text = if (isUpdateNote) noteModel?.date.replaceIfNull() else ""
            tvTime.text = if (isUpdateNote) noteModel?.time.replaceIfNull() else ""
        }
        setupButton()
    }

    private fun setupButton() {
        binding.run {
            btnDate.setOnClickListener {
                showDatePicker()
            }

            floatBtn.let {
                it.setImageResource(if (isUpdateNote) R.drawable.ic_edit else R.drawable.ic_add)
                it.setOnClickListener {
                    onClickFloatBtn()
                }
            }

            floatBtnDelete.let {
                it.visible(isUpdateNote)
                it.setOnClickListener {
                    noteModel?.let { model -> viewModel.deleteNote(model) }
                    alarmReceiver.cancelAlarm(requireContext(), noteModel?.id.replaceIfNull())
                }
            }
        }
    }

    private fun onClickFloatBtn() {
        val noteTitle = binding.etNoteTitle.text.toString()
        var noteId: Long? = null
        val date = binding.tvDate.text.toString()
        val time = binding.tvTime.text.toString()
        val message = getString(R.string.detail_message_alarm_manager, noteTitle)

        if (noteTitle.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please complete your input!!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        lifecycleScope.launch {
            if (isUpdateNote) {
                noteModel?.apply {
                    this.date = date.replaceIfNull()
                    this.time = time.replaceIfNull()
                    this.title = noteTitle.replaceIfNull()
                }?.let { viewModel.updateNote(it) }
            } else {
                noteId = viewModel.addNote(
                    NoteModel(0, noteTitle, date, time, false)
                )
            }

            alarmReceiver.setOneTimeAlarm(
                requireContext(),
                date,
                time,
                message,
                if (isUpdateNote) noteModel?.id?.toLong().replaceIfNull()
                else noteId.replaceIfNull()
            )

            findNavController().popBackStack()
        }
    }

    private fun showDatePicker() {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(childFragmentManager, DATE_PICKER_TAG)
    }

    private fun showTimePicker() {
        val timePickerFragmentOne = TimePickerFragment()
        timePickerFragmentOne.show(childFragmentManager, TIME_PICKER_ONCE_TAG)
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        binding.tvDate.text = dateFormat.format(calendar.time)
        showTimePicker()
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.tvTime.text = dateFormat.format(calendar.time)
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
        private const val ARGS_DATA_NOTE = BuildConfig.APPLICATION_ID + "ARGS_DATA_NOTE"
        private const val ARGS_IS_UPDATE_NOTE = BuildConfig.APPLICATION_ID + "ARGS_IS_UPDATE_NOTE"

        fun newInstance(
            data: NoteModel? = null,
            isUpdateNote: Boolean = false
        ): Bundle {
            val dialog = NoteDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARGS_DATA_NOTE, data)
            bundle.putBoolean(ARGS_IS_UPDATE_NOTE, isUpdateNote)
            dialog.arguments = bundle
            return bundle
        }
    }
}