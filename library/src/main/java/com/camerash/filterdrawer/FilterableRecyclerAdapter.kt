package com.camerash.filterdrawer

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

abstract class FilterableRecyclerAdapter<Parent, Child, Data> :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), FilterDrawer.OnChildSelectListener<Parent, Child>, RecyclerAdapterFilter<Parent, Child, Data> where Parent : ParentItem, Child : ChildItem {

    abstract val dataList: List<Data>
    private var filteredDataList = listOf<Data>()
    private var filterDrawer: FilterDrawer<Parent, Child>? = null

    fun bindFilterDrawer(filterDrawer: FilterDrawer<Parent, Child>) {
        this.filterDrawer = filterDrawer
        filterDrawer.addChildSelectListener(this)
    }

    final override fun onChildSelect(parent: Parent, childItem: Child) = filter()

    final override fun onChildDeselect(parent: Parent, childItem: Child) = filter()

    final override fun onReset() = filter()

    private fun filter() {
        val fd = filterDrawer ?: return
        filterWithFilterMap(fd.getSelectedChildrens())
    }

    private fun filterWithFilterMap(filterMap: Map<Parent, Child>) {
        filteredDataList = dataList.filter { filter(it, filterMap) }
        DiffUtil.calculateDiff(FilterRecyclerViewDiffCallback(dataList, filteredDataList)).dispatchUpdatesTo(this)
    }

}