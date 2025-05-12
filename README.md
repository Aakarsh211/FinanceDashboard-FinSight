FinSight: The All-In-One Finance Dashboard

FinSight is a modern, Kotlin-based Android application that delivers comprehensive stock market insights by integrating with a custom Python REST API. Designed with both investors and developers in mind, the app offers real-time stock data, interactive charts, financial news, analyst ratings, and AI-driven insights — all in one unified interface.

A proper installable application is underway via the play store, keep your eyes peeled!! For now this application can be accessed and downloaded from Android studio on your favorite android device!

Current Features:

Unified Stock Search: Search stocks using either ticker symbols (e.g., AAPL) or company names (Apple Inc.) with dynamic dropdown suggestions.

Interactive Price Charts: View detailed historical price charts with support for multiple timeframes (24hr, 1W, 1M, 3M, YTD, 1Y, 5Y).

Detailed Stock Information: Automatically displays price, volume, and other key metrics upon search.

Stock News: Curated financial headlines from reputable news sources with reference to the selected stock.

Analyst Ratings: Aggregated analyst sentiment from trusted financial platforms. Buy/Hold/Sell ratings, target prices, and rationale.

AI-Generated Insights: Natural Language Processing (NLP)-driven sentiment analysis on:
                                 - News coverage
                                 - Internet discussions (reddit, Stockchase, etc.)
                                 - Financial fundamentals (earnings growth, demand indicators)

Modern UI: Built with Jetpack Compose for fluid navigation, responsive layout, and clean theming. Vertical navigation buttons for quick access to all insight modules.

Scalable Backend Architecture: REST API developed in Python using FastAPI for performance and modularity. 
                               Handling:
                                 - Ticker/company name search
                                 - Historical stock data aggregation
                                 - News fetching
                                 - Analyst rating collection
                                 - AI insights generation (via sentiment models and financial analysis)


How It Works:
1: Search Function: A single input bar lets users type either a ticker or stock name. Suggestions appear in real time from the backend.
2: Unified Display: Upon selecting a stock, the stock details and chart are shown on the same screen for quick manual analysis by the user.
3: Modular Navigation: Buttons on the same page allow users to explore news, analyst opinions, or AI-based recommendations, each seamlessly opening a dedicated screen.
4: Data Fetching: REST API endpoints return relevant data that is parsed and displayed using ViewModel architecture.

The Tech Stack (Tools Used):

| Layer            | Technology                              |
| ---------------- | --------------------------------------- |
| Frontend         | Kotlin, Jetpack Compose                 |
| Backend          | Python, FastAPI                         |
| Data Fetching    | Retrofit (Android), HTTPX (Python)      |
| Charting         | MPAndroidChart                          |
| Architecture     | MVVM (Model-View-ViewModel)             |
| NLP Models       | TextBlob, VADER, or custom models       |
| External APIs    | Yahoo Finance, Alpha Vantage, News APIs |

AUTHOR: 
Aakarsh Gupta
aakarsh1@ualberta.ca
Calgary, AB
University of Alberta (B.Sc in Computer Engineering Co-op)
LinkedIn: https://www.linkedin.com/in/aakarsh-gupta-556814256/



