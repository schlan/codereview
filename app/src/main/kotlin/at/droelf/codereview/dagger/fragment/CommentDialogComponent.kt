package at.droelf.codereview.dagger.fragment

import at.droelf.codereview.ui.dialog.CommentDialog
import dagger.Subcomponent

@FragmentScope
@Subcomponent(
        modules = arrayOf(
                CommentDialogModule::class
        )
)
interface CommentDialogComponent {
    fun inject(commentDialog: CommentDialog): CommentDialog
}