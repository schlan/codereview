package at.droelf.codereview.ui.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import at.droelf.codereview.R
import java.nio.DoubleBuffer


public class FileChangesView(context: Context, additions: Int, deletions: Int, changes: Int): LinearLayout(context) {

    val boxes: List<View>

    init {
        LayoutInflater.from(context).inflate(R.layout.view_file_changes, this, true)
        boxes = listOf(
                findViewById(R.id.file_changes_1),
                findViewById(R.id.file_changes_2),
                findViewById(R.id.file_changes_3),
                findViewById(R.id.file_changes_4),
                findViewById(R.id.file_changes_5)
        )
        setFileChangeInformation(changes.toDouble(), additions.toDouble(), deletions.toDouble())
    }


    fun setFileChangeInformation(totalLines: Double, additions: Double, deletions: Double) {
        //TODO make this work ...
        val percentAdditions = ((additions / totalLines) * 5).toInt()
        val percentDeletions = ((deletions / totalLines) * 5).toInt()
        val noChanges = 5 - percentAdditions - percentDeletions

        val backgroundlist: MutableList<ColorDrawable> = arrayListOf()
        if(percentAdditions > 0){
            (0..percentAdditions-1).forEach { backgroundlist.add(ColorDrawable(ContextCompat.getColor(context, R.color.build_pass))) }
        }
        if(percentDeletions > 0){
            (0..percentDeletions-1).forEach { backgroundlist.add(ColorDrawable(ContextCompat.getColor(context, R.color.build_fail))) }
        }
        if(noChanges > 0){
            (0..noChanges-1).forEach { backgroundlist.add(ColorDrawable(ContextCompat.getColor(context, R.color.bg_gray))) }
        }

        boxes.forEachIndexed { i, view -> view.background = backgroundlist[i] }
    }
}