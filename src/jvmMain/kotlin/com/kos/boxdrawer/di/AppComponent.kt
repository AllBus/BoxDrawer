package com.kos.boxdrawer.di

import dagger.Component

@Component(modules = [AppModule::class])
internal interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent
    }
}
