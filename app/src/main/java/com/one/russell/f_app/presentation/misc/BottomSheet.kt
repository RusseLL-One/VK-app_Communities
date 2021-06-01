package com.one.russell.f_app.presentation.misc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.one.russell.f_app.Constants
import com.one.russell.f_app.R
import com.one.russell.f_app.data.DetailedCommunity
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class BottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img_close.setOnClickListener { dismissAllowingStateLoss()  }

        showPlaceholders()
    }

    fun setData(detailedCommunity: DetailedCommunity) {
        txt_title.text = detailedCommunity.title
        txt_followers_count.text = composeFollowersCount(detailedCommunity.followers, detailedCommunity.friends)
        txt_article.text = detailedCommunity.description
        txt_last_post_date.text = composeLastPostDate(detailedCommunity.lastPostDate)

        article_container.visibility = if (detailedCommunity.description.isEmpty()) View.GONE else View.VISIBLE
        hidePlaceholders()

        btn_open.setOnClickListener {
            val url = Constants.VK_BASE_URL + detailedCommunity.screenName
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            requireContext().startActivity(i)
        }
    }

    private fun composeLastPostDate(lastPostDate: Date): String {
        val dateFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
        val dateString = dateFormat.format(lastPostDate)
        return resources.getString(R.string.item_details_last_news, dateString)
    }

    private fun composeFollowersCount(followers: Int, friends: Int): String {
        val followersCount = composeCountWithSuffix(followers)
        val friendsCount = composeCountWithSuffix(friends)

        val followersPlural = resources.getQuantityString(R.plurals.followers_plural, if (followers < 1000) followers else 1000, followersCount)
        val friendsPlural = resources.getQuantityString(R.plurals.friends_plural, if (friends < 1000) friends else 1000, friendsCount)
        return "$followersPlural · $friendsPlural"
    }

    private fun composeCountWithSuffix(count: Int): String {
        var suffix = ""
        var shrinkedCount = count
        if (shrinkedCount >= 1000) {
            shrinkedCount = (shrinkedCount.toFloat() / 1000).roundToInt()
            suffix = "K"
        }
        if (shrinkedCount >= 1000) {
            shrinkedCount = (shrinkedCount.toFloat() / 1000).roundToInt()
            suffix = "M"
        }

        return shrinkedCount.toString() + suffix
    }

    private fun showPlaceholders() {
        txt_title.setBackgroundResource(R.drawable.text_placeholder)
        txt_followers_count.setBackgroundResource(R.drawable.text_placeholder)
        txt_article.setBackgroundResource(R.drawable.text_placeholder)
        txt_last_post_date.setBackgroundResource(R.drawable.text_placeholder)
    }

    private fun hidePlaceholders() {
        txt_title.setBackgroundResource(0)
        txt_followers_count.setBackgroundResource(0)
        txt_article.setBackgroundResource(0)
        txt_last_post_date.setBackgroundResource(0)
    }
}