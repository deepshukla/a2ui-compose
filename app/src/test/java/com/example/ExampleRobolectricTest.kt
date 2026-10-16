package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.a2ui.parser.A2UICatalogRegistry
import com.example.a2ui.parser.A2UIParser
import com.example.a2ui.parser.A2UISchemaGenerator
import com.example.a2ui.parser.ParseResult
import com.example.a2ui.repository.A2UIPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("A2UI Compose", appName)
    }

    @Test
    fun testClimateTracePresetParsing() {
        val preset = A2UIPresets.getPreset("climatetrace")
        val result = A2UIParser.parse(preset.jsonContent)

        assertTrue("Parsing ClimateTrace preset should succeed", result is ParseResult.Success)
        val surface = (result as ParseResult.Success).surface
        assertEquals("climatetrace_summary", surface.surfaceId)
        assertNotNull("Root component should exist", surface.rootComponent)
        assertEquals("Column", surface.rootComponent?.component)
        assertTrue("Root should contain children", surface.rootComponent?.children?.isNotEmpty() == true)

        val chartComp = surface.components["chart_emissions"]
        assertNotNull(chartComp)
        assertEquals("EmissionsChart", chartComp?.component)
        val categories = chartComp?.getListProperty("categories")
        assertTrue(categories?.isNotEmpty() == true)
    }

    @Test
    fun testCatalogSchemaGenerator() {
        val schema = A2UISchemaGenerator.generateJsonSchema()
        assertTrue("Schema should contain draft-07", schema.contains("draft-07"))
        assertTrue("Schema should list components", schema.contains("components"))
        assertTrue("Schema should mention A2UI", schema.contains("A2UI"))
    }

    @Test
    fun testCatalogRegistryDefinitions() {
        val components = A2UICatalogRegistry.components
        assertTrue("Catalog should contain components", components.size >= 10)
        assertNotNull("EmissionsChart should exist in catalog", A2UICatalogRegistry.getComponent("EmissionsChart"))
        assertNotNull("MetricCard should exist in catalog", A2UICatalogRegistry.getComponent("MetricCard"))
        assertNotNull("Button should exist in catalog", A2UICatalogRegistry.getComponent("Button"))
    }

    @Test
    fun testInvalidJsonHandling() {
        val result = A2UIParser.parse("{ invalid json }")
        assertTrue("Invalid JSON should return ParseResult.Error", result is ParseResult.Error)
    }
}
