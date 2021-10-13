package ru.sber.services

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Scope("singleton")
class SingletonService

@Component
@Scope("prototype")
class PrototypeService