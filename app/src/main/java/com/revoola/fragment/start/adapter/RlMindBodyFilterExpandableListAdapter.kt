package com.revoola.fragment.start.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.revoola.R
import com.revoola.fragment.start.mind.RLFragMindClasses
import com.revoola.model.RLMindBodyFilterGroupItemModel



class RlMindBodyFilterExpandableListAdapter(private val context: Context, private val groupList: List<RLMindBodyFilterGroupItemModel>) : BaseExpandableListAdapter() {

    private val selectedChildItems = mutableSetOf<Pair<Int, Int>>()

    override fun getGroupCount(): Int = groupList.size

    override fun getChildrenCount(groupPosition: Int): Int = groupList[groupPosition].childItems.size

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

    private fun RlChangeRightIcon(groupTitle: String, isExpanded: Boolean, imgRight: ImageView){
        if (isExpanded){
            if (groupTitle.equals(context.getString(R.string.edit_your_account_data))){
                imgRight.setImageResource(R.drawable.ic_arrow_up_outline)
            }
        }else{
            if (groupTitle.equals(context.getString(R.string.edit_your_account_data))){
                imgRight.setImageResource(R.drawable.ic_chevron_right)
            }
        }

    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.rl_mind_body_chield_item, parent, false)
        val key = groupList[groupPosition]
        val childData = key.childItems ?: listOf()

        val tvChildLeft = view.findViewById<TextView>(R.id.txt_card_title_left)
        val tvChildRight = view.findViewById<TextView>(R.id.txt_card_title_right)

        val index = childPosition * 2

        tvChildLeft.text = childData.getOrNull(index) ?: ""
        tvChildRight.text = childData.getOrNull(index + 1) ?: ""


        // Check if this child is selected and apply the background
        val isSelected = selectedChildItems.contains(Pair(groupPosition, childPosition))
        if (isSelected) {
           // view.setBackgroundColor(context.resources.getColor(R.color.selected_item_background, null)) // Change to selected background color
        } else {
           // view.setBackgroundColor(context.resources.getColor(R.color.default_item_background, null)) // Default background color
        }

        // Handle child click to toggle selection
        view.setOnClickListener {
            if (isSelected) {
                selectedChildItems.remove(Pair(groupPosition, childPosition))
            } else {
                selectedChildItems.add(Pair(groupPosition, childPosition))
            }
            notifyDataSetChanged()  // Refresh the list view
            updateMainViewWithSelectedItems()  // Update the main view (outside the adapter)
        }



        return view
    }

    private fun updateMainViewWithSelectedItems() {
        // You can pass selectedChildItems to your main activity or fragment.
        // Example:
        val selectedItems = selectedChildItems.map { (groupPosition, childPosition) ->
            "${groupList[groupPosition].title} - ${groupList[groupPosition].childItems[childPosition]}"
        }.joinToString(", ")
        // Here, update the main view (e.g., a TextView or other view) to show the selected items
        // Example:
        (context as? RLFragMindClasses)?.updateSelectionView(selectedItems)
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}
