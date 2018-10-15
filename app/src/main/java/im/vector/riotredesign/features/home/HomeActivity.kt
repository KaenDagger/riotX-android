package im.vector.riotredesign.features.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import im.vector.matrix.android.api.Matrix
import im.vector.matrix.android.api.MatrixCallback
import im.vector.matrix.android.api.failure.Failure
import im.vector.matrix.android.internal.database.model.EventEntity
import im.vector.matrix.android.internal.events.sync.data.SyncResponse
import im.vector.riotredesign.R
import im.vector.riotredesign.core.platform.RiotActivity
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject

class HomeActivity : RiotActivity() {

    private val matrix by inject<Matrix>()
    private val synchronizer = matrix.currentSession?.synchronizer()
    private val realmHolder = matrix.currentSession?.realmInstanceHolder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        synchronizeButton.setOnClickListener { synchronize() }
    }

    private fun synchronize() {
        synchronizeButton.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        synchronizer?.synchronize(object : MatrixCallback<SyncResponse> {
            override fun onSuccess(data: SyncResponse?) {
                synchronizeButton.visibility = View.VISIBLE
                loadingView.visibility = View.GONE
            }

            override fun onFailure(failure: Failure) {
                synchronizeButton.visibility = View.VISIBLE
                loadingView.visibility = View.GONE
                Toast.makeText(this@HomeActivity, failure.toString(), Toast.LENGTH_LONG).show()
            }
        })
        if (realmHolder != null) {
            val results = realmHolder.realm.where(EventEntity::class.java).equalTo("chunk.room.roomId", "!UlckfcnwgLKswCmUbe:matrix.org").findAll()
            results.addChangeListener(RealmChangeListener<RealmResults<EventEntity>> {
                Toast.makeText(this@HomeActivity, "Room events data changed", Toast.LENGTH_LONG).show()
            })
        }


    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }

    }

}