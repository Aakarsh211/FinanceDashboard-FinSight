import yfinance as yf
import matplotlib.pyplot as plt
import datetime
import mplcursors

def get_stock_info(ticker):
    ticker = ticker.upper()
    try:
        stock = yf.Ticker(ticker)
        info = stock.info

        hist = stock.history(period="1d")
        current_price = hist["Close"].iloc[-1] if not hist.empty else "N/A"

        print(f"\n--- {ticker} Stock Info ---")
        print(f"Name: {info.get('shortName', 'N/A')}")
        print(f"Current Price: {current_price}")
        print(f"Previous Close: {info.get('previousClose', 'N/A')}")
        print(f"Open: {info.get('open', 'N/A')}")
        print(f"Bid: {info.get('bid', 'N/A')}")
        print(f"Ask: {info.get('ask', 'N/A')}")
        print(f"Day's Range: {info.get('dayLow', 'N/A')} - {info.get('dayHigh', 'N/A')}")
        print(f"52 Week Range: {info.get('fiftyTwoWeekLow', 'N/A')} - {info.get('fiftyTwoWeekHigh', 'N/A')}")
        print(f"Volume: {info.get('volume', 'N/A')}")
        print(f"Avg Volume: {info.get('averageVolume', 'N/A')}")
        print(f"Net Assets: {info.get('totalAssets', 'N/A')}")
        print(f"NAV: {info.get('navPrice', 'N/A')}")
        print(f"PE Ratio: {info.get('trailingPE', 'N/A')}")
        print(f"Yield: {info.get('yield', 'N/A')}")
        print(f"YTD Return: {info.get('ytdReturn', 'N/A')}")
        print(f"Beta (5Y Monthly): {info.get('beta', 'N/A')}")
        print(f"Expense Ratio: {info.get('annualReportExpenseRatio', 'N/A')}")
        print(f"Market Cap: {info.get('marketCap', 'N/A')}")
        print(f"Sector: {info.get('sector', 'N/A')}")
        print(f"Industry: {info.get('industry', 'N/A')}")

    except Exception as e:
        print(f"Error fetching data for {ticker}: {e}")


def plot_stock_chart(ticker):
    ticker = ticker.upper()
    stock = yf.Ticker(ticker)

    # Time range menu
    range_options = {
        "1": ("1mo", "Last 1 Month"),
        "2": ("3mo", "Last 3 Months"),
        "3": ("6mo", "Last 6 Months"),
        "4": ("1y", "Last 1 Year"),
        "5": ("5y", "Last 5 Years")
    }

    print("\nChoose time range:")
    for key, (_, label) in range_options.items():
        print(f"{key}. {label}")

    choice = input("Enter choice (1-5): ").strip()
    period, label = range_options.get(choice, ("3mo", "Last 3 Months"))  # default fallback

    # Get historical data
    hist = stock.history(period=period)

    if hist.empty:
        print("No historical data available.")
        return

    # Plot
    plt.figure(figsize=(10, 5))
    plt.plot(hist.index, hist['Close'], label='Close Price', color='blue')
    plt.title(f"{ticker} - {label}")
    plt.xlabel("Date")
    plt.ylabel("Price (USD)")
    plt.grid(True)
    plt.legend()

    # Add hover cursor
    cursor = mplcursors.cursor(hover=True)
    cursor.connect("add", lambda sel: sel.annotation.set_text(
    f"{hist.index[int(sel.index)].strftime('%Y-%m-%d')}\n${sel.target[1]:.2f}"))

    plt.tight_layout()
    plt.show()