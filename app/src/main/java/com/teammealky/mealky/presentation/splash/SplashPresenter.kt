package com.teammealky.mealky.presentation.splash

import com.teammealky.mealky.domain.model.Token
import com.teammealky.mealky.domain.model.User
import com.teammealky.mealky.domain.usecase.signin.SignInWithTokenUseCase
import com.teammealky.mealky.domain.usecase.token.GetTokenUseCase
import com.teammealky.mealky.domain.usecase.user.SaveUserUseCase
import com.teammealky.mealky.presentation.commons.presenter.BasePresenter
import com.teammealky.mealky.presentation.commons.presenter.BaseUI
import timber.log.Timber
import javax.inject.Inject

class SplashPresenter @Inject constructor(
        private val signInWithTokenUseCase: SignInWithTokenUseCase,
        private val saveUserUseCase: SaveUserUseCase,
        private val getTokenUseCase: GetTokenUseCase
) : BasePresenter<SplashPresenter.UI>() {

    override fun attach(ui: UI) {
        super.attach(ui)
        validateToken()
    }

    private fun validateToken() {
        disposable.add(getTokenUseCase.execute(
                { token ->
                    if (token == Token.emptyToken())
                        ui().perform { it.toSignIn() }
                    else
                        sendToken(token.token)
                },
                { e ->
                    Timber.e("KUBA_LOG Method:sendToken ***** $e *****")
                    ui().perform { it.toSignIn() }
                })
        )
    }

    private fun sendToken(token: String) {
        disposable.add(signInWithTokenUseCase.execute(
                SignInWithTokenUseCase.Params(token),
                { user ->
                    saveUser(user)
                },
                { e ->
                    Timber.e("KUBA_LOG Method:sendToken ***** $e *****")
                    ui().perform { it.toSignIn() }
                })
        )
    }

    private fun saveUser(user: User) {
        disposable.add(saveUserUseCase.execute(
                user,
                {
                    ui().perform { ui -> ui.toMainActivity() }
                },
                { e ->
                    Timber.e("KUBA_LOG Method:saveUser ***** $e *****")
                    ui().perform { it.toSignIn() }
                })
        )
    }

    interface UI : BaseUI {
        fun toSignIn()
        fun toMainActivity()
    }
}