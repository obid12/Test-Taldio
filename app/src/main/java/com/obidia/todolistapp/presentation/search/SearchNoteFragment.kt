package com.obidia.todolistapp.presentation.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.obidia.todolistapp.BuildConfig
import com.obidia.todolistapp.R
import com.obidia.todolistapp.databinding.FragmentSearchNoteBinding
import com.obidia.todolistapp.databinding.ItemNoteBinding
import com.obidia.todolistapp.domain.model.NoteModel
import com.obidia.todolistapp.presentation.broadcastreceiver.AlarmReceiver
import com.obidia.todolistapp.presentation.detail.NoteDetailFragment
import com.obidia.todolistapp.presentation.list.adapter.NoteListAdapter
import com.obidia.todolistapp.utils.ShimmerAdapter
import com.obidia.todolistapp.utils.error
import com.obidia.todolistapp.utils.loading
import com.obidia.todolistapp.utils.success
import com.obidia.todolistapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNoteFragment : Fragment() {
    private lateinit var binding: FragmentSearchNoteBinding
    private val viewModel: SearchNoteViewModel by viewModels()
    private var adapter: NoteListAdapter = NoteListAdapter()
    private var lisDeleteNote: MutableList<NoteModel> = mutableListOf()
    private val alarmReceiver: AlarmReceiver = AlarmReceiver()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchNoteBinding.inflate(layoutInflater, container, false)
        setupToolBar()
        setupSearch()
        setupRecycleView()
        setupShimmerAdapter()
        return binding.root
    }

    private fun setupShimmerAdapter() {
        val adapter = ShimmerAdapter(R.layout.shimmer_item_note, 10)
        binding.rvShimmer.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter
        }
    }

    private fun setupToolBar() {
        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSearch() {
        binding.run {
            tilSearch.editText?.textChangedListener(iconDelete)
            iconDelete.setOnClickListener {
                onIconDeleteClickListener(tilSearch)
            }
            etSearch.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    v.run {
                        clearFocus()
                        hideKeyboard()
                    }
                    observer(tilSearch.editText?.text.toString())
                    binding.listSearchAnimator.visible(true)
                }
                false
            }
        }
    }

    private fun observer(char: String) {
        lifecycleScope.launch {
            viewModel.getSearchNote(char)
                .flowWithLifecycle(lifecycle)
                .catch { }
                .collect { state ->
                    state.loading {
                        showContent(SHOW_SIMMER)
                    }
                    state.success {
                        if (it.isEmpty()) {
                            showContent(SHOW_EMPTY)
                            return@success
                        }

                        showContent(SHOW_CONTENT)
                        setupAdapter(it)
                    }
                    state.error { }
                }
        }
    }

    private fun setupAdapter(listNote: ArrayList<NoteModel>) {
        adapter.let {
            it.submitList(listNote)
            it.setOnClickItem { data, binding ->
                goToDetailNote(data, binding)
            }
            it.setOnCheckBoxListener { item, position ->
                item?.let { data -> viewModel.updateNote(data) }
                it.updateItem(position)
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
        binding.floatBtn.run {
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

    private fun goToDetailNote(
        data: NoteModel? = null,
        binding: ItemNoteBinding? = null,
    ) {
        val extras = binding?.container?.let {
            it to BuildConfig.APPLICATION_ID
        }?.let {
            FragmentNavigatorExtras(it)
        }

        binding?.container?.transitionName = BuildConfig.APPLICATION_ID

        val bundle = NoteDetailFragment.newInstance(
            data,
            true
        )

        findNavController().navigate(
            resId = R.id.action_searchNoteFragment_to_noteDetailFragment,
            args = bundle,
            navOptions = null,
            extras
        )
    }

    private fun setupRecycleView() {
        binding.let {
            it.rvSearch.run {
                adapter = this@SearchNoteFragment.adapter
                itemAnimator = null
                layoutManager = object : LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }
        }
    }

    private fun View.hideKeyboard() {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun EditText.textChangedListener(
        iconDelete: ImageView,
    ) {
        addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    iconDelete.isVisible = s?.isNotEmpty() == true
                }

            }
        )
    }

    private fun onIconDeleteClickListener(textInputLayout: TextInputLayout) {
        binding.listSearchAnimator.visible(false)
        textInputLayout.run {
            editText?.setText("")
            requestFocus()
        }
    }

    private fun showContent(index: Int) {
        binding.listSearchAnimator.displayedChild = index
    }

    companion object {
        const val SHOW_SIMMER = 0
        const val SHOW_CONTENT = 1
        const val SHOW_EMPTY = 2
    }
}