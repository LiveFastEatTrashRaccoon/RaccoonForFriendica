package com.livefast.eattrash.raccoonforfriendica.core.architecture.di

import androidx.lifecycle.ViewModel
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bindFactory
import org.kodein.di.bindProvider
import kotlin.reflect.cast

/**
 * Utility to be used in module definition to define a binding for a [ViewModel] without arguments.
 *
 * @param T [ViewModel] type
 * @param overrides whether this bind must or must not override an existing binding.
 * @param block a block which returns the instance to bind
 */
inline fun <reified T : ViewModel> DI.Builder.bindViewModel(
    overrides: Boolean? = null,
    noinline block: DirectDI.() -> T,
) {
    bindProvider(
        tag = T::class.diKey,
        overrides = overrides,
    ) {
        block()
    }
}

/**
 * Utility to be used in module definition to define a binding for a [ViewModel] with arguments.
 *
 * @param T [ViewModel] type
 * @param A [ViewModelCreationArgs] type for the arguments
 * @param overrides whether this bind must or must not override an existing binding.
 * @param block a block which returns the instance to bind taking in some parameters [A]
 */
inline fun <reified A : ViewModelCreationArgs, reified T : ViewModel> DI.Builder.bindViewModelWithArgs(
    overrides: Boolean? = null,
    noinline block: DirectDI.(A) -> T,
) {
    bindFactory<ViewModelCreationArgs, T>(
        tag = T::class.diKey,
        overrides = overrides,
    ) { args: ViewModelCreationArgs ->
        block(A::class.cast(args))
    }
}
