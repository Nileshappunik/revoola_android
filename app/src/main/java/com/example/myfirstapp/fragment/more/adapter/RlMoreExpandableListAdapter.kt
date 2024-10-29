package com.example.myfirstapp.fragment.more.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.myfirstapp.R
import com.example.myfirstapp.model.RLMoreGroupItemModel

class RlMoreExpandableListAdapter(
    private val context: Context,
    private val groupList: List<RLMoreGroupItemModel>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = groupList.size

    override fun getChildrenCount(groupPosition: Int): Int = groupList[groupPosition].childItems.size

    override fun getGroup(groupPosition: Int): Any = groupList[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any = groupList[groupPosition].childItems[childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val groupTitle = (getGroup(groupPosition) as RLMoreGroupItemModel).title
        val groupIcon = (getGroup(groupPosition) as RLMoreGroupItemModel).icon
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.rl_expandable_list_item_more, parent, false)
        val textView = view.findViewById<TextView>(R.id.txt_account)
        val imgAccount = view.findViewById<ImageView>(R.id.img_account)
        val imgRight = view.findViewById<ImageView>(R.id.img_right)//ic_chevron_right
        val layMoreClick = view.findViewById<RelativeLayout>(R.id.lay_more_click)
        if (groupTitle.equals(context.getString(R.string.signout))){
            layMoreClick.setBackgroundColor(context.resources.getColor(R.color.AppSignOutBGColor))
        }else{
            layMoreClick.setBackgroundColor(context.resources.getColor(R.color.AppLightGrayColor))
        }
        textView.text = groupTitle
        imgAccount.setImageResource(groupIcon)
        imgRight.setImageResource(R.drawable.ic_chevron_right)

        RlChangeRightIcon(groupTitle,isExpanded,imgRight)
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
        val childItem = getChild(groupPosition, childPosition) as String
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.rl_expandable_list_item_more, parent, false)
        val textView = view.findViewById<TextView>(R.id.txt_account)
        val txt_Child = view.findViewById<TextView>(R.id.txt_Child)
        val imgAccount = view.findViewById<ImageView>(R.id.img_account)
        val viewimgtxt1 = view.findViewById<View>(R.id.viewimgtxt1)
        textView.visibility=View.GONE
        imgAccount.visibility=View.GONE
        viewimgtxt1.visibility=View.GONE
        txt_Child.visibility=View.VISIBLE
        txt_Child.text = childItem
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}
