package com.teammealky.mealky.presentation.addmeal

import com.teammealky.mealky.domain.model.Ingredient
import com.teammealky.mealky.presentation.addmeal.model.MealViewModel
import com.teammealky.mealky.presentation.commons.presenter.BasePresenter
import com.teammealky.mealky.presentation.commons.presenter.BaseUI
import com.teammealky.mealky.presentation.addmeal.AddMealPresenter.ValidationResult.*
import com.teammealky.mealky.presentation.addmeal.model.ThumbnailImage
import javax.inject.Inject
import timber.log.Timber

class AddMealPresenter @Inject constructor() : BasePresenter<AddMealPresenter.UI>() {

    var model: MealViewModel = MealViewModel.basicMealViewModel()
    var attachments = mutableListOf<ThumbnailImage>()

    override fun attach(ui: UI) {
        super.attach(ui)
        ui().perform { it.showImagesQueue(attachments) }
    }

    fun fieldsChanged(title: String?, preparationTime: String?, description: String?) {
        val titleString = title ?: ""
        val preparationTimeString = preparationTime?.toIntOrNull()?.toString() ?: ""
        val descriptionString = description ?: ""

        model = MealViewModel(titleString, preparationTimeString, descriptionString)

        ui().perform { it.enableConfirmBtn(fieldsAreNoteEmpty()) }
    }

    private fun fieldsAreNoteEmpty() = model.description.trim().isNotEmpty() && model.title.trim().isNotEmpty()
            && model.preparationTime.trim().isNotEmpty()

    fun touchedOutside() {
        ui().perform { it.hideKeyboard() }
    }

    fun confirmBtnClicked() {
        ui().perform {
            it.clearErrors()
            it.isLoading(true)
        }
        val result = fieldsValidated()
        if (allAreCorrect(result)) {
            //todo send request to api
            ui().perform {
                it.showToast()
                it.toMealsFragment()
            }
        } else {
            ui().perform { it.showErrors(result) }
        }
    }

    private fun allAreCorrect(result: List<ValidationResult>): Boolean {
        return result.all { it == CORRECT }
    }

    private fun fieldsValidated(): List<ValidationResult> {
        val errors = mutableListOf<ValidationResult>()
        if (model.title.isEmpty())
            errors.add(TITLE_ERROR)
        if (model.description.isEmpty())
            errors.add(PREP_ERROR)
        if (model.preparationTime.isEmpty() || model.preparationTime.toInt() <= 0)
            errors.add(PREP_TIME_ERROR)

        if (errors.isEmpty())
            errors.add(CORRECT)

        return errors
    }

    fun addImagesBtnClicked() {
        ui().perform { it.showGalleryCameraDialog() }
    }

    fun addIngredientsBtnClicked() {
        Timber.tag("KUBA").v("addIngredientsBtnClicked ")
    }

    fun onInformationPassed(imagePath: String) {
        attachments.add(ThumbnailImage(getNewId(), imagePath))
        ui().perform { it.showImagesQueue(attachments) }
    }

    private fun getNewId() = ((attachments.maxBy { it.id }?.id) ?: attachments.size) + 1

    fun onInformationPassed(ingredient: Ingredient) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onImageDeleteClicked(image: ThumbnailImage) {
        attachments.remove(image)
        ui().perform { it.showImagesQueue(attachments) }
    }

    interface UI : BaseUI {
        fun enableConfirmBtn(isEnabled: Boolean)
        fun toMealsFragment()
        fun showErrors(errors: List<ValidationResult>)
        fun showToast()
        fun clearErrors()
        fun isLoading(isLoading: Boolean)
        fun showGalleryCameraDialog()
        fun showAddIngredientDialog(ingredients: List<Ingredient>)
        fun showImagesQueue(attachments: MutableList<ThumbnailImage>)
    }

    enum class ValidationResult {
        TITLE_ERROR,
        PREP_ERROR,
        PREP_TIME_ERROR,
        INGREDIENTS_ERROR,
        IMAGES_ERROR,
        CORRECT
    }
}