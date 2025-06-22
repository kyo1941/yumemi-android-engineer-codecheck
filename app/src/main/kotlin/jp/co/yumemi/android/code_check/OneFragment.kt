/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.code_check.data.repository.GitHubRepositoryImpl
import jp.co.yumemi.android.code_check.databinding.FragmentOneBinding
import kotlinx.coroutines.launch


class OneFragment : Fragment(R.layout.fragment_one) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentOneBinding.bind(view)

        val repository = GitHubRepositoryImpl()
        val viewModel = OneViewModel(repository)

        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        val adapter = CustomAdapter { item ->
            goToRepositoryFragment(item)
        }

        binding.searchInputText
            .setOnEditorActionListener { editText, action, _ ->
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    val inputText = editText.text.toString().trim()

                    if (inputText.isEmpty()) {
                        binding.searchInputLayout.error = getString(R.string.error_empty_search)
                        binding.searchInputLayout.isErrorEnabled = true
                        binding.searchInputText.requestFocus()

                        return@setOnEditorActionListener true
                    }

                    binding.searchInputLayout.isErrorEnabled = false

                    hideKeyboard(editText)

                    lifecycleScope.launch {
                        val items = viewModel.searchResults(inputText)
                        adapter.submitList(items)
                    }

                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

        binding.recyclerView.also {
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = adapter
        }
    }

    fun goToRepositoryFragment(item: Item) {
        val action = OneFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(item = item)
        findNavController().navigate(action)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}

class CustomAdapter(
    private val onItemClick: (Item) -> Unit,
) : ListAdapter<Item, CustomAdapter.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val repositoryNameView: TextView = view.findViewById(R.id.repositoryNameView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.repositoryNameView.text = item.name

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
}
