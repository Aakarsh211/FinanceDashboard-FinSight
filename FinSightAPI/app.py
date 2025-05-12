from flask import Flask, request, jsonify
import yfinance as yf
from flask_cors import CORS
from stock_utils import get_stock_info, get_sentiment

app = Flask(__name__)
CORS(app)  # Enable CORS for Android Emulator

@app.route("/searchStocks", methods=["GET"])
def search_stocks():
    query = request.args.get('query', '').strip().lower()
    
    if not query or len(query) < 2:
        return jsonify([])
    
    try:
        # Use Yahoo Finance's search API
        url = f"https://query2.finance.yahoo.com/v1/finance/search?q={query}"
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        data = response.json()
        
        # Extract relevant results
        results = []
        for item in data.get('quotes', [])[:10]:  # Limit to 10 results
            if item.get('symbol') and item.get('shortname'):
                results.append({
                    "ticker": item['symbol'],
                    "name": item['shortname']
                })
        
        return jsonify(results)
        
    except Exception as e:
        print(f"Search error: {str(e)}")
        return jsonify([])  # Return empty array on failure

@app.route("/getPriceHistory", methods=["GET"])
def get_price_history():
    ticker = request.args.get('ticker')
    range_map = {
        '24h': ('1d', '5m'),
        '1W': ('5d', '15m'),
        '1M': ('1mo', '1h'),
        '3M': ('3mo', '1h'),
        'YTD': ('ytd', '1d'),
        '1Y': ('1y', '1d'),
        '5Y': ('5y', '1wk')
    }
    time_range = request.args.get('range', '1M')
    period, interval = range_map.get(time_range, ('1mo', '1h'))

    stock = yf.Ticker(ticker)
    hist = stock.history(period=period, interval=interval)

    result = [
        {"timestamp": str(idx), "price": round(row["Close"], 2)}
        for idx, row in hist.iterrows()
    ]
    return jsonify(result)

@app.route("/stock/<ticker>", methods=["GET"])
def stock_info(ticker):
    try:
        data = get_stock_info(ticker)
        return jsonify(data)
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
