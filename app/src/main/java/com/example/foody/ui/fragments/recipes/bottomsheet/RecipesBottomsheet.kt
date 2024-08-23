package com.example.foody.ui.fragments.recipes.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.foody.R
import com.example.foody.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foody.viewmodel.RecipesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Locale

class RecipesBottomsheet : BottomSheetDialogFragment() {
    private lateinit var recipesViewModel: RecipesViewModel

    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val mView = inflater.inflate(R.layout.recipes_bottomsheet, container, false)

        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner) { value ->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            updateChip(
                value.selectedMealTypeId,
                mView.findViewById<ChipGroup>(R.id.mealType_chipGroup)
            )
            updateChip(
                value.selectedDietTypeId,
                mView.findViewById<ChipGroup>(R.id.dietType_chipGroup)
            )
        }


        mView.findViewById<ChipGroup>(R.id.mealType_chipGroup)
            .setOnCheckedStateChangeListener { group, selectedChipId ->
                val chip = group.findViewById<Chip>(selectedChipId.first())
                val selectedMealType = chip.text.toString().toLowerCase(Locale.ROOT)
                mealTypeChip = selectedMealType
                mealTypeChipId = selectedChipId.first()
            }

        mView.findViewById<ChipGroup>(R.id.dietType_chipGroup)
            .setOnCheckedStateChangeListener  { group, selectedChipId ->
                val chip = group.findViewById<Chip>(selectedChipId.first())
                val selectedMealType = chip.text.toString().toLowerCase(Locale.ROOT)
                dietTypeChip = selectedMealType
                dietTypeChipId = selectedChipId.first()
            }

        mView.findViewById<Button>(R.id.apply_btn).setOnClickListener {
            recipesViewModel.saveMealAndDietType(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )
            val action = RecipesBottomsheetDirections.actionRecipesBottomsheetToRecipesFragment(backFromBottomSheet = true)
            findNavController().navigate(action)
        }

        return mView
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            } catch (e: Exception) {
                Log.d("RecipesBottomSheet", e.message.toString())
            }
        }

    }


}