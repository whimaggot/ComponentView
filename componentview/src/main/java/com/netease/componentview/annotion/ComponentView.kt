package com.netease.componentview.annotion

import com.netease.componentview.base.IComponentView
import java.lang.annotation.ElementType
import java.lang.annotation.Inherited
import java.lang.annotation.Target
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(ElementType.TYPE)
annotation class ComponentView(
    vararg val view: KClass<out IComponentView<*, *>>
)

