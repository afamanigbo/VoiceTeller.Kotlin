import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aiconverse.voiceteller.repository.wallet.CardModel

class RecyclerViewAdapter(
    private val layout: Int,
    private val onBind: (view: View, item: Any, position: Int) -> Unit,
    private val onViewClicked: (view: View, item: Any, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private val items = ArrayList<Any>()

    fun addItem(item: Any) {
        this.items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addWalletCardItems(items: MutableList<CardModel>) {
        this.items.addAll(items)

        notifyDataSetChanged()
    }

    fun clearItems() {
        this.items.clear()
        notifyDataSetChanged()

    }

    class ViewHolder(
        private val view: View,
        val onBind: (view: View, item: Any, position: Int) -> Unit,
        val onViewClicked: (view: View, item: Any, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        fun bind(item: Any, position: Int) {
            onBind(view, item, position)

            view.setOnClickListener { onViewClicked(view, item, position) }
        }
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)

        return ViewHolder(view, onBind, onViewClicked)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(items[pos], pos)
    }

}
