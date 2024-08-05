package accountdetail

import accountdetail.composable.AccountHeader
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.koin.core.parameter.parametersOf

class AccountDetailScreen(
    private val id: String,
) : Screen {
    @Composable
    override fun Content() {
        val model = getScreenModel<AccountDetailMviModel>(parameters = { parametersOf(id) })
        val uiState by model.uiState.collectAsState()

        Scaffold(
            topBar = {
            },
            content = { padding ->
                Box(
                    modifier = Modifier.padding(padding),
                ) {
                    LazyColumn {
                        item {
                            uiState.account?.let {
                                AccountHeader(account = it)
                            }
                        }
                    }
                }
            },
        )
    }
}
