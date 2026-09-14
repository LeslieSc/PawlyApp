package com.example.pawlyapp.ui.onboarding.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawlyapp.R
import kotlinx.coroutines.launch

@Composable
fun OnboardingView(
    onFinishOnboarding: () -> Unit
) {

    val backgroundColor = Color(0xFFF7F1EA)
    val cardColor = Color.White
    val primaryBrown = Color(0xFF8B6B4F)
    val darkBrown = Color(0xFF4E342E)
    val softBeige = Color(0xFFE8D8C3)
    val textSoft = Color(0xFF8D7B68)

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(
                horizontal = 24.dp,
                vertical = 22.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Saltar",
                color = textSoft,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    }
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            when (page) {

                0 -> OnboardingPage(
                    title = "Bienvenido a Pawly App",
                    description = "Todo lo que necesitas para cuidar a tu perro con cariño y tranquilidad",
                    imageRes = R.drawable.perro1,
                    imageDescription = "Golden retriever de bienvenida",
                    backgroundColor = softBeige,
                    darkBrown = darkBrown,
                    textSoft = textSoft
                )

                1 -> OnboardingFeaturesPage(
                    darkBrown = darkBrown,
                    textSoft = textSoft,
                    primaryBrown = primaryBrown,
                    softBeige = softBeige
                )

                2 -> OnboardingPage(
                    title = "Disfruten más momentos juntos",
                    description = "Comienza a cuidarlo y acompáñalo en cada etapa",
                    imageRes = R.drawable.perrito3,
                    imageDescription = "Persona abrazando a su golden retriever",
                    backgroundColor = softBeige,
                    darkBrown = darkBrown,
                    textSoft = textSoft
                )
            }
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            repeat(3) { index ->

                val selected =
                    pagerState.currentPage == index

                Box(
                    modifier = Modifier
                        .size(
                            width = if (selected) 24.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            if (selected) {
                                primaryBrown
                            } else {
                                softBeige
                            }
                        )
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Button(
            onClick = {

                if (pagerState.currentPage < 2) {

                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
                    }

                } else {

                    onFinishOnboarding()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryBrown,
                contentColor = cardColor
            )
        ) {

            Text(
                text =
                    if (pagerState.currentPage == 2) {
                        "Comenzar"
                    } else {
                        "Siguiente"
                    },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.size(10.dp)
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun OnboardingPage(
    title: String,
    description: String,
    imageRes: Int,
    imageDescription: String,
    backgroundColor: Color,
    darkBrown: Color,
    textSoft: Color
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 34.dp,
                        topEnd = 34.dp,
                        bottomStart = 64.dp,
                        bottomEnd = 64.dp
                    )
                )
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(
                    id = imageRes
                ),
                contentDescription = imageDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = title,
            fontSize = 29.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = 8.dp
            )
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = description,
            fontSize = 16.sp,
            lineHeight = 23.sp,
            color = textSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = 20.dp
            )
        )
    }
}

@Composable
private fun OnboardingFeaturesPage(
    darkBrown: Color,
    textSoft: Color,
    primaryBrown: Color,
    softBeige: Color
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Su bienestar, siempre contigo",
            fontSize = 27.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            color = darkBrown,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = 10.dp
            )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Todo lo que necesitas para acompañar su salud y rutina.",
            fontSize = 15.sp,
            lineHeight = 21.sp,
            color = textSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = 18.dp
            )
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        FeatureCard(
            title = "Consejos personalizados",
            description = "Cuidados pensados para su bienestar",
            icon = Icons.Default.Lightbulb,
            primaryBrown = primaryBrown,
            softBeige = softBeige,
            darkBrown = darkBrown,
            textSoft = textSoft
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        FeatureCard(
            title = "Monitorea su salud",
            description = "Lleva un registro de sus cuidados",
            icon = Icons.Default.Favorite,
            primaryBrown = primaryBrown,
            softBeige = softBeige,
            darkBrown = darkBrown,
            textSoft = textSoft
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        FeatureCard(
            title = "Registra su actividad",
            description = "Acompaña sus paseos y momentos activos",
            icon = Icons.Default.Pets,
            primaryBrown = primaryBrown,
            softBeige = softBeige,
            darkBrown = darkBrown,
            textSoft = textSoft
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Image(
            painter = painterResource(
                id = R.drawable.perrito2
            ),
            contentDescription = "Cachorro de Pawly",
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 28.dp,
                        topEnd = 28.dp,
                        bottomStart = 50.dp,
                        bottomEnd = 50.dp
                    )
                ),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    primaryBrown: Color,
    softBeige: Color,
    darkBrown: Color,
    textSoft: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(18.dp)
            )
            .background(Color.White)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(
                    RoundedCornerShape(14.dp)
                )
                .background(softBeige),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryBrown,
                modifier = Modifier.size(25.dp)
            )
        }

        Spacer(
            modifier = Modifier.size(12.dp)
        )

        Column {

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = darkBrown
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = textSoft
            )
        }
    }
}