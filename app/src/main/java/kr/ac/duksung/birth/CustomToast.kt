package kr.ac.duksung.birth

// CustomToast.kt
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast

object CustomToast {
    fun showToast(context: Context, message: String) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.custom_toast, null)

        val textViewMessage = view.findViewById<TextView>(R.id.textViewToastMessage)
        textViewMessage.text = message

        with(Toast(context)) {
            duration = Toast.LENGTH_SHORT
            view = view
            show()
        }
    }
}
