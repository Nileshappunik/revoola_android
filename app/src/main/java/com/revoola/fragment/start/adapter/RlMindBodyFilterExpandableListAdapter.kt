package com.revoola.fragment.start.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.revoola.R
import com.revoola.fragment.start.mind.RLFragMindClasses
import com.revoola.model.RLMindBodyFilterGroupItemModel
import com.revoola.model.RLMindBodyFilterGroupSelectItemModel


class RlMindBodyFilterExpandableListAdapter(private val context: Context,
                                            private val groupList: List<RLMindBodyFilterGroupItemModel>,
                                            private val onSelected: (MutableList<RLMindBodyFilterGroupItemModel>) -> Unit ) : BaseExpandableListAdapter() {

    private val selectedLeftItems: MutableSet<Pair<Int, Int>> = mutableSetOf() // For left TextView
    private val selectedRightItems: MutableSet<Pair<Int, Int>> = mutableSetOf() // For right TextView

    private val selectedItemsMap: MutableList<RLMindBodyFilterGroupSelectItemModel> = mutableListOf()

    override fun getGroupCount(): Int = groupList.size

    override fun getChildrenCount(groupPosition: Int): Int {
        val childItems = groupList[groupPosition].childItems
        val isEven = childItems.size % 2 == 0
        if (isEven){
           return  childItems.size/2

        }else{
            return (childItems.size/ 2)+1

        }
    }

    override fun getGroup(groupPosition: Int): Any = groupList[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any = groupList[groupPosition].childItems[childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupTitle = (getGroup(groupPosition) as RLMindBodyFilterGroupItemModel).title
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.rl_common_class_filter_card, parent, false)
        val txt_card_title = view.findViewById<TextView>(R.id.txt_card_title)
        txt_card_title.text = groupTitle
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.rl_mind_body_chield_item, parent, false)
        val groupNamekey = groupList[groupPosition]
        val childData = groupNamekey.childItems ?: listOf<String>().distinct()

        val tvChildLeft = view.findViewById<TextView>(R.id.txt_card_title_left)
        val tvChildRight = view.findViewById<TextView>(R.id.txt_card_title_right)

        val index = childPosition * 2
        tvChildLeft.text = childData.getOrNull(index) ?: ""
        tvChildRight.text = childData.getOrNull(index + 1) ?: ""

        if (childData.getOrNull(index + 1).isNullOrEmpty()){
            tvChildRight.visibility=View.GONE
        }else{
            tvChildRight.visibility=View.VISIBLE
        }

        // Update background and text color for left TextView
        if (selectedLeftItems.contains(Pair(groupPosition, childPosition))) {
            tvChildLeft.setBackgroundResource(R.drawable.mind_body_filter_border_green)
            tvChildLeft.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
        } else {
            tvChildLeft.setBackgroundResource(R.drawable.mind_body_filter_border_gray)
            tvChildLeft.setTextColor(ContextCompat.getColor(context, R.color.AppTextGrayColor))
        }

        // Update background and text color for right TextView
        if (selectedRightItems.contains(Pair(groupPosition, childPosition))) {
            tvChildRight.setBackgroundResource(R.drawable.mind_body_filter_border_green)
            tvChildRight.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
        } else {
            tvChildRight.setBackgroundResource(R.drawable.mind_body_filter_border_gray)
            tvChildRight.setTextColor(ContextCompat.getColor(context, R.color.AppTextGrayColor))
        }

        // Handle click for left TextView
        tvChildLeft.setOnClickListener {
            val item = Pair(groupPosition, childPosition)
            if (selectedLeftItems.contains(item)) {
                selectedLeftItems.remove(item) // Deselect if already selected
                val selecteditem=RLMindBodyFilterGroupSelectItemModel(groupNamekey.title.toString(), tvChildLeft.text.toString())
                selectedItemsMap.remove(selecteditem)
            } else {
                selectedLeftItems.add(item) // Select this item
                val selecteditem=RLMindBodyFilterGroupSelectItemModel(groupNamekey.title.toString(), tvChildLeft.text.toString())
                selectedItemsMap.add(selecteditem)
            }
            notifyDataSetChanged() // Refresh the list
            onSelected(RLGetSelectedItems().toMutableList())
        }

        // Handle click for right TextView
        tvChildRight.setOnClickListener {
            val item = Pair(groupPosition, childPosition)
            if (selectedRightItems.contains(item)) {
                selectedRightItems.remove(item) // Deselect if already selected
                val selecteditem=RLMindBodyFilterGroupSelectItemModel(groupNamekey.title.toString(), tvChildRight.text.toString())
                selectedItemsMap.remove(selecteditem)
            } else {
                selectedRightItems.add(item) // Select this item
                val selecteditem=RLMindBodyFilterGroupSelectItemModel(groupNamekey.title.toString(), tvChildRight.text.toString())
                selectedItemsMap.add(selecteditem)
            }
            notifyDataSetChanged() // Refresh the list
            onSelected(RLGetSelectedItems().toMutableList())
        }
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    fun RLGetSelectedItems(): List<RLMindBodyFilterGroupItemModel> {
        val selectedItems = mutableListOf<RLMindBodyFilterGroupItemModel>()
        val groupedItems = selectedItemsMap
            .groupBy { it.title } // Group by title
            .map { (title, items) ->
                // For each group, create an RLMindBodyFilterGroupItemModel
                RLMindBodyFilterGroupItemModel(title, items.map { it.childItems }.distinct())
            }
        // Add the grouped items to the selectedItems list
        selectedItems.addAll(groupedItems)
        return selectedItems
    }

}
