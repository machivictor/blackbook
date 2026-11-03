package com.machivictordev.blackbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.machivictordev.blackbook.ui.util.LocalCurrency
import com.machivictordev.blackbook.ui.util.formatMoney

@Composable
fun Dashboard(
    totalOutstandingMinor: Long,
    totalDueMinor: Long,
    totalCollectedMinor: Long,
    activeDebtsCount: Int
) {
    val currency = LocalCurrency.current

    // Summary Card
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Total outstanding",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    text = formatMoney(totalOutstandingMinor, currency.symbol),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = "Across $activeDebtsCount active debts",
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }

//        Spacer(modifier = Modifier.size(8.dp))
//
//        Row() {
//
//            Column(
//                modifier = Modifier
//                    .clip(RoundedCornerShape(20.dp))
//                    .background(MaterialTheme.colorScheme.errorContainer)
//                    .padding(16.dp)
//                    .weight(1f)
//            ) {
//
//                Text(
//                    text = "Total debt given",
//                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                )
//
//                Spacer(modifier = Modifier.size(4.dp))
//
//                Text(
//                    text = formatMoney(totalDueMinor, currency.symbol),
//                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                )
//            }
//
//            Spacer(modifier = Modifier.size(8.dp))
//
//            Column(
//                modifier = Modifier
//                    .clip(RoundedCornerShape(20.dp))
//                    .background(MaterialTheme.colorScheme.secondaryContainer)
//                    .padding(16.dp)
//                    .weight(1f)
//            ) {
//
//                Text(
//                    text = "Total collected",
//                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                )
//
//                Spacer(modifier = Modifier.size(4.dp))
//
//                Text(
//                    text = formatMoney(totalCollectedMinor, currency.symbol),
//                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                )
//            }
//        }
    }
}
