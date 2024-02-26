package gomoku.http.models

import java.net.URI
import com.fasterxml.jackson.annotation.JsonProperty

const val MEDIA_TYPE = "application/vnd.siren+json"
const val URL = "http://localhost:8080"

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

class SirenBuilderScope<T>(
    val properties: T,

    ) {
    private val classes = mutableListOf<String>()
    private val entities = mutableListOf<EntityModel<*>>()
    private val actions = mutableListOf<ActionModel>()
    private val links = mutableListOf<LinkModel>()
    private var title = ""

    fun addTitle(value: String) {
        title = value
    }

    fun clazz(value: String) {
        classes.add(value)
    }

    fun <U> entity(
        clazz: List<String>,
        rel: List<URI>,
        href: URI,
        value: U,
        links: List<LinkModel>,
        block: EntityBuilderScope<U>.() -> Unit
    ) {
        val scope = EntityBuilderScope(clazz, rel,href, value, links)
        scope.block()
        entities.add(scope.build())
    }

    fun action(
        name: String,
        clazz: List<String>,
        title: String,
        method: String,
        href: URI,
        type: String,
        block: ActionBuilderScope.() -> Unit
    ) {
        val scope = ActionBuilderScope(name, clazz, title, method, href, type)
        scope.block()
        actions.add(scope.build())
    }

    fun link(
        rel: List<String>,
        clazz: List<String>,
        href: URI,
        title: String,
        type: String,
        block: LinkBuilderScope.() -> Unit
    ) {
        val scope = LinkBuilderScope(rel, clazz, href.toASCIIString(), title, type)
        scope.block()
        links.add(scope.build())
    }

    fun build(): SirenModel<T> = SirenModel(
        clazz = classes,
        properties = properties,
        entities = entities,
        actions = actions,
        links = links,
        title = title
    )
}

class EntityBuilderScope<T>(
    val clazz: List<String>,
    val rel: List<URI>,
    val href: URI,
    val properties: T,
    val links: List<LinkModel>
) {
    private val classes = mutableListOf<String>()
    private val relMutable = mutableListOf<URI>()
    private val linksMutable = mutableListOf<LinkModel>()


    fun clazz(value: String) {
        classes.add(value)
    }

    fun res(value: URI) {
        relMutable.add(value)
    }

    fun link(rel: List<String>, clazz: List<String>, href: URI, title: String, type: String) {
        linksMutable.add(LinkModel(rel, clazz, href.toASCIIString(), title, type))
    }

    fun build(): EntityModel<T> = EntityModel(
        clazz = clazz + classes,
        rel = rel + relMutable,
        href = href,
        properties = properties,
        links = links + linksMutable
    )
}

class ActionBuilderScope(
    private val name: String,
    private val clazz: List<String>,
    private val title: String,
    private val method: String,
    private val href: URI,
    private val type: String
) {
    private val fields = mutableListOf<FieldsModel>()
    private val classes = mutableListOf<String>()

    fun clazz(value: String) {
        classes.add(value)
    }

    fun textField(name: String, clazz: List<String>, title: String, value: String) {
        fields.add(FieldsModel(name, clazz, "text", value, title))
    }
    fun passwordField(name: String, clazz: List<String>, title: String, value: String) {
        fields.add(FieldsModel(name, clazz, "password", value, title))
    }

    fun numberField(name: String, clazz: List<String>, title: String, value: String) {
        fields.add(FieldsModel(name, clazz, "number", value, title))
    }

    fun hiddenField(name: String, clazz: List<String>, title: String, value: String) {
        fields.add(FieldsModel(name, clazz, "hidden", value, title))
    }

    fun build(): ActionModel = ActionModel(
        name = name,
        clazz = clazz + classes,
        title = title,
        method = method,
        href = href.toASCIIString(),
        type = type,
        fields = if (fields.isNotEmpty()) fields[0] else FieldsModel("", emptyList(), "", "", "")
    )
}

class LinkBuilderScope(
    val clazz: List<String>,
    val rel: List<String>,
    val href: String,
    val title: String,
    val type: String
) {
    private val classes = mutableListOf<String>()
    private val relMutable = mutableListOf<String>()

    fun clazz(value: String) {
        classes.add(value)
    }

    fun res(value: String) {
        relMutable.add(value)
    }


    fun build(): LinkModel = LinkModel(
        clazz = clazz + classes,
        rel = rel + relMutable,
        href = href,
        title = title,
        type = type
    )
}

fun <T> siren(value: T, block: SirenBuilderScope<T>.() -> Unit): SirenModel<T> {
    val scope = SirenBuilderScope(value)
    scope.block()
    return scope.build()
}
