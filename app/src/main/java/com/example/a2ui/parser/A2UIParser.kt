package com.example.a2ui.parser

import com.example.a2ui.model.A2UIAction
import com.example.a2ui.model.A2UIComponent
import com.example.a2ui.model.A2UISurface
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

sealed class ParseResult {
    data class Success(val surface: A2UISurface, val warnings: List<String> = emptyList()) : ParseResult()
    data class Error(val message: String, val line: Int? = null) : ParseResult()
}

object A2UIParser {

    /**
     * Parse raw JSON string into an A2UISurface with adjacency list mapping.
     */
    fun parse(jsonString: String): ParseResult {
        val trimmed = jsonString.trim()
        if (trimmed.isEmpty()) {
            return ParseResult.Error("JSON string is empty.")
        }

        try {
            val warnings = mutableListOf<String>()

            val componentsJsonArray: JSONArray
            var surfaceId = "main"
            var catalog = A2UICatalogRegistry.catalogUri
            var title = "A2UI Surface"
            val initialData = mutableMapOf<String, Any?>()

            if (trimmed.startsWith("[")) {
                // Raw array of components
                componentsJsonArray = JSONArray(trimmed)
            } else if (trimmed.startsWith("{")) {
                val rootObj = JSONObject(trimmed)
                if (rootObj.has("surfaceId")) surfaceId = rootObj.getString("surfaceId")
                if (rootObj.has("catalog")) catalog = rootObj.getString("catalog")
                if (rootObj.has("title")) title = rootObj.getString("title")

                if (rootObj.has("dataModel")) {
                    val dataObj = rootObj.getJSONObject("dataModel")
                    val keys = dataObj.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        initialData[k] = dataObj.opt(k)
                    }
                }

                if (rootObj.has("components")) {
                    componentsJsonArray = rootObj.getJSONArray("components")
                } else if (rootObj.has("component") && rootObj.has("id")) {
                    // Single root component object
                    componentsJsonArray = JSONArray().apply { put(rootObj) }
                } else {
                    return ParseResult.Error("Missing 'components' array in A2UI envelope.")
                }
            } else {
                return ParseResult.Error("JSON must begin with '{' or '['.")
            }

            val componentMap = mutableMapOf<String, A2UIComponent>()

            for (i in 0 until componentsJsonArray.length()) {
                val item = componentsJsonArray.optJSONObject(i) ?: continue
                val id = item.optString("id", "")
                val componentType = item.optString("component", item.optString("type", ""))

                if (id.isEmpty()) {
                    warnings.add("Component at index $i is missing an 'id'. Skipped.")
                    continue
                }
                if (componentType.isEmpty()) {
                    warnings.add("Component '$id' is missing 'component' type. Defaulted to Box.")
                }

                // Children
                val childrenList = mutableListOf<String>()
                if (item.has("children")) {
                    val childArr = item.optJSONArray("children")
                    if (childArr != null) {
                        for (c in 0 until childArr.length()) {
                            val childId = childArr.optString(c, "")
                            if (childId.isNotEmpty()) childrenList.add(childId)
                        }
                    }
                }

                // Properties
                val propsMap = mutableMapOf<String, Any?>()
                if (item.has("properties")) {
                    val pObj = item.optJSONObject("properties")
                    if (pObj != null) {
                        val pKeys = pObj.keys()
                        while (pKeys.hasNext()) {
                            val key = pKeys.next()
                            propsMap[key] = jsonValueToAny(pObj.opt(key))
                        }
                    }
                }
                // Also copy direct properties for resilience if LLM put them at component root
                val directKeys = item.keys()
                while (directKeys.hasNext()) {
                    val k = directKeys.next()
                    if (k !in setOf("id", "component", "type", "children", "properties", "action", "weight")) {
                        if (!propsMap.containsKey(k)) {
                            propsMap[k] = jsonValueToAny(item.opt(k))
                        }
                    }
                }

                // Action
                var actionObj: A2UIAction? = null
                if (item.has("action")) {
                    val aObj = item.optJSONObject("action")
                    if (aObj != null) {
                        val actionName = aObj.optString("name", "")
                        val params = mutableMapOf<String, Any?>()
                        if (aObj.has("parameters")) {
                            val paramObj = aObj.optJSONObject("parameters")
                            if (paramObj != null) {
                                val paramKeys = paramObj.keys()
                                while (paramKeys.hasNext()) {
                                    val pk = paramKeys.next()
                                    params[pk] = jsonValueToAny(paramObj.opt(pk))
                                }
                            }
                        }
                        if (actionName.isNotEmpty()) {
                            actionObj = A2UIAction(name = actionName, parameters = params)
                        }
                    }
                }

                val weight = if (item.has("weight")) item.optDouble("weight", 0.0).toFloat() else null

                val comp = A2UIComponent(
                    id = id,
                    component = if (componentType.isNotEmpty()) componentType else "Box",
                    children = childrenList,
                    properties = propsMap,
                    action = actionObj,
                    weight = weight
                )
                componentMap[id] = comp
            }

            if (!componentMap.containsKey("root")) {
                // If no component has id 'root', check if there's only one or pick first
                val firstKey = componentMap.keys.firstOrNull()
                if (firstKey != null) {
                    warnings.add("No component with id='root' found; defaulting root to '$firstKey'.")
                } else {
                    return ParseResult.Error("No valid components parsed.")
                }
            }

            val rootId = if (componentMap.containsKey("root")) "root" else componentMap.keys.first()

            val surface = A2UISurface(
                surfaceId = surfaceId,
                catalog = catalog,
                title = title,
                components = componentMap,
                dataModel = initialData,
                rootComponentId = rootId
            )

            return ParseResult.Success(surface, warnings)

        } catch (e: JSONException) {
            return ParseResult.Error("JSON Syntax Error: ${e.message}")
        } catch (e: Exception) {
            return ParseResult.Error("Parse Error: ${e.message}")
        }
    }

    private fun jsonValueToAny(value: Any?): Any? {
        return when (value) {
            null, JSONObject.NULL -> null
            is JSONArray -> {
                val list = mutableListOf<Any?>()
                for (i in 0 until value.length()) {
                    list.add(jsonValueToAny(value.opt(i)))
                }
                list
            }
            is JSONObject -> {
                val map = mutableMapOf<String, Any?>()
                val keys = value.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    map[k] = jsonValueToAny(value.opt(k))
                }
                map
            }
            else -> value
        }
    }

    /**
     * Formats JSON string with 2-space indentation.
     */
    fun formatJson(raw: String): String {
        return try {
            val trimmed = raw.trim()
            if (trimmed.startsWith("{")) {
                JSONObject(trimmed).toString(2)
            } else if (trimmed.startsWith("[")) {
                JSONArray(trimmed).toString(2)
            } else {
                raw
            }
        } catch (e: Exception) {
            raw
        }
    }
}
