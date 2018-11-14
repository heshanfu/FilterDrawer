package com.camerash.filterdrawer

import android.support.annotation.ColorRes
import android.support.annotation.MenuRes
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.TextView

class FilterDrawer<Parent, Child>(private val builder: DrawerBuilder<Parent, Child>, val drawerLayout: DrawerLayout, val filterView: View) where Parent: ParentItem, Child: ChildItem {

    val toolbar: Toolbar
    val toolbarTitle: TextView
    val recyclerView: RecyclerView
    val resetBtn: Button
    val applyBtn: Button
    val adapter = ParentAdapter(builder.itemList, builder.childSelectListenerList)

    init {
        toolbar = filterView.findViewById(R.id.toolbar)
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title)
        recyclerView = filterView.findViewById(R.id.filter_recycler_view)
        resetBtn = filterView.findViewById(R.id.filter_reset_btn)
        applyBtn = filterView.findViewById(R.id.filter_apply_btn)
        constructFilter()
    }

    private fun constructFilter() {
        applyToolbarOptions()
        applyButtonsOptions()
        setupListeners()
        setupRecyclerView()
    }

    private fun applyToolbarOptions() {
        showToolbar(builder.displayToolbar)
        setToolbarTitle(builder.toolbarTitle)
        inflateToolbarMenu(builder.toolbarMenuResId)
    }

    private fun applyButtonsOptions() {
        resetBtn.text = builder.resetText
        resetBtn.setTextColor(builder.resetColor)
        applyBtn.text = builder.applyText
        applyBtn.setTextColor(builder.applyColor)
    }

    private fun setupListeners() {
        builder.drawerListener?.let { addDrawerListener(it) }
        builder.filterControlClickListener?.let { setFilterControlClickListener(it) }
    }

    private fun setupRecyclerView() {
        val llm = LinearLayoutManager(builder.activity)
        val did = DividerItemDecoration(builder.activity, llm.orientation)
        recyclerView.layoutManager = llm
        recyclerView.addItemDecoration(did)
        recyclerView.adapter = adapter
    }

    fun showToolbar(show: Boolean) {
        toolbar.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setToolbarTitle(title: String) {
        toolbarTitle.text = title
    }

    fun inflateToolbarMenu(@MenuRes menuRes: Int) {
        if (menuRes == 0) return
        toolbar.inflateMenu(menuRes)
    }

    fun updateItems(itemList: Collection<Parent>) {
        adapter.updateItems(ArrayList(itemList))
    }

    fun addToolbarMenuListener(listener: Toolbar.OnMenuItemClickListener) {
        toolbar.setOnMenuItemClickListener(listener)
    }

    fun addDrawerListener(drawerListener: DrawerLayout.DrawerListener) {
        drawerLayout.addDrawerListener(drawerListener)
    }

    fun setFilterControlClickListener(filterControlClickListener: OnFilterControlClickListener) {
        resetBtn.setOnClickListener { filterControlClickListener.onResetClick() }
        applyBtn.setOnClickListener { filterControlClickListener.onApplyClick() }
    }

    fun addChildSelectListener(childSelectListener: OnChildSelectListener<Parent, Child>) {
        adapter.childSelectListenerList.add(childSelectListener)
    }

    fun removeChildSelectListener(childSelectListener: OnChildSelectListener<Parent, Child>) {
        adapter.childSelectListenerList.remove(childSelectListener)
    }

    fun setResetText(text: String) {
        builder.resetText = text
        resetBtn.text = builder.resetText
    }

    fun setResetColor(@ColorRes color: Int) {
        val act = builder.activity ?: return
        builder.resetColor = ContextCompat.getColor(act, color)
        resetBtn.setTextColor(builder.resetColor)
    }

    fun setApplyText(text: String) {
        builder.applyText = text
        applyBtn.text = builder.applyText
    }

    fun setApplyColor(@ColorRes color: Int) {
        val act = builder.activity ?: return
        builder.applyColor = ContextCompat.getColor(act, color)
        applyBtn.setTextColor(builder.applyColor)
    }

    fun openFilterDrawer() {
        drawerLayout.openDrawer(filterView)
    }

    fun closeFilterDrawer() {
        drawerLayout.closeDrawer(filterView)
    }

    fun resetFilter() {
        adapter.reset()
    }

    fun getSelectedChildrens(): Map<Parent, Set<Child>> {
        return adapter.getSelectedChildren()
    }

    interface OnChildSelectListener <Parent, Child> where Parent: ParentItem, Child: ChildItem {
        fun onChildSelect(parent: Parent, child: Set<Child>)
        fun onChildDeselect(parent: Parent, child: Set<Child>)
        fun onReset()
    }

    interface OnFilterControlClickListener {
        fun onResetClick()
        fun onApplyClick()
    }
}