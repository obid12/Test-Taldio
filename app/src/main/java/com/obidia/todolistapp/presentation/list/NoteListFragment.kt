package com.obidia.todolistapp.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.obidia.todolistapp.BuildConfig
import com.obidia.todolistapp.R
import com.obidia.todolistapp.databinding.FragmentNoteListBinding
import com.obidia.todolistapp.databinding.ItemNoteBinding
import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.presentation.broadcastreceiver.AlarmReceiver
import com.obidia.todolistapp.presentation.detail.NoteDetailFragment
import com.obidia.todolistapp.presentation.list.adapter.NoteListAdapter
import com.obidia.todolistapp.presentation.list.adapter.NoteListFinishAdapter
import com.obidia.todolistapp.utils.error
import com.obidia.todolistapp.utils.loading
import com.obidia.todolistapp.utils.replaceIfNull
import com.obidia.todolistapp.utils.success
import com.obidia.todolistapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteListFragment : Fragment() {
    private lateinit var binding: FragmentNoteListBinding

    private val noteListAdapter: NoteListAdapter = NoteListAdapter()
    private val noteListFinishAdapter: NoteListFinishAdapter = NoteListFinishAdapter()
    private val viewModel: NoteListViewModel by viewModels()
    private var lisDeleteNote: MutableList<NoteModel> = mutableListOf()
    private val alarmReceiver: AlarmReceiver = AlarmReceiver()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteListBinding.inflate(layoutInflater, container, false)
        setupObserver()
        setupRecycleView()
        setFloatButton()
        setSwipeNoteItem()
        setupToolBar()

        return binding.root
    }

    private fun setupToolBar() {
        binding.headToolBar.let {
            it.imgToolbar.visible(false)
            it.toolbarMenuDelete.visible(false)
            it.toolbarMenu.run {
                setImageResource(R.drawable.ic_search)
                setOnClickListener {
                    findNavController().navigate(NoteListFragmentDirections.actionNoteListFragmentToSearchNoteFragment())
                }
            }
            it.titleToolbar.text = getString(R.string.list_text_toolbar)
        }
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            viewModel.getAllNote(false).flowWithLifecycle(lifecycle).catch { }.collect { state ->
                state.loading { }
                state.success {
                    setupAdapter(it)
                }
                state.error { }
            }
        }

        lifecycleScope.launch {
            viewModel.getAllNote(true).flowWithLifecycle(lifecycle).catch { }.collect { state ->
                state.loading { }
                state.success {
                    setupAdapterFinish(it)
                    binding.tvCompleteTitle.run {
                        text = getString(R.string.list_title_complete, it.size.toString())
                        visible(it.isNotEmpty())
                    }
                }
                state.error { }
            }
        }
    }

    private fun setupRecycleView() {
        binding.let {
            it.rvListNote.run {
                adapter = noteListAdapter
                itemAnimator = null
                layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }

            it.rvListNoteFinish.run {
                adapter = noteListFinishAdapter
                itemAnimator = null
                layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }
        }
    }

    private fun setupAdapter(listNote: ArrayList<NoteModel>) {
        noteListAdapter.let {
            it.submitList(listNote)
            it.setOnClickItem { data, binding ->
                goToDetailNote(data, binding, true)
            }
            it.setOnCheckBoxListener { item, _ ->
                item?.let { data -> viewModel.updateNote(data) }
                alarmReceiver.cancelAlarm(requireContext(), item?.id.replaceIfNull())
            }
            it.setOnLongClickIte { data, isChecked ->
                if (!isChecked) {
                    lisDeleteNote.remove(data)
                    setIvDelete()
                    return@setOnLongClickIte
                }

                lisDeleteNote.add(data)
                setIvDelete()
            }
        }
    }

    private fun setIvDelete() {
        binding.headToolBar.toolbarMenuDelete.run {
            visible(lisDeleteNote.isNotEmpty())
            setOnClickListener {
                val listDeleteRoom: ArrayList<NoteModel> = arrayListOf()
                listDeleteRoom.addAll(lisDeleteNote)
                viewModel.deleteListNote(listDeleteRoom)
                listDeleteRoom.forEach {
                    alarmReceiver.cancelAlarm(requireContext(), it.id)
                }
                lisDeleteNote.clear()
                this.visible(false)
            }
        }
    }

    private fun setupAdapterFinish(listNote: ArrayList<NoteModel>) {
        noteListFinishAdapter.let {
            it.submitList(listNote)
            it.setOnClickItem { data, binding ->
                goToDetailNote(data, binding, true)
            }
            it.setOnCheckBoxListener { item, _ ->
                item?.let { data -> viewModel.updateNote(data) }
            }
            it.setOnLongClickIte { data, isChecked ->
                if (!isChecked) {
                    lisDeleteNote.remove(data)
                    setIvDelete()
                    return@setOnLongClickIte
                }

                lisDeleteNote.add(data)
                setIvDelete()
            }
        }
    }

    private fun goToDetailNote(
        data: NoteModel? = null,
        binding: ItemNoteBinding? = null,
        isUpdate: Boolean = false
    ) {
        val extras = binding?.container?.let {
            it to BuildConfig.APPLICATION_ID
        }?.let {
            FragmentNavigatorExtras(it)
        }

        binding?.container?.transitionName = BuildConfig.APPLICATION_ID

        val bundle = NoteDetailFragment.newInstance(
            data,
            isUpdate
        )

        findNavController().navigate(
            resId = R.id.action_noteListFragment_to_noteDetailFragment,
            args = bundle,
            navOptions = null,
            extras
        )
    }

    private fun setFloatButton() {
        binding.floatBtn.setOnClickListener {
            goToDetailNote()
        }
    }

    private fun setSwipeNoteItem() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = noteListAdapter.currentList[position]
                viewModel.deleteNote(note)
                alarmReceiver.cancelAlarm(requireContext(), note.id)
                Snackbar.make(view!!, "Successfully deleted Note", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.addNote(note)
                        val message = getString(R.string.detail_message_alarm_manager, note.title)
                        alarmReceiver.setOneTimeAlarm(
                            requireContext(),
                            note.date.replaceIfNull(),
                            note.time.replaceIfNull(),
                            message,
                            note.id.replaceIfNull().toLong()
                        )
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvListNote)
        }
    }
}