package at.droelf.codereview.ui.adapter;

import at.droelf.codereview.model.GithubModel

interface NotificationFragmentAdapterCommander {
    fun removeItem(wrapper: GithubModel.PullRequest): Unit
}