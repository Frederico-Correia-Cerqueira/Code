package infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.example.gomoku.infrastructure.UserDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder


@OptIn(ExperimentalCoroutinesApi::class)
class UserDataStoreTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = TestScope(UnconfinedTestDispatcher()),
            produceFile = { tmpFolder.newFile("test.preferences_pb") })

    @Test
    fun getUserInfo_returns_null_if_no_user_info_is_stored(): Unit = runTest {
        // Arrange
        val sut = UserDataStore(testDataStore)
        // Act
        val userInfo = sut.getUserInfo()
        // Assert
        assertNull(userInfo)
    }
}