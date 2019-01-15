package com.teammealky.mealky.presentation.meals

import com.teammealky.mealky.domain.model.Meal
import com.teammealky.mealky.domain.usecase.meals.ListMealsUseCase
import com.teammealky.mealky.presentation.commons.presenter.BasePresenter
import com.teammealky.mealky.presentation.commons.presenter.BaseUI
import timber.log.Timber
import javax.inject.Inject


class MealListPresenter @Inject constructor(
        private val getMealsUseCase: ListMealsUseCase
) : BasePresenter<MealListPresenter.UI>() {

    private var maxPages: Int = 0
    private var pageNumber: Int = 0
    private var meals = emptyList<Meal>()
    private var visibleItemId = 0

    fun onItemClicked(model: Meal) {
        ui().perform { it.openItem(model) }
    }

    override fun attach(ui: UI) {
        super.attach(ui)
        firstRequest()
    }

    private fun firstRequest() {
        ui().perform { it.isLoading(true) }
        if (meals.isEmpty()) {
            disposable.add(getMealsUseCase.execute(
                    ListMealsUseCase.Params(page = 0, limit = LIMIT),
                    { page ->
                        maxPages = page.totalPages
                        loadMore()
                    },
                    { e ->
                        ui().perform { it.showErrorMessage({ firstRequest() }, e, false) }
                    })
            )
        } else
            refresh()
    }

    private fun refresh() {
        ui().perform {
            it.fillList(meals)
            it.setVisibleItem(visibleItemId)
            it.isLoading(false)
        }
    }

    fun loadMore(query: String = "") {
        ui().perform { it.isLoading(true) }
        disposable.add(getMealsUseCase.execute(
                ListMealsUseCase.Params(query, pageNumber, LIMIT),
                { page ->
                    ui().perform {
                        meals += page.items
                        it.fillList(page.items)
                        it.isLoading(false)
                    }
                    if (pageNumber >= maxPages - 1)
                        pageNumber = 0
                    else
                        pageNumber++
                },
                { e ->
                    Timber.e("KUBA_LOG Method:loadMore ***** $e *****")
                }))
    }


    fun onPaused(itemPosition: Int) {
        this.visibleItemId = itemPosition
    }

    interface UI : BaseUI {
        fun setupRecyclerView()
        fun openItem(meal: Meal)
        fun isLoading(isLoading: Boolean)
        fun fillList(meals: List<Meal>)
        fun setVisibleItem(visibleItemId: Int)
    }

    companion object {
        const val LIMIT = 8
    }
}