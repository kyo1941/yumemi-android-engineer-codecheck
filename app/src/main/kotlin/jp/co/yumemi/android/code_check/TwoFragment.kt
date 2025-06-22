/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.databinding.FragmentTwoBinding

class TwoFragment : Fragment(R.layout.fragment_two) {

    private val args: TwoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTwoBinding.bind(view)

        val item = args.item

        Log.d("検索した日時", item.searchedAt.toString())

        binding.ownerIconView.load(item.ownerIconUrl)
        binding.nameView.text = item.name
        binding.languageView.text = getString(R.string.written_language, item.language)
        binding.starsView.text = getString(R.string.stars_count, item.stargazersCount)
        binding.watchersView.text = getString(R.string.watchers_count, item.watchersCount)
        binding.forksView.text = getString(R.string.forks_count, item.forksCount)
        binding.openIssuesView.text = getString(R.string.open_issues_count, item.openIssuesCount)
    }
}
