import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun TimeWheelPicker(
    initialHours: Int = 0,
    initialMinutes: Int = 0,
    initialSeconds: Int = 0,
    initialMillis: Int = 0,
    onTimeSelected: (hours: Int, minutes: Int, seconds: Int, millis: Int) -> Unit
) {
    // Локальные состояния для выбранных значений
    var selectedHours by remember { mutableIntStateOf(initialHours) }
    var selectedMinutes by remember { mutableIntStateOf(initialMinutes) }
    var selectedSeconds by remember { mutableIntStateOf(initialSeconds) }
    var selectedMillis1 by remember { mutableIntStateOf(initialMillis / 100) }
    var selectedMillis2 by remember { mutableIntStateOf(initialMillis % 100 / 10) }
    var selectedMillis3 by remember { mutableIntStateOf(initialMillis % 10) }

    // Перерисовывать родительский экран, когда выбирается новое значение
    LaunchedEffect(
        selectedHours,
        selectedMinutes,
        selectedSeconds,
        selectedMillis1,
        selectedMillis2,
        selectedMillis3
    ) {
        onTimeSelected(
            selectedHours,
            selectedMinutes,
            selectedSeconds,
            selectedMillis1 * 100 + selectedMillis2 * 10 + selectedMillis3
        )
    }

    // В три столбца размещаем три колёсика
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // Колёсико для часов: 0..23
        WheelPicker(
            items = (0..23).map { it.toString().padStart(2, '0') },
            selectedIndex = selectedHours,
            onItemSelected = { selectedHours = it },
            modifier = Modifier.weight(1f)
        )

        // Колёсико для минут: 0..59
        WheelPicker(
            items = (0..59).map { it.toString().padStart(2, '0') },
            selectedIndex = selectedMinutes,
            onItemSelected = { selectedMinutes = it },
            modifier = Modifier.weight(1f)
        )

        // Колёсико для секунд: 0..59
        WheelPicker(
            items = (0..59).map { it.toString().padStart(2, '0') },
            selectedIndex = selectedSeconds,
            onItemSelected = { selectedSeconds = it },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.weight(0.1f))

        WheelPicker(
            items = (0..9).map { it.toString() },
            selectedIndex = selectedMillis1,
            onItemSelected = { selectedMillis1 = it },
            modifier = Modifier.weight(0.7f)
        )
        WheelPicker(
            items = (0..9).map { it.toString() },
            selectedIndex = selectedMillis2,
            onItemSelected = { selectedMillis2 = it },
            modifier = Modifier.weight(0.7f)
        )
        WheelPicker(
            items = (0..9).map { it.toString() },
            selectedIndex = selectedMillis3,
            onItemSelected = { selectedMillis3 = it },
            modifier = Modifier.weight(0.7f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    items: List<String>,
    modifier: Modifier = Modifier,
    // Сколько элементов визуально видно до и после текущего выбранного
    visibleCount: Int = 5,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    // Состояние списка: запустимся с выбранным элементом
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    // Поведение «прилипания» к ближайшему элементу при прокрутке
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Следим за изменением первого видимого элемента и обновляем выбранный индекс
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                onItemSelected(index)
            }
    }

    // Высота одного элемента (при желании можно вычислить динамически)
    val itemHeight = 40.dp

    Box(
        modifier = modifier
            .height(itemHeight * visibleCount) // Общая высота: несколько элементов
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    ) {
        // Сам список
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            contentPadding = PaddingValues(vertical = (itemHeight * (visibleCount / 2))),
            verticalArrangement = Arrangement.Center
        ) {
            itemsIndexed(items) { index, item ->
                // Проверяем, является ли текущий элемент «активным»
                val isSelected = (index == selectedIndex)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = if (isSelected) 32.sp else 24.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                }
            }
        }

        // Визуально можно добавить полупрозрачный градиент сверху и снизу,
        // чтобы «затенять» невыбранные элементы
    }
}

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF3F51B5)
@Composable
private fun Time() {
    TimeWheelPicker { hours, minutes, seconds, millis-> }
}
