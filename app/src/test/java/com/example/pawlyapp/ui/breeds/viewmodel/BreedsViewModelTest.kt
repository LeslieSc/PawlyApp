package com.example.pawlyapp.ui.breeds.viewmodel

import com.example.pawlyapp.ui.breeds.model.RazaPerro
import com.example.pawlyapp.ui.breeds.network.GistService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para [BreedsViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BreedsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeBreedsList = listOf(
        RazaPerro(
            id = 1,
            nombre = "Labrador Retriever",
            origen = "Canadá",
            tamano = "Grande",
            temperamento = "Amigable y activo",
            nivelCuidado = "Medio",
            descripcion = "Raza ideal para familias",
            recomendaciones = "Paseos diarios",
            imagenUrl = "https://example.com/labrador.jpg",
        ),
        RazaPerro(
            id = 2,
            nombre = "Pastor Alemán",
            origen = "Alemania",
            tamano = "Grande",
            temperamento = "Leal y protector",
            nivelCuidado = "Alto",
            descripcion = "Perro guardián",
            recomendaciones = "Entrenamiento constante",
            imagenUrl = "https://example.com/pastor.jpg",
        ),
        RazaPerro(
            id = 3,
            nombre = "Chihuahua",
            origen = "México",
            tamano = "Pequeño",
            temperamento = "Valiente y apegado",
            nivelCuidado = "Bajo",
            descripcion = "Raza de tamaño pequeño",
            recomendaciones = "Proteger del frío",
            imagenUrl = "https://example.com/chihuahua.jpg",
        ),
    )

    private class FakeGistService(private val breeds: List<RazaPerro>) : GistService {
        override suspend fun getBreeds(): List<RazaPerro> = breeds
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchingExistingBreed_returnsCorrectResults() {
        runTest {
            val fakeService = FakeGistService(fakeBreedsList)
            val viewModel = BreedsViewModel(gistService = fakeService)
            advanceUntilIdle()

            viewModel.onSearchQueryChanged("Labrador")
            advanceUntilIdle()

            val results = viewModel.breeds.value
            assertEquals(1, results.size)
            assertEquals("Labrador Retriever", results.first().nombre)
        }
    }

    @Test
    fun searchingIsCaseInsensitive_returnsCorrectResults() {
        runTest {
            val fakeService = FakeGistService(fakeBreedsList)
            val viewModel = BreedsViewModel(gistService = fakeService)
            advanceUntilIdle()

            viewModel.onSearchQueryChanged("chihuahua")
            advanceUntilIdle()

            val results = viewModel.breeds.value
            assertEquals(1, results.size)
            assertEquals("Chihuahua", results.first().nombre)
        }
    }

    @Test
    fun searchingIsAccentInsensitive_returnsCorrectResults() {
        runTest {
            val fakeService = FakeGistService(fakeBreedsList)
            val viewModel = BreedsViewModel(gistService = fakeService)
            advanceUntilIdle()

            viewModel.onSearchQueryChanged("Pastor aleman")
            advanceUntilIdle()

            val results = viewModel.breeds.value
            assertEquals(1, results.size)
            assertEquals("Pastor Alemán", results.first().nombre)
        }
    }

    @Test
    fun searchingNonexistentBreed_returnsEmptyList() {
        runTest {
            val fakeService = FakeGistService(fakeBreedsList)
            val viewModel = BreedsViewModel(gistService = fakeService)
            advanceUntilIdle()

            viewModel.onSearchQueryChanged("RazaDesconocidaXYZ")
            advanceUntilIdle()

            val results = viewModel.breeds.value
            assertTrue(results.isEmpty())
        }
    }

    @Test
    fun clearingSearchQuery_restoresCompleteBreedList() {
        runTest {
            val fakeService = FakeGistService(fakeBreedsList)
            val viewModel = BreedsViewModel(gistService = fakeService)
            advanceUntilIdle()

            viewModel.onSearchQueryChanged("Chihuahua")
            advanceUntilIdle()
            assertEquals(1, viewModel.breeds.value.size)

            viewModel.clearSearch()
            advanceUntilIdle()

            val restoredResults = viewModel.breeds.value
            assertEquals(3, restoredResults.size)
            assertEquals(fakeBreedsList, restoredResults)
        }
    }

    @Test
    fun updatingSearchQuery_correctlyUpdatesFilteredStateFlow() {
        runTest {
            val fakeService = FakeGistService(fakeBreedsList)
            val viewModel = BreedsViewModel(gistService = fakeService)
            advanceUntilIdle()

            assertEquals(3, viewModel.breeds.value.size)

            viewModel.onSearchQueryChanged("Alemania")
            advanceUntilIdle()
            assertEquals(1, viewModel.breeds.value.size)
            assertEquals("Pastor Alemán", viewModel.breeds.value.first().nombre)

            viewModel.onSearchQueryChanged("Canadá")
            advanceUntilIdle()
            assertEquals(1, viewModel.breeds.value.size)
            assertEquals("Labrador Retriever", viewModel.breeds.value.first().nombre)
        }
    }

    @Test
    fun filteringByBreedSize_returnsMatchingBreeds() {
        runTest {
            val fakeService = FakeGistService(fakeBreedsList)
            val viewModel = BreedsViewModel(gistService = fakeService)
            advanceUntilIdle()

            viewModel.onSizeFilterChanged("Pequeño")
            advanceUntilIdle()

            val results = viewModel.breeds.value
            assertEquals(1, results.size)
            assertEquals("Chihuahua", results.first().nombre)
        }
    }

    @Test
    fun filteringByCareLevel_returnsMatchingBreeds() {
        runTest {
            val fakeService = FakeGistService(fakeBreedsList)
            val viewModel = BreedsViewModel(gistService = fakeService)
            advanceUntilIdle()

            viewModel.onCareLevelFilterChanged("Alto")
            advanceUntilIdle()

            val results = viewModel.breeds.value
            assertEquals(1, results.size)
            assertEquals("Pastor Alemán", results.first().nombre)
        }
    }
}
