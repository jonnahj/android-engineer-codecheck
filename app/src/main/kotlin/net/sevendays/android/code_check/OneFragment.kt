/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package net.sevendays.android.code_check

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.sevendays.android.code_check.databinding.FragmentOneBinding


class OneFragment: Fragment(R.layout.fragment_one){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        StrictMode.setVmPolicy(
            VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build()
        )

        val _binding= FragmentOneBinding.bind(view)

        val _viewModel= OneViewModel()

        val _layoutManager= LinearLayoutManager(requireActivity())
        val _dividerItemDecoration= DividerItemDecoration(requireActivity(), _layoutManager.orientation)
        val _adapter= CustomAdapter(object : CustomAdapter.OnItemClickListener {
            override fun itemClick(item: item){
                gotoRepositoryFragment(item)
            }
        })

        _binding.searchInputText
            .setOnEditorActionListener{ editText, action, _ ->
                if (action== EditorInfo.IME_ACTION_SEARCH){
                    editText.text.toString().let {
                        _viewModel.searchResults(it).apply{
                            _adapter.submitList(this)
                        }
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

        _binding.searchInputText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                println("The input is: " + s)
                s.toString().let {
                    _viewModel.searchResults(it).apply {
                        _adapter.submitList(this)
                    }
                }
            }
        })

        _binding.recyclerView.also{
            it.layoutManager= _layoutManager
            it.addItemDecoration(_dividerItemDecoration)
            it.adapter= _adapter
        }
    }

    fun gotoRepositoryFragment(item: item)
    {
        val _action= OneFragmentDirections.actionRepositoriesFragmentToRepositoryFragment(item= item)
        findNavController().navigate(_action)
    }
}

val diff_util= object: DiffUtil.ItemCallback<item>(){
    override fun areItemsTheSame(oldItem: item, newItem: item): Boolean
    {
        return oldItem.name== newItem.name
    }

    override fun areContentsTheSame(oldItem: item, newItem: item): Boolean
    {
        return oldItem== newItem
    }

}

class CustomAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<item, CustomAdapter.ViewHolder>(diff_util){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnItemClickListener{
    	fun itemClick(item: item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
    	val _view= LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
    	return ViewHolder(_view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
    	val _item= getItem(position)
        (holder.itemView.findViewById<View>(R.id.repositoryNameView) as TextView).text=
            _item.name

    	holder.itemView.setOnClickListener{
     		itemClickListener.itemClick(_item)
    	}
    }
}
