package com.example.gomoku.http

import java.net.URI

data class SirenModel<T>(
    val clazz: List<String>? = null,
    val properties: T,
    val entities: List<EntityModel<*>>,
    val actions: List<ActionModel>,
    val links: List<LinkModel>,
    val title: String? = ""
)

data class EntityModel<T>(
    val clazz: List<String>? = null,
    val rel: List<URI>? = null,
    val href: URI? = null,
    val properties: T? = null,
    val links: List<LinkModel>? = null
)

data class ActionModel(
    val name: String,
    val clazz: List<String>? = null,
    val title: String? = null,
    val method: String? = null,
    val href: String,
    val type: String? = null,
    val fields: FieldsModel? = null
)

data class LinkModel(
    val clazz: List<String>? = null,
    val rel: List<String>,
    val href: String,
    val title: String? = null,
    val type: String? = null
)

data class FieldsModel(
    val name: String,
    val clazz: List<String>? = null,
    val type: String? = null,
    val value: String? = null,
    val title: String? = null
)

