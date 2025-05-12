import yfinance as yf
from transformers import pipeline

# Initialize once
sentiment_pipeline = pipeline("sentiment-analysis")

def format_market_cap(market_cap):
    if market_cap is None:
        return None
    for unit, div in (("T", 1e12), ("B", 1e9), ("M", 1e6), ("K", 1e3)):
        if market_cap >= div:
            return f"{market_cap/div:.2f}{unit}"
    return str(market_cap)

def get_stock_info(ticker: str) -> dict:
    ticker = ticker.upper()
    stock = yf.Ticker(ticker)
    info = stock.info or {}

    # Current price via 1d history (more reliable for ETFs)
    hist1d = stock.history(period="1d")
    current_price = (
        hist1d["Close"].iloc[-1]
        if not hist1d.empty
        else info.get("regularMarketPrice")
    )

    # 52-week high/low via 1y history
    hist1y = stock.history(period="1y")
    close_series = hist1y["Close"]
    week52_high = close_series.max() if not close_series.empty else info.get("fiftyTwoWeekHigh")
    week52_low  = close_series.min() if not close_series.empty else info.get("fiftyTwoWeekLow")

    return {
        "name":             info.get("longName"),
        "current_price":    current_price,
        "previous_close":   info.get("previousClose"),
        "open":             info.get("open"),
        "day_low":          info.get("dayLow"),
        "day_high":         info.get("dayHigh"),
        "day_range":        f"{info.get('dayLow')} - {info.get('dayHigh')}",
        "week_52_high":     week52_high,
        "week_52_low":      week52_low,
        "volume":           info.get("volume"),
        "average_volume":   info.get("averageVolume"),
        "market_cap":       format_market_cap(info.get("marketCap")),
        "pe_ratio":         info.get("trailingPE"),
        "dividend_yield":   info.get("dividendYield"),
        "sector":           info.get("sector"),
        "industry":         info.get("industry"),
    }

def get_sentiment(news_texts: list[str]) -> list[dict]:
    return sentiment_pipeline(news_texts)
