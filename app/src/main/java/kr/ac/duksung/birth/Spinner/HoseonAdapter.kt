package kr.ac.duksung.birth.Spinner

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import kr.ac.duksung.birth.R
import kr.ac.duksung.birth.databinding.ItemSpinnerBinding

class HoseonAdapter (
    context: Context,
    @LayoutRes private val resId: Int, private val values: MutableList<HoseonModel>

) : ArrayAdapter<HoseonModel>(context, resId, values) {
    override fun getCount() = values.size

    override fun getItem(position: Int) = values[position]

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = values[position]
        try {
            binding.tvHoseon.text = model.hoseon
            binding.tvHoseon.setTextColor(ContextCompat.getColor(context, R.color.white))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = values[position]
        try {
            binding.tvHoseon.text = model.hoseon
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }
}